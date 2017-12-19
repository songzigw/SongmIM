package cn.songm.im.client;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;

import cn.songm.im.client.handler.BrokerAckHandler;
import cn.songm.im.client.handler.HeartbeatRequestHandler;
import cn.songm.im.client.handler.LoginAuthRequestHandler;
import cn.songm.im.client.handler.SendRequestHandler;
import cn.songm.im.codec.AckListener;
import cn.songm.im.codec.Codec;
import cn.songm.im.codec.IMException.ErrorCode;
import cn.songm.im.codec.Result;
import cn.songm.im.codec.Session;
import cn.songm.im.codec.json.JsonUtilsInit;
import cn.songm.im.codec.model.Conversation.Ctype;
import cn.songm.im.codec.model.Message;
import cn.songm.im.codec.model.Message.Direction;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.timeout.ReadTimeoutHandler;

public class IMClient {

    private final Logger log = LoggerFactory.getLogger(getClass());

    private LoginAuthRequestHandler loginAuthRequestHandler;
    private HeartbeatRequestHandler heartbeatRequestHandler;
    private SendRequestHandler sendRequestHandler;
    private BrokerAckHandler brokerAckHandler;
    private String host;
    private int port;
    
    private final Session session;
    private IMCallback callback;
    
    public IMClient(String host, int port) {
        JsonUtilsInit.initialization();
        this.session = new Session();
        loginAuthRequestHandler = new LoginAuthRequestHandler(this);
        heartbeatRequestHandler = new HeartbeatRequestHandler();
        sendRequestHandler = new SendRequestHandler();
        brokerAckHandler = new BrokerAckHandler(this);
        this.host = host;
        this.port = port;
        this.init();
    }
    
    public IMClient(String host, int port, IMCallback callback) {
        this(host, port);
        setCallback(callback);
    }
    
    public Session getSession() {
        return session;
    }

    public IMCallback getCallback() {
        return callback;
    }

    public void setCallback(IMCallback callback) {
        this.callback = callback;
    }
    
    public boolean isConnected() {
        return loginAuthRequestHandler.isConnected();
    }

    private Bootstrap b;
    private EventLoopGroup g;
    private ChannelFuture f;
    
    private void init() {
        g = new NioEventLoopGroup();
        b = new Bootstrap();
        b.group(g).channel(NioSocketChannel.class);
        b.remoteAddress(host, port);
        b.handler(new ChannelInitializer<SocketChannel>() {
            @Override
            protected void initChannel(SocketChannel ch) throws Exception {
                ch.pipeline().addLast("codec", new Codec());
                ch.pipeline().addLast("readTimeoutHandler", new ReadTimeoutHandler(50));
                // 登入授权验证
                ch.pipeline().addLast("loginAuthRequestHandler", loginAuthRequestHandler);
                // 心跳检测机制
                ch.pipeline().addLast("heartbeatRequestHandler", heartbeatRequestHandler);
                ch.pipeline().addLast("sendRequestHandler", sendRequestHandler);
                ch.pipeline().addLast("brokerAckHandler", brokerAckHandler);
            }
        });
    }
    
    public void connect(String tokenId, String uid) throws InterruptedException {
        session.setTokenId(tokenId);
        session.setUid(uid);
        // 发起异步连接操作
        f = b.connect().sync();
        Result<Session> r = loginAuthRequestHandler.loginAuth(f.channel());
        if (r.getErrorCode() != ErrorCode.OK.getCode()) {
            throw new InterruptedException(String.format("error: %d->%s", r.getErrorCode(), r.getMessage()));
        }
        BeanUtils.copyProperties(r.getData(), session);
        log.info("Client -> {}:{} started.", host, port);
        
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    f.channel().closeFuture().sync();
                } catch (InterruptedException e) {
                    log.error(e.getMessage(), e);
                } finally {
                    g.shutdownGracefully();
                    log.info("Client -> {}:{} stopped.", host, port);
                }
            }
        }, "imClient-" + session.getUid()).start();
    }
    
    public void send(Message msg, AckListener<Message> listener) {
        msg.setConv(Ctype.PRIVATE);
        msg.setDirection(Direction.SEND);
        msg.setFrom(session.getUid());
        msg.setfNick(session.getToken().getNick());
        msg.setfAvatar(session.getToken().getAvatar());
        sendRequestHandler.buildMessage(f.channel(), msg, listener);
    }
    
}
