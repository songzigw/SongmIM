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
package songm.im.entity;

import java.io.Serializable;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * 用户与服务端的会话
 *
 * @author zhangsong
 * @since 0.1, 2016-7-29
 * @version 0.1
 * 
 */
public class Session extends Entity implements Serializable {

    private static final long serialVersionUID = 1689305158269907021L;

    /** 默认超时间 */
    public static final long TIME_OUT = 1000 * 24 * 60 * 60;

    /** 用户与服务端会话唯一标示符 */
    public static final String CLIENT_KEY = "songm_im_key";

    /** 会话唯一标示 */
    private String id;

    /** 会话创建时间 */
    private Date createdTime;

    /** 会话访问时间 */
    private Date accessTime;

    private Map<String, Object> attribute;

    private String tokenId;

    public Session() {
        createdTime = new Date();
        accessTime = createdTime;
    }

    public Session(String sessionId, String tokenId) {
        this();
        this.id = sessionId;
        this.tokenId = tokenId;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
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

    public void setCreatedTime(Date createdTime) {
        this.createdTime = createdTime;
    }

    public void setAccessTime(Date accessTime) {
        this.accessTime = accessTime;
    }

    public long getCreatedTime() {
        return createdTime.getTime();
    }

    public long getAccessTime() {
        return accessTime.getTime();
    }

    public void updateAccessTime() {
        accessTime = new Date();
    }

    public String getTokenId() {
        return tokenId;
    }

    public void setTokenId(String tokenId) {
        this.tokenId = tokenId;
    }

    public boolean isTimeout() {
        if (new Date().getTime() - accessTime.getTime() > TIME_OUT) {
            return true;
        }
        return false;
    }
}
