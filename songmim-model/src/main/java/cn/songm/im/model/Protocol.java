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

import cn.songm.songmq.core.president.MQProtocol;

/**
 * 客户端与服务端的通信协议
 *
 * @author zhangsong
 * @since 0.1, 2017-2-25
 * @version 0.1
 * 
 */
public class Protocol implements MQProtocol {

    private static final long serialVersionUID = -6682442257464565580L;

    public static final short VERSION = 1;
    public static final short HEADER_LEN = 20;

    /** 协议版本号 */
    private short version;
    /** 包头长度 */
    private short headerLen;
    /** 包体长度 */
    private int packetLen;
    /** 序列号 */
    private long sequence;
    /** 操作项 */
    private int operation;
    /** 包体部分 */
    private byte[] body;

    public int getPacketLen() {
        return packetLen;
    }

    public void setPacketLen(int packetLen) {
        this.packetLen = packetLen;
    }

    public short getHeaderLen() {
        return headerLen;
    }

    public void setHeaderLen(short headerLen) {
        this.headerLen = headerLen;
    }

    public short getVersion() {
        return version;
    }

    public void setVersion(short version) {
        this.version = version;
    }

    public int getOperation() {
        return operation;
    }

    public void setOperation(int operation) {
        this.operation = operation;
    }

    public long getSequence() {
        return sequence;
    }

    public void setSequence(long sequence) {
        this.sequence = sequence;
    }

    public byte[] getBody() {
        return body;
    }

    public void setBody(byte[] body) {
        this.body = body;
    }

    @Override
    public String toString() {
        return String.format(
                "Protocol [version=%d, headerLen=%d, packetLen=%d, sequence=%d, operation=%d, body=%s]",
                version, headerLen, packetLen, sequence, operation,
                (body == null ? "null" : new String(body)));
    }
    
    public static enum Operation {
        /** 连接授权 */
        CONN_AUTH(1),
        /** 连接关闭 */
        CONN_CLOSE(2),
        
        /** 客户端发起心跳 */
        HEARTBEAT(3),
        
        /** 发布消息到服务端 */
        PUBLISH_MSG(4),

        /** 转发消息到客户端 */
        BROKER_MSG(5),
        
        ;

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
