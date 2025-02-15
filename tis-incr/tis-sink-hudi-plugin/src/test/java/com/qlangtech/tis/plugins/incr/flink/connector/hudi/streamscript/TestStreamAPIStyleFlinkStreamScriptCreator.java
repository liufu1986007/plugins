/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.qlangtech.tis.plugins.incr.flink.connector.hudi.streamscript;

import com.qlangtech.tis.plugin.datax.hudi.partition.FieldValBasedPartition;
import com.qlangtech.tis.plugin.datax.hudi.partition.OffPartition;
import org.junit.Assert;
import org.junit.Test;

/**
 * @author: 百岁（baisui@qlangtech.com）
 * @create: 2022-03-24 16:50
 **/
public class TestStreamAPIStyleFlinkStreamScriptCreator extends BaiscFlinkStreamScriptCreator {
    @Test
    public void testStreamScriptGenerate() throws Exception {
        validateGenerateScript(StreamScriptType.STREAM_API, new OffPartition(),(mdata) -> {
            StreamAPIStyleFlinkStreamScriptCreator.HudiStreamTemplateData tplData
                    = (StreamAPIStyleFlinkStreamScriptCreator.HudiStreamTemplateData) mdata;
            String createTabDdl = tplData.getFlinkStreamerConfig(targetTableName);

            Assert.assertNotNull(createTabDdl);
            System.out.println(createTabDdl);
        });
    }


    @Test
    public void testStreamScriptGenerateWithFieldValBasedPartition () throws Exception {
        FieldValBasedPartition fieldValBasedPartition = new FieldValBasedPartition();
        fieldValBasedPartition.partitionPathField = "kind";

        validateGenerateScript(StreamScriptType.STREAM_API, fieldValBasedPartition,(mdata) -> {
            StreamAPIStyleFlinkStreamScriptCreator.HudiStreamTemplateData tplData
                    = (StreamAPIStyleFlinkStreamScriptCreator.HudiStreamTemplateData) mdata;
            String createTabDdl = tplData.getFlinkStreamerConfig(targetTableName);

            Assert.assertNotNull(createTabDdl);
            System.out.println(createTabDdl);
        });
    }


}
