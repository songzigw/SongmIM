package cn.songm.songmq.core.util;

import java.lang.reflect.Type;
import java.util.Date;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

public class DateTypeAdapter
        implements JsonSerializer<Date>, JsonDeserializer<Date> {

    @Override
    public Date deserialize(JsonElement json, Type typeOfT,
            JsonDeserializationContext context) throws JsonParseException {
        if (null != json) {
            return new Date(json.getAsLong());
        }
        return null;
    }

    @Override
    public JsonElement serialize(Date src, Type typeOfSrc,
            JsonSerializationContext context) {
        if (null != src) {
            return new JsonPrimitive(src.getTime());
        }
        return null;
    }
}
