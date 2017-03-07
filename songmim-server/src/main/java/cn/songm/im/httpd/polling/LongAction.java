package cn.songm.im.httpd.polling;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import cn.songm.common.utils.JsonUtils;
import cn.songm.im.IMException;
import cn.songm.im.model.ChLongPolling;
import cn.songm.im.model.Result;
import cn.songm.im.model.Session;
import cn.songm.im.model.SessionCh;
import cn.songm.im.service.TokenService;
import io.netty.channel.Channel;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.QueryStringDecoder;

@Component
public class LongAction extends PollingAction {

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
        try {
            // 连接成功
            ses = tokenService.online(token, session, clp);
            res.setData(ses);
        } catch (IMException e) {
            // 连接失败
            res.setErrorCode(e.getErrorCode().name());
            res.setErrorDesc(e.getDescription());
            return (callback + "(" + JsonUtils.toJson(res, res.getClass())
                    + ")").getBytes();
        }

        // 第一次连接成功
        if (ses.isFirstConn(chId)) {
            ses.setAttribute("ch_id", clp.getChId());
            return (callback + "(" + JsonUtils.toJson(res, res.getClass())
                    + ")").getBytes();
        }

        // 获取消息
        long start = System.currentTimeMillis();
        ChLongPolling chLp = ses.getChannel(chId);
        byte[] resMsg = null;
        do {
            resMsg = chLp.getResMsg();
            if (resMsg != null) {
                break;
            }
            if (System.currentTimeMillis() - start > TIME_OUT) {
                Result<Object> m = new Result<Object>();
                // 返回空消息，客户端不做任何处理
                resMsg = JsonUtils.toJsonBytes(m, m.getClass());
                break;
            }
        } while (true);

        return (callback + "(" + new String(resMsg) + ")").getBytes();
    }

}
