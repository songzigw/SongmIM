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
package cn.songm.im.service.mqueue;

import java.util.Iterator;
import java.util.Set;
import java.util.concurrent.ConcurrentSkipListSet;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cn.songm.im.codec.Session;
import cn.songm.im.codec.Token;
import cn.songm.im.codec.model.Conversation;
import cn.songm.im.codec.model.Conversation.Ctype;
import cn.songm.im.codec.model.Message;
import cn.songm.im.codec.model.Message.Direction;
import cn.songm.im.codec.model.UnreadMessage;
import cn.songm.im.server.handler.actioner.BrokerPushActioner;
import cn.songm.im.service.SessionService;
import cn.songm.songmq.core.MQueueManager;
import cn.songm.songmq.core.MQueueModel;
import cn.songm.songmq.core.MessageEvent;
import cn.songm.songmq.core.MessageEventManager;
import cn.songm.songmq.core.MessageListener;
import cn.songm.songmq.core.Topic;
import cn.songm.songmq.core.util.JsonUtils;
import io.netty.channel.Channel;

/**
 * SongMQ客户端
 * 
 * @author zhangsong
 *
 */
public class SongMQUser extends ClientUser implements MessageListener {

    private static final Logger LOG = LoggerFactory.getLogger(SongMQUser.class);
    
    private final Token token;
    private final Set<Session> sessions;
    private final Set<Conversation> convs;
    private int unreadMsg;
    private SessionService sessionService;

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
    
    public SongMQUser(Token token, SessionService sservice) {
        if (null == token) {
            throw new IllegalArgumentException("token is null");
        }
        this.token = token;
        sessions = new ConcurrentSkipListSet<>();
        convs = new ConcurrentSkipListSet<>();
        sessionService = sservice;
        this.subscribe(Ctype.PRIVATE, token.getUid());
        LOG.debug(token.toString());
    }

    @Override
    public void addSession(Session session) {
        if (sessions.contains(session)) {
            return;
        }
        sessions.add(session);
    }

    @Override
    public void removeSession(Session session) {
        if (!sessions.contains(session)) return;
        sessions.remove(session);
        sessionService.removeSession(session.getSessionId());
    }

    @Override
    public boolean isSessions() {
        return !sessions.isEmpty();
    }

    @Override
    public Session[] clearSessions() {
        Session[] sesArr = new Session[sessions.size()];
        int i = 0;
        Iterator<Session> iter = sessions.iterator();
        while (iter.hasNext()) {
            Session ses = iter.next();
            sesArr[i] = ses;
            this.removeSession(ses);
            i++;
        }
        return sesArr;
    }

    @Override
    public void publish(String target, Message message) {
        message.setDirection(Message.Direction.SEND);
        Ctype convType = message.getConv();
        // 发送给对方
        Topic topic = new Topic();
        topic.setModel(MQueueModel.PUB_SUB);
        topic.setName(topic(convType, this.token.getAppKey(), target));
        try {
            MQueueManager.getInstance().createMQ(topic).pushMessage(message.clone());
        } catch (CloneNotSupportedException e) {
            LOG.error(e.getMessage(), e);
        }

        // 给自己发送一份
        if (convType.equals(Ctype.PRIVATE)) {
            Topic topicSelf = new Topic();
            topicSelf.setModel(MQueueModel.PUB_SUB);
            topicSelf.setName(topic(convType, this.token.getAppKey(), token.getUid()));
            try {
                MQueueManager.getInstance().createMQ(topicSelf).pushMessage(message.clone());
            } catch (CloneNotSupportedException e) {
                LOG.error(e.getMessage(), e);
            }
        }
        // 创建会话信息
        createConv(message);
    }
    
    private Conversation createConv(Message message) {
        Conversation conv = null;
        String convId = this.jointConvId(message);
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
            this.convs.add(conv);
        }
        switch (conv.getType()) {
        case PRIVATE:
            if (message.getDirection().equals(Direction.SEND)) {
                conv.setObjectum(message.getTo());
                conv.setObjNick(message.gettNick());
                conv.setObjAvatar(message.gettAvatar());
            } else if (message.getDirection().equals(Direction.RECEIVE)) {
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
        conv.setDirection(message.getDirection());
        conv.setBody(JsonUtils.getInstance().toJson(message.getJbody()));
        LOG.debug(conv.toString());
        return conv;
    }

    /**
     * 拼接业务会话ID
     * @param message
     * @return
     */
    private String jointConvId(Message message) {
        StringBuilder str = new StringBuilder();
        Ctype convType = message.getConv();
        str.append(convType.getValue()).append("_")
                .append(token.getUid()).append("_");
        switch (convType) {
        case PRIVATE:
            if (Direction.SEND.equals(message.getDirection())) {
                str.append(message.getTo());
            } else if (Direction.RECEIVE.equals(message.getDirection())) {
                str.append(message.getFrom());
            }
            break;
        case GROUP:
            str.append(message.getTo());
            break;
        default:
            throw new IllegalArgumentException("convType");
        }
        return str.toString();
    }
    
    @Override
    public void receive(Message message) {
        if (message.getFrom().equals(this.token.getUid())) {
            message.setDirection(Direction.SEND);
        } else {
            message.setDirection(Message.Direction.RECEIVE);
        }
        Iterator<Session> iter = sessions.iterator();
        while (iter.hasNext()) {
            Session session = (Session) iter.next();
            Channel ch = sessionService.getChannel(session.getSessionId());
            BrokerPushActioner.push(ch, message);
        }

        if (message.getFrom().equals(this.token.getUid())) {
            return;
        }
        if (message.getType().equals(Message.Mtype.UNREAD)) {
            return;
        }
        // 创建会话信息并增加未读消息数
        this.setUnreadMsg(createConv(message), true);
    }
    
    private void setUnreadMsg(Conversation conv, boolean flag) {
        if (flag) {
            // 未读消息数增加
            conv.setUnreadCount(conv.getUnreadCount() + 1);
            unreadMsg++;
        } else {
            // 未读消息数减少
            unreadMsg -= conv.getUnreadCount();
            conv.setUnreadCount(0);
            if (unreadMsg < 0) unreadMsg = 0;
        }
        
        Message msg = new Message();
        msg.setConv(conv.getType());
        msg.setType(Message.Mtype.UNREAD);
        switch (conv.getType()) {
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
        msg.setDirection(Message.Direction.SEND);
        UnreadMessage unread = new UnreadMessage();
        unread.setNumber(conv.getUnreadCount());
        unread.setTotal(unreadMsg);
        msg.setJbody(unread);
        
        Topic topic = new Topic();
        topic.setModel(MQueueModel.PUB_SUB);
        topic.setName(topic(Ctype.PRIVATE,
                this.token.getAppKey(), this.token.getUid()));
        MQueueManager.getInstance().createMQ(topic).pushMessage(msg);
    }

    // 清除未读消息数
    public void clearUnreadMsg(Ctype ctype, String subjectum, String objectum) {
        String convId = ctype.getValue() + "_" + subjectum + "_" + objectum;
        for (Conversation c : this.convs) {
            if (c.getId().equals(convId)) {
                setUnreadMsg(c, false);
                break;
            }
        }
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
        this.receive((Message) event.getPayload());
    }
}
