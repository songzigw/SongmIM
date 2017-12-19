package cn.songm.im.server.handler.actioner;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

/**
 * Actioner容器
 * 
 * @author zhangsong
 *
 */
@Component
public class ActionerContainer implements ApplicationContextAware {

    private Map<Integer, Actioner> ops = new HashMap<Integer, Actioner>();
    
    @Override
    public void setApplicationContext(ApplicationContext context)
            throws BeansException {
        Map<String, Actioner> beans = context.getBeansOfType(Actioner.class);
        for (Actioner er : beans.values()) {
            ops.put(er.operation(), er);
        }
    }

    public Actioner find(Integer op) {
        return ops.get(op);
    }
}
