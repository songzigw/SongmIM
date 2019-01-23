package cn.songm.songmq.core;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

public class CallbackInvoker {

    private static final int COUNT = 1;

    private final CountDownLatch countDown;
    private String eventId;
    private Object result;
    private Throwable reason;
    private List<CallbackListener> listeners;

    {
        countDown = new CountDownLatch(COUNT);
        listeners = Collections
                .synchronizedList(new ArrayList<CallbackListener>());
    }
    
    public CallbackInvoker(String eventId) {
        this.eventId = eventId;
    }
    
    public CallbackInvoker(Long eventId) {
        this(eventId.toString());
    }

    private void publish() {
        for (CallbackListener listener : listeners) {
            listener.onCallBack(result);
        }
        CallbackManager.getInstance().remove(eventId);
    }

    public void setReason(Throwable reason) {
        this.reason = reason;
        this.publish();
        countDown.countDown();
    }

    public void setResult(Object result) {
        this.result = result;
        this.publish();
        countDown.countDown();
    }

    public Object getResult(long timeout, TimeUnit unit) {
        boolean flag = false;
        try {
            flag = countDown.await(timeout, unit);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        } finally {
            CallbackManager.getInstance().remove(eventId);
        }
        if (!flag) {
            throw new RuntimeException();
        }
        
        if (reason != null) {
            throw new RuntimeException(reason);
        }
        return result;
    }

    public void join(CallbackListener listener) {
        this.listeners.add(listener);
    }

    public String getEventId() {
        return eventId;
    }

}
