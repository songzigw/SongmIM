package cn.songm.im.server.handler.actioner;

import java.lang.reflect.Type;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.google.gson.reflect.TypeToken;

import cn.songm.im.codec.Protocol;
import cn.songm.im.codec.Protocol.Operation;
import cn.songm.im.codec.Result;
import cn.songm.im.codec.Session;
import cn.songm.im.codec.model.Message;
import cn.songm.songmq.core.CallbackInvoker;
import cn.songm.songmq.core.CallbackListener;
import cn.songm.songmq.core.CallbackManager;
import cn.songm.songmq.core.util.JsonUtils;
import io.netty.channel.Channel;

/**
 * Broker消息推送
 * @author zhangsong
 *
 */
@Component
public class BrokerPushActioner implements Actioner {

    private final static Logger log = LoggerFactory.getLogger(BrokerPushActioner.class);
    
    @Override
    public int operation() {
        return Operation.BROACK.getValue();
    }

    @Override
    public void action(Channel ch, Protocol pro, Session ses) {
        log.debug("response: {}, {}", pro.getOperation(), ch);
        Type type = new TypeToken<Result<Message>>() {}.getType();
        Result<Message> res = JsonUtils.getInstance().fromJson(pro.getBody(), type);
        // 触发事件
        CallbackManager.getInstance().trigger(pro.getSequence(), res);
    }

    public static void push(Channel ch, Message message) {
        Protocol pro = new Protocol();
        pro.setOperation(Operation.BROKER);
        pro.setBody(JsonUtils.getInstance().toJsonBytes(message));
        // 定义事件
        CallbackInvoker invoker = new CallbackInvoker(Long.toString(pro.getSequence()));
        // 事件监听器
        invoker.join(new CallbackListener() {
            @Override
            public void onCallBack(Object t) {
                System.out.println(t);
            }
        });
        // 注册事件
        CallbackManager.getInstance().put(invoker);
        log.debug("request: {}, {}", pro.getOperation(), ch);
        ch.writeAndFlush(pro);
    }
}
