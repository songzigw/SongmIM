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
package songm.im.server;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * 长轮询管道
 * @author zhangsong
 *
 */
public class ChannelLongPolling extends IMChannel {

    private List<byte[]> queue;

    private String chId;

    public ChannelLongPolling(String chId) {
        queue = Collections.synchronizedList(new LinkedList<byte[]>());
        this.chId = chId;
    }
    
    public String getChId() {
        return chId;
    }

    public void setChId(String chId) {
        this.chId = chId;
    }

    public void addResMsg(byte[] msg) {
        queue.add(msg);
    }
    
    public byte[] getResMsg() {
        if (queue.size() == 0) {
            return null;
        }
        return queue.remove(0);
    }
    
    public void clearResMsg() {
        queue.clear();
    }
}
