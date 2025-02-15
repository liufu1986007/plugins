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

package com.qlangtech.tis.compiler.streamcode;

import com.alibaba.citrus.turbine.Context;
import com.qlangtech.tis.compiler.java.FileObjectsContext;
import com.qlangtech.tis.extension.PluginWrapper;
import com.qlangtech.tis.extension.impl.IOUtils;
import com.qlangtech.tis.manage.common.Config;
import com.qlangtech.tis.manage.common.TisUTF8;
import com.qlangtech.tis.manage.common.incr.StreamContextConstant;
import com.qlangtech.tis.runtime.module.misc.IControlMsgHandler;
import com.qlangtech.tis.test.TISEasyMock;
import org.apache.commons.io.FileUtils;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import scala.tools.ScalaCompilerSupport;

import java.io.File;
import java.util.Collections;

/**
 * @author: 百岁（baisui@qlangtech.com）
 * @create: 2022-03-29 10:43
 **/
public class TestCompileAndPackage implements TISEasyMock {

    @Rule
    public TemporaryFolder folder = TemporaryFolder.builder().build();

    @Test
    public void testCompile() throws Exception {
        CompileAndPackage cap = new CompileAndPackage(Collections.singletonList(
                new PluginWrapper.Dependency("tis-sink-hudi-plugin", Config.getMetaProps().getVersion(), false)));

        File rootDir = folder.newFolder(ScalaCompilerSupport.KEY_SCALA_SOURCE_ROOT_DIR);
        String hudiSource = "HudiSourceHandle.scala";
        FileUtils.writeStringToFile(new File(rootDir, "tis/" + hudiSource)
                , IOUtils.loadResourceFromClasspath(TestCompileAndPackage.class, hudiSource + ".tpl")
                , TisUTF8.get(), false);

//        Context context, IControlMsgHandler msgHandler
//                , String appName, Map< IDBNodeMeta, List<String>> dbNameMap, File sourceRoot, FileObjectsContext xmlConfigs
        File sourceRoot = new File(rootDir, "../../..");
        FileObjectsContext objsContext = new FileObjectsContext();
        Context context = this.mock("context", Context.class);
        IControlMsgHandler msgHander = this.mock("msgHander", IControlMsgHandler.class);

        this.replay();
        cap.process(context, msgHander, "test", Collections.emptyMap(), sourceRoot, objsContext);

        File genJar = new File(sourceRoot, StreamContextConstant.getIncrStreamJarName("test"));
        Assert.assertTrue(genJar.exists());

        this.verifyAll();
    }
}
