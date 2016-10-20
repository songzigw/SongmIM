package songm.im.utils;

import java.lang.reflect.Type;

import com.google.gson.Gson;

import songm.im.entity.Result;

public class JsonUtils {

    private static Gson gson = new Gson();

    public static <T, E> String toJson(Result<E> obj, Class<T> clazz) {
        return gson.toJson(obj, clazz);
    }

    public static <T, E> byte[] toJsonBytes(Result<E> obj, Class<T> clazz) {
        String json = gson.toJson(obj, clazz);
        if (json == null) {
            return null;
        }
        return json.getBytes();
    }
    
    public static <T, E> String toJson(Object obj, Class<T> clazz) {
        return gson.toJson(obj, clazz);
    }
    
    public static <T, E> byte[] toJsonBytes(Object obj, Class<T> clazz) {
        String json = gson.toJson(obj, clazz);
        if (json == null) {
            return null;
        }
        return json.getBytes();
    }
    
    public static <T> T fromJson(String str, Class<T> clazz) {
        return gson.fromJson(str, clazz);
    }

    public static <T> T fromJson(byte[] json, Class<T> clazz) {
        return fromJson(new String(json), clazz);
    }

    public static <T> T fromJson(String str, Type type) {
        return gson.fromJson(str, type);
    }
}
