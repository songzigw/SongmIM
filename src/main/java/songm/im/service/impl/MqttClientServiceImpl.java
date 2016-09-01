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

import songm.im.mqtt.MqttClientUser;
import songm.im.mqtt.MqttMessageListener;
import songm.im.service.MqttClientService;

@Service("mqttClientService")
public class MqttClientServiceImpl implements MqttClientService {

    private Map<String, MqttClientUser> clientItems = new HashMap<String, MqttClientUser>();

    @Value("${mqtt.broker}")
    private String broker = "tcp://iot.eclipse.org:1883";
    @Value("${mqtt.qos}")
    private int qos = 2;

    @Override
    public MqttClientUser createClient(String uid, MqttMessageListener listener) {
        MqttClientUser client = getClient(uid);
        if (client != null) {
            client.addListener(listener);
            return client;
        }

        MqttConnectOptions connOpts = new MqttConnectOptions();
        connOpts.setCleanSession(true);

        try {
            client = new MqttClientUser(broker, uid);
            client.addListener(listener);
            client.connect(connOpts);
        } catch (MqttException e) {
            e.printStackTrace();
        }

        clientItems.put(uid, client);
        return client;
    }

    @Override
    public MqttClientUser getClient(String uid) {
        return clientItems.get(uid);
    }

    @Override
    public void disconnect(String uid) {
        MqttClientUser client = this.getClient(uid);
        if (client != null) {
            try {
                client.disconnect();
            } catch (MqttException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void publish(String uid, String topic, byte[] body) {
        MqttClientUser client = this.getClient(uid);
        if (client == null) {
            return;
        }

        MqttMessage message = new MqttMessage(body);
        message.setQos(qos);
        try {
            client.publish(topic, message);
        } catch (MqttException e) {
            e.printStackTrace();
        }
    }
}
