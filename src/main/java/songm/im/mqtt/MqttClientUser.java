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

import io.netty.channel.Channel;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;

import songm.im.entity.SessionCh;

/**
 * Mqtt客户端用户
 * 
 * @author zhangsong
 *
 */
public final class MqttClientUser extends MqttClient implements ClientUser {

    private final Set<SessionCh> sessions;

    public MqttClientUser(String serverURI, String userId) throws MqttException {
        super(serverURI, userId);
        sessions = new HashSet<SessionCh>();

        MqttClientUser client = this;
        super.setCallback(new MqttCallback() {

            @Override
            public void connectionLost(Throwable cause) {

            }

            @Override
            public void messageArrived(String topic, MqttMessage message)
                    throws Exception {
                client.trigger(message.getPayload(), null);
            }

            @Override
            public void deliveryComplete(IMqttDeliveryToken token) {

            }

        });
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
    
    public synchronized void clearSessions() {
        Iterator<SessionCh> iter = sessions.iterator();
        while (iter.hasNext()) {
            SessionCh session = (SessionCh) iter.next();
            session.clearCh();
        }
        sessions.clear();
    }
}
