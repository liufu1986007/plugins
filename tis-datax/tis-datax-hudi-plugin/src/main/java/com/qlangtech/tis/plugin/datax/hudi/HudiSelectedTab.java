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

package com.qlangtech.tis.plugin.datax.hudi;

import com.alibaba.citrus.turbine.Context;
import com.qlangtech.tis.extension.TISExtension;
import com.qlangtech.tis.manage.common.Option;
import com.qlangtech.tis.plugin.annotation.FormField;
import com.qlangtech.tis.plugin.annotation.FormFieldType;
import com.qlangtech.tis.plugin.annotation.Validator;
import com.qlangtech.tis.plugin.datax.SelectedTab;
import com.qlangtech.tis.plugin.datax.hudi.partition.HudiTablePartition;
import com.qlangtech.tis.runtime.module.misc.IControlMsgHandler;

import java.util.List;

/**
 * @author: 百岁（baisui@qlangtech.com）
 * @create: 2022-01-28 15:58
 **/
public class HudiSelectedTab extends SelectedTab {

    public static final String KEY_RECORD_FIELD = "recordField";
    public static final String KEY_PARTITION_PATH_FIELD = "partition";
    public static final String KEY_SOURCE_ORDERING_FIELD = "sourceOrderingField";


    @FormField(ordinal = 1, type = FormFieldType.ENUM, validate = {Validator.require})
    public String recordField;

    @FormField(ordinal = 2, validate = {Validator.require})
    public HudiTablePartition partition;

    @FormField(ordinal = 3, type = FormFieldType.ENUM, validate = {Validator.require})
    public String sourceOrderingField;


    /**
     * 主键候选字段
     *
     * @return
     */
    public static List<Option> getPrimaryKeys() {
        return getContextTableCols((cols) -> cols.stream().filter((col) -> col.isPk()));
    }

    /**
     * 分区键候选字段
     *
     * @return
     */
    public static List<Option> getPartitionKeys() {
        return getContextTableCols((cols) -> cols.stream()
                .filter((col) -> {
                    switch (col.getType().getCollapse()) {
                        // case STRING:
                        case INT:
                        case Long:
                        case Date:
                            return true;
                    }
                    return false;
                }));
    }


    @TISExtension
    public static class DefaultDescriptor extends SelectedTab.DefaultDescriptor {

        @Override
        protected boolean validateAll(IControlMsgHandler msgHandler, Context context, SelectedTab postFormVals) {

            HudiSelectedTab tab = (HudiSelectedTab) postFormVals;
            boolean success = true;
            if (!tab.containCol(tab.sourceOrderingField)) {
                msgHandler.addFieldError(context, KEY_SOURCE_ORDERING_FIELD, "'"
                        + tab.sourceOrderingField + "'需要在" + SelectedTab.KEY_FIELD_COLS + "中被选中");
                success = false;
            }

            if (!tab.containCol(tab.recordField)) {
                msgHandler.addFieldError(context, KEY_RECORD_FIELD
                        , "'" + tab.recordField + "'需要在" + SelectedTab.KEY_FIELD_COLS + "中被选中");
                success = false;
            }

            return success;
        }


    }

}
