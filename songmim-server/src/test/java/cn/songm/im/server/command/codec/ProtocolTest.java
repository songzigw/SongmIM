package cn.songm.im.server.command.codec;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import cn.songm.im.server.command.codec.Protocol.Operation;

@RunWith(JUnit4.class)
public class ProtocolTest {

    @Test
    public void testToString() {
	Protocol pro = new Protocol();
	pro.setOperation(Operation.CONNECT_REQ);
	String body = "你好啊";
	pro.setBody(body.getBytes());
	pro.setLength();
	System.out.println(pro.toString());
    }
}
