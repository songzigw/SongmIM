package cn.songm.im.httpd;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

@Component
public class ActionManager implements ApplicationContextAware {

    private ApplicationContext context;

    @Override
    public void setApplicationContext(ApplicationContext applicationContext)
            throws BeansException {
        this.context = applicationContext;
    }

    private Map<String, HttpAction> actions = new HashMap<String, HttpAction>();

    @Bean(name = "httpActions")
    public Map<String, HttpAction> actions() {
        Map<String, HttpAction> beans = context
                .getBeansOfType(HttpAction.class);
        for (HttpAction a : beans.values()) {
            actions.put(a.uri(), a);
        }
        return actions;
    }

    public HttpAction find(String uri) {
        return actions.get(uri);
    }
}
