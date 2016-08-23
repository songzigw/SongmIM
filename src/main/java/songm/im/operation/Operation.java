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
package songm.im.operation;

import io.netty.channel.Channel;
import songm.im.entity.Protocol;

public interface Operation {

    public int operation();

    void action(Channel ch, Protocol pro);

    public static enum Type {
        /** 连接请求 */
        CONN_REQUEST(1),
        /** 连接关闭 */
        CONN_CLOSE(2),
        
        /** 消息发生 */
        MESSAGE(3),
        /** 消息从客户端发送过来 */
        MSG_SEND(4);
        
        private final int value;
        
        private Type(int value) {
            this.value = value;
        }

        public int getValue() {
            return value;
        }
        
        public static Type getInstance(int v) {
            for (Type type : Type.values()) {
                if (type.getValue() == v) {
                    return type;
                }
            }
            return null;
        }
    }
}
