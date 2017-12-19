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
import java.util.concurrent.ConcurrentHashMap;

import javax.annotation.Resource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import cn.songm.im.codec.IMException;
import cn.songm.im.codec.Session;
import cn.songm.im.codec.model.Conversation.Ctype;
import cn.songm.im.service.ClientService;
import cn.songm.im.service.SessionService;
import cn.songm.im.service.TokenService;
import cn.songm.im.service.mqueue.ClientUser;
import cn.songm.im.service.mqueue.SongMQUser;

@Service("clientService")
public class ClientServiceImpl implements ClientService {

    private Map<String, ClientUser> clientItems;
    {
        clientItems = new ConcurrentHashMap<>();
    }

    @Resource(name = "sessionService")
    private SessionService sessionService;
    @Autowired
    private TokenService tokenService;

    @Override
    public ClientUser createClient(Session session) throws IMException {
        ClientUser client = getClient(session.getUid());
        if (client != null) {
            client.addSession(session);
            return client;
        }

        client = new SongMQUser(
                tokenService.getTokenById(session.getTokenId()),
                sessionService);
        client.addSession(session);

        return clientItems.put(session.getUid(), client);
    }

    @Override
    public ClientUser getClient(String uid) {
        return clientItems.get(uid);
    }

    @Override
    public void removeClient(String uid) {
        ClientUser client = getClient(uid);
        if (client != null) {
            Session[] sess = client.clearSessions();
            for (Session ses : sess) {
                sessionService.removeSession(ses.getSessionId());
            }
            client.unsubscribe(Ctype.PRIVATE, uid);
            clientItems.remove(uid);
        }
    }
}
