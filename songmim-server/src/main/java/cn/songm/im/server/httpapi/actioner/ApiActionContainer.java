package cn.songm.im.server.httpapi.actioner;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

@Component
public class ApiActionContainer implements ApplicationContextAware {

    private Map<String, ApiActioner> actions = new HashMap<String, ApiActioner>();

    @Override
    public void setApplicationContext(ApplicationContext applicationContext)
            throws BeansException {
        Map<String, ApiActioner> beans = applicationContext
                .getBeansOfType(ApiActioner.class);
        for (ApiActioner a : beans.values()) {
            actions.put(a.uri(), a);
        }
    }

    public ApiActioner find(String uri) {
        return actions.get(uri);
    }
}
