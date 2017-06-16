package cn.songm.im.server.httpd;

import cn.songm.common.beans.Result;
import cn.songm.common.utils.JsonUtils;
import cn.songm.im.business.IMException;
import io.netty.channel.Channel;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.QueryStringDecoder;

public abstract class HttpAction {

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
        BACKSTAGE_TOKEN("/backstage/token"),
        POLLING_LONG("/jsonp/polling"),
        POLLING_MESSAGE("/jsonp/message"),;

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
