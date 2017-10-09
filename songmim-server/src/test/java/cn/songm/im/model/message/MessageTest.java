package cn.songm.im.model.message;

import java.lang.reflect.Field;
import java.util.Date;

import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cn.songm.common.utils.JsonUtils;
import cn.songm.im.model.json.JsonUtilsInit;
import cn.songm.im.model.message.Conversation.Ctype;
import cn.songm.im.model.message.Message.Direction;
import cn.songm.im.model.message.Message.Mtype;

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
        msg.setfAvatar("http://tva1.sinaimg.cn/crop.0.0.200.200.50/006q8Q6bjw8f20zsdem2mj305k05kdfw.jpg");
        msg.setTo("1002");
        msg.settNick("1002_nick");
        msg.settAvatar("//tva1.sinaimg.cn/crop.0.0.200.200.50/006q8Q6bjw8f20zsdem2mj305k05kdfw.jpg");
        msg.setCreated(new Date());
        msg.setUpdated(new Date());
        msg.setDirection(Direction.RECEIVE);
        msg.setJbody(jbody);
        
        LOG.info(JsonUtils.getInstance().toJson(msg));
    }
    
    @Test
    public void testJsonToTextMessage() {
        String s = "{\"conv\":\"private\",\"type\":\"text\",\"from\":\"10001\",\"fNick\":\"张松1\",\"fAvatar\":\"//tva1.sinaimg.cncrop.0.0.200.200.50006q8Q6bjw8f20zsdem2mj305k05kdfw.jpg\",\"to\":\"10002\",\"tNick\":\"张松2\",\"tAvatar\":\"//tva2.sinaimg.cn/crop.0.0.199.199.180/005Zseqhjw1eplix1brxxj305k05kjrf.jpg\",\"jbody\":{\"text\":\"哈爱国\"},\"chId\":\"c4b301fffece26eb-0000b745-00000005-748f192fd7de78d6-27fe6b76\",\"direction\":\"send\"}";
        Message msg = JsonUtils.getInstance().fromJson(s, Message.class);
        LOG.info(msg.toString());
    }
    
    public static void main(String[] args) throws NoSuchFieldException, SecurityException {
        Message msg = new Message();
        Class<?> msgClazz = msg.getClass();
        Field f = msgClazz.getDeclaredField("conv");
        if (f.getType() == String.class) {
            System.out.println(f.getType());
        } else {
            System.out.println(f.getType());
        }
    }
}
