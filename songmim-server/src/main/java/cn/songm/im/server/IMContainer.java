package cn.songm.im.server;

import java.util.Map;

import cn.songm.im.codec.json.JsonUtilsInit;
import cn.songm.songmq.core.president.MQServer;

public class IMContainer implements Container {

    private static final String CONFIG = "classpath:songmim-application.xml";

    private MQContext context;

    public IMContainer() {
        JsonUtilsInit.initialization();
        context = new MQContext(CONFIG);
        context.start();
    }

    @Override
    public void start() {
        Map<String, MQServer> beans = context.getBeansOfType(MQServer.class);
        for (MQServer ser : beans.values()) {
            ser.startup();
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
