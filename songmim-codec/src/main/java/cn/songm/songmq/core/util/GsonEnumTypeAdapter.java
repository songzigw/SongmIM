package cn.songm.songmq.core.util;

import java.lang.reflect.Type;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

public class GsonEnumTypeAdapter<E>
        implements JsonSerializer<E>, JsonDeserializer<E> {

    private final GsonEnum<E> gsonEnum;

    public GsonEnumTypeAdapter(GsonEnum<E> gsonEnum) {
        this.gsonEnum = gsonEnum;
    }

    @Override
    public E deserialize(JsonElement json, Type typeOfT,
            JsonDeserializationContext context) throws JsonParseException {
        if (null != json) {
            return gsonEnum.deserialize(json.getAsString());
        }
        return null;
    }

    @SuppressWarnings("rawtypes")
    @Override
    public JsonElement serialize(E src, Type typeOfSrc,
            JsonSerializationContext context) {
        if (null != src && src instanceof GsonEnum) {
            return new JsonPrimitive(((GsonEnum) src).serialize());
        }
        return null;
    }

}
