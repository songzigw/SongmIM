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

import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;

import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;

/**
 * Mqtt客户端用户
 * 
 * @author zhangsong
 *
 */
public final class MqttClientUser extends MqttClient {

    private final Collection<MqttMessageListener> listeners;

    public MqttClientUser(String serverURI, String userId) throws MqttException {
        super(serverURI, userId);
        listeners = new HashSet<MqttMessageListener>();
        MqttClientUser client = this;
        super.setCallback(new MqttCallback() {

            @Override
            public void connectionLost(Throwable cause) {

            }

            @Override
            public void messageArrived(String topic, MqttMessage message)
                    throws Exception {
                client.trigger(message.getPayload());
            }

            @Override
            public void deliveryComplete(IMqttDeliveryToken token) {

            }

        });
    }

    /**
     * 添加事件监听
     * 
     * @param listener
     */
    public void addListener(MqttMessageListener listener) {
        listeners.add(listener);
    }

    /**
     * 移除事件监听
     * 
     * @param listener
     */
    public void removeListener(MqttMessageListener listener) {
        listeners.remove(listener);
    }

    private void trigger(byte[] payload) {
        Iterator<MqttMessageListener> iter = listeners.iterator();
        while (iter.hasNext()) {
            MqttMessageListener listener = (MqttMessageListener) iter.next();
            listener.onReceived(payload);
        }
    }
}
