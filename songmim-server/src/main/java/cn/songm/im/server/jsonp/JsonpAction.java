package cn.songm.im.server.jsonp;

import org.springframework.beans.factory.annotation.Autowired;

import cn.songm.im.codec.IMException;
import cn.songm.im.codec.IMException.ErrorCode;
import cn.songm.im.codec.Result;
import cn.songm.im.codec.Session;
import cn.songm.im.service.SessionService;
import cn.songm.songmq.core.util.JsonUtils;
import io.netty.channel.Channel;
import io.netty.handler.codec.http.HttpHeaders;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.QueryStringDecoder;

public abstract class JsonpAction {

    @Autowired
    protected SessionService sessionService;

    public void checkSession(HttpRequest req) throws IMException {
        HttpHeaders headers = req.headers();
        String sesId = headers.getAndConvert("session");
        Session ses = sessionService.getSession(sesId);
        if (ses == null) {
            throw new IMException(ErrorCode.SESSION_DISABLED,
                    "Session disabled.");
        }
        ses.updateAccess();
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
        POLLING_LONG("/polling/long"),
        POLLING_MESSAGE("/polling/message");

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
