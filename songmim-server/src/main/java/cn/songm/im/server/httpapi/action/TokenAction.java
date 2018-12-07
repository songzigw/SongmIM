package cn.songm.im.server.httpapi.action;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import cn.songm.im.codec.IMException;
import cn.songm.im.codec.Result;
import cn.songm.im.codec.Token;
import cn.songm.im.server.httpapi.ApiAction;
import cn.songm.im.server.httpapi.RequestParam;
import cn.songm.songmq.core.util.JsonUtils;
import io.netty.channel.Channel;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.HttpRequest;

@Component
public class TokenAction extends ApiAction {

    @Value("${songmim.server.key}")
    private String serverKey;
    
    @Override
    public String uri() {
        return Uri.BACKSTAGE_TOKEN.getValue();
    }

    @Override
    public byte[] active(Channel ch, HttpRequest req) throws IMException {
        checkSign(req);

        RequestParam param = new RequestParam((FullHttpRequest)req);
        String uid = param.getParameter("uid");
        String nick = param.getParameter("nick");
        String avatar = param.getParameter("avatar");
        
        String appKey = req.headers().getAndConvert(APPKEY, serverKey);
        Token token = authService.createToken(appKey, uid, nick, avatar);
        Result<Token> res = new Result<Token>();
        res.setData(token);
        return JsonUtils.getInstance().toJsonBytes(res);
    }

}
