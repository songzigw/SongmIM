package cn.songm.im.codec;

import java.util.Arrays;

import cn.songm.songmq.core.president.MQProtocol;

public class Protocol implements MQProtocol {

    private static final long serialVersionUID = 7959039455488988479L;

    /** 固定头部19个字节 */
    public static final short HEADER_FIXED = 17;
    
    // 消息头
    /** 版本信息，1个字节 */
    private Version version;
    /** 消息长度 4个字节*/
    private int length;
    /** 序列号 8个字节*/
    private long sequence;
    /** 操作项 4个字节*/
    private Operation operation;
    
    // 消息体
    /** 消息体 */
    private byte[] body;

    public Protocol() {
        this.version = Version.SONGM_IM_1;
        this.sequence = System.currentTimeMillis();
    }
    
    public Protocol(Version v) {
        this.version = v;
    }
    
    public Version getVersion() {
        return version;
    }

    public void setVersion(Version version) {
        this.version = version;
    }

    public int getLength() {
        return length;
    }

    public void setLength(int length) {
        this.length = length;
    }

    public long getSequence() {
        return sequence;
    }

    public void setSequence(long sequence) {
        this.sequence = sequence;
    }

    public Operation getOperation() {
        return operation;
    }

    public void setOperation(Operation operation) {
        this.operation = operation;
    }

    public byte[] getBody() {
        return body;
    }

    public void setBody(byte[] body) {
        this.body = body;
    }

    @Override
    public String toString() {
        StringBuilder str = new StringBuilder();
        str.append(getClass().getSimpleName()).append(" [");
        str.append("version=").append(version);
        str.append(", length=").append(length);
        str.append(", sequence=").append(sequence);
        str.append(", operation=").append(operation);
        str.append(", body=").append(Arrays.toString(body));
        str.append("]");
        return str.toString();
    }
    
    public enum Operation {
        LOGIN_REQ(1),
        LOGIN_RESP(2),
        PING(3),
        PONG(4), BROKER(5), BROACK(6), SEND_REQ(7), SEND_RESP(8), DISCONNECT(9);
        
        private int value;
        
        private Operation(int v) {
            this.value = v;
        }
        
        public int getValue() {
            return value;
        }

        public static Operation instance(int v) {
            for (Operation o : values()) {
                if (o.value == v) {
                    return o;
                }
            }
            throw new IllegalArgumentException(String.format("out of v: %d", v));
        }
    }
}
