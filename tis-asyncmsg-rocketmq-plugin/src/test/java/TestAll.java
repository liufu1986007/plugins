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
import com.qlangtech.async.message.client.consumer.TestRocketMQListenerFactory;
import com.qlangtech.tis.component.TestIncrComponent;
import com.qlangtech.tis.component.TestPlugin;
import com.qlangtech.tis.component.TestRockMqPluginValidate;
import com.qlangtech.tis.util.TestHeteroList;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

/*
 * @create: 2020-01-15 14:20
 *
 * @author 百岁（baisui@qlangtech.com）
 * @date 2020/04/13
 */
public class TestAll extends TestCase {

    public static Test suite() {
        TestSuite suite = new TestSuite();
        suite.addTestSuite(TestPlugin.class);
        suite.addTestSuite(TestIncrComponent.class);
        suite.addTestSuite(TestRockMqPluginValidate.class);
        suite.addTestSuite(TestHeteroList.class);
        suite.addTestSuite(TestRocketMQListenerFactory.class);
        return suite;
    }
}
