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
package songm.im.mqtt;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.MqttPersistenceException;

import io.netty.channel.Channel;
import songm.im.IMException;
import songm.im.IMException.ErrorCode;
import songm.im.entity.Conversation;
import songm.im.entity.SessionCh;
import songm.im.entity.Token;
import songm.im.service.ClientService;

/**
 * Mqtt客户端用户
 * 
 * @author zhangsong
 *
 */
public final class MqttClientUser extends MqttClient implements ClientUser {

    private final Set<SessionCh> sessions;
    private final Token token;
    private ClientService clientService;

    public MqttClientUser(String broker, Token token,
            MqttConnectOptions opts) throws MqttException {
        super(broker, token.getUid());
        this.token = token;
        this.initCallback();
        
        String topic = "/appid/zhangsong/uid/" + token.getUid();
        this.connect(opts);
        this.subscribe(topic);

        sessions = new HashSet<SessionCh>();
    }

    private void initCallback() {
        MqttClientUser client = this;
        super.setCallback(new MqttCallback() {
            @Override
            public void connectionLost(Throwable cause) {
                if (clientService != null) {
                    clientService.removeClient(token.getUid());
                }
            }

            @Override
            public void messageArrived(String topic, MqttMessage message) throws Exception {
                client.trigger(message.getPayload(), null);
            }

            @Override
            public void deliveryComplete(IMqttDeliveryToken token) {

            }
        });
    }

    public void setClientService(ClientService clientService) {
        this.clientService = clientService;
    }
    
    public synchronized void addSession(SessionCh session) {
        for (SessionCh ses : sessions) {
            if (ses.getSessionId().equals(session.getSessionId())) {
                return;
            }
        }
        sessions.add(session);
    }

    public synchronized void removeSession(SessionCh session) {
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

    public synchronized boolean isSessions() {
        return !sessions.isEmpty();
    }

    public synchronized SessionCh[] clearSessions() {
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
    public void publish(Conversation.Type conv, String to, byte[] body) throws IMException {
        String topic = "/appid/zhangsong/uid/" + to;
        MqttMessage message = new MqttMessage(body);
        message.setQos(2);
        try {
            this.publish(topic, message);
        } catch (MqttPersistenceException e) {
            throw new IMException(ErrorCode.MQ_PUBLISH, "MQ Publish", e);
        } catch (MqttException e) {
            throw new IMException(ErrorCode.MQ_PUBLISH, "MQ Publish", e);
        }
    }
}
