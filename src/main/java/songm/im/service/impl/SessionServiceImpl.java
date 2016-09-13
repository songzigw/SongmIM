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

import songm.im.entity.Session;
import songm.im.entity.SessionCh;
import songm.im.entity.Token;
import songm.im.mqtt.ClientUser;
import songm.im.service.ClientService;
import songm.im.service.SessionService;
import songm.im.utils.Sequence;

@Service("sessionService")
public class SessionServiceImpl implements SessionService {

    private Map<String, Session> sesItems = new HashMap<String, Session>();

    @Resource(name = "mqttClientService")
    private ClientService mqttClientService;

    @Override
    public Session create(Token token, String sessionId, Channel ch) {
        SessionCh ses = (SessionCh) getSession(sessionId);
        if (ses != null) {
            ses.addCh(ch);
            return ses;
        }

        sessionId = Sequence.getInstance().getSequence(28);
        ses = new SessionCh(sessionId, token.getTokenId(), token.getUid());
        ses.setAttribute(KEY_UID, token.getUid());
        ses.addCh(ch);
        sesItems.put(sessionId, ses);
        
        mqttClientService.createClient(ses);
        return ses;
    }

    @Override
    public Session getSession(String sessionId) {
        if (sessionId == null) {
            return null;
        }
        return sesItems.get(sessionId);
    }

    @Override
    public Session remove(String sessionId) {
        SessionCh session = (SessionCh) getSession(sessionId);
        if (session == null) {
            return null;
        }
        // 将Session中所有管道连接清除
        session.clearCh();
        
        // 将客户端用户中对应的Session删除
        ClientUser cUser = mqttClientService.getClient(session.getUid());
        if (cUser != null) {
            cUser.removeSession(session);
            if (!cUser.isSessions())
                mqttClientService.disconnect(session.getUid());
        }
        return sesItems.remove(sessionId);
    }

}
