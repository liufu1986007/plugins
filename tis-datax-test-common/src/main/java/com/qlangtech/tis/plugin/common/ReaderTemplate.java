/**
 * Copyright (c) 2020 QingLang, Inc. <baisui@qlangtech.com>
 * <p>
 * This program is free software: you can use, redistribute, and/or modify
 * it under the terms of the GNU Affero General Public License, version 3
 * or later ("AGPL"), as published by the Free Software Foundation.
 * <p>
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE.
 * <p>
 * You should have received a copy of the GNU Affero General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */

package com.qlangtech.tis.plugin.common;

import com.qlangtech.tis.datax.*;
import com.qlangtech.tis.datax.impl.DataXCfgGenerator;
import com.qlangtech.tis.datax.impl.DataxReader;
import junit.framework.TestCase;
import org.easymock.EasyMock;

import java.io.IOException;
import java.util.Iterator;
import java.util.Optional;

/**
 * @author: 百岁（baisui@qlangtech.com）
 * @create: 2021-05-23 17:06
 **/
public class ReaderTemplate {
    public static void validateDataXReader(String assertFileName, String dataXName, DataxReader dataxReader) throws IOException {
        IDataxProcessor processor = EasyMock.mock("dataxProcessor", IDataxProcessor.class);

        IDataxGlobalCfg dataxGlobalCfg = EasyMock.mock("dataxGlobalCfg", IDataxGlobalCfg.class);
        EasyMock.expect(processor.getDataXGlobalCfg()).andReturn(dataxGlobalCfg).anyTimes();
        IDataxWriter dataxWriter = EasyMock.mock("dataxWriter", IDataxWriter.class);
        EasyMock.expect(processor.getWriter(null)).andReturn(dataxWriter).anyTimes();
        IDataxContext dataxContext = EasyMock.mock("dataxWriterContext", IDataxContext.class);
        EasyMock.expect(dataxWriter.getSubTask(Optional.empty())).andReturn(dataxContext).anyTimes();


        EasyMock.replay(processor, dataxGlobalCfg, dataxWriter, dataxContext);

//        try (InputStream reader = this.getClass().getResourceAsStream("oss-datax-reader-assert.json")) {
//            JSONObject jsonObject = JSON.parseObject(IOUtils.toString(reader, TisUTF8.getName()));
//            System.out.println("nullFormat:" + jsonObject.getJSONObject("parameter").getString("nullFormat"));
//        }

        valiateReaderCfgGenerate(assertFileName, processor, dataxReader, dataXName);


        EasyMock.verify(processor, dataxGlobalCfg, dataxWriter, dataxContext);
    }

    private static void valiateReaderCfgGenerate(String assertFileName, IDataxProcessor processor
            , DataxReader dataXReader, String dataXName) throws IOException {


        IDataxReaderContext dataxReaderContext = null;
        Iterator<IDataxReaderContext> subTasks = dataXReader.getSubTasks();
        int dataxReaderContextCount = 0;
        while (subTasks.hasNext()) {
            dataxReaderContext = subTasks.next();
            dataxReaderContextCount++;
        }
        TestCase.assertEquals(1, dataxReaderContextCount);
        TestCase.assertNotNull(dataxReaderContext);


        DataXCfgGenerator dataProcessor = new DataXCfgGenerator(null, dataXName, processor) {
            @Override
            public String getTemplateContent() {
                return dataXReader.getTemplate();
            }
        };

        String readerCfg = dataProcessor.generateDataxConfig(dataxReaderContext, Optional.empty());
        TestCase.assertNotNull(readerCfg);
        System.out.println(readerCfg);
        JsonUtils.assertJSONEqual(dataXReader.getClass(), assertFileName, readerCfg);
    }
}
