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

package com.qlangtech.tis.plugin.datax;

import com.qlangtech.tis.annotation.Public;
import com.qlangtech.tis.datax.IDataxContext;
import com.qlangtech.tis.datax.IDataxProcessor;
import com.qlangtech.tis.extension.TISExtension;
import com.qlangtech.tis.extension.impl.IOUtils;
import com.qlangtech.tis.plugin.datax.common.BasicDataXRdbmsWriter;
import com.qlangtech.tis.plugin.datax.common.InitWriterTable;
import com.qlangtech.tis.plugin.ds.ColumnMetaData;
import com.qlangtech.tis.plugin.ds.DataType;
import com.qlangtech.tis.plugin.ds.ISelectedTab;
import com.qlangtech.tis.plugin.ds.postgresql.PGDataSourceFactory;
import org.apache.commons.lang.StringUtils;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * @author: baisui 百岁
 * @create: 2021-04-07 15:30
 * @see com.alibaba.datax.plugin.writer.postgresqlwriter.PostgresqlWriter
 **/
@Public
public class DataXPostgresqlWriter extends BasicDataXRdbmsWriter<PGDataSourceFactory> {


//    @FormField(ordinal = 0, type = FormFieldType.INPUTTEXT, validate = {Validator.require})
//    public String jdbcUrl;
//    @FormField(ordinal = 1, type = FormFieldType.INPUTTEXT, validate = {Validator.require})
//    public String username;
//    @FormField(ordinal = 2, type = FormFieldType.INPUTTEXT, validate = {Validator.require})
//    public String password;
//    @FormField(ordinal = 3, type = FormFieldType.INPUTTEXT, validate = {Validator.require})
//    public String table;
//    @FormField(ordinal = 4, type = FormFieldType.INPUTTEXT, validate = {Validator.require})
//    public String column;
//    @FormField(ordinal = 5, type = FormFieldType.INPUTTEXT, validate = {})
//    public String preSql;
//    @FormField(ordinal = 6, type = FormFieldType.INPUTTEXT, validate = {})
//    public String postSql;
//    @FormField(ordinal = 7, type = FormFieldType.INPUTTEXT, validate = {})
//    public String batchSize;
//
//    @FormField(ordinal = 8, type = FormFieldType.TEXTAREA, validate = {Validator.require})
//    public String template;

    @Override
    public void initWriterTable(String targetTabName, List<String> jdbcUrls) throws Exception {
        InitWriterTable.process(this.dataXName, targetTabName, jdbcUrls);
    }

    public static String getDftTemplate() {
        return IOUtils.loadResourceFromClasspath(DataXPostgresqlWriter.class, "DataXPostgresqlWriter-tpl.json");
    }

    public static void main(String[] args) {
        System.out.println(StringUtils.EMPTY.toCharArray()[0]);
    }

    @Override
    public StringBuffer generateCreateDDL(IDataxProcessor.TableMap tableMapper) {
        if (!this.autoCreateTable) {
            return null;
        }

        // 多个主键
        boolean multiPk = tableMapper.getSourceCols().stream().filter((col) -> col.isPk()).count() > 1;

        final CreateTableSqlBuilder createTableSqlBuilder = new CreateTableSqlBuilder(tableMapper) {

            @Override
            protected void appendExtraColDef(List<ColWrapper> pks) {
//                if (!pks.isEmpty()) {
//                    script.append("  PRIMARY KEY (").append(pks.stream().map((pk) -> "`" + pk.getName() + "`")
//                            .collect(Collectors.joining(","))).append(")").append("\n");
//                }
                if (multiPk) {
                    this.script.append(", CONSTRAINT ").append("uk_" + getCreateTableName() + "_unique_" + pks.stream().map((c) -> c.getName()).collect(Collectors.joining("_")))
                            .append(" UNIQUE(")
                            .append(pks.stream().map((c) -> c.getName()).collect(Collectors.joining(","))).append(")");
                }
            }

            @Override
            protected char colEscapeChar() {
                return '\"';
            }

            @Override
            protected ColWrapper createColWrapper(ISelectedTab.ColMeta c) {
                return new ColWrapper(c) {
                    @Override
                    public String getMapperType() {
                        return convertType(this.meta);
                    }
                };
            }

            @Override
            protected void appendTabMeta(List<ColWrapper> pks) {

            }

            /**
             * https://www.runoob.com/mysql/mysql-data-types.html
             * @param col
             * @return
             */
            private String convertType(ISelectedTab.ColMeta col) {
                DataType type = col.getType();
                String colType = type.accept(new DataType.TypeVisitor<String>() {
                    @Override
                    public String bigInt(DataType type) {
                        return "BIGINT";
                    }

                    @Override
                    public String doubleType(DataType type) {
                        return "FLOAT8";
                    }

                    @Override
                    public String dateType(DataType type) {
                        return "DATE";
                    }

                    @Override
                    public String timestampType(DataType type) {
                        return "TIMESTAMP";
                    }

                    @Override
                    public String bitType(DataType type) {
                        return "BIT";
                    }

                    @Override
                    public String blobType(DataType type) {
                        return "BYTEA";
                    }

                    @Override
                    public String varcharType(DataType type) {
                        return "VARCHAR(" + type.columnSize + ")";
                    }

                    @Override
                    public String intType(DataType type) {
                        return "INTEGER";
                    }

                    @Override
                    public String floatType(DataType type) {
                        return "FLOAT4";
                    }

                    @Override
                    public String decimalType(DataType type) {
                        return "DECIMAL";
                    }

                    @Override
                    public String timeType(DataType type) {
                        return "TIME";
                    }

                    @Override
                    public String tinyIntType(DataType dataType) {
                        return smallIntType(dataType);
                    }

                    @Override
                    public String smallIntType(DataType dataType) {
                        return "SMALLINT";
                    }
                });

                return colType + (!multiPk && col.isPk() ? " PRIMARY KEY" : StringUtils.EMPTY);
            }

        };
        return createTableSqlBuilder.build();
    }

    @Override
    public IDataxContext getSubTask(Optional<IDataxProcessor.TableMap> tableMap) {
        PostgreWriterContext writerContext = new PostgreWriterContext(this, tableMap.get());

        return writerContext;
    }


    @TISExtension()
    public static class DefaultDescriptor extends RdbmsWriterDescriptor {
        public DefaultDescriptor() {
            super();
        }

        @Override
        public boolean isSupportTabCreate() {
            return true;
        }

        @Override
        public String getDisplayName() {
            return DataXPostgresqlReader.PG_NAME;
        }
    }
}
