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

package com.qlangtech.tis.realtime;

import com.google.common.collect.Maps;
import com.qlangtech.tis.realtime.transfer.DTO;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;

import java.util.Map;

/**
 * @author: 百岁（baisui@qlangtech.com）
 * @create: 2022-02-18 12:19
 **/
public abstract class DefaultFlinkSourceHandle extends BasicFlinkSourceHandle {

    /**
     * 处理各个表对应的数据流
     *
     * @param
     */
    protected void processTableStream(StreamExecutionEnvironment env
            , Map<String, DTOStream> tab2OutputTag, SinkFuncs sinkFunction) {
        Map<String, DataStream<DTO>> streamMap = Maps.newHashMap();
        for (Map.Entry<String, DTOStream> e : tab2OutputTag.entrySet()) {
            streamMap.put(e.getKey(), e.getValue().getStream());
        }
        this.processTableStream(streamMap, sinkFunction);
    }

    /**
     * 处理各个表对应的数据流
     *
     * @param
     */
    protected abstract void processTableStream(Map<String, DataStream<DTO>> streamMap, SinkFuncs sinkFunction);
}
