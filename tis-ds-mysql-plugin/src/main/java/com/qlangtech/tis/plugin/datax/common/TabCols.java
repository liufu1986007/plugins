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

package com.qlangtech.tis.plugin.datax.common;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @author: 百岁（baisui@qlangtech.com）
 * @create: 2021-06-05 11:53
 **/
public class TabCols {
    private final List<String> cols;

    public TabCols(List<String> cols) {
        this.cols = cols;
    }

    public String getColsQuotes() {
        return getColumnWithLink("\"`", "`\"");
    }

    public String getCols() {
        return getColumnWithLink("`", "`");
    }

    private String getColumnWithLink(String left, String right) {
        return this.cols.stream().map(r -> (left + r + right)).collect(Collectors.joining(","));
    }
}
