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

import com.qlangtech.tis.datax.DataXJobSubmit;
import com.qlangtech.tis.datax.DataxExecutor;
import com.qlangtech.tis.datax.IDataxProcessor;
import com.qlangtech.tis.extension.TISExtension;
import com.qlangtech.tis.fullbuild.indexbuild.IRemoteJobTrigger;
import com.qlangtech.tis.manage.common.Config;
import com.qlangtech.tis.manage.common.TisSubModule;
import com.qlangtech.tis.order.center.IJoinTaskContext;
import com.tis.hadoop.rpc.RpcServiceReference;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.Objects;

/**
 * @author: 百岁（baisui@qlangtech.com）
 * @create: 2021-04-27 17:28
 **/
@TISExtension()
public class LocalDataXJobSubmit extends DataXJobSubmit {

    private String mainClassName = DataxExecutor.class.getName();
    private File workingDirectory = new File(".");
    private String classpath;

    private final static Logger logger = LoggerFactory.getLogger(LocalDataXJobSubmit.class);

    @Override
    public InstanceType getType() {
        return InstanceType.LOCAL;
    }

    @Override
    public IRemoteJobTrigger createDataXJob(IJoinTaskContext taskContext, RpcServiceReference statusRpc
            , IDataxProcessor dataxProcessor, String dataXfileName) {
        if (StringUtils.isEmpty(this.classpath)) {
            File assebleDir = new File(Config.getTisHome(), TisSubModule.TIS_ASSEMBLE.moduleName);
            if (!assebleDir.exists()) {
                throw new IllegalStateException("target asseble dir is not exist:" + assebleDir.getPath());
            }
            this.classpath = assebleDir.getPath() + "/lib/*:" + assebleDir.getPath() + "/conf";
        }
        logger.info("dataX Job:{},classpath:{},workingDir:{}", dataXfileName, this.classpath, workingDirectory.getPath());

        Objects.requireNonNull(statusRpc, "statusRpc can not be null");
        return TaskExec.getRemoteJobTrigger(taskContext, this, dataXfileName);
    }


    public void setMainClassName(String mainClassName) {
        this.mainClassName = mainClassName;
    }


    public void setWorkingDirectory(File workingDirectory) {
        this.workingDirectory = workingDirectory;
    }

    public String getMainClassName() {
        return mainClassName;
    }

    public File getWorkingDirectory() {
        return workingDirectory;
    }

    public String getClasspath() {
        if (StringUtils.isEmpty(this.classpath)) {
            throw new IllegalStateException("param classpath can not be null");
        }
        return classpath;
    }

    public void setClasspath(String classpath) {
        this.classpath = classpath;
    }
}
