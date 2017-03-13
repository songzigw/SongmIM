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

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import cn.songm.im.IMException;
import cn.songm.im.model.Conversation;
import cn.songm.im.model.SessionCh;
import cn.songm.im.mqueue.ClientUser;
import cn.songm.im.mqueue.SongMQClientUser;
import cn.songm.im.service.ClientService;
import cn.songm.im.service.SessionService;

@Service("clientService")
public class ClientServiceImpl implements ClientService {

    private Map<String, ClientUser> clientItems = Collections
            .synchronizedMap(new HashMap<String, ClientUser>());

    @Resource(name = "sessionService")
    private SessionService sessionService;

    @Override
    public ClientUser createClient(SessionCh session) throws IMException {
        ClientUser client = getClient(session.getUid());
        if (client != null) {
            client.addSession(session);
            return client;
        }

        client = new SongMQClientUser(session.getToken());
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
            SessionCh[] sess = client.clearSessions();
            for (SessionCh ses : sess) {
                sessionService.removeSession(ses.getSessionId());
            }
            client.unsubscribe(Conversation.Type.PRIVATE, uid);
            clientItems.remove(uid);
        }
    }
}
