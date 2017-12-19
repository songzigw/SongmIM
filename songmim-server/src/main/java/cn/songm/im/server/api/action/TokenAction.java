package cn.songm.im.server.api.action;

import org.springframework.stereotype.Component;

import cn.songm.im.codec.IMException;
import cn.songm.im.codec.Result;
import cn.songm.im.codec.Token;
import cn.songm.im.server.api.ApiAction;
import cn.songm.songmq.core.util.JsonUtils;
import io.netty.channel.Channel;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.QueryStringDecoder;

@Component
public class TokenAction extends ApiAction {

    @Override
    public String uri() {
        return Uri.BACKSTAGE_TOKEN.getValue();
    }

    @Override
    public byte[] active(Channel ch, HttpRequest req) throws IMException {
        checkSign(req);

        String appKey = req.headers().getAndConvert(APPKEY);
        QueryStringDecoder decoder = new QueryStringDecoder(req.uri());
        String uid = getParamValue(decoder, "uid");
        String nick = getParamValue(decoder, "nick");
        String avatar = getParamValue(decoder, "avatar");
        Token token = authService.createToken(appKey, uid, nick, avatar);
        Result<Token> res = new Result<Token>();
        res.setData(token);
        return JsonUtils.getInstance().toJsonBytes(res);
    }

}
