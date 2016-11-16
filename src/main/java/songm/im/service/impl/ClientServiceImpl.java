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

import javax.annotation.Resource;

import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import songm.im.IMException;
import songm.im.IMException.ErrorCode;
import songm.im.entity.SessionCh;
import songm.im.mqtt.ClientUser;
import songm.im.mqtt.MqttClientUser;
import songm.im.service.ClientService;
import songm.im.service.SessionService;

@Service("clientService")
public class ClientServiceImpl implements ClientService {

    private Map<String, ClientUser> clientItems = new HashMap<String, ClientUser>();

    @Resource(name = "sessionService")
    private SessionService sessionService;
    @Value("${mqtt.broker}")
    private String broker;
    @Value("${mqtt.qos}")
    private int qos;
    private boolean clearSession = true;

    @Override
    public ClientUser createClient(SessionCh session)  throws IMException {
        MqttClientUser client = (MqttClientUser) getClient(session.getUid());
        if (client != null) {
            client.addSession(session);
            return client;
        }

        MqttConnectOptions opts = new MqttConnectOptions();
        opts.setCleanSession(clearSession);

        try {
            client = new MqttClientUser(broker, session.getToken(), opts);
        } catch (MqttException e) {
            throw new IMException(ErrorCode.MQ_CONNECT, "MQ Connect", e);
        }
        client.setClientService(this);
        client.addSession(session);

        clientItems.put(session.getUid(), client);
        return client;
    }

    @Override
    public ClientUser getClient(String uid) {
        return clientItems.get(uid);
    }

    @Override
    public void removeClient(String uid) {
        MqttClientUser client = (MqttClientUser) getClient(uid);
        if (client != null) {
            if (client.isConnected()) {
                try {
                    client.disconnect();
                } catch (MqttException e) {}
            }
            SessionCh[] sess = client.clearSessions();
            for (SessionCh ses : sess) {
                sessionService.removeSession(ses.getSessionId());
            }
            clientItems.remove(uid);
        }
    }
}
