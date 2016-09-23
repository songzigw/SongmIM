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
package songm.im.handler;

import io.netty.channel.Channel;
import songm.im.IMException;
import songm.im.entity.Protocol;

/**
 * 消息事件操作
 * 
 * @author zhangsong
 *
 */
public interface Handler {

    public int operation();

    void action(Channel ch, Protocol pro) throws IMException;

    public static enum Operation {
        /** 连接请求（授权） */
        CONN_AUTH(1),
        /** 连接关闭 */
        CONN_CLOSE(2),

        /** 消息发生 */
        MESSAGE(3),
        /** 消息从客户端发送过来 */
        MSG_SEND(4);

        private final int value;

        private Operation(int value) {
            this.value = value;
        }

        public int getValue() {
            return value;
        }

        public static Operation getInstance(int v) {
            for (Operation op : Operation.values()) {
                if (op.getValue() == v) {
                    return op;
                }
            }
            return null;
        }
    }
}
