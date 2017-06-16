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

import cn.songm.common.utils.GsonEnum;
import cn.songm.im.model.message.Conversation.Ctype;
import cn.songm.songmq.core.president.MQMessage;

/**
 * 消息实体，用来容纳和存储到客户端收到的消息， 以及服务端生成的消息经过这个消息实体包装，传递到客户端。
 * 
 * @author zhangsong
 * @since 0.1, 2016-8-23
 * @version 0.1
 *
 */
public class Message implements MQMessage, Cloneable {

    private static final long serialVersionUID = 3649240217021961002L;

    /** 消息ID */
    private Long msgId;
    /** 会话类型 */
    private Ctype conv;
    /** 消息类型 */
    private Mtype type;
    /** 通道id */
    private String chId;
    /** 会话方向 */
    private Direction direction;
    /** 发送方 */
    private String from;
    private String fNick;
    private String fAvatar;
    /** 接收方 */
    private String to;
    private String tNick;
    private String tAvatar;
    /** 创建时间 */
    private Date created;
    /** 修改时间 */
    private Date updated;
    /** 具体消息内容 */
    private MessageContent jbody;

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

    public MessageContent getJbody() {
        return jbody;
    }

    public void setJbody(MessageContent jbody) {
        this.jbody = jbody;
    }

    public Mtype getType() {
        return type;
    }

    public void setType(Mtype type) {
        this.type = type;
    }

    public Ctype getConv() {
        return conv;
    }

    public void setConv(Ctype conv) {
        this.conv = conv;
    }

    public Long getMsgId() {
        return msgId;
    }

    public void setMsgId(Long msgId) {
        this.msgId = msgId;
    }

    public Direction getDirection() {
        return direction;
    }

    public void setDirection(Direction direction) {
        this.direction = direction;
    }

    public String getfNick() {
        return fNick;
    }

    public void setfNick(String fNick) {
        this.fNick = fNick;
    }

    public String getfAvatar() {
        return fAvatar;
    }

    public void setfAvatar(String fAvatar) {
        this.fAvatar = fAvatar;
    }

    public String gettNick() {
        return tNick;
    }

    public void settNick(String tNick) {
        this.tNick = tNick;
    }

    public String gettAvatar() {
        return tAvatar;
    }

    public void settAvatar(String tAvatar) {
        this.tAvatar = tAvatar;
    }

    @Override
    public Message clone() throws CloneNotSupportedException {
        return (Message) super.clone();
    }

    @Override
    public String toString() {
        return "Message [msgId=" + msgId + ", conv=" + conv + ", type=" + type
                + ", chId=" + chId + ", direction=" + direction + ", from="
                + from + ", fNick=" + fNick + ", fAvatar=" + fAvatar + ", to="
                + to + ", tNick=" + tNick + ", tAvatar=" + tAvatar
                + ", created=" + created + ", updated=" + updated + ", jbody="
                + jbody + "]";
    }

    public static enum Direction implements GsonEnum<Direction> {
        SEND("send"), RECEIVE("receive");

        private String value;

        private Direction(String v) {
            this.value = v;
        }

        public String getValue() {
            return this.value;
        }

        public static Direction instance(String v) {
            for (Direction t : Direction.values()) {
                if (t.getValue().equals(v)) {
                    return t;
                }
            }
            throw new RuntimeException();
        }

        @Override
        public String serialize() {
            return this.getValue();
        }

        @Override
        public Direction deserialize(String jsonEnum) {
            return instance(jsonEnum);
        }
    }

    public static enum Mtype implements GsonEnum<Mtype> {
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

        @Override
        public String serialize() {
            return this.getValue();
        }

        @Override
        public Mtype deserialize(String jsonEnum) {
            return instance(jsonEnum);
        }
    }
}
