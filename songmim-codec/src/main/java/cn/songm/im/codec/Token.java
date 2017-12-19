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
package cn.songm.im.codec;

import java.io.Serializable;

import cn.songm.songmq.core.util.Sequence;
import cn.songm.songmq.core.util.StringUtils;

/**
 * 用户在IM服务器上的凭证，一个用户只能有一个Token
 *
 * @author zhangsong
 * @since 0.1, 2016-7-29
 * @version 0.1
 * 
 */
public class Token implements Serializable {

    private static final long serialVersionUID = 1356174819334484641L;

    private String tokenId;
    private String uid;
    private String nick;
    private String avatar;
    private String appKey;

    private Token(String uid, String nick, String avatar, String appKey, String tokenId) {
        if (StringUtils.isEmpty(uid) || StringUtils.isEmpty(nick)) {
            throw new IllegalArgumentException("uid | nick");
        }
        this.uid = uid;
        this.nick = nick;
        this.avatar = avatar;
        this.appKey = appKey;
        if (tokenId != null) {
            this.tokenId = tokenId;
        } else {
            this.tokenId = Sequence.getInstance().getSequence(16);
        }
    }
    
    public Token(String uid, String nick, String avatar, String appKey) {
        this(uid, nick, avatar, appKey, null);
    }
    
    public String getTokenId() {
        return tokenId;
    }

    public void setTokenId(String tokenId) {
        this.tokenId = tokenId;
    }

    public String getAppKey() {
        return appKey;
    }

    public void setAppKey(String appKey) {
        this.appKey = appKey;
    }
    
    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getNick() {
        return nick;
    }

    public void setNick(String nick) {
        this.nick = nick;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((uid == null) ? 0 : uid.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null) return false;
        if (getClass() != obj.getClass()) return false;
        Token other = (Token) obj;
        if (uid == null) {
            if (other.uid != null) return false;
        } else if (!uid.equals(other.uid)) return false;
        return true;
    }

    @Override
    public String toString() {
        return String.format(
                "Token [tokenId=%s, uid=%s, nick=%s, avatar=%s, appKey=%s]",
                tokenId, uid, nick, avatar, appKey);
    }

}
