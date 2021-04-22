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
package com.qlangtech.async.message.client.sender;

import com.qlangtech.async.message.client.constants.NotifyDelayType;
import org.apache.rocketmq.client.producer.SendCallback;

import java.util.Map;

/*
 * 发送消息
 * Created by binggun on 2015/6/8
 *
 * @author 百岁（baisui@qlangtech.com）
 * @date 2020/04/13
 */
public interface SendManager {

    /**
     * 发消息，此时发送key取md5(msgTopic+msgTag+内容)，默认采用Hessian序列化
     *
     * @param tag        消息标签
     * @param msg        消息内容
     * @param hessianSer 是否使用hessian序列化，默认不传值 false，使用hessian序列化，true 不使用hessian序列化。直接msg.toString().getBytes()
     * @return 序列id，出错时返回null
     */
    public String sendMsg(String tag, Object msg, boolean... hessianSer);

    /**
     * 发消息，此时发送key取md5(msgTopic+msgTag+内容)，默认采用Hessian序列化
     *
     * @param tag        消息标签
     * @param msg        消息内容
     * @param hessianSer 是否使用hessian序列化，默认不传值 false，使用hessian序列化，true 不使用hessian序列化。直接msg.toString().getBytes()
     * @return 序列id，出错时返回null
     * @see NotifyDelayType
     */
    public String sendMsg(String tag, Object msg, NotifyDelayType notifyDelayType, boolean... hessianSer);

    /**
     * 发消息，默认采用Hessian序列化
     *
     * @param tag        消息标签
     * @param msg        消息内容
     * @param key        业务关键属性，用于查询&重发使用
     * @param hessianSer 是否使用hessian序列化，默认不传值 false，使用hessian序列化，true 不使用hessian序列化。直接msg.toString().getBytes()
     * @return 序列id，出错时返回null
     */
    public String sendMsg(String tag, Object msg, String key, boolean... hessianSer);

    /**
     * 发消息，默认采用Hessian序列化
     *
     * @param tag            消息标签
     * @param msg            消息内容
     * @param key            业务关键属性，用于查询&重发使用
     * @param userProperties 用户属性
     * @param hessianSer     是否使用hessian序列化，默认不传值 false，使用hessian序列化，true 不使用hessian序列化。直接msg.toString().getBytes()
     * @return 序列id，出错时返回null
     */
    public String sendMsg(String tag, Object msg, String key, Map<String, String> userProperties, boolean... hessianSer);

    /**
     * 发消息，默认采用Hessian序列化
     *
     * @param tag            消息标签
     * @param msg            消息内容
     * @param key            业务关键属性，用于查询&重发使用
     * @param userProperties 用户属性
     * @param hessianSer     是否使用hessian序列化，默认不传值 false，使用hessian序列化，true 不使用hessian序列化。直接msg.toString().getBytes()
     * @return 序列id，出错时返回null
     * @see NotifyDelayType
     */
    public String sendMsg(String tag, Object msg, String key, Map<String, String> userProperties, NotifyDelayType notifyDelayType, boolean... hessianSer);

    /**
     * 发消息，默认采用Hessian序列化
     *
     * @param tag            消息标签
     * @param msg            消息内容
     * @param userProperties 用户属性
     * @param hessianSer     是否使用hessian序列化，默认不传值 false，使用hessian序列化，true 不使用hessian序列化。直接msg.toString().getBytes()
     * @return 序列id，出错时返回null
     */
    public String sendMsg(String tag, Object msg, Map<String, String> userProperties, boolean... hessianSer);

    /**
     * 发送P2P  MQTT消息
     *
     * @param msg  消息内容
     * @param tags 接收消息对象列表
     * @return 序列id，出错时返回null
     */
    public void sendP2pMqttMsg(String msg, String... tags);

    /**
     * 异步发送消息，发送结果callback通知。
     *
     * @param msg          消息内容
     * @param tag          消息标签
     * @param sendCallback 异步发送消息回调接口
     */
    public void sendMsgAsync(String tag, Object msg, SendCallback sendCallback);

    /**
     * OneWay的方式发消息，服务器无应答。
     *
     * @param msg 消息内容
     * @param tag 消息标签
     */
    public void sendMsgOneWay(String tag, Object msg);
}
