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

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import cn.songm.common.utils.CodeUtils;
import cn.songm.common.utils.Sequence;
import cn.songm.im.IMConfig;
import cn.songm.im.IMException;
import cn.songm.im.model.SessionCh;
import cn.songm.im.model.Token;
import cn.songm.im.service.TokenService;
import cn.songm.im.service.SessionService;
import io.netty.channel.Channel;

@Service("authService")
public class TokenServiceImpl implements TokenService {

    // TokenItems可以做到持久化，目前先这样
    private Map<String, Token> tokenItems = Collections
            .synchronizedMap(new HashMap<String, Token>());

    @Autowired
    private SessionService sessionService;

    @Override
    public boolean sign(String key, String nonce, String signature,
            long timestamp) {
        long curr = System.currentTimeMillis();
        long mis = curr - timestamp;
        if (mis > MISTIMING || mis < -MISTIMING) {
            return false;
        }

        String sKey = IMConfig.getInstance().getServerKey();
        String secret = IMConfig.getInstance().getServerSecret();
        if (!sKey.equals(key)) {
            return false;
        }

        StringBuilder toSign = new StringBuilder(secret).append(nonce)
                .append(timestamp);
        String sign = CodeUtils.sha1(toSign.toString());
        if (!sign.equals(signature)) {
            return false;
        }

        return true;
    }

    @Override
    public Token createToken(String appKey, String uid, String nick,
            String avatar) {
        Token token = new Token();
        token.setUid(uid);
        token.setNick(nick);
        token.setAvatar(avatar);
        token.setAppKey(appKey);

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

        token.setTokenId(Sequence.getInstance().getSequence(16));
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
    public SessionCh online(String tokenId, String sessionId, Channel ch)
            throws IMException {
        return sessionService.createSession(tokenId, sessionId, ch);
    }

    @Override
    public SessionCh offline(String sessionId) throws IMException {
        return sessionService.removeSession(sessionId);
    }

}
