package cn.songm.im.server;

import java.util.List;

import org.springframework.context.support.AbstractApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import cn.songm.songmq.core.president.MQServer;
import cn.songm.songmq.server.Container;
import cn.songm.songmq.server.MQContext;

public class IMContainer implements Container {

    //private static final Logger LOG = LoggerFactory
    //        .getLogger(MQContainer.class);
    private static final String CONFIG = "classpath:config/songmim-application.xml";

    private AbstractApplicationContext context;

    public IMContainer() {
        context = new ClassPathXmlApplicationContext(CONFIG);
        context.start();
    }

    @Override
    public void start() {
        MQContext mqContext = (MQContext) context.getBean("mqContext");
        List<MQServer> servers = mqContext.getMqServers();
        for (MQServer mqser : servers) {
            mqser.start();
        }
    }

    @Override
    public void stop() {
        if (context != null) {
            MQContext mqContext = (MQContext) context.getBean("mqContext");
            List<MQServer> servers = mqContext.getMqServers();
            for (MQServer mqser : servers) {
                mqser.shutdown();
            }
        }
        context.stop();
        context.close();
    }

    @Override
    public MQContext getContext() {
        return (MQContext) context.getBean("mqContext");
    }

}
