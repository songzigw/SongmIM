package cn.songm.im.server.command.codec;

import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Array;
import java.lang.reflect.Field;

import cn.songm.songmq.core.president.MQProtocol;

public class Protocol implements MQProtocol {

    private static final long serialVersionUID = 4633085203462490782L;

    /** 固定头部19个字节 */
    public static final short HEADER_FIXED = 16;
    
    // 消息头
    /** 消息长度 4个字节 */
    private int length;
    /** 序列号 8个字节 */
    private long sequence;
    /** 操作项 4个字节 */
    private Operation operation;

    // 消息体
    private byte[] body;

    public Protocol() {
	this.sequence = System.currentTimeMillis();
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

    public byte[] getBody() {
	return body;
    }

    public void setBody(byte[] body) {
	this.body = body;
    }

    public Operation getOperation() {
        return operation;
    }

    public void setOperation(Operation operation) {
        this.operation = operation;
    }

    public void setLength() {
	if (body != null) {
	    length = Protocol.HEADER_FIXED + body.length;
	} else {
	    length = Protocol.HEADER_FIXED;
	}
    }
    
    @Override
    public String toString() {
	try {
	    return toString(this);
	} catch (IllegalArgumentException e) {
	    
	} catch (IllegalAccessException e) {
	    
	}
	return null;
    }
    
    public static String toString(Object obj) throws IllegalArgumentException, IllegalAccessException {
	if (obj == null) return null;
	
	Class<?> cla = obj.getClass();
	if (cla.isEnum() || cla == Integer.class || cla == Short.class || cla == Byte.class || cla == Long.class || cla == Double.class
		|| cla == Float.class || cla == Boolean.class || cla == String.class || cla == Character.class) {
	    return obj.toString();
	}

	StringBuilder str = new StringBuilder();	
	if (cla.isArray()) {
	    str.append("[");
	    for (int i = 0; i < Array.getLength(obj); i++) {
		if (i > 0) str.append(", ");
		Object val = Array.get(obj, i);
		
		if (val != null && !val.equals("")) {
		    str.append(toString(val));
		}
	    }
	    str.append("]");
	    return str.toString();
	}
	
	Field[] fields = cla.getDeclaredFields();
	AccessibleObject.setAccessible(fields, true);
	str.append("[");
	for (int i = 0; i < fields.length; i++) {
	    if (i > 0) str.append(", ");
	    Field f = fields[i];
	    str.append(f.getName()).append("=");
	    if (!f.getType().isPrimitive()) {
		str.append(toString(f.get(obj)));
	    } else {
		str.append(f.get(obj));
	    }
	}
	str.append("]");
	
	return str.toString();
    }
    
    public enum Operation {
	CONNECT_REQ(1),
	CONNECT_ACK(2),
	PING(3), PONG(4),
	DISCONNECT(5);
	
	private int value;
	
	private Operation(int v) {
	    this.value = v;
	}
	
	public int getValue() {
	    return value;
	}
	
	public static Operation instance(int v) {
	    for (Operation o : values()) {
		if (o.value == v) return o;
	    }
	    throw new IllegalArgumentException(String.format("out of v: %d", v));
	}
	
	@Override
	public String toString() {
	    return new StringBuilder(super.toString()).append("(").append(value).append(")").toString();
	}
    }
}
