package songm.im.web;

import java.io.PrintWriter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import songm.im.IMException.ErrorCode;
import songm.im.entity.Result;
import songm.im.service.AuthService;
import songm.im.utils.JsonUtils;

public class AuthInterceptor implements HandlerInterceptor {

    private static final String APPKEY = "SM-Server-Key";
    private static final String NONCE = "SM-Nonce";
    private static final String TIMESTAMP = "SM-Timestamp";
    private static final String SIGNATURE = "SM-Signature";
    
    @Autowired
    private AuthService authService;
    
    @Override
    public boolean preHandle(HttpServletRequest request,
            HttpServletResponse response, Object handler) throws Exception {
        String key = request.getHeader(APPKEY);
        String nonce = request.getHeader(NONCE);
        String signature = request.getHeader(SIGNATURE);
        long timestamp = 0;
        try {
            timestamp = Long.parseLong(request.getHeader(TIMESTAMP));
        } catch (Exception e) {}
        boolean f = authService.auth(key, nonce, signature, timestamp);
        if (f) {
            return true;
        }
        Result<Object> result = new Result<Object>();
        result.setErrorCode(ErrorCode.AUTH_FAILURE.name(), "授权失败");
        response.setContentType("text/json; charset=UTF-8");
        PrintWriter out = response.getWriter();
        out.print(JsonUtils.toJson(result, result.getClass()));
        return false;
    }

    @Override
    public void postHandle(HttpServletRequest request,
            HttpServletResponse response, Object handler,
            ModelAndView modelAndView) throws Exception {
        
        
    }

    @Override
    public void afterCompletion(HttpServletRequest request,
            HttpServletResponse response, Object handler, Exception ex)
            throws Exception {
        
        
    }

}
