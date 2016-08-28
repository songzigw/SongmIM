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

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import songm.im.Config;
import songm.im.IMException;
import songm.im.IMException.ErrorCode;
import songm.im.entity.Session;
import songm.im.entity.Token;
import songm.im.service.AuthService;
import songm.im.service.SessionService;
import songm.im.utils.CodeUtils;
import songm.im.utils.Sequence;

@Service
public class AuthServiceImpl implements AuthService {

    // tokenItems可以做到持久化，目前先这样
    private Map<String, Token> tokenItems = new HashMap<String, Token>();
    @Autowired
    private SessionService sessionService;

    @Override
    public boolean auth(String key, String nonce, String signature,
            long timestamp) {
        long curr = (new Date()).getTime();
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
    public Token getToken(String uid, String nick, String avatar) {
        Token token = new Token();
        token.setId(Sequence.getInstance().getSequence(16));
        token.setUid(uid);
        token.setNick(nick);
        token.setAvatar(avatar);
        tokenItems.put(token.getId(), token);
        return token;
    }

    @Override
    public Session online(String tokenId, String sessionId) throws IMException {
        Token token = tokenItems.get(tokenId);
        if (token == null) {
            throw new IMException(ErrorCode.TOKEN_INVALID, "Token invalid");
        }

        return sessionService.create(token, sessionId);
    }

    @Override
    public Session offline(String sessionId) {
        return sessionService.remove(sessionId);
    }

}
