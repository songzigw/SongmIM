package cn.songm.im.server.jsonp.action;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

import cn.songm.im.server.jsonp.JsonpAction;

@Component
public class JsonpActionContainer implements ApplicationContextAware {

    private Map<String, JsonpAction> actions = new HashMap<String, JsonpAction>();

    @Override
    public void setApplicationContext(ApplicationContext applicationContext)
            throws BeansException {
        Map<String, JsonpAction> beans = applicationContext
                .getBeansOfType(JsonpAction.class);
        for (JsonpAction a : beans.values()) {
            actions.put(a.uri(), a);
        }
    }

    public JsonpAction find(String uri) {
        return actions.get(uri);
    }
}
