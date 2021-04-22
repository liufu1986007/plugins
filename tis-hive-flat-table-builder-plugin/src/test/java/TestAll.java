/**
 * Copyright (c) 2020 QingLang, Inc. <baisui@qlangtech.com>
 * <p>
 *   This program is free software: you can use, redistribute, and/or modify
 *   it under the terms of the GNU Affero General Public License, version 3
 *   or later ("AGPL"), as published by the Free Software Foundation.
 * <p>
 *  This program is distributed in the hope that it will be useful, but WITHOUT
 *  ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 *   FITNESS FOR A PARTICULAR PURPOSE.
 * <p>
 *  You should have received a copy of the GNU Affero General Public License
 *  along with this program. If not, see <http://www.gnu.org/licenses/>.
 */

import com.qlangtech.tis.dump.hive.TestHiveDBUtils;
import com.qlangtech.tis.fullbuild.indexbuild.impl.TestYarnTableDumpFactory;
import com.qlangtech.tis.hive.TestHiveInsertFromSelectParser;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

/**
 * @author: baisui 百岁
 * @create: 2020-06-03 14:09
 **/
public class TestAll extends TestCase {

    public static Test suite() {
        TestSuite suite = new TestSuite();
        suite.addTestSuite(TestHiveInsertFromSelectParser.class);
        suite.addTestSuite(TestHiveDBUtils.class);
        suite.addTestSuite(TestYarnTableDumpFactory.class);
        return suite;
    }
}
