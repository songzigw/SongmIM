package cn.songm.im.httpd.jsonp;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import cn.songm.common.utils.JsonUtils;
import cn.songm.im.IMException;
import cn.songm.im.httpd.HttpAction;
import cn.songm.im.model.ChLongPolling;
import cn.songm.im.model.Result;
import cn.songm.im.model.Session;
import cn.songm.im.model.SessionCh;
import cn.songm.im.model.message.Message;
import cn.songm.im.service.TokenService;
import io.netty.channel.Channel;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.QueryStringDecoder;

@Component
public class PollingAction extends JsonpAction {

    private static final long TIME_OUT = 30 * 1000;

    @Autowired
    private TokenService tokenService;

    @Override
    public String uri() {
        return Uri.POLLING_LONG.getValue();
    }

    @Override
    public byte[] active(Channel ch, HttpRequest req) throws IMException {
        QueryStringDecoder decoder = new QueryStringDecoder(req.uri());
        String chId = getParamValue(decoder, "chId");
        String token = getParamValue(decoder, "token");
        String session = getParamValue(decoder, "session");
        String callback = getParamValue(decoder, "callback");

        Result<Session> res = new Result<Session>();
        SessionCh ses = null;
        ChLongPolling clp = new ChLongPolling(chId);
        // 连接成功
        ses = tokenService.online(token, session, clp);
        res.setData(ses);

        // 第一次连接成功
        if (ses.isFirstConn(chId)) {
            ses.setAttribute("ch_id", clp.getChId());
            return HttpAction.callback(callback, res);
        }

        // 获取消息
        long start = System.currentTimeMillis();
        ChLongPolling chLp = ses.getChannel(chId);
        Result<Message> resMsg = new Result<Message>();
        byte[] message = null;
        do {
            message = chLp.getMessage();
            if (message != null) {
                Message msg = JsonUtils.fromJson(message, Message.class);
                resMsg.setData(msg);
                break;
            }
            if (System.currentTimeMillis() - start > TIME_OUT) {
                break;
            }
        } while (true);

        return HttpAction.callback(callback, resMsg);
    }

}
