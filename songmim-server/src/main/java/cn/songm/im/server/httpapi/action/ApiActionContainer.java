package cn.songm.im.server.httpapi.action;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

import cn.songm.im.server.httpapi.ApiAction;

@Component
public class ApiActionContainer implements ApplicationContextAware {

    private Map<String, ApiAction> actions = new HashMap<String, ApiAction>();

    @Override
    public void setApplicationContext(ApplicationContext applicationContext)
            throws BeansException {
        Map<String, ApiAction> beans = applicationContext
                .getBeansOfType(ApiAction.class);
        for (ApiAction a : beans.values()) {
            actions.put(a.uri(), a);
        }
    }

    public ApiAction find(String uri) {
        return actions.get(uri);
    }
}
