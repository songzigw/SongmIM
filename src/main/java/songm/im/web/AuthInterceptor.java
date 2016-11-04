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

    @Autowired
    private AuthService authService;
    
    @Override
    public boolean preHandle(HttpServletRequest request,
            HttpServletResponse response, Object handler) throws Exception {
        String key = request.getHeader("key");
        String nonce = request.getHeader("nonce");
        String signature = request.getHeader("signature");
        long timestamp = Long.parseLong(request.getHeader("timestamp"));
        boolean f = authService.auth(key, nonce, signature, timestamp);
        if (f) {
            return true;
        }
        Result<Object> result = new Result<Object>();
        result.setErrorCode(ErrorCode.AUTH_FAILURE.name());
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
