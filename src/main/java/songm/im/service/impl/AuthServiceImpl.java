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
package songm.im.service.impl;

import io.netty.channel.Channel;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import songm.im.Config;
import songm.im.IMException;
import songm.im.IMException.ErrorCode;
import songm.im.entity.SessionCh;
import songm.im.entity.Token;
import songm.im.service.AuthService;
import songm.im.service.SessionService;
import songm.im.utils.CodeUtils;
import songm.im.utils.Sequence;

@Service("authService")
public class AuthServiceImpl implements AuthService {

    // TokenItems可以做到持久化，目前先这样
    private Map<String, Token> tokenItems = new HashMap<String, Token>();
    @Autowired
    private SessionService sessionService;

    @Override
    public boolean auth(String key, String nonce, String signature,
            long timestamp) {
        long curr = System.currentTimeMillis();
        long mis = curr - timestamp;
        if (mis > MISTIMING || mis < -MISTIMING) {
            return false;
        }

        String sKey = Config.getInstance().getServerKey();
        String secret = Config.getInstance().getServerSecret();
        if (!sKey.equals(key)) {
            return false;
        }

        StringBuilder toSign = new StringBuilder(secret).append(nonce).append(
                timestamp);
        String sign = CodeUtils.sha1(toSign.toString());
        if (!sign.equals(signature)) {
            return false;
        }

        return true;
    }

    @Override
    public Token createToken(String uid, String nick, String avatar) {
        Token token = new Token();
        token.setUid(uid);
        token.setNick(nick);
        token.setAvatar(avatar);
        
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
    public Token getTokenByTokenId(String tokenId) {
        return tokenItems.get(tokenId);
    }

    @Override
    public SessionCh online(String tokenId, String sessionId, Channel ch) throws IMException {
        Token token = tokenItems.get(tokenId);
        if (token == null) {
            throw new IMException(ErrorCode.TOKEN_INVALID, "Token invalid");
        }
        return sessionService.createSession(token, sessionId, ch);
    }

    @Override
    public SessionCh offline(String sessionId) throws IMException {
        return sessionService.removeSession(sessionId);
    }

}
