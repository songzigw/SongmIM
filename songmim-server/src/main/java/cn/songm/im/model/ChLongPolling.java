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
package cn.songm.im.model;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import cn.songm.im.model.message.Message;

/**
 * 长轮询管道
 * 
 * @author zhangsong
 *
 */
public class ChLongPolling extends IMChannel {

    private List<Message> queue;

    private String chId;

    public ChLongPolling(String chId) {
        this.chId = chId;
        queue = Collections.synchronizedList(new LinkedList<Message>());
    }

    public String getChId() {
        return chId;
    }

    public void setChId(String chId) {
        this.chId = chId;
    }

    public void addMessage(Message message) {
        queue.add(message);
    }

    public Message getMessage() {
        if (queue.size() == 0) {
            return null;
        }
        return queue.remove(0);
    }

    public void clearMessage() {
        queue.clear();
    }
}
