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

//import com.alibaba.datax.common.util.Configuration;

import com.alibaba.datax.plugin.writer.hdfswriter.SupportHiveDataType;
import com.qlangtech.tis.datax.IDataxContext;
import com.qlangtech.tis.datax.IDataxProcessor;
import com.qlangtech.tis.datax.impl.DataxWriter;
import com.qlangtech.tis.hive.HiveColumn;
import com.qlangtech.tis.offline.FileSystemFactory;
import com.qlangtech.tis.plugin.KeyedPluginStore;
import com.qlangtech.tis.plugin.annotation.FormField;
import com.qlangtech.tis.plugin.annotation.FormFieldType;
import com.qlangtech.tis.plugin.annotation.Validator;
import com.qlangtech.tis.plugin.ds.ColumnMetaData;
import org.apache.commons.lang.StringUtils;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * @author: 百岁（baisui@qlangtech.com）
 * @create: 2021-06-01 10:05
 **/
public abstract class BasicFSWriter extends DataxWriter implements KeyedPluginStore.IPluginKeyAware {

    protected static final String KEY_FIELD_NAME_HIVE_CONN = "hiveConn";

    @FormField(ordinal = 5, type = FormFieldType.SELECTABLE, validate = {Validator.require})
    public String fsName;
    @FormField(ordinal = 6, type = FormFieldType.ENUM, validate = {Validator.require})
    public String fileType;

    @FormField(ordinal = 9, type = FormFieldType.ENUM, validate = {Validator.require})
    public String writeMode;
    @FormField(ordinal = 10, type = FormFieldType.INPUTTEXT, validate = {Validator.require})
    public String fieldDelimiter;
    @FormField(ordinal = 11, type = FormFieldType.ENUM, validate = {})
    public String compress;
    @FormField(ordinal = 12, type = FormFieldType.ENUM, validate = {})
    public String encoding;


//    public String hadoopConfig;
//    @FormField(ordinal = 11, type = FormFieldType.ENUM, validate = {})

    //    @FormField(ordinal = 12, type = FormFieldType.INPUTTEXT, validate = {})
//    public String haveKerberos;
//    @FormField(ordinal = 13, type = FormFieldType.INPUTTEXT, validate = {})
//    public String kerberosKeytabFilePath;
//    @FormField(ordinal = 14, type = FormFieldType.INPUTTEXT, validate = {})
//    public String kerberosPrincipal;
    public String dataXName;

    public static <TT extends BasicFSWriter> TT getWriterPlugin(String dataxName) {
        DataxWriter dataxWriter = load(null, dataxName);
        if (!(dataxWriter instanceof BasicFSWriter)) {
            throw new BasicHdfsWriterJob.JobPropInitializeException("datax Writer must be type of 'BasicFSWriter',but now is:" + dataxWriter.getClass());
        }
        return (TT) dataxWriter;
    }

    @Override
    public void setKey(KeyedPluginStore.Key key) {
        this.dataXName = key.keyVal.getVal();
    }

    private FileSystemFactory fileSystem;

    public FileSystemFactory getFs() {
        if (fileSystem == null) {
            this.fileSystem = FileSystemFactory.getFsFactory(fsName);
        }
        Objects.requireNonNull(this.fileSystem, "fileSystem has not be initialized");
        return fileSystem;
    }


    protected static SupportHiveDataType convert2HiveType(ColumnMetaData.DataType type) {
        Objects.requireNonNull(type, "param type can not be null");

        return type.accept(new ColumnMetaData.TypeVisitor<SupportHiveDataType>() {
            @Override
            public SupportHiveDataType intType(ColumnMetaData.DataType type) {
                return SupportHiveDataType.INT;
            }
            @Override
            public SupportHiveDataType floatType(ColumnMetaData.DataType type) {
                return SupportHiveDataType.FLOAT;
            }

            @Override
            public SupportHiveDataType decimalType(ColumnMetaData.DataType type) {
                return SupportHiveDataType.DOUBLE;
            }

            @Override
            public SupportHiveDataType timeType(ColumnMetaData.DataType type) {
                return SupportHiveDataType.TIMESTAMP;
            }

            @Override
            public SupportHiveDataType tinyIntType(ColumnMetaData.DataType dataType) {
                return SupportHiveDataType.TINYINT;
            }

            @Override
            public SupportHiveDataType smallIntType(ColumnMetaData.DataType dataType) {
                return SupportHiveDataType.SMALLINT;
            }

            @Override
            public SupportHiveDataType longType(ColumnMetaData.DataType type) {
                return SupportHiveDataType.BIGINT;
            }

            @Override
            public SupportHiveDataType doubleType(ColumnMetaData.DataType type) {
                return SupportHiveDataType.DOUBLE;
            }

            @Override
            public SupportHiveDataType dateType(ColumnMetaData.DataType type) {
                return SupportHiveDataType.DATE;
            }

            @Override
            public SupportHiveDataType timestampType(ColumnMetaData.DataType type) {
                return SupportHiveDataType.TIMESTAMP;
            }

            @Override
            public SupportHiveDataType bitType(ColumnMetaData.DataType type) {
                return SupportHiveDataType.BOOLEAN;
            }

            @Override
            public SupportHiveDataType blobType(ColumnMetaData.DataType type) {
                return SupportHiveDataType.STRING;
            }

            @Override
            public SupportHiveDataType varcharType(ColumnMetaData.DataType type) {
                return SupportHiveDataType.STRING;
            }
        });

//        switch (type.getCollapse()) {
//            case Long:
//                return SupportHiveDataType.BIGINT;
//            case Double:
//                return SupportHiveDataType.DOUBLE;
//            case STRING:
//                return SupportHiveDataType.STRING;
//            case Boolean:
//                return SupportHiveDataType.BOOLEAN;
//            case Date:
//                return SupportHiveDataType.DATE;
//            case INT:
//                return SupportHiveDataType.INT;
//            default:
//                throw new IllegalStateException("illeal type:" + type);
//        }
    }


    @Override
    public final IDataxContext getSubTask(Optional<IDataxProcessor.TableMap> tableMap) {
        if (!tableMap.isPresent()) {
            throw new IllegalArgumentException("param tableMap shall be present");
        }
        if (StringUtils.isBlank(this.dataXName)) {
            throw new IllegalStateException("param 'dataXName' can not be null");
        }
        FSDataXContext dataXContext = getDataXContext(tableMap.get());

        return dataXContext;
        //  throw new RuntimeException("dbName:" + dbName + " relevant DS is empty");
    }

    protected abstract FSDataXContext getDataXContext(IDataxProcessor.TableMap tableMap);

    public class FSDataXContext implements IDataxContext {

        final IDataxProcessor.TableMap tabMap;
        private final String dataxName;

        public FSDataXContext(IDataxProcessor.TableMap tabMap, String dataxName) {
            Objects.requireNonNull(tabMap, "param tabMap can not be null");
            this.tabMap = tabMap;
            this.dataxName = dataxName;
        }

        public String getDataXName() {
            return this.dataxName;
        }

        public String getTableName() {
            String tabName = this.tabMap.getTo();
            if (StringUtils.isBlank(tabName)) {
                throw new IllegalStateException("tabName of tabMap can not be null ,tabMap:" + tabMap);
            }
            return tabName;
        }

        public List<HiveColumn> getCols() {
            return this.tabMap.getSourceCols().stream().map((c) -> {
                HiveColumn col = new HiveColumn();
                col.setName(c.getName());
                col.setType(convert2HiveType(c.getType()).name());
                return col;
            }).collect(Collectors.toList());
        }


        public String getFileType() {
            return fileType;
        }

        public String getWriteMode() {
            return writeMode;
        }

        public String getFieldDelimiter() {
            return fieldDelimiter;
        }

        public String getCompress() {
            return compress;
        }

        public String getEncoding() {
            return encoding;
        }


        public boolean isContainCompress() {
            return StringUtils.isNotEmpty(compress);
        }

        public boolean isContainEncoding() {
            return StringUtils.isNotEmpty(encoding);
        }
    }
}
