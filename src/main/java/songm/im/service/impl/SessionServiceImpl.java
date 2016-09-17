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

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import songm.im.IMException;
import songm.im.entity.SessionCh;
import songm.im.entity.Token;
import songm.im.mqtt.ClientUser;
import songm.im.service.ClientService;
import songm.im.service.SessionService;
import songm.im.utils.Sequence;

@Service("sessionService")
public class SessionServiceImpl implements SessionService {

    private Map<String, SessionCh> sesItems = new HashMap<String, SessionCh>();

    @Resource(name = "clientService")
    private ClientService clientService;

    @Override
    public SessionCh createSession(Token token, String sessionId, Channel ch) throws IMException {
        SessionCh ses = getSession(sessionId);
        if (ses != null) {
            ses.addCh(ch);
            return ses;
        }

        sessionId = Sequence.getInstance().getSequence(28);
        ses = new SessionCh(sessionId, token.getTokenId(), token.getUid());
        ses.addCh(ch);
   
        clientService.createClient(ses);
        sesItems.put(sessionId, ses);
   
        return ses;
    }

    @Override
    public SessionCh getSession(String sessionId) {
        if (sessionId == null) {
            return null;
        }
        return sesItems.get(sessionId);
    }

    @Override
    public SessionCh removeSession(String sessionId) throws IMException {
        SessionCh session = getSession(sessionId);
        if (session == null) {
            return null;
        }
        // 将Session中所有管道连接清除
        session.clearChannel();
        
        // 将客户端用户中对应的Session删除
        ClientUser cUser = clientService.getClient(session.getUid());
        if (cUser != null) {
            cUser.removeSession(session);
            if (!cUser.isSessions())
                clientService.removeClient(session.getUid());
        }
        return sesItems.remove(sessionId);
    }

    @Override
    public void removeChannel(String sessionId, Channel ch) throws IMException {
        SessionCh session = getSession(sessionId);
        if (session == null) {
            return;
        }
        // 清除管道
        session.removeChannel(ch);
        
        if (!session.isChannels()) {
            removeSession(sessionId);
        }
    }

}
