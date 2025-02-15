/**
 *   Licensed to the Apache Software Foundation (ASF) under one
 *   or more contributor license agreements.  See the NOTICE file
 *   distributed with this work for additional information
 *   regarding copyright ownership.  The ASF licenses this file
 *   to you under the Apache License, Version 2.0 (the
 *   "License"); you may not use this file except in compliance
 *   with the License.  You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 *   Unless required by applicable law or agreed to in writing, software
 *   distributed under the License is distributed on an "AS IS" BASIS,
 *   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *   See the License for the specific language governing permissions and
 *   limitations under the License.
 */

package com.qlangtech.tis.plugin.datax;

import com.qlangtech.tis.datax.IDataxContext;
import com.qlangtech.tis.datax.IDataxProcessor;
import com.qlangtech.tis.plugin.ds.ISelectedTab;
import org.apache.commons.lang.StringUtils;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @author: 百岁（baisui@qlangtech.com）
 * @create: 2021-07-01 16:52
 **/
public class DataXFtpWriterContext implements IDataxContext {
    private final DataXFtpWriter writer;
    private final IDataxProcessor.TableMap tableMapper;

    public DataXFtpWriterContext(DataXFtpWriter writer, IDataxProcessor.TableMap tableMapper) {
        this.writer = writer;
        this.tableMapper = tableMapper;
    }

    public String getProtocol() {
        return this.writer.protocol;
    }

    public String getHost() {
        return this.writer.host;
    }

    public boolean isContainPort() {
        return this.writer.port != null;
    }

    public Integer getPort() {
        return this.writer.port;
    }

    public boolean isContainTimeout() {
        return this.writer.timeout != null;
    }

    public Integer getTimeout() {
        return this.writer.timeout;
    }

    public String getUsername() {
        return this.writer.username;
    }

    public String getPassword() {
        return this.writer.password;
    }

    public String getPath() {
        return this.writer.path;
    }

    public String getFileName() {
        return this.tableMapper.getTo();
    }

    public String getWriteMode() {
        return this.writer.writeMode;
    }

    public boolean isContainFieldDelimiter() {
        return StringUtils.isNotBlank(this.writer.fieldDelimiter);
    }

    public String getFieldDelimiter() {
        return this.writer.fieldDelimiter;
    }

    public boolean isContainEncoding() {
        return StringUtils.isNotBlank(this.writer.encoding);
    }

    public String getEncoding() {
        return this.writer.encoding;
    }

    public boolean isContainNullFormat() {
        return StringUtils.isNotBlank(this.writer.nullFormat);
    }

    public String getNullFormat() {
        return this.writer.nullFormat;
    }

    public boolean isContainDateFormat() {
        return StringUtils.isNotBlank(this.writer.dateFormat);
    }

    public String getDateFormat() {
        return this.writer.dateFormat;
    }

    public boolean isContainFileFormat() {
        return StringUtils.isNotBlank(this.writer.fileFormat);
    }

    public String getFileFormat() {
        return this.writer.fileFormat;
    }

    public boolean isContainSuffix() {
        return StringUtils.isNotBlank(this.writer.suffix);
    }

    public String getSuffix() {
        return this.writer.suffix;
    }

    public boolean isContainHeader() {
        List<ISelectedTab.ColMeta> cols = tableMapper.getSourceCols();
        return (this.writer.header != null && this.writer.header && cols.size() > 0);
    }

    public String getHeader() {
        return tableMapper.getSourceCols().stream().map((c) -> "'" + c.getName() + "'").collect(Collectors.joining(","));
    }

}
