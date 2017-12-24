package cn.songm.im.codec.message;

import java.util.Date;

import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.JsonObject;

import cn.songm.im.codec.json.JsonUtilsInit;
import cn.songm.im.codec.model.Conversation.Ctype;
import cn.songm.im.codec.model.Message;
import cn.songm.im.codec.model.Message.Direction;
import cn.songm.im.codec.model.Message.Mtype;
import cn.songm.im.codec.model.TextMessage;
import cn.songm.songmq.core.util.JsonUtils;

@RunWith(JUnit4.class)
public class MessageTest {

    private static final Logger LOG = LoggerFactory.getLogger(MessageTest.class);
    
    @BeforeClass
    public static void setUpBeforeClass() {
        JsonUtilsInit.initialization();
    }
    
    /**
     * 测试文本消息对象转换为Json格式
     */
    @Test
    public void testTextMessageToJson() {
        TextMessage jbody = new TextMessage("这是一个文本消息");
        
        Message msg = new Message();
        msg.setMsgId(10000l);
        msg.setConv(Ctype.PRIVATE);
        msg.setType(Mtype.TEXT);
        msg.setFrom("10001");
        msg.setfNick("1001_nick");
        msg.setfAvatar("1001_avatar");
        msg.setTo("1002");
        msg.settNick("1002_nick");
        msg.settAvatar("1002_avatar");
        msg.setCreated(new Date());
        msg.setUpdated(new Date());
        msg.setDirection(Direction.RECEIVE);
        msg.setJbody(jbody);
        
        LOG.info(JsonUtils.getInstance().toJson(msg));
    }
    
    @Test
    public void testJsonToTextMessage() {
        JsonObject jobj = new JsonObject();
        jobj.addProperty("msgId", 1000);
        jobj.addProperty("conv", Ctype.PRIVATE.getValue());
        jobj.addProperty("type", Mtype.TEXT.getValue());
        jobj.addProperty("direction", Direction.RECEIVE.getValue());
        jobj.addProperty("from", "10001");
        jobj.addProperty("fNick", "1001_nick");
        jobj.addProperty("fAvatar", "1001_avatar");
        jobj.addProperty("to", "1002");
        jobj.addProperty("tNick", "1002_nick");
        jobj.addProperty("tAvatar", "1002_avatar");
        jobj.addProperty("created", 1496717960553l);
        jobj.addProperty("updated", 1496717960553l);
        JsonObject body = new JsonObject();
        body.addProperty("text", "这是一个文本消息");
        jobj.add("jbody", body);
        String s = jobj.toString();
        System.out.println(s);
        Message msg = JsonUtils.getInstance().fromJson(s, Message.class);
        LOG.info(msg.toString());
    }
}
