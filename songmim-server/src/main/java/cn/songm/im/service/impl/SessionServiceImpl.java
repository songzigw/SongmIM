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
import java.util.Timer;
import java.util.TimerTask;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import cn.songm.common.utils.Sequence;
import cn.songm.im.IMException;
import cn.songm.im.IMException.ErrorCode;
import cn.songm.im.model.SessionCh;
import cn.songm.im.model.Token;
import cn.songm.im.mqueue.ClientUser;
import cn.songm.im.service.TokenService;
import cn.songm.im.service.ClientService;
import cn.songm.im.service.SessionService;
import io.netty.channel.Channel;

@Service("sessionService")
public class SessionServiceImpl extends TimerTask implements SessionService {

    private static final Logger LOG = LoggerFactory
            .getLogger(SessionServiceImpl.class);
    private static final long DELAY = 60 * 1000;
    private static final long PERIOD = 60 * 1000;

    private Map<String, SessionCh> sesItems;
    private Timer timer;

    @Resource(name = "clientService")
    private ClientService clientService;
    @Resource(name = "authService")
    private TokenService authService;

    public SessionServiceImpl() {
        sesItems = Collections
                .synchronizedMap(new HashMap<String, SessionCh>());
        // 守护线程
        timer = new Timer("SessionTimeoutTimer", true);
        timer.schedule(this, DELAY, PERIOD);
    }

    @Override
    public void run() {
        int start = sesItems.size();
        for (Map.Entry<String, SessionCh> ent : sesItems.entrySet()) {
            if (ent.getValue().isTimeout()) {
                try {
                    this.removeSession(ent.getKey());
                    LOG.debug("[RemoveSession: {}]", ent.getValue());
                } catch (Exception e) {
                    LOG.error(e.getMessage(), e);
                }
            }
        }
        int end = sesItems.size();
        LOG.info("[StartNumber: {}, EndNumber: {}] SessionTimeoutCheck", start,
                end);
    }

    @Override
    public SessionCh createSession(String tokenId, String sessionId, Channel ch)
            throws IMException {
        Token token = authService.getTokenById(tokenId);
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
        sesItems.put(sessionId, ses);

        return ses;
    }

    @Override
    public SessionCh getSession(String sessionId) {
        if (sessionId == null) return null;

        SessionCh ses = sesItems.get(sessionId);
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
        SessionCh ses = sesItems.get(sessionId);
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
        return sesItems.remove(sessionId);
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
