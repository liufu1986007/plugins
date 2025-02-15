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

package com.qlangtech.tis.plugin.datax.hudi;

import com.alibaba.citrus.turbine.Context;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.qlangtech.tis.TIS;
import com.qlangtech.tis.annotation.Public;
import com.qlangtech.tis.config.ParamsConfig;
import com.qlangtech.tis.config.hive.IHiveConn;
import com.qlangtech.tis.config.hive.IHiveConnGetter;
import com.qlangtech.tis.config.spark.ISparkConnGetter;
import com.qlangtech.tis.datax.IDataXBatchPost;
import com.qlangtech.tis.datax.IDataXPluginMeta;
import com.qlangtech.tis.datax.IDataxProcessor;
import com.qlangtech.tis.datax.impl.DataXCfgGenerator;
import com.qlangtech.tis.datax.impl.DataxProcessor;
import com.qlangtech.tis.datax.impl.DataxWriter;
import com.qlangtech.tis.exec.IExecChainContext;
import com.qlangtech.tis.extension.Describable;
import com.qlangtech.tis.extension.Descriptor;
import com.qlangtech.tis.extension.TISExtension;
import com.qlangtech.tis.extension.impl.IOUtils;
import com.qlangtech.tis.extension.impl.SuFormProperties;
import com.qlangtech.tis.fs.IPath;
import com.qlangtech.tis.fullbuild.indexbuild.IRemoteTaskTrigger;
import com.qlangtech.tis.fullbuild.indexbuild.RunningStatus;
import com.qlangtech.tis.offline.FileSystemFactory;
import com.qlangtech.tis.plugin.KeyedPluginStore;
import com.qlangtech.tis.plugin.annotation.FormField;
import com.qlangtech.tis.plugin.annotation.FormFieldType;
import com.qlangtech.tis.plugin.annotation.SubForm;
import com.qlangtech.tis.plugin.annotation.Validator;
import com.qlangtech.tis.plugin.datax.BasicFSWriter;
import com.qlangtech.tis.plugin.datax.DataXHdfsWriter;
import com.qlangtech.tis.plugin.datax.hudi.spark.SparkSubmitParams;
import com.qlangtech.tis.plugin.ds.ISelectedTab;
import com.qlangtech.tis.runtime.module.misc.IFieldErrorHandler;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;

/**
 * @author: 百岁（baisui@qlangtech.com）
 * @create: 2022-01-21 13:02
 **/
@Public
public class DataXHudiWriter extends BasicFSWriter implements KeyedPluginStore.IPluginKeyAware, IHiveConn, IDataXBatchPost {
    public static final String DATAX_NAME = "Hudi";

    public static final String KEY_FIELD_NAME_SPARK_CONN = "sparkConn";

    public static final String HUDI_FILESYSTEM_NAME = "hudi_hdfs";
    private static final Logger logger = LoggerFactory.getLogger(DataXHudiWriter.class);

    @FormField(ordinal = 0, type = FormFieldType.SELECTABLE, validate = {Validator.require})
    public String sparkConn;

    @FormField(ordinal = 1, validate = {Validator.require})
    public SparkSubmitParams sparkSubmitParam;

    @FormField(ordinal = 2, type = FormFieldType.SELECTABLE, validate = {Validator.require})
    public String hiveConn;

    @FormField(ordinal = 8, type = FormFieldType.ENUM, validate = {Validator.require})
    public String tabType;

    @FormField(ordinal = 9, type = FormFieldType.INPUTTEXT, validate = {Validator.require, Validator.db_col_name})
    public String partitionedBy;

//    @FormField(ordinal = 10, type = FormFieldType.ENUM, validate = {Validator.require})
//    // 目标源中是否自动创建表，这样会方便不少
//    public boolean autoCreateTable;


    @FormField(ordinal = 10, type = FormFieldType.INT_NUMBER, validate = {Validator.require, Validator.integer})
    public Integer shuffleParallelism;

    @FormField(ordinal = 11, type = FormFieldType.ENUM, validate = {Validator.require})
    public String batchOp;


    @FormField(ordinal = 100, type = FormFieldType.TEXTAREA, validate = {Validator.require})
    public String template;


    public HudiWriteTabType getHudiTableType() {
        return HudiWriteTabType.parse(tabType);
    }

    @Override
    public IHiveConnGetter getHiveConnMeta() {
        return ParamsConfig.getItem(this.hiveConn, IHiveConnGetter.PLUGIN_NAME);
    }


//    /**
//     * @return
//     */
//    public static List<Option> allPrimaryKeys(Object contextObj) {
//        List<Option> pks = Lists.newArrayList();
//        pks.add(new Option("base_id"));
//        return pks;
//    }

    @Override
    protected HudiDataXContext getDataXContext(IDataxProcessor.TableMap tableMap) {
        return new HudiDataXContext(tableMap, this.dataXName);
    }


    public ISparkConnGetter getSparkConnGetter() {
        if (StringUtils.isEmpty(this.sparkConn)) {
            throw new IllegalArgumentException("param sparkConn can not be null");
        }
        return ISparkConnGetter.getConnGetter(this.sparkConn);
    }

    public Connection getConnection() {
        try {
            ParamsConfig connGetter = (ParamsConfig) getHiveConnMeta();
            return connGetter.createConfigInstance();
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void setKey(KeyedPluginStore.Key key) {
        this.dataXName = key.keyVal.getVal();
    }

    @Override
    public String getTemplate() {
        return template;
    }

    public static String getDftTemplate() {
        return IOUtils.loadResourceFromClasspath(DataXHudiWriter.class, "DataXHudiWriter-tpl.json");
    }

//    @Override
//    public StringBuffer generateCreateDDL(IDataxProcessor.TableMap tableMapper) {
//        return null;
//    }

    @TISExtension()
    public static class DefaultDescriptor extends DataXHdfsWriter.DefaultDescriptor implements DataxWriter.IRewriteSuFormProperties {
        private transient SuFormProperties rewriteSubFormProperties;

        public DefaultDescriptor() {
            super();
            this.registerSelectOptions(KEY_FIELD_NAME_SPARK_CONN, () -> ParamsConfig.getItems(ISparkConnGetter.PLUGIN_NAME));
            this.registerSelectOptions(BasicFSWriter.KEY_FIELD_NAME_HIVE_CONN, () -> ParamsConfig.getItems(IHiveConnGetter.PLUGIN_NAME));
        }


        public boolean validateFsName(IFieldErrorHandler msgHandler, Context context, String fieldName, String value) {
            if (!HUDI_FILESYSTEM_NAME.equals(value)) {
                msgHandler.addFieldError(context, fieldName, "必须定义一个ID为'" + HUDI_FILESYSTEM_NAME + "'的配置项");
                return false;
            }
            return true;
        }

        public boolean validateTabType(IFieldErrorHandler msgHandler, Context context, String fieldName, String value) {

            try {
                HudiWriteTabType.parse(value);
            } catch (Throwable e) {
                msgHandler.addFieldError(context, fieldName, e.getMessage());
                return false;
            }
            return true;
        }

        public boolean validateBatchOp(IFieldErrorHandler msgHandler, Context context, String fieldName, String value) {

            try {
                BatchOpMode.parse(value);
            } catch (Throwable e) {
                msgHandler.addFieldError(context, fieldName, e.getMessage());
                return false;
            }
            return true;
        }


        @Override
        public SuFormProperties overwriteSubPluginFormPropertyTypes(SuFormProperties subformProps) throws Exception {

            if (rewriteSubFormProperties != null) {
                return rewriteSubFormProperties;
            }

            String overwriteSubField = IOUtils.loadResourceFromClasspath(DataXHudiWriter.class
                    , DataXHudiWriter.class.getSimpleName() + "."
                            + subformProps.getSubFormFieldName() + IDataxProcessor.DATAX_CREATE_DATAX_CFG_FILE_NAME_SUFFIX, true);
            JSONObject subField = JSON.parseObject(overwriteSubField);
            Class<? extends Describable> clazz
                    = (Class<? extends Describable>) DataXHudiWriter.class.getClassLoader().loadClass(subField.getString(SubForm.FIELD_DES_CLASS));
            Descriptor newSubDescriptor = TIS.get().getDescriptor(clazz);
            rewriteSubFormProperties = SuFormProperties.copy(Descriptor.filterFieldProp(Descriptor.buildPropertyTypes(Optional.of(newSubDescriptor), clazz)), clazz, newSubDescriptor, subformProps);

            return rewriteSubFormProperties;
        }

//        @Override
//        public SuFormProperties.SuFormPropertiesBehaviorMeta overwriteBehaviorMeta(
//                SuFormProperties.SuFormPropertiesBehaviorMeta behaviorMeta) throws Exception {


//            {
//                "clickBtnLabel": "设置",
//                    "onClickFillData": {
//                "cols": {
//                    "method": "getTableMetadata",
//                    "params": ["id"]
//                }
//            }
//            }

        // Map<String, SuFormProperties.SuFormPropertyGetterMeta> onClickFillData = behaviorMeta.getOnClickFillData();

//            SuFormProperties.SuFormPropertyGetterMeta propProcess = new SuFormProperties.SuFormPropertyGetterMeta();
//            propProcess.setMethod(DataSourceMeta.METHOD_GET_PRIMARY_KEYS);
//            propProcess.setParams(Collections.singletonList("id"));
//            onClickFillData.put(HudiSelectedTab.KEY_RECORD_FIELD, propProcess);
//
//            propProcess = new SuFormProperties.SuFormPropertyGetterMeta();
//            propProcess.setMethod(DataSourceMeta.METHOD_GET_PARTITION_KEYS);
//            propProcess.setParams(Collections.singletonList("id"));
//            onClickFillData.put(HudiSelectedTab.KEY_PARTITION_PATH_FIELD, propProcess);
//            onClickFillData.put(HudiSelectedTab.KEY_SOURCE_ORDERING_FIELD, propProcess);


//            return behaviorMeta;
//        }

        @Override
        public boolean isRdbms() {
            return false;
        }

        @Override
        protected IDataXPluginMeta.EndType getEndType() {
            return IDataXPluginMeta.EndType.Hudi;
        }

        @Override
        public String getDisplayName() {
            return DATAX_NAME;
        }
    }

    private transient AtomicReference<DataXCfgGenerator.GenerateCfgs> generateCfgs;

    @Override
    public IRemoteTaskTrigger createPostTask(IExecChainContext execContext, ISelectedTab tab) {

        if (generateCfgs == null) {
            generateCfgs = new AtomicReference<>();
        }

        DataXCfgGenerator.GenerateCfgs genCfg
                = generateCfgs.updateAndGet((pre) -> {
            if (pre == null) {
                if (dataXName == null) {
                    throw new IllegalStateException("prop dataXName can not be null");
                }
                DataxProcessor dataxProcessor = DataxProcessor.load(null, dataXName);
                pre = DataXCfgGenerator.GenerateCfgs.readFromGen(dataxProcessor.getDataxCfgDir(null));
                logger.info("create GenerateCfgs with genTime:" + pre.getGenTime());
                return pre;
            }
            return pre;
        });
        return new HudiDumpPostTask(execContext, (HudiSelectedTab) tab, this, genCfg);
    }

    @Override
    public IRemoteTaskTrigger createPreExecuteTask(IExecChainContext execContext, ISelectedTab tab) {


        return new IRemoteTaskTrigger() {
            @Override
            public String getTaskName() {
                return "prep_" + tab.getName();
            }

            @Override
            public RunningStatus getRunningStatus() {
                throw new UnsupportedOperationException();
            }

            @Override
            public void run() {
                // 创建hud schema
                FileSystemFactory fsFactory = getFs();
                IPath dumpDir = HudiTableMeta.getDumpDir(fsFactory.getFileSystem(), tab.getName(), execContext.getPartitionTimestamp(), getHiveConnMeta());
                HudiTableMeta.createFsSourceSchema(fsFactory.getFileSystem(), tab.getName(), dumpDir, (HudiSelectedTab) tab);
            }
        };
    }

    public class HudiDataXContext extends FSDataXContext {

        private final HudiSelectedTab hudiTab;

        public HudiDataXContext(IDataxProcessor.TableMap tabMap, String dataxName) {
            super(tabMap, dataxName);
            ISelectedTab tab = tabMap.getSourceTab();
            if (!(tab instanceof HudiSelectedTab)) {
                throw new IllegalStateException(" param tabMap.getSourceTab() must be type of "
                        + HudiSelectedTab.class.getSimpleName() + " but now is :" + tab.getClass());
            }
            this.hudiTab = (HudiSelectedTab) tab;
        }

        public String getRecordField() {
            return this.hudiTab.recordField;
        }

//        public String getPartitionPathField() {
////            return this.hudiTab.partition.;
//            return "xxx";
//        }

        public String getSourceOrderingField() {
            return this.hudiTab.sourceOrderingField;
        }

        public Integer getShuffleParallelism() {
            return shuffleParallelism;
        }

        public BatchOpMode getOpMode() {
            return BatchOpMode.parse(batchOp);
        }

        public HudiWriteTabType getTabType() {
            return getHudiTableType();
        }
    }
}
