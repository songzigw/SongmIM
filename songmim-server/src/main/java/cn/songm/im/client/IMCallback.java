package cn.songm.im.client;

import cn.songm.im.codec.model.Message;

public interface IMCallback {

    public void onDisconnected();

    public void onMessage(Message message);
}
