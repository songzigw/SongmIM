package cn.songm.im.httpd.backstage;

import org.springframework.stereotype.Component;

import cn.songm.common.utils.JsonUtils;
import cn.songm.im.IMException;
import cn.songm.im.model.Result;
import cn.songm.im.model.Token;
import io.netty.channel.Channel;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.QueryStringDecoder;

@Component
public class TokenAction extends BackstageAction {

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
        return JsonUtils.toJsonBytes(res, res.getClass());
    }

}
