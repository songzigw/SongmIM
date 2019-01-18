package cn.songm.im.cli;

public interface CliCallback {

    public void onDisconnect();
    
    public void onMessage(String msg);
}
