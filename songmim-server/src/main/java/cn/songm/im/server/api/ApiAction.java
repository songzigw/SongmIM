package cn.songm.im.server.api;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import cn.songm.im.codec.IMException;
import cn.songm.im.codec.IMException.ErrorCode;
import cn.songm.im.codec.Result;
import cn.songm.im.service.TokenService;
import cn.songm.songmq.core.util.JsonUtils;
import io.netty.channel.Channel;
import io.netty.handler.codec.http.HttpHeaders;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.QueryStringDecoder;

public abstract class ApiAction {

    public static final String APPKEY = "SM-Server-Key";
    private static final String NONCE = "SM-Nonce";
    private static final String TIMESTAMP = "SM-Timestamp";
    private static final String SIGNATURE = "SM-Signature";

    @Autowired
    protected TokenService authService;
    @Value("${is.check.sign}")
    private boolean isCheckSign;

    public void checkSign(HttpRequest req) throws IMException {
        if (!isCheckSign) return;
        
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
    
    public abstract String uri();

    public abstract byte[] active(Channel ch, HttpRequest req)
            throws IMException;

    public String getParamValue(QueryStringDecoder decoder, String param) {
        if (decoder.parameters().get(param) != null
                && !decoder.parameters().get(param).isEmpty()) {
            return decoder.parameters().get(param).get(0);
        }
        return null;
    }

    public static enum Uri {
        BACKSTAGE_TOKEN("/token");

        private final String value;

        private Uri(String value) {
            this.value = value;
        }

        public String getValue() {
            return this.value;
        }

        public static Uri getInstance(String v) {
            for (Uri uri : Uri.values()) {
                if (uri.getValue() == v) {
                    return uri;
                }
            }
            return null;
        }
    }

    public static <T> byte[] callback(String callback, Result<T> result) {
        return (callback + "(" + JsonUtils.getInstance().toJson(result)
                + ")").getBytes();
    }
}
