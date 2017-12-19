package cn.songm.im.server.handler.actioner;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import cn.songm.im.codec.IMException.ErrorCode;
import cn.songm.im.codec.Protocol;
import cn.songm.im.codec.Protocol.Operation;
import cn.songm.im.codec.Result;
import cn.songm.im.codec.Session;
import cn.songm.im.codec.model.Message;
import cn.songm.im.service.ClientService;
import cn.songm.im.service.mqueue.ClientUser;
import cn.songm.songmq.core.util.JsonUtils;
import io.netty.channel.Channel;

@Component
public class SendResponseActioner implements Actioner {

    private final Logger log = LoggerFactory.getLogger(getClass());
    
    @Autowired
    private ClientService clientService;
    
    @Override
    public int operation() {
        return Operation.SEND_REQ.getValue();
    }

    @Override
    public void action(Channel ch, Protocol pro, Session ses) {
        log.debug("response: {}, {}", pro.getOperation(), ch);
        Message msg = JsonUtils.getInstance().fromJson(pro.getBody(), Message.class);
        
        // 发送应答
        Result<Message> res = new Result<Message>();
        Protocol resp = new Protocol();
        resp.setOperation(Operation.SEND_RESP);

        if (!msg.getFrom().equals(ses.getUid())) {
            res.setErrorCode(ErrorCode.SOURCE_INVALID.getCode());
            res.setMessage("消息来源无效");
            ch.writeAndFlush(resp);
            return;
        }

        // 发送消息
        ClientUser fromUser = clientService.getClient(ses.getUid());
        fromUser.publish(msg.getTo(), msg);
        
        res.setData(msg);
        resp.setBody(JsonUtils.getInstance().toJsonBytes(res));
        ch.writeAndFlush(resp);
        log.debug("response: {}, {}", pro.getOperation(), ch);
    }

}
