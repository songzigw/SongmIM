package cn.songm.im.business;

import java.util.Date;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicInteger;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import cn.songm.im.business.mqueue.ClientUser;
import cn.songm.im.business.mqueue.SongMQClient;
import cn.songm.im.model.Token;
import cn.songm.im.model.json.JsonUtilsInit;
import cn.songm.im.model.message.Conversation.Ctype;
import cn.songm.im.model.message.Message;
import cn.songm.im.model.message.Message.Direction;
import cn.songm.im.model.message.Message.Mtype;
import cn.songm.im.model.message.MessageContent;
import cn.songm.im.model.message.TextMessage;
import cn.songm.songmq.core.MessageEvent;
import cn.songm.songmq.core.MessageEventManager;
import junit.framework.Assert;

@RunWith(JUnit4.class)
public class ClientUserTest {

    private static final String APP_ID = "haiemlrnzdjz";

    private ClientUser user1;
    private ClientUser user2;
    private Token token1;
    private Token token2;
    
    public ClientUserTest() {
        token1 = new Token("1001", "zhangs1", "/a/b/c1" , APP_ID);
        token2 = new Token("1002", "zhangs2", "/a/b/c2" , APP_ID);
    }

    @BeforeClass
    public static void beforeClass() {
        JsonUtilsInit.initialization();
    }

    @AfterClass
    public static void afterClass() {
        MessageEventManager.getInstance().getExecutor().shutdown();
    }

    @Before
    public void setUp() throws Exception {
        
    }

    @After
    public void tearDown() throws Exception {
        
    }
    
    private static final int MSG_COUNT = 3;
    // 发送方收到自己发送的消息
    private Message msg1 = null;
    // 接收方收到发送方的消息
    private Message msg2 = null;
    // 接收方收到的未读消息数
    private Message msg3 = null;
    // 服务器接收一次消息，服务器对外发送消息次数
    private AtomicInteger msgCount;
    private CountDownLatch latch;
    {
        latch = new CountDownLatch(MSG_COUNT);
        msgCount = new AtomicInteger(0);
    }
    
    @Test
    public void testPublishUser1ToUser2() {
        String text = "Message: User1 to User2.";
        MessageContent jbody = new TextMessage(text);
        Message message = new Message();
        message.setChId("channel1");
        message.setDirection(Direction.SEND);
        message.setConv(Ctype.PRIVATE);
        message.setFrom(token1.getUid());
        message.setfNick(token1.getNick());
        message.setfAvatar(token1.getAvatar());
        message.setTo(token2.getUid());
        message.settNick(token2.getNick());
        message.settAvatar(token2.getAvatar());
        message.setType(Mtype.TEXT);
        message.setCreated(new Date());
        message.setJbody(jbody);
        
        // 发送方客户端
        user1 = new SongMQClient(token1) {
            @Override
            public void onMessage(MessageEvent event) {
                super.onMessage(event);
                msg1 = (Message) event.getSource();
                msgCount.incrementAndGet();
                latch.countDown();
            }
        };
        
        // 接收方客户端
        user2 = new SongMQClient(token2) {
            @Override
            public void onMessage(MessageEvent event) {
                super.onMessage(event);
                Message m = (Message) event.getSource();
                switch (m.getType()) {
                    case TEXT:
                        msg2 = m;
                        break;
                    case UNREAD:
                        msg3 = m;
                        break;
                    default:
                        break;
                }
                msgCount.incrementAndGet();
                latch.countDown();
            }
        };
        
        user1.publish(token2.getUid(), message);
        
        try {
            latch.await();
        } catch (InterruptedException e) {
        }
        user1.unsubscribe(message.getConv(), token1.getUid());
        user2.unsubscribe(message.getConv(), token2.getUid());
        assertMessage(message);
    }
    
    @Test
    public void testPublishUser2ToUser1() {
        String text = "Message: User2 to User1.";
        MessageContent jbody = new TextMessage(text);
        Message message = new Message();
        message.setChId("channel1");
        message.setDirection(Direction.SEND);
        message.setConv(Ctype.PRIVATE);
        message.setFrom(token2.getUid());
        message.setfNick(token2.getNick());
        message.setfAvatar(token2.getAvatar());
        message.setTo(token1.getUid());
        message.settNick(token1.getNick());
        message.settAvatar(token1.getAvatar());
        message.setType(Mtype.TEXT);
        message.setCreated(new Date());
        message.setJbody(jbody);
        
        // 发送方客户端
        user2 = new SongMQClient(token2) {
            @Override
            public void onMessage(MessageEvent event) {
                super.onMessage(event);
                msg1 = (Message) event.getSource();
                msgCount.incrementAndGet();
                latch.countDown();
            }
        };
        
        // 接收方客户端
        user1 = new SongMQClient(token1) {
            @Override
            public void onMessage(MessageEvent event) {
                super.onMessage(event);
                Message m = (Message) event.getSource();
                switch (m.getType()) {
                    case TEXT:
                        msg2 = m;
                        break;
                    case UNREAD:
                        msg3 = m;
                        break;
                    default:
                        break;
                }
                msgCount.incrementAndGet();
                latch.countDown();
            }
        };
        
        user2.publish(token1.getUid(), message);
        
        try {
            latch.await();
        } catch (InterruptedException e) {
        }
        user1.unsubscribe(message.getConv(), token1.getUid());
        user2.unsubscribe(message.getConv(), token2.getUid());
        assertMessage(message);
    }
    
    private void assertMessage(Message message) {
        Assert.assertTrue(msgCount.get() == MSG_COUNT);
        
        Assert.assertEquals(message.getConv(), msg1.getConv());
        Assert.assertEquals(message.getType(), msg1.getType());
        Assert.assertEquals(message.getChId(), msg1.getChId());
        Assert.assertEquals(message.getDirection(), msg1.getDirection());
        Assert.assertEquals(message.getFrom(), msg1.getFrom());
        Assert.assertEquals(message.getFrom(), msg1.getFrom());
        Assert.assertEquals(message.getfNick(), msg1.getfNick());
        Assert.assertEquals(message.getfAvatar(), msg1.getfAvatar());
        Assert.assertEquals(message.getCreated(), msg1.getCreated());
        Assert.assertEquals(message.getJbody(), msg1.getJbody());
        
        Assert.assertEquals(message.getType(), msg2.getType());
        Assert.assertEquals(message.getConv(), msg2.getConv());
        Assert.assertEquals(message.getChId(), msg2.getChId());
        Assert.assertEquals(Direction.RECEIVE, msg2.getDirection());
        Assert.assertEquals(message.getFrom(), msg2.getFrom());
        Assert.assertEquals(message.getFrom(), msg2.getFrom());
        Assert.assertEquals(message.getfNick(), msg2.getfNick());
        Assert.assertEquals(message.getfAvatar(), msg2.getfAvatar());
        Assert.assertEquals(message.getCreated(), msg2.getCreated());
        Assert.assertEquals(message.getJbody(), msg2.getJbody());
        
        Assert.assertEquals(Mtype.UNREAD, msg3.getType());
        Assert.assertEquals(message.getConv(), msg3.getConv());
        // 由服务器内部产生的消息没有ChannelID，所有比较没有意义。
        // Assert.assertEquals(message.getChId(), msg3.getChId());
        Assert.assertEquals(Direction.RECEIVE, msg3.getDirection());
        Assert.assertEquals(message.getFrom(), msg3.getFrom());
        Assert.assertEquals(message.getFrom(), msg3.getFrom());
        Assert.assertEquals(message.getfNick(), msg3.getfNick());
        Assert.assertEquals(message.getfAvatar(), msg3.getfAvatar());
    }
}
