package songm.im.web;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ui.ModelMap;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.context.request.WebRequestInterceptor;

public class MustInterceptor implements WebRequestInterceptor {
    
    private static final Logger LOG = LoggerFactory
            .getLogger(MustInterceptor.class);

    @Override
    public void preHandle(WebRequest request) throws Exception {
        LOG.info("HTTP: {}", request.getLocale());
    }

    @Override
    public void postHandle(WebRequest request, ModelMap model) throws Exception {
        
    }

    @Override
    public void afterCompletion(WebRequest request, Exception ex)
            throws Exception {
        
    }

}
