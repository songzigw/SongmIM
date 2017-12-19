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
package cn.songm.im.codec.model;

import java.io.Serializable;

import cn.songm.im.codec.model.Message.Direction;
import cn.songm.songmq.core.util.GsonEnum;

/**
 * 会话
 *
 * @author zhangsong
 * @since 0.1, 2016-7-29
 * @version 0.1
 * 
 */
public class Conversation implements Serializable {

    private static final long serialVersionUID = -6267677672827188664L;

    private String id;

    private Ctype type;
    private Direction direction;
    private String subjectum;
    private String subNick;
    private String subAvatar;
    private String objectum;
    private String objNick;
    private String objAvatar;
    private Long timestamp;
    private Integer unreadCount;
    private String body;

    public Conversation() {
        this.unreadCount = 0;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Ctype getType() {
        return type;
    }

    public void setType(Ctype type) {
        this.type = type;
    }

    public Direction getDirection() {
        return direction;
    }

    public void setDirection(Direction direction) {
        this.direction = direction;
    }

    public String getSubjectum() {
        return subjectum;
    }

    public void setSubjectum(String subjectum) {
        this.subjectum = subjectum;
    }

    public String getSubNick() {
        return subNick;
    }

    public void setSubNick(String subNick) {
        this.subNick = subNick;
    }

    public String getSubAvatar() {
        return subAvatar;
    }

    public void setSubAvatar(String subAvatar) {
        this.subAvatar = subAvatar;
    }

    public String getObjectum() {
        return objectum;
    }

    public void setObjectum(String objectum) {
        this.objectum = objectum;
    }

    public String getObjNick() {
        return objNick;
    }

    public void setObjNick(String objNick) {
        this.objNick = objNick;
    }

    public String getObjAvatar() {
        return objAvatar;
    }

    public void setObjAvatar(String objAvatar) {
        this.objAvatar = objAvatar;
    }

    public Long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Long timestamp) {
        this.timestamp = timestamp;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public Integer getUnreadCount() {
        return unreadCount;
    }

    public void setUnreadCount(Integer unreadCount) {
        this.unreadCount = unreadCount;
    }
    
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((id == null) ? 0 : id.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null) return false;
        if (getClass() != obj.getClass()) return false;
        Conversation other = (Conversation) obj;
        if (id == null) {
            if (other.id != null) return false;
        } else if (!id.equals(other.id)) return false;
        return true;
    }

    @Override
    public String toString() {
        StringBuilder str = new StringBuilder();
        str.append("Conversation [");
        str.append("id=").append(this.id).append(", ");
        str.append("type=").append(this.type).append(", ");
        str.append("direction=").append(this.direction).append(", ");
        str.append("subjectum=").append(this.subjectum).append(", ");
        str.append("subNick=").append(this.subNick).append(", ");
        str.append("subAvatar=").append(this.subAvatar).append(", ");
        str.append("objectum=").append(this.objectum).append(", ");
        str.append("objNick=").append(this.objNick).append(", ");
        str.append("objAvatar=").append(this.objAvatar).append(", ");
        str.append("timestamp=").append(this.timestamp).append(", ");
        str.append("unreadCount=").append(this.unreadCount).append(", ");
        str.append("body=").append(this.body);
        str.append("]");
        return str.toString();
    }
    
    /**
     * 聊天类型
     * @author zhangsong
     *
     */
    public static enum Ctype implements GsonEnum<Ctype> {
        /** 私聊 */
        PRIVATE("private"),
        /** 群聊 */
        GROUP("group"),;

        private String value;

        private Ctype(String v) {
            this.value = v;
        }

        public String getValue() {
            return this.value;
        }

        public static Ctype instance(String v) {
            for (Ctype t : Ctype.values()) {
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
        public Ctype deserialize(String jsonEnum) {
            return instance(jsonEnum);
        }
    }

}
