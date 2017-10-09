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
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import cn.songm.common.utils.StringUtils;

/**
 * 用户与服务端的会话
 *
 * @author zhangsong
 * @since 0.1, 2016-7-29
 * @version 0.1
 * 
 */
public class Session implements Serializable {

    private static final long serialVersionUID = 1689305158269907021L;

    /** 默认超时间 */
    public static final long TIME_OUT = 60 * 1000;

    /** 会话唯一标示 */
    private String sessionId;

    /** 会话TokenID */
    private String tokenId;

    /** 用户UID */
    private String uid;

    /** 会话创建时间 */
    private Date created;

    /** 会话访问时间 */
    private Date access;

    private Map<String, Object> attribute;

    private Token token;
    
    public Session() {
        created = new Date();
        access = created;
    }

    private Session(String uid, String tokenId, String sessionId) {
        this();
        this.uid = uid;
        this.tokenId = tokenId;
        if (sessionId != null) {
            this.sessionId = sessionId;
        } else {
            this.sessionId = StringUtils.get32UUID();
        }
    }
    
    public Session(String uid, String tokenId) {
        this(uid, tokenId, null);
    }

    public String getSessionId() {
        return sessionId;
    }

    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }

    public Object getAttribute(String name) {
        if (attribute == null) {
            return null;
        }
        return attribute.get(name);
    }

    public void setAttribute(String name, Object value) {
        if (attribute == null) {
            attribute = new HashMap<String, Object>();
        }
        attribute.put(name, value);
    }

    public Map<String, Object> getAttribute() {
        return this.attribute;
    }

    public void setAttribute(Map<String, Object> attribute) {
        this.attribute = attribute;
    }

    public void setCreated(Date created) {
        this.created = created;
    }

    public long getCreated() {
        return created.getTime();
    }

    public void setAccess(Date access) {
        this.access = access;
    }

    public long getAccess() {
        return access.getTime();
    }

    public void updateAccess() {
        access = new Date();
    }

    public String getTokenId() {
        return tokenId;
    }

    public void setTokenId(String tokenId) {
        this.tokenId = tokenId;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public Token getToken() {
        return token;
    }

    public void setToken(Token token) {
        this.token = token;
    }

    public boolean isTimeout() {
        if (System.currentTimeMillis() - access.getTime() > TIME_OUT) {
            return true;
        }
        return false;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result
                + ((sessionId == null) ? 0 : sessionId.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null) return false;
        if (getClass() != obj.getClass()) return false;
        Session other = (Session) obj;
        if (sessionId == null) {
            if (other.sessionId != null) return false;
        } else if (!sessionId.equals(other.sessionId)) return false;
        return true;
    }

    @Override
    public String toString() {
        StringBuilder str = new StringBuilder("Session [");
        str.append(", sessionId=").append(sessionId);
        str.append(", tokenId=").append(tokenId);
        str.append(", uid=").append(uid);
        str.append(", created=").append(created);
        str.append(", access=").append(access);
        str.append(", attribute=").append(attribute);
        str.append(", token=").append(token);
        str.append("]");
        return str.toString();
    }
}
