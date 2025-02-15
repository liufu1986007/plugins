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

package com.qlangtech.tis.plugin.datax.common;

import com.alibaba.citrus.turbine.Context;
import com.qlangtech.tis.TIS;
import com.qlangtech.tis.datax.impl.DataxWriter;
import com.qlangtech.tis.plugin.KeyedPluginStore;
import com.qlangtech.tis.plugin.annotation.FormField;
import com.qlangtech.tis.plugin.annotation.FormFieldType;
import com.qlangtech.tis.plugin.annotation.Validator;
import com.qlangtech.tis.plugin.ds.*;
import com.qlangtech.tis.runtime.module.misc.IFieldErrorHandler;
import org.apache.commons.lang.StringUtils;

import java.util.List;

/**
 * @author: 百岁（baisui@qlangtech.com）
 * @create: 2021-06-23 12:07
 **/
public abstract class BasicDataXRdbmsWriter<DS extends DataSourceFactory> extends DataxWriter implements IDataSourceFactoryGetter, KeyedPluginStore.IPluginKeyAware {
    public static final String KEY_DB_NAME_FIELD_NAME = "dbName";

    @FormField(identity = false, ordinal = 0, type = FormFieldType.ENUM, validate = {Validator.require})
    public String dbName;

    @FormField(ordinal = 3, type = FormFieldType.TEXTAREA, validate = {})
    public String preSql;

    @FormField(ordinal = 6, type = FormFieldType.TEXTAREA, validate = {})
    public String postSql;

    @FormField(ordinal = 9, type = FormFieldType.TEXTAREA, validate = {})
    public String session;

    @FormField(ordinal = 12, type = FormFieldType.INT_NUMBER, validate = {Validator.integer})
    public Integer batchSize;

    @FormField(ordinal = 10, type = FormFieldType.ENUM, validate = {Validator.require})
    // 目标源中是否自动创建表，这样会方便不少
    public boolean autoCreateTable;

    @FormField(ordinal = 15, type = FormFieldType.TEXTAREA, validate = {Validator.require})
    public String template;

    public String dataXName;

    @Override
    public Integer getRowFetchSize() {
        throw new UnsupportedOperationException("just support in DataX Reader");
    }

    @Override
    public void setKey(KeyedPluginStore.Key key) {
        this.dataXName = key.keyVal.getVal();
    }

    @Override
    public final String getTemplate() {
        return this.template;
    }

    @Override
    public DS getDataSourceFactory() {
        if (StringUtils.isBlank(this.dbName)) {
            throw new IllegalStateException("prop dbName can not be null");
        }
        return getDs(this.dbName);
    }

    private static <DS> DS getDs(String dbName) {
        DataSourceFactoryPluginStore dsStore = TIS.getDataBasePluginStore(new PostedDSProp(dbName));
        return (DS) dsStore.getPlugin();
    }


    @Override
    protected Class<RdbmsWriterDescriptor> getExpectDescClass() {
        return RdbmsWriterDescriptor.class;
    }

    public abstract void initWriterTable(String targetTabName, List<String> jdbcUrls) throws Exception ;

    protected static abstract class RdbmsWriterDescriptor extends BaseDataxWriterDescriptor {
        @Override
        public final boolean isRdbms() {
            return true;
        }

        /**
         * 是否支持自动创建
         *
         * @return
         */
        public boolean isSupportTabCreate() {
            return !this.isRdbms();
        }

        /**
         * @param msgHandler
         * @param context
         * @param fieldName
         * @param val
         * @return
         */
        public boolean validateBatchSize(IFieldErrorHandler msgHandler, Context context, String fieldName, String val) {
            int batchSize = Integer.parseInt(val);
            if (batchSize < 1) {
                msgHandler.addFieldError(context, fieldName, "必须大于0");
                return false;
            }
            int maxVal = getMaxBatchSize();
            if (batchSize > maxVal) {
                msgHandler.addFieldError(context, fieldName, "不能大于" + maxVal);
                return false;
            }
            return true;
        }

        protected int getMaxBatchSize() {
            return 2024;
        }

        public boolean validateDbName(IFieldErrorHandler msgHandler, Context context, String fieldName, String dbName) {
            BasicDataSourceFactory ds = getDs(dbName);
            if (ds.getJdbcUrls().size() > 1) {
                msgHandler.addFieldError(context, fieldName, "不支持分库数据源，目前无法指定数据路由规则，请选择单库数据源");
                return false;
            }
            return true;
        }
    }

}
