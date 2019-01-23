package cn.songm.im.server;

/**
 * 应用服务容器
 * @author zhangsong
 *
 * @param <T>
 */
public interface Container {

    /**
     * 容器开启
     */
    void start();

    /**
     * 容器停止
     */
    void stop();

    /**
     * 获取上下文
     * @return
     */
    Context getContext();
}
