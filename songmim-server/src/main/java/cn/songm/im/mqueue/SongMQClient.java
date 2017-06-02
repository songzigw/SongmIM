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

import cn.songm.common.utils.JsonUtils;
import cn.songm.im.model.Conversation;
import cn.songm.im.model.Conversation.Ctype;
import cn.songm.im.model.SessionCh;
import cn.songm.im.model.Token;
import cn.songm.im.model.message.Message;
import cn.songm.im.model.message.UnreadMessage;
import cn.songm.songmq.core.MQueueManager;
import cn.songm.songmq.core.MQueueModel;
import cn.songm.songmq.core.MessageEvent;
import cn.songm.songmq.core.MessageEventManager;
import cn.songm.songmq.core.MessageListener;
import cn.songm.songmq.core.Topic;
import io.netty.channel.Channel;

/**
 * SongMQ客户端
 * 
 * @author zhangsong
 *
 */
public final class SongMQClient extends ClientUser implements MessageListener {

    private final Token token;
    private final Set<SessionCh> sessions;
    private final Set<Conversation> convs;
    private int unreadMsg;

    public static String topic(Ctype convType, String appKey,
            String target) {
        String topic = null;
        switch (convType) {
        case PRIVATE:
            topic = "/appkey/" + appKey + "/uid/" + target;
            break;
        case GROUP:
            topic = "/appkey/" + appKey + "/gid/" + target;
            break;
        default:
            throw new IllegalArgumentException("convType");
        }
        return topic;
    }
    
    public SongMQClient(Token token) {
        this.token = token;
        sessions = Collections.synchronizedSet(new HashSet<SessionCh>());
        convs = Collections.synchronizedSet(new HashSet<Conversation>());
        this.subscribe(Ctype.PRIVATE, token.getUid());
    }

    @Override
    public void addSession(SessionCh session) {
        for (SessionCh ses : sessions) {
            if (ses.getSessionId().equals(session.getSessionId())) {
                return;
            }
        }
        sessions.add(session);
    }

    @Override
    public void removeSession(SessionCh session) {
        session.clearChannels();
        sessions.remove(session);
    }

    @Override
    public boolean isSessions() {
        return !sessions.isEmpty();
    }

    @Override
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
    public void publish(Ctype convType, String target, Message message) {
        Topic topic = new Topic();
        topic.setModel(MQueueModel.PUB_SUB);
        topic.setName(topic(convType, this.token.getAppKey(), target));
        MQueueManager.getInstance().createMQ(topic).pushMessage(message);
        // 创建会话信息
        createConv(true, message);
    }
    
    private Conversation createConv(boolean publish, Message message) {
        Conversation conv = null;
        String convId = this.getConvId(publish, message);
        for (Conversation c : this.convs) {
            if (c.getId().equals(convId)) {
                conv = c;
                break;
            }
        }
        if (conv == null) {
            conv = new Conversation();
            conv.setUnreadCount(0);
            conv.setId(convId);
            conv.setType(message.getConv());
            conv.setSubjectum(token.getUid());
            conv.setSubNick(token.getNick());
            conv.setSubAvatar(token.getAvatar());
        }
        Ctype convType = Ctype.instance(conv.getType());
        switch (convType) {
        case PRIVATE:
            if (publish) {
                conv.setObjectum(message.getTo());
                conv.setObjNick(message.gettNick());
                conv.setObjAvatar(message.gettAvatar());
            } else {
                conv.setObjectum(message.getFrom());
                conv.setObjNick(message.getfNick());
                conv.setObjAvatar(message.getfAvatar());
            }
            break;
        case GROUP:
            conv.setObjectum(message.getTo());
            conv.setObjNick(message.gettNick());
            conv.setObjAvatar(message.gettAvatar());
            break;
        default:
            throw new IllegalArgumentException("convType");
        }
        conv.setBody(message.getJbody());
        return conv;
    }
    
    private String getConvId(boolean publish, Message message) {
        Ctype convType = Ctype.instance(message.getConv());
        switch (convType) {
        case PRIVATE:
            if (publish) {
                return convType.getValue() + "_" + token.getUid() + "_"
                        + message.getTo();
            } else {
                return convType.getValue() + "_" + token.getUid() + "_"
                        + message.getFrom();
            }
        case GROUP:
            return convType.getValue() + "_" + token.getUid() + "_"
                    + message.getTo();
        default:
            throw new IllegalArgumentException("convType");
        }
    }
    
    @Override
    public void receive(Message message, Channel out) {
        message.setDirection(Message.Direction.RECEIVE.getValue());
        Iterator<SessionCh> iter = sessions.iterator();
        while (iter.hasNext()) {
            SessionCh session = (SessionCh) iter.next();
            session.onReceived(message, out);
        }
        if (message.getFrom().equals(this.token.getUid())) {
            return;
        }
        if (message.getType().equals(Message.Mtype.UNREAD.getValue())) {
            return;
        }
        // 创建会话信息
        Conversation conv = createConv(false, message);
        this.incUnreadMsg(conv);
    }
    
    private void incUnreadMsg(Conversation conv) {
        // 未读消息数增加
        conv.setUnreadCount(conv.getUnreadCount() + 1);
        unreadMsg++;
        
        Message msg = new Message();
        msg.setConv(conv.getType());
        msg.setType(Message.Mtype.UNREAD.getValue());
        Ctype ctype = Ctype.instance(conv.getType());
        switch (ctype) {
        case PRIVATE:
            msg.setFrom(conv.getObjectum());
            msg.setfNick(conv.getObjNick());
            msg.setfAvatar(conv.getObjAvatar());
            msg.setTo(conv.getSubjectum());
            msg.settNick(conv.getSubNick());
            msg.settAvatar(conv.getSubAvatar());
            break;
        case GROUP:
            msg.setFrom(conv.getSubjectum());
            msg.setfNick(conv.getSubNick());
            msg.setfAvatar(conv.getSubAvatar());
            msg.setTo(conv.getObjectum());
            msg.settNick(conv.getObjNick());
            msg.settAvatar(conv.getObjAvatar());
            break;
        default:
            throw new IllegalArgumentException("ctype");
        }
        msg.setDirection(Message.Direction.SEND.getValue());
        UnreadMessage unread = new UnreadMessage();
        unread.setNumber(conv.getUnreadCount());
        unread.setTotal(unreadMsg);
        msg.setJbody(JsonUtils.toJson(unread));
        
        Topic topic = new Topic();
        topic.setModel(MQueueModel.PUB_SUB);
        topic.setName(topic(Ctype.PRIVATE,
                this.token.getAppKey(), this.token.getUid()));
        MQueueManager.getInstance().createMQ(topic).pushMessage(msg);
    }

    public void decUnreadMsg(String type, String subjectum, String objectum) {
        String convId = type + "_" + subjectum + "_" + objectum;
        Conversation conv = null;
        for (Conversation c : this.convs) {
            if (c.getId().equals(convId)) {
                conv = c; break;
            }
        }
        if (conv == null) {
            return;
        }
        // 未读消息数减少
        unreadMsg -= conv.getUnreadCount();
        conv.setUnreadCount(0);
        if (unreadMsg < 0) {
            unreadMsg = 0;
        }
        
        Message msg = new Message();
        msg.setConv(conv.getType());
        msg.setType(Message.Mtype.UNREAD.getValue());
        Ctype ctype = Ctype.instance(conv.getType());
        switch (ctype) {
        case PRIVATE:
            msg.setFrom(conv.getObjectum());
            msg.setfNick(conv.getObjNick());
            msg.setfAvatar(conv.getObjAvatar());
            msg.setTo(conv.getSubjectum());
            msg.settNick(conv.getSubNick());
            msg.settAvatar(conv.getSubAvatar());
            break;
        case GROUP:
            msg.setFrom(conv.getSubjectum());
            msg.setfNick(conv.getSubNick());
            msg.setfAvatar(conv.getSubAvatar());
            msg.setTo(conv.getObjectum());
            msg.settNick(conv.getObjNick());
            msg.settAvatar(conv.getObjAvatar());
            break;
        default:
            throw new IllegalArgumentException("ctype");
        }
        msg.setDirection(Message.Direction.SEND.getValue());
        UnreadMessage unread = new UnreadMessage();
        unread.setNumber(conv.getUnreadCount());
        unread.setTotal(unreadMsg);
        msg.setJbody(JsonUtils.toJson(unread));
        
        Topic topic = new Topic();
        topic.setModel(MQueueModel.PUB_SUB);
        topic.setName(topic(Ctype.PRIVATE,
                this.token.getAppKey(), this.token.getUid()));
        MQueueManager.getInstance().createMQ(topic).pushMessage(msg);
    }
    
    @Override
    public void subscribe(Ctype convType, String target) {
        Topic topic = new Topic();
        topic.setModel(MQueueModel.PUB_SUB);
        topic.setName(topic(convType, this.token.getAppKey(), target));
        MessageEventManager.getInstance().subscribe(topic, this);
    }

    @Override
    public void unsubscribe(Ctype convType, String target) {
        Topic topic = new Topic();
        topic.setModel(MQueueModel.PUB_SUB);
        topic.setName(topic(convType, this.token.getAppKey(), target));
        MessageEventManager.getInstance().unsubscribe(topic, this);
    }

    @Override
    public void onMessage(MessageEvent event) {
        this.receive((Message) event.getPayload(), null);
    }
}
