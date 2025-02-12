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

import com.qlangtech.tis.datax.IDataxProcessor;
import com.qlangtech.tis.plugin.datax.common.RdbmsWriterContext;
import com.qlangtech.tis.plugin.ds.postgresql.PGDataSourceFactory;

import java.util.stream.Collectors;

/**
 * @author: 百岁（baisui@qlangtech.com）
 * @create: 2021-06-23 15:43
 **/
public class PostgreWriterContext extends RdbmsWriterContext<DataXPostgresqlWriter, PGDataSourceFactory> {

    public PostgreWriterContext(DataXPostgresqlWriter writer, IDataxProcessor.TableMap tabMapper) {
        super(writer, tabMapper);
    }


    @Override
    protected String colEscapeChar() {
        return PostgresReaderContext.colEscapeChar;
    }
}
