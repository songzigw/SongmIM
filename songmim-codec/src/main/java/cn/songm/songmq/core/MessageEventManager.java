package cn.songm.songmq.core;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * 消息事件管理器
 * 
 * @author zhangsong
 *
 */
public class MessageEventManager {

    private static AtomicBoolean flag = new AtomicBoolean(false);
    // 核心线程数
    public static final int CORE_POOL_SIZE = 10;
    // 最大线程数
    public static final int MAX_POOL_SIZE = 30;

    private final Map<Topic, Set<MessageListener>> listeners;
    private final ExecutorService executor;

    private MessageEventManager() {
        listeners = Collections
                .synchronizedMap(new HashMap<Topic, Set<MessageListener>>());
        executor = new ThreadPoolExecutor(CORE_POOL_SIZE, MAX_POOL_SIZE, 60L,
                TimeUnit.SECONDS, new SynchronousQueue<Runnable>());
    }

    private static MessageEventManager manager;

    public static MessageEventManager getInstance() {
        if (manager == null && flag.compareAndSet(false, true)) {
            manager = new MessageEventManager();
        }
        if (manager == null) {
            getInstance();
        }
        return manager;
    }

    /**
     * 主题订阅
     * 
     * @param topic
     * @param listener
     */
    public void subscribe(Topic topic, MessageListener listener) {
        // 创建消息队列
        MessageQueue mq = MQueueManager.getInstance().createMQ(topic);
        topic = mq.getTopic();
        Set<MessageListener> set = listeners.get(topic);
        if (set == null) {
            set = new HashSet<MessageListener>();
            listeners.put(topic, set);
        }
        set.add(listener);
    }

    /**
     * 取消主题订阅
     * 
     * @param topic
     * @param listener
     */
    public void unsubscribe(Topic topic, MessageListener listener) {
        Set<MessageListener> set = listeners.get(topic);
        if (set == null) {
            return;
        }
        set.remove(listener);
    }

    /**
     * 触发主题消息
     * 
     * @param event
     */
    public void trigger(MessageEvent event) {
        Topic topic = event.getTopic();
        Set<MessageListener> set = listeners.get(topic);
        if (set == null) return;

        switch (topic.getModel()) {
        case POINT_2_P:
            if (set.iterator().hasNext()) {
                set.iterator().next().onMessage(event);
            }
            break;
        case PUB_SUB:
            for (MessageListener ler : set) {
                ler.onMessage(event);
            }
            break;
        default:
            break;
        }
    }

    public ExecutorService getExecutor() {
        return executor;
    }
}
