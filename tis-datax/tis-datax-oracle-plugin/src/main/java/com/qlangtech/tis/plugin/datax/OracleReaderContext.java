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

package com.qlangtech.tis.plugin.datax;

import com.qlangtech.tis.plugin.datax.common.RdbmsReaderContext;
import com.qlangtech.tis.plugin.ds.IDataSourceDumper;
import com.qlangtech.tis.plugin.ds.oracle.OracleDataSourceFactory;
import org.apache.commons.lang.StringUtils;

/**
 * @author: 百岁（baisui@qlangtech.com）
 * @create: 2021-06-24 15:41
 **/
public class OracleReaderContext extends RdbmsReaderContext<DataXOracleReader, OracleDataSourceFactory> {
    public OracleReaderContext(String jobName, String sourceTableName, IDataSourceDumper dumper, DataXOracleReader reader) {
        super(jobName, sourceTableName, dumper, reader);
    }

    public String getUserName() {
        return this.dsFactory.getUserName();
    }

    public boolean isContainPassword() {
        return StringUtils.isNotEmpty(this.dsFactory.getPassword());
    }

    public String getPassword() {
        return this.dsFactory.getPassword();
    }

//    @FormField(ordinal = 5, type = FormFieldType.INPUTTEXT, validate = {})
//    public Boolean splitPk;
//
//    @FormField(ordinal = 8, type = FormFieldType.INT_NUMBER, validate = {})
//    public Integer fetchSize;
//
//    @FormField(ordinal = 8, type = FormFieldType.TEXTAREA, validate = {})
//    public String session;

    public boolean isContainSplitPk() {
        return this.plugin.splitPk != null;
    }

    public boolean isSplitPk() {
        return this.plugin.splitPk;
    }

    public boolean isContainFetchSize() {
        return this.plugin.fetchSize != null;
    }

    public int getFetchSize() {
        return this.plugin.fetchSize;
    }

    public boolean isContainSession() {
        return StringUtils.isNotEmpty(this.plugin.session);
    }

    public String getSession() {
        return this.plugin.session;
    }

    @Override
    protected String colEscapeChar() {
        return StringUtils.EMPTY;
    }
}
