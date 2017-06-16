package cn.songm.im.server;

import java.util.Map;

import cn.songm.songmq.core.president.MQServer;
import cn.songm.songmq.server.Container;
import cn.songm.songmq.server.MQContext;

public class IMContainer implements Container {

    // private static final Logger LOG = LoggerFactory
    // .getLogger(MQContainer.class);
    private static final String CONFIG = "classpath:config/songmim-application.xml";

    private MQContext context;

    public IMContainer() {
        context = new MQContext(CONFIG);
        context.start();
    }

    @Override
    public void start() {
        Map<String, MQServer> beans = context.getBeansOfType(MQServer.class);
        for (MQServer ser : beans.values()) {
            ser.start();
        }
    }

    @Override
    public void stop() {
        Map<String, MQServer> beans = context.getBeansOfType(MQServer.class);
        for (MQServer ser : beans.values()) {
            ser.shutdown();
        }
        context.stop();
        context.close();
    }

    @Override
    public MQContext getContext() {
        return context;
    }

}
