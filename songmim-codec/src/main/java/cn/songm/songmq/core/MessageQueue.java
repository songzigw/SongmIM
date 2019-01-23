package cn.songm.songmq.core;

import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;

import cn.songm.songmq.core.president.MQMessage;

/**
 * 消息队列
 * 
 * @author zhangsong
 * @since 0.1, 2017-02-18
 * @version 0.1
 *
 */
public abstract class MessageQueue {

    private Topic topic;
    private ConcurrentLinkedQueue<MQMessage> queue;

    public MessageQueue(Topic topic) {
        this.topic = topic;
        queue = new ConcurrentLinkedQueue<MQMessage>();
    }

    public MQMessage getMessage() {
        return queue.poll();
    }

    public Topic getTopic() {
        return topic;
    }

    public boolean pushMessage(MQMessage payload) {
        boolean f = queue.offer(payload);
        if (f) {
            MQueueTask task = new MQueueTask(this);
            MessageEventManager.getInstance().getExecutor().submit(task);
        }
        return f;
    }

    public boolean pushMessage(List<MQMessage> payloads) {
        return queue.addAll(payloads);
    }
    
    public void triggerMessage() {
        MQMessage msg = queue.poll();
        if (msg == null) return;
        MessageEvent event = new MessageEvent(topic, msg);
        MessageEventManager.getInstance().trigger(event);
    }
}
