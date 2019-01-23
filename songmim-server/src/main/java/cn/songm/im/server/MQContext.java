package cn.songm.im.server;

import org.springframework.context.support.ClassPathXmlApplicationContext;

public class MQContext extends ClassPathXmlApplicationContext
        implements Context {

    public MQContext(String configLocation) {
        super(configLocation);
    }

}
