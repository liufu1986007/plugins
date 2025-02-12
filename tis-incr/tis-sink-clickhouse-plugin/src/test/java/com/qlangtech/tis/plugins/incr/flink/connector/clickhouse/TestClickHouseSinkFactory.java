/**
 *   Licensed to the Apache Software Foundation (ASF) under one
 *   or more contributor license agreements.  See the NOTICE file
 *   distributed with this work for additional information
 *   regarding copyright ownership.  The ASF licenses this file
 *   to you under the Apache License, Version 2.0 (the
 *   "License"); you may not use this file except in compliance
 *   with the License.  You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 *   Unless required by applicable law or agreed to in writing, software
 *   distributed under the License is distributed on an "AS IS" BASIS,
 *   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *   See the License for the specific language governing permissions and
 *   limitations under the License.
 */

package com.qlangtech.tis.plugins.incr.flink.connector.clickhouse;

import com.google.common.collect.Maps;
import com.qlangtech.tis.datax.IDataxProcessor;
import com.qlangtech.tis.datax.IDataxReader;
import com.qlangtech.tis.plugin.datax.DataXClickhouseWriter;
import com.qlangtech.tis.plugin.datax.SelectedTab;
import com.qlangtech.tis.plugin.ds.ColumnMetaData;
import com.qlangtech.tis.plugin.ds.ISelectedTab;
import com.qlangtech.tis.plugin.ds.clickhouse.ClickHouseDataSourceFactory;
import com.qlangtech.tis.realtime.transfer.DTO;
import com.qlangtech.tis.test.TISEasyMock;
import com.qlangtech.tis.trigger.util.JsonUtil;
import com.qlangtech.tis.util.DescriptorsJSON;
import junit.framework.TestCase;
import org.apache.commons.compress.utils.Lists;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.functions.sink.SinkFunction;
import org.easymock.EasyMock;

import java.sql.Types;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author: 百岁（baisui@qlangtech.com）
 * @create: 2021-12-02 09:13
 **/
public class TestClickHouseSinkFactory extends TestCase implements TISEasyMock {

//    public void test() {
//        Path path = Paths.get("/tmp/tis-clickhouse-sink");
//        System.out.println(Files.isDirectory(path, LinkOption.NOFOLLOW_LINKS));
//    }

    public void testDescriptorsJSONGenerate() {
        ClickHouseSinkFactory sinkFactory = new ClickHouseSinkFactory();
        DescriptorsJSON descJson = new DescriptorsJSON(sinkFactory.getDescriptor());

        JsonUtil.assertJSONEqual(ClickHouseSinkFactory.class, "clickhouse-sink-factory.json"
                , descJson.getDescriptorsJSON(), (m, e, a) -> {
                    assertEquals(m, e, a);
                });

    }

    public void testCreateSinkFunction() throws Exception {

        String tableName = "totalpayinfo";
        String colEntityId = "entity_id";
        String colNum = "num";
        String colId = "id";
        String colCreateTime = "create_time";

        IDataxProcessor dataxProcessor = mock("dataxProcessor", IDataxProcessor.class);

        IDataxReader dataxReader = mock("dataxReader", IDataxReader.class);
        List<ISelectedTab> selectedTabs = Lists.newArrayList();
        SelectedTab totalpayinfo = mock(tableName, SelectedTab.class);
        EasyMock.expect(totalpayinfo.getName()).andReturn(tableName);
        List<ISelectedTab.ColMeta> cols = Lists.newArrayList();
        ISelectedTab.ColMeta cm = new ISelectedTab.ColMeta();
        cm.setName(colEntityId);
        cm.setType(new DataType(Types.VARCHAR, 6));
        cols.add(cm);

        cm = new ISelectedTab.ColMeta();
        cm.setName(colNum);
        cm.setType(new DataType(Types.INTEGER));
        cols.add(cm);

        cm = new ISelectedTab.ColMeta();
        cm.setName(colId);
        cm.setType(new DataType(Types.VARCHAR, 32));
        cm.setPk(true);
        cols.add(cm);

        cm = new ISelectedTab.ColMeta();
        cm.setName(colCreateTime);
        cm.setType(new DataType(Types.BIGINT));
        cols.add(cm);

        EasyMock.expect(totalpayinfo.getCols()).andReturn(cols).anyTimes();
        selectedTabs.add(totalpayinfo);
        EasyMock.expect(dataxReader.getSelectedTabs()).andReturn(selectedTabs);

        EasyMock.expect(dataxProcessor.getReader(null)).andReturn(dataxReader);


        DataXClickhouseWriter dataXWriter = mock("dataXWriter", DataXClickhouseWriter.class);
        dataXWriter.initWriterTable(tableName, Collections.singletonList("jdbc:clickhouse://192.168.28.201:8123/tis"));


        ClickHouseDataSourceFactory sourceFactory = new ClickHouseDataSourceFactory();

        sourceFactory.userName = "default";
        sourceFactory.dbName = "tis";
        sourceFactory.password = "123456";
        sourceFactory.port = 8123;
        sourceFactory.nodeDesc = "192.168.28.201";

        EasyMock.expect(dataXWriter.getDataSourceFactory()).andReturn(sourceFactory);

        EasyMock.expect(dataxProcessor.getWriter(null)).andReturn(dataXWriter);


        Map<String, IDataxProcessor.TableAlias> aliasMap = new HashMap<>();
        IDataxProcessor.TableAlias tab = new IDataxProcessor.TableAlias(tableName);
        aliasMap.put(tableName, tab);
        EasyMock.expect(dataxProcessor.getTabAlias()).andReturn(aliasMap);

        this.replay();

        ClickHouseSinkFactory clickHouseSinkFactory = new ClickHouseSinkFactory();
        clickHouseSinkFactory.ignoringSendingException = true;
        clickHouseSinkFactory.maxBufferSize = 1;
        clickHouseSinkFactory.numRetries = 5;
        clickHouseSinkFactory.numWriters = 1;
        clickHouseSinkFactory.queueMaxCapacity = 1;
        clickHouseSinkFactory.timeout = 30;

        Map<IDataxProcessor.TableAlias, SinkFunction<DTO>>
                sinkFuncs = clickHouseSinkFactory.createSinkFunction(dataxProcessor);
        assertTrue(sinkFuncs.size() > 0);

        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        env.setParallelism(1);

        DTO d = new DTO();
        d.setTableName(tableName);
        Map<String, Object> after = Maps.newHashMap();
        after.put(colEntityId, "334556");
        after.put(colNum, "5");
        after.put(colId, "123dsf124325253dsf123");
        after.put(colCreateTime, "20211113115959");
        d.setAfter(after);
        assertEquals(1, sinkFuncs.size());

        for (Map.Entry<IDataxProcessor.TableAlias, SinkFunction<DTO>> entry : sinkFuncs.entrySet()) {
            env.fromElements(new DTO[]{d}).addSink(entry.getValue()).name("clickhouse");
            break;
        }

        env.execute("testJob");

        Thread.sleep(5000);

        this.verifyAll();
    }
}
