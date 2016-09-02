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

package songm.im.entity;

import java.io.Serializable;

/**
 * 客户端与服务端的通信协议
 *
 * @author  zhangsong
 * @since   0.1, 2016-7-29
 * @version 0.1
 * 
 */
public class Protocol implements Serializable {

    private static final long serialVersionUID = 6913303658057671577L;

    public static final short VERSION = 1;
    public static final short HEADER_LEN = 20;

    private short version;
    private short headerLen;
    private int operation;
    private long sequence;
    private int packetLen;
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
        String text;
        if (body == null) {
            text = "null";
        } else {
            text = new String(body);
        }
        return "Proto{" + "packetLen=" + packetLen + ", headerLen=" + headerLen
                + ", version=" + version + ", operation=" + operation
                + ", sequence=" + sequence + ", body=" + text + '}';
    }
}