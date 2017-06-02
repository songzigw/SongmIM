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
package cn.songm.im.model;

import java.io.Serializable;

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

    private String type;
    private String subjectum;
    private String subNick;
    private String subAvatar;
    private String objectum;
    private String objNick;
    private String objAvatar;
    private Long timestamp;
    private String body;
    private Integer unreadCount;

    public Conversation() {
        this.unreadCount = 0;
    }
    
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
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

    public static enum Ctype {
        /** 私聊 */
        PRIVATE("private"),
        /** 群聊 */
        GROUP("group"),
        ;

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
    }

}
