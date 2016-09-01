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
package songm.im.service;

import songm.im.mqtt.MqttClientUser;
import songm.im.mqtt.MqttMessageListener;

/**
 * MQTT客户端业务逻辑处理
 * 
 * @author zhangsong
 *
 */
public interface MqttClientService {

    /**
     * 创建MQTT客户端
     * 
     * @param uid
     * @param listener
     * @return
     */
    public MqttClientUser createClient(String uid, MqttMessageListener listener);

    /**
     * 获取MQTT客户端
     * 
     * @param uid
     * @return
     */
    public MqttClientUser getClient(String uid);

    /**
     * 断开连接
     * 
     * @param uid
     */
    public void disconnect(String uid);

    /**
     * 发布消息
     * 
     * @param uid
     * @param topic
     * @param body
     */
    public void publish(String uid, String topic, byte[] body);
}
