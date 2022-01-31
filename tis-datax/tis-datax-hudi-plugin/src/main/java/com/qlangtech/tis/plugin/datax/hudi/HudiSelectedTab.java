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

import com.qlangtech.tis.plugin.annotation.FormField;
import com.qlangtech.tis.plugin.annotation.FormFieldType;
import com.qlangtech.tis.plugin.annotation.Validator;
import com.qlangtech.tis.plugin.datax.SelectedTab;

/**
 * @author: 百岁（baisui@qlangtech.com）
 * @create: 2022-01-28 15:58
 **/
public class HudiSelectedTab extends SelectedTab {

    public static final String KEY_RECORD_FIELD = "recordField";

    @FormField(ordinal = 1, type = FormFieldType.ENUM, validate = {Validator.require})
    public String recordField;

    @FormField(ordinal = 2, type = FormFieldType.ENUM, validate = {Validator.require})
    public String partitionPathField;

    @FormField(ordinal = 3, type = FormFieldType.ENUM, validate = {Validator.require})
    public String sourceOrderingField;


}
