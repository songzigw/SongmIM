package cn.songm.songmq.core.util;

import java.lang.reflect.Type;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class JsonUtils {

    private static JsonUtils instance;

    private Gson gson;
    
    private JsonUtils() {}
    
    public synchronized static void init(GsonBuilder builder) {
        if (null != instance) {
            return;
        }
        instance = new JsonUtils();
        instance.gson = builder.create();
    }
    
    public static JsonUtils getInstance() {
        if (null == instance) {
            throw new NullPointerException();
        }
        return instance;
    }

    public <T> String toJson(Object obj, Type type) {
        return gson.toJson(obj, type);
    }

    public <T> String toJson(Object obj, Class<T> clazz) {
        return gson.toJson(obj, clazz);
    }

    public <T> String toJson(Object obj) {
        return toJson(obj, obj.getClass());
    }
    
    public <T> byte[] toJsonBytes(Object obj, Class<T> clazz) {
        return toJson(obj, clazz).getBytes();
    }
    
    public <T> byte[] toJsonBytes(Object obj) {
        return toJson(obj).getBytes();
    }

    public <T> T fromJson(String str, Class<T> clazz) {
        return gson.fromJson(str, clazz);
    }

    public <T> T fromJson(byte[] json, Class<T> clazz) {
        return fromJson(new String(json), clazz);
    }

    public <T> T fromJson(String json, Type typeOfT) {
        return gson.fromJson(json, typeOfT);
    }
    
    public <T> T fromJson(byte[] json, Type typeOfT) {
        return fromJson(new String(json), typeOfT);
    }
}
