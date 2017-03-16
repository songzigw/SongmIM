/*
 * Copyright [2016] [zhangsong <songm.cn>].
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *     http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * 
 */
package cn.songm.im.model.message;

import java.util.Date;

import cn.songm.common.utils.JsonUtils;
import cn.songm.songmq.core.president.MQMessage;

/**
 * 消息实体，用来容纳和存储到客户端收到的消息， 以及服务端生成的消息经过这个消息实体包装，传递到客户端。
 * 
 * @author zhangsong
 * @since 0.1, 2016-8-23
 * @version 0.1
 *
 */
public class Message implements MQMessage {

    private static final long serialVersionUID = 3649240217021961002L;

    /** 消息ID */
    private Integer msgId;
    /** 会话类型 */
    private String conv;
    /** 消息类型 */
    private String type;
    /** 通道id */
    private String chId;
    /** 发送方 */
    private String from;
    /** 接收方 */
    private String to;
    /** 创建时间 */
    private Date created;
    /** 修改时间 */
    private Date updated;
    /** 具体消息内容 */
    private String jbody;

    public Date getCreated() {
        return created;
    }

    public void setCreated(Date created) {
        this.created = created;
    }

    public Date getUpdated() {
        return updated;
    }

    public void setUpdated(Date updated) {
        this.updated = updated;
    }

    public String getChId() {
        return chId;
    }

    public void setChId(String chId) {
        this.chId = chId;
    }

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public String getTo() {
        return to;
    }

    public void setTo(String to) {
        this.to = to;
    }

    public String getJbody() {
        return jbody;
    }

    public void setJbody(String jbody) {
        this.jbody = jbody;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getConv() {
        return conv;
    }

    public void setConv(String conv) {
        this.conv = conv;
    }

    public Integer getMsgId() {
        return msgId;
    }

    public void setMsgId(Integer msgId) {
        this.msgId = msgId;
    }

    @Override
    public String toString() {
        return "Message [conv=" + conv + ", type=" + type + ", chId=" + chId
                + ", from=" + from + ", to=" + to + ", created=" + created
                + ", updated=" + updated + ", jbody=" + jbody + "]";
    }

    public static enum Mtype {
        /** 文本消息 */
        TEXT("text"),
        /** 图片消息 */
        IMAGE("image"),

        /** 未读数消息 */
        UNREAD("unread"),

        /** 正在输入状态 */
        WRITING("writing");

        private String value;

        private Mtype(String v) {
            this.value = v;
        }

        public String getValue() {
            return this.value;
        }

        public static Mtype instance(String v) {
            for (Mtype t : Mtype.values()) {
                if (t.getValue().equals(v)) {
                    return t;
                }
            }
            throw new RuntimeException();
        }
    }

    public MessageContent factory() {
        Mtype msgType = Mtype.instance(type);
        MessageContent content = null;
        switch (msgType) {
        case TEXT:
            content = JsonUtils.fromJson(jbody, TextMessage.class);
            break;
        case IMAGE:
            content = JsonUtils.fromJson(jbody, ImageMessage.class);
            break;
        case UNREAD:
            content = JsonUtils.fromJson(jbody, UnreadMessage.class);
            break;
        default:
            throw new RuntimeException("msgType out");
        }
        return content;
    }

}
