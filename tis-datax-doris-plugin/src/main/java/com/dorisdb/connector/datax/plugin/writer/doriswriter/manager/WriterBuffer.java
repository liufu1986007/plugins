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

package com.dorisdb.connector.datax.plugin.writer.doriswriter.manager;

import com.qlangtech.tis.manage.common.TisUTF8;

import java.util.ArrayList;
import java.util.List;

/**
 * @author: 百岁（baisui@qlangtech.com）
 * @create: 2021-09-09 17:18
 **/
public class WriterBuffer {
    public final List<byte[]> buffer = new ArrayList<>();
    public int size;
    public int rowCount;

    public synchronized void addRow(String row) {
        byte[] content = row.getBytes(TisUTF8.get());
        this.size += content.length;
        this.rowCount += rowCount;
        buffer.add(content);
    }

}
