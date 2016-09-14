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
package songm.im.service;

import io.netty.channel.Channel;
import songm.im.IMException;
import songm.im.entity.Session;
import songm.im.entity.Token;

/**
 * 第三方后台访问控制，用户访问授权等操作。
 * 
 * @author zhangsong
 * @since 0.1, 2016-8-23
 * @version 0.1
 *
 */
public interface AuthService {

    /** 客户端的时间和服务端的时间误差范围 */
    public static final long MISTIMING = 5 * 1000;

    /**
     * 第三方应用后台访问授权
     * 
     * @param key
     * @param nonce
     * @param signature
     * @param timestamp
     * @return
     */
    public boolean auth(String key, String nonce, String signature,
            long timestamp);

    /**
     * 第三方应用后台服务发起请求，给用户分配一个通信令牌
     * 
     * @param uid
     * @param nick
     * @param avatar
     * @return
     */
    public Token createToken(String uid, String nick, String avatar);

    /**
     * 删除Token
     * @param tokenId
     * @return
     */
    public Token deleteToken(String tokenId);
    
    /**
     * 获取Token
     * 
     * @param tokenId
     * @return
     */
    public Token getTokenByTokenId(String tokenId);

    /**
     * 用户凭借通信令牌上线，并获取当前用户的会话信息
     * 
     * @param tokenId
     * @param sessionId
     * @param ch
     * @return
     */
    public Session online(String tokenId, String sessionId, Channel ch) throws IMException;

    /**
     * 用户下线处理
     * 
     * @param sessionId
     * @return
     */
    public Session offline(String sessionId) throws IMException;
}
