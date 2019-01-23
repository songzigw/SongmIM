package cn.songm.songmq.core;

import java.util.EventObject;

/**
 * 主题消息事件
 * 
 * @author zhangsong
 *
 */
public class MessageEvent extends EventObject {

    private static final long serialVersionUID = -1435397047690536132L;

    private Topic topic;

    public MessageEvent(Topic topic, Object payload) {
        super(payload);
        this.topic = topic;
    }

    public Topic getTopic() {
        return topic;
    }

    public Object getPayload() {
        return this.source;
    }
}
