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

import com.alibaba.citrus.turbine.Context;
import com.qlangtech.tis.runtime.module.misc.IFieldErrorHandler;

/**
 * @author: 百岁（baisui@qlangtech.com）
 * @create: 2021-05-15 16:19
 **/
public class MockFieldErrorHandler implements IFieldErrorHandler {
    @Override
    public void addFieldError(Context context, String fieldName, String msg, Object... params) {
    }

    @Override
    public boolean validateBizLogic(BizLogic logicType, Context context, String fieldName, String value) {
        return false;
    }
}
