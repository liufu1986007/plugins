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

import com.qlangtech.tis.datax.IDataxReaderContext;
import com.qlangtech.tis.plugin.ds.DataSourceFactory;
import com.qlangtech.tis.plugin.ds.IDataSourceDumper;
import org.apache.commons.lang.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author: 百岁（baisui@qlangtech.com）
 * @create: 2021-06-05 10:22
 **/
public class RdbmsReaderContext<READER extends BasicDataXRdbmsReader, DS extends DataSourceFactory>
        extends BasicRdbmsContext<READER, DS> implements IDataxReaderContext {
    private final String name;
    private final String sourceTableName;
    private String where;


    private final IDataSourceDumper dumper;

    public RdbmsReaderContext(String jobName, String sourceTableName, IDataSourceDumper dumper, READER reader) {
        super(reader, (reader != null) ? (DS) reader.getDataSourceFactory() : null);
        this.name = jobName;
        this.sourceTableName = sourceTableName;
//        this.reader = reader;
//        this.dsFactory = ;
        this.dumper = dumper;
    }

    public String getJdbcUrl() {
        return this.dumper.getDbHost();
    }


    public boolean isContainWhere() {
        return StringUtils.isNotBlank(this.where);
    }

    public String getWhere() {
        return where;
    }

    public void setWhere(String where) {
        this.where = where;
    }

    @Override
    public String getTaskName() {
        return this.name;
    }

    @Override
    public String getSourceEntityName() {
        return this.sourceTableName;
    }




}
