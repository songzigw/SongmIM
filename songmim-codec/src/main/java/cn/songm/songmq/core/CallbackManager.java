package cn.songm.songmq.core;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * 回调管理器
 * 
 * @author zhangsong
 *
 */
public class CallbackManager {

    private static AtomicBoolean flag;
    static {
        flag = new AtomicBoolean(false);
    }

    private final Map<String, CallbackInvoker> callbackMap;
    {
        callbackMap = Collections.synchronizedMap(
                new HashMap<String, CallbackInvoker>());
    }

    private static CallbackManager instance;

    private CallbackManager() {
    }

    public static CallbackManager getInstance() {
        if (instance == null && flag.compareAndSet(false, true)) {
            instance = new CallbackManager();
        }
        if (instance == null) {
            getInstance();
        }
        return instance;
    }
    
    public void put(CallbackInvoker invoker) {
        callbackMap.put(invoker.getEventId(), invoker);
    }
    
    public void remove(String eventId) {
        callbackMap.remove(eventId);
    }
    
    public CallbackInvoker get(String eventId) {
        return callbackMap.get(eventId);
    }
    
    public void trigger(String eventId, Object result) {
        CallbackInvoker inv = callbackMap.get(eventId);
        if (inv != null) {
            inv.setResult(result);
        }
    }
    
    public void trigger(Long eventId, Object result) {
        this.trigger(eventId.toString(), result);
    }
}
