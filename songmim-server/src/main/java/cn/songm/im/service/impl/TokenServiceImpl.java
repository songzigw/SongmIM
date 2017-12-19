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
package cn.songm.im.service.impl;

import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import cn.songm.im.codec.IMException;
import cn.songm.im.codec.Session;
import cn.songm.im.codec.Token;
import cn.songm.im.service.SessionService;
import cn.songm.im.service.TokenService;
import cn.songm.songmq.core.util.CodeUtils;
import io.netty.channel.Channel;

@Service("tokenService")
public class TokenServiceImpl implements TokenService {

    // tokenItems 可以做到持久化，目前先这样
    private Map<String, Token> tokenItems = new ConcurrentHashMap<>();

    @Autowired
    private SessionService sessionService;
    @Value("${songmim.server.key}")
    private String serverKey;
    @Value("${songmim.server.secret}")
    private String serverSecret;

    @Override
    public boolean sign(String key, String nonce, String signature,
            long timestamp) {
        long curr = System.currentTimeMillis();
        long mis = curr - timestamp;
        if (mis > MISTIMING || mis < -MISTIMING) {
            return false;
        }

        if (!serverKey.equals(key)) {
            return false;
        }

        StringBuilder toSign = new StringBuilder();
        toSign.append(serverSecret).append(nonce).append(timestamp);
        String sign = CodeUtils.sha1(toSign.toString());
        if (!sign.equals(signature)) {
            return false;
        }

        return true;
    }

    @Override
    public Token createToken(String appKey, String uid, String nick,
            String avatar) {
        Token token = new Token(uid, nick, avatar, appKey);

        if (tokenItems.containsValue(token)) {
            Set<Entry<String, Token>> set = tokenItems.entrySet();
            for (Entry<String, Token> s : set) {
                if (s.getValue().getUid().equals(uid)) {
                    s.getValue().setNick(nick);
                    s.getValue().setAvatar(avatar);
                    return s.getValue();
                }
            }
        }

        tokenItems.put(token.getTokenId(), token);
        return token;
    }

    @Override
    public Token deleteToken(String tokenId) {
        return tokenItems.remove(tokenId);
    }

    @Override
    public Token getTokenById(String tokenId) {
        return tokenItems.get(tokenId);
    }

    @Override
    public Session online(String tokenId, String uid, Channel ch)
            throws IMException {
        return sessionService.createSession(tokenId, uid, ch);
    }

    @Override
    public Session offline(String sessionId) throws IMException {
        return sessionService.removeSession(sessionId);
    }

}
