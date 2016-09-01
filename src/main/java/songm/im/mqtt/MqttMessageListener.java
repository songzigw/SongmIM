package songm.im.mqtt;


public interface MqttMessageListener {

    /**
     * 收到消息时
     * 
     * @param payload
     */
    public abstract void onReceived(byte[] payload);
}
