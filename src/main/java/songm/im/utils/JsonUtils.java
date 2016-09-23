package songm.im.utils;

import com.google.gson.Gson;

public class JsonUtils {

    private static Gson gson = new Gson();

    public static <T> String toJson(Object obj, Class<T> clazz) {
        return gson.toJson(obj, clazz);
    }

    public static <T> byte[] toJsonBytes(Object obj, Class<T> clazz) {
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

}
