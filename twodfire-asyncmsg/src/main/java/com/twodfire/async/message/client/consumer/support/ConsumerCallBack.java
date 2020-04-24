/* * Copyright 2020 QingLang, Inc.
 *
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.twodfire.async.message.client.consumer.support;

import com.qlangtech.tis.async.message.client.consumer.AsyncMsg;

/*
 * 消费类接口,实现该类处理具体的业务类型
 * <p>例子: DoBusiness类只处理messageTagA和messageTagB的消息</p>
 * <blockquote><pre>
 * @MessageTag(tag = {"messageTagA","messageTagB"})
 * public class DoBusiness implements ConsumerCallBack {
 *      @Override
 *      public boolean process(AsyncMsgTO msgTO) {
 *          //do business
 *          return false;
 *      }
 * }
 * </pre></blockquote>
 *
 * @author 百岁（baisui@qlangtech.com）
 * @date 2015-12-09
 */
public interface ConsumerCallBack {

    /**
     * 业务具体处理方法
     *
     * @return
     */
    boolean process(AsyncMsg msg);
}
