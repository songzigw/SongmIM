package cn.songm.im.codec.message;

import java.util.Date;

import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
        String s = "{\"msgId\":10000,\"conv\":\"private\",\"type\":\"text\",\"direction\":\"receive\",\"from\":\"10001\",\"fNick\":\"1001_nick\",\"fAvatar\":\"1001_avatar\",\"to\":\"1002\",\"tNick\":\"1002_nick\",\"tAvatar\":\"1002_avatar\",\"created\":1496717960553,\"updated\":1496717960553,\"jbody\":{\"text\":\"这是一个文本消息\"}}";
        Message msg = JsonUtils.getInstance().fromJson(s, Message.class);
        LOG.info(msg.toString());
    }
}
