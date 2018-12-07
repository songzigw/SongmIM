package cn.songm.im.server.httpapi;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.HttpMethod;
import io.netty.handler.codec.http.QueryStringDecoder;
import io.netty.handler.codec.http.multipart.Attribute;
import io.netty.handler.codec.http.multipart.HttpPostRequestDecoder;
import io.netty.handler.codec.http.multipart.InterfaceHttpData;

public class RequestParam {

    private FullHttpRequest fullReq;
    private Map<String, String> map = new HashMap<>();
    
    public RequestParam(FullHttpRequest fullReq) {
	this.fullReq = fullReq;
	try {
	    this.parse();
	} catch (IOException e) {
	    throw new RuntimeException(e);
	}
    }
    
    private Map<String, String> parse() throws IOException {
	HttpMethod method = fullReq.method();
	
	if (HttpMethod.GET == method) {
	    QueryStringDecoder dec = new QueryStringDecoder(fullReq.uri());
	    dec.parameters().entrySet().forEach(entry -> {
		map.put(entry.getKey(), entry.getValue().get(0));
	    });
	} else if (HttpMethod.POST == method) {
	    HttpPostRequestDecoder dec = new HttpPostRequestDecoder(fullReq);
	    dec.offer(fullReq);
	    List<InterfaceHttpData> list = dec.getBodyHttpDatas();
	    for (InterfaceHttpData param : list) {
		Attribute data = (Attribute) param;
		map.put(data.getName(), data.getValue());
	    }
	} else {
	    throw new RuntimeException(method.toString());
	}
	return map;
    }
    
    public String getParameter(String name) {
	return map.get(name);
    }
}
