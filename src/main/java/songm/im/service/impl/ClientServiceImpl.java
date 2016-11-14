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

import java.util.HashMap;
import java.util.Map;

import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import songm.im.IMException;
import songm.im.IMException.ErrorCode;
import songm.im.entity.SessionCh;
import songm.im.mqtt.ClientUser;
import songm.im.mqtt.MqttClientUser;
import songm.im.service.ClientService;

@Service("clientService")
public class ClientServiceImpl implements ClientService {

    private Map<String, ClientUser> clientItems = new HashMap<String, ClientUser>();

    @Value("${mqtt.broker}")
    private String broker = "tcp://iot.eclipse.org:1883";
    @Value("${mqtt.qos}")
    private int qos = 2;
    private boolean clearSession = true;

    @Override
    public ClientUser createClient(SessionCh session)  throws IMException {
        MqttClientUser client = (MqttClientUser) getClient(session.getUid());
        if (client != null) {
            client.addSession(session);
            return client;
        }

        MqttConnectOptions connOpts = new MqttConnectOptions();
        connOpts.setCleanSession(clearSession);

        try {
            client = new MqttClientUser(broker, session.getUid());
            client.connect(connOpts);
        } catch (MqttException e) {
            throw new IMException(ErrorCode.MQ_CONNECT, "MQ Connect", e);
        }

        clientItems.put(session.getUid(), client);
        String topic = "/appid/zhangsong/uid/" + session.getUid();
        try {
            client.subscribe(topic);
        } catch (MqttException e) {
            try {
                client.close();
            } catch (MqttException e1) {}
            throw new IMException(ErrorCode.MQ_CONNECT, "MQ Connect", e);
        }
        return client;
    }

    @Override
    public ClientUser getClient(String uid) {
        return clientItems.get(uid);
    }

    @Override
    public void removeClient(String uid)  throws IMException {
        MqttClientUser client = (MqttClientUser) getClient(uid);
        if (client != null) {
            try {
                client.disconnect();
            } catch (MqttException e) {
                throw new IMException(ErrorCode.MQ_DISCONNECT, "MQ Disconnect", e);
            }
            client.clearSessions();
            clientItems.remove(uid);
        }
    }

    @Override
    public void publish(String uid, String topic, byte[] body)  throws IMException {
        MqttClientUser client = (MqttClientUser) this.getClient(uid);
        if (client == null) return;

        topic = "/appid/zhangsong/uid/" + topic;
        MqttMessage message = new MqttMessage(body);
        message.setQos(qos);
        try {
            client.publish(topic, message);
        } catch (MqttException e) {
            throw new IMException(ErrorCode.MQ_PUBLISH, "MQ Publish", e);
        }
    }

}
