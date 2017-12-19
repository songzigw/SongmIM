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
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ConcurrentHashMap;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import cn.songm.im.codec.IMException;
import cn.songm.im.codec.IMException.ErrorCode;
import cn.songm.im.codec.Session;
import cn.songm.im.codec.Token;
import cn.songm.im.service.ClientService;
import cn.songm.im.service.SessionService;
import cn.songm.im.service.TokenService;
import cn.songm.im.service.mqueue.ClientUser;
import io.netty.channel.Channel;

@Service("sessionService")
public class SessionServiceImpl extends TimerTask implements SessionService {

    private final Logger LOG = LoggerFactory.getLogger(getClass());
    private static final long DELAY = 60 * 1000;
    private static final long PERIOD = 60 * 1000;

    private Map<String, Object[]> sessionItems;
    private Timer timer;

    @Resource(name = "clientService")
    private ClientService clientService;
    @Resource(name = "tokenService")
    private TokenService tokenService;

    public SessionServiceImpl() {
        sessionItems = new ConcurrentHashMap<>();
        // 守护线程
        timer = new Timer("sessionTimeoutTimer", true);
        timer.schedule(this, DELAY, PERIOD);
    }

    @Override
    public void run() {
        int start = sessionItems.size();

        try {
            for (Map.Entry<String, Object[]> ent : sessionItems.entrySet()) {
                Object[] array = ent.getValue();
                Session ses = (Session) array[0];
                if (ses.isTimeout()) {
                    this.removeSession(ent.getKey());
                    LOG.debug("remove: {}", ent.getValue());
                }
            }
        } catch (Exception e) {
            LOG.error(e.getMessage(), e);
        }

        int end = sessionItems.size();
        LOG.info("start: {}, end: {}", start, end);
    }

    @Override
    public Session createSession(String tokenId, String uid, Channel ch)
            throws IMException {
        Token token = tokenService.getTokenById(tokenId);
        if (token == null) {
            throw new IMException(ErrorCode.TOKEN_INVALID, "Token invalid");
        }
        if (!token.getUid().equals(uid)) {
            throw new IMException(ErrorCode.UID_INVALID, "Uid invalid");
        }

        Session ses = new Session(token);

        clientService.createClient(ses);
        sessionItems.put(ses.getSessionId(), new Object[] {ses, ch});

        return ses;
    }

    @Override
    public Session getSession(String sessionId) {
        if (sessionId == null) return null;

        Object[] array = sessionItems.get(sessionId);
        if (array == null) {
            return null;
        }
        return (Session) array[0];
    }

    @Override
    public Session removeSession(String sessionId) {
        if (!sessionItems.containsKey(sessionId)) {
            return null;
        }
        
        Object[] array = sessionItems.remove(sessionId);
        if (array == null) return null;
        Session ses = (Session) array[0];
        Channel ch = (Channel) array[1];
        ch.close().syncUninterruptibly();
        
        // 将客户端用户中对应的Session删除
        ClientUser cUser = clientService.getClient(ses.getUid());
        if (cUser != null) {
            cUser.removeSession(ses);
            if (!cUser.isSessions()) {
                clientService.removeClient(ses.getUid());
            }
        }
        return ses;
    }

    @Override
    public Channel getChannel(String sessionId) {
        if (sessionId == null) return null;

        Object[] array = sessionItems.get(sessionId);
        if (array == null) {
            return null;
        }
        return (Channel) array[1];
    }

}
