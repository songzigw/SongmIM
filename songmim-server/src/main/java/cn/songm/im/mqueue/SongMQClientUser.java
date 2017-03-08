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
package cn.songm.im.mqueue;

import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import cn.songm.im.model.Conversation;
import cn.songm.im.model.Conversation.Type;
import cn.songm.im.model.SessionCh;
import cn.songm.im.model.Token;
import cn.songm.songmq.core.MQueueManager;
import cn.songm.songmq.core.MQueueModel;
import cn.songm.songmq.core.MessageEvent;
import cn.songm.songmq.core.MessageEventManager;
import cn.songm.songmq.core.MessageListener;
import cn.songm.songmq.core.Topic;
import io.netty.channel.Channel;

/**
 * SongMQ客户端用户
 * 
 * @author zhangsong
 *
 */
public final class SongMQClientUser implements ClientUser, MessageListener {

    private final Token token;
    private final Set<SessionCh> sessions;

    public static String topic(Conversation.Type convType, String appKey,
            String id) {
        String topic = null;
        switch (convType) {
        case PRIVATE:
            topic = "/appkey/" + appKey + "/uid/" + id;
            break;
        case GROUP:
            topic = "/appkey/" + appKey + "/gid/" + id;
            break;
        case NOTICE:
            topic = "/appkey/" + appKey + "/nid/" + id;
            break;
        default:
            throw new IllegalArgumentException("convType");
        }
        return topic;
    }
    
    public SongMQClientUser(Token token) {
        this.token = token;
        sessions = Collections.synchronizedSet(new HashSet<SessionCh>());
    }

    public void addSession(SessionCh session) {
        for (SessionCh ses : sessions) {
            if (ses.getSessionId().equals(session.getSessionId())) {
                return;
            }
        }
        sessions.add(session);
    }

    public void removeSession(SessionCh session) {
        session.clearChannels();
        sessions.remove(session);
    }

    public void trigger(byte[] payload, Channel out) {
        Iterator<SessionCh> iter = sessions.iterator();
        while (iter.hasNext()) {
            SessionCh session = (SessionCh) iter.next();
            session.onReceived(payload, out);
        }
    }

    public boolean isSessions() {
        return !sessions.isEmpty();
    }

    public SessionCh[] clearSessions() {
        SessionCh[] sesArr = new SessionCh[sessions.size()];
        int i = 0;
        for (SessionCh ses : sessions) {
            sesArr[i] = ses;
            ses.clearChannels();
            i++;
        }
        sessions.clear();
        return sesArr;
    }

    @Override
    public void publish(Conversation.Type convType, String id, byte[] body) {
        Topic topic = new Topic();
        topic.setModel(MQueueModel.BROADCAST);
        topic.setName(topic(convType, this.token.getAppKey(), id));
        MQueueManager.getInstance().createMQ(topic).pushMessage(body);
    }

    @Override
    public void subscribe(Type convType, String id) {
        Topic topic = new Topic();
        topic.setModel(MQueueModel.BROADCAST);
        topic.setName(topic(convType, this.token.getAppKey(), id));
        MessageEventManager.getInstance().subscribe(topic, this);
    }

    @Override
    public void unsubscribe(Type convType, String id) {
        Topic topic = new Topic();
        topic.setModel(MQueueModel.BROADCAST);
        topic.setName(topic(convType, this.token.getAppKey(), id));
        MessageEventManager.getInstance().unsubscribe(topic, this);
    }

    @Override
    public void onMessage(MessageEvent event) {
        this.trigger(event.getPayload(), null);
    }
}
