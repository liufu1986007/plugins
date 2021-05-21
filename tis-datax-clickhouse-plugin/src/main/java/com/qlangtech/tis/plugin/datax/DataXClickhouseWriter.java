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

package com.qlangtech.tis.plugin.datax;

import com.qlangtech.tis.datax.IDataxContext;
import com.qlangtech.tis.datax.IDataxProcessor;
import com.qlangtech.tis.datax.IDataxReaderContext;
import com.qlangtech.tis.datax.ISelectedTab;
import com.qlangtech.tis.datax.impl.DataxReader;
import com.qlangtech.tis.datax.impl.DataxWriter;
import com.qlangtech.tis.extension.TISExtension;
import com.qlangtech.tis.extension.impl.IOUtils;
import com.qlangtech.tis.plugin.annotation.FormField;
import com.qlangtech.tis.plugin.annotation.FormFieldType;
import com.qlangtech.tis.plugin.annotation.Validator;

import java.util.Iterator;
import java.util.List;
import java.util.Optional;

/**
 * https://github.com/alibaba/DataX/blob/master/clickhousewriter/src/main/resources/plugin_job_template.json
 *
 * @author: 百岁（baisui@qlangtech.com）
 * @create: 2021-05-16 21:48
 **/
public class DataXClickhouseWriter extends DataxWriter {

    public static final String DATAX_NAME = "Clickhouse";

    @FormField(ordinal = 79, type = FormFieldType.TEXTAREA, validate = {Validator.require})
    public String template;

    @Override
    public IDataxContext getSubTask(Optional<IDataxProcessor.TableMap> tableMap) {
        return null;
    }

    @Override
    public String getTemplate() {
        return template;
    }



    public static String getDftTemplate() {
        return IOUtils.loadResourceFromClasspath(DataXClickhouseWriter.class, "DataXElasticsearchWriter-tpl.json");
    }

    @TISExtension()
    public static class DefaultDescriptor extends DataxWriter.BaseDataxWriterDescriptor {
        public DefaultDescriptor() {
            super();
        }

        @Override
        public boolean isRdbms() {
            return true;
        }

        @Override
        public String getDisplayName() {
            return DATAX_NAME;
        }
    }
}
