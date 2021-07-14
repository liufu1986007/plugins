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

import com.qlangtech.tis.TIS;
import junit.framework.TestCase;

import java.io.InputStream;

/**
 * @author: 百岁（baisui@qlangtech.com）
 * @create: 2021-07-14 19:21
 **/
public class TestResourceLoad extends TestCase {

    public void testLoad() {
        // hadoop-common 包中的一个资源文件
        String respath = "common-version-info.properties";
        InputStream res = TIS.get().pluginManager.uberClassLoader.getResourceAsStream(respath);
        assertNotNull(respath, res);
    }
}
