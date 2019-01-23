package cn.songm.songmq.core;

import java.util.EventListener;

/**
 * 主题消息监听器
 * 
 * @author zhangsong
 *
 */
public interface MessageListener extends EventListener {

    public void onMessage(MessageEvent event);
}
