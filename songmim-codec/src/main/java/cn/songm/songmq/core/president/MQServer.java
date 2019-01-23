package cn.songm.songmq.core.president;

public interface MQServer extends Runnable {

    void startup();

    void shutdown();
}
