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
import java.util.Timer;
import java.util.TimerTask;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import songm.im.IMException;
import songm.im.IMException.ErrorCode;
import songm.im.entity.SessionCh;
import songm.im.entity.Token;
import songm.im.mqtt.ClientUser;
import songm.im.service.AuthService;
import songm.im.service.ClientService;
import songm.im.service.SessionService;
import songm.im.utils.Sequence;

@Service("sessionService")
public class SessionServiceImpl implements SessionService {

    private static final Logger LOG = LoggerFactory
            .getLogger(SessionServiceImpl.class);
    private static final long DELAY = 60 * 1000;
    private static final long PERIOD = 60 * 1000;
    private Map<String, SessionCh> sessionItems = new HashMap<String, SessionCh>();

    @Resource(name = "clientService")
    private ClientService clientService;
    @Resource(name = "authService")
    private AuthService authService;

    public SessionServiceImpl() {
        this.init();
    }

    private void init() {
        SessionService _t = this;
        TimerTask task = new TimerTask() {
            @Override
            public void run() {
                LOG.debug("Session timeout check start...");
                for (Map.Entry<String, SessionCh> ent : sessionItems.entrySet()) {
                    if (ent.getValue().isTimeout()) {
                        try {
                            _t.removeSession(ent.getKey());
                            LOG.debug("RemoveSession {}", ent.getValue());
                        } catch (Exception e) {
                            LOG.error("RemoveSession", e);
                        }
                    }
                }
                LOG.debug("Session timeout check end...");
            }
        };
        // 守护线程
        Timer timer = new Timer(true);
        timer.schedule(task, DELAY, PERIOD);
    }

    @Override
    public SessionCh createSession(String tokenId, String sessionId, Channel ch)
            throws IMException {
        Token token = authService.getTokenByTokenId(tokenId);
        if (token == null) {
            throw new IMException(ErrorCode.TOKEN_INVALID, "Token invalid");
        }

        SessionCh ses = getSession(sessionId);
        if (ses != null) {
            ses.addCh(ch);
            return ses;
        }

        sessionId = Sequence.getInstance().getSequence(28);
        ses = new SessionCh(sessionId, token);
        ses.addCh(ch);

        clientService.createClient(ses);
        sessionItems.put(sessionId, ses);

        return ses;
    }

    @Override
    public SessionCh getSession(String sessionId) {
        if (sessionId == null) return null;

        SessionCh ses = sessionItems.get(sessionId);
        if (ses == null) return null;

        if (ses.isTimeout()) {
            this.removeSession(sessionId);
            return null;
        }
        ses.updateAccess();
        return ses;
    }

    @Override
    public SessionCh removeSession(String sessionId) {
        SessionCh ses = sessionItems.get(sessionId);
        if (ses == null) return null;
        // 将Session中所有管道连接清除
        ses.clearChannels();

        // 将客户端用户中对应的Session删除
        ClientUser cUser = clientService.getClient(ses.getUid());
        if (cUser != null) {
            cUser.removeSession(ses);
            if (!cUser.isSessions()) {
                clientService.removeClient(ses.getUid());
            }
        }
        return sessionItems.remove(sessionId);
    }

    @Override
    public void removeChannel(String sessionId, Channel ch) throws IMException {
        SessionCh ses = getSession(sessionId);
        if (ses == null) return;

        // 清除管道
        ses.removeChannel(ch);

        if (!ses.isChannels()) {
            removeSession(sessionId);
        }
    }

}
