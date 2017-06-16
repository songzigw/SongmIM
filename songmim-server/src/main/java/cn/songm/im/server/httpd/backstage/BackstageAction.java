package cn.songm.im.server.httpd.backstage;

import org.springframework.beans.factory.annotation.Autowired;

import cn.songm.im.business.IMException;
import cn.songm.im.business.IMException.ErrorCode;
import cn.songm.im.business.service.TokenService;
import cn.songm.im.server.httpd.HttpAction;
import io.netty.handler.codec.http.HttpHeaders;
import io.netty.handler.codec.http.HttpRequest;

public abstract class BackstageAction extends HttpAction {

    public static final String APPKEY = "SM-Server-Key";
    private static final String NONCE = "SM-Nonce";
    private static final String TIMESTAMP = "SM-Timestamp";
    private static final String SIGNATURE = "SM-Signature";

    @Autowired
    protected TokenService authService;

    public void checkSign(HttpRequest req) throws IMException {
        HttpHeaders headers = req.headers();
        long timestamp = 0;
        try {
            timestamp = Long.parseLong(headers.getAndConvert(TIMESTAMP));
        } catch (Exception e) {
            throw new IMException(ErrorCode.SIGN_FAILURE, "时间戳错误");
        }
        String key = (String) headers.get(APPKEY);
        String nonce = (String) headers.get(NONCE);
        String signature = (String) headers.get(SIGNATURE);
        if (!authService.sign(key, nonce, signature, timestamp)) {
            throw new IMException(ErrorCode.SIGN_FAILURE, "签名失败");
        }
    }
}
