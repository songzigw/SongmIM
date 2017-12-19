package cn.songm.im.codec.json;

import java.lang.reflect.Field;
import java.lang.reflect.Type;
import java.util.Map.Entry;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;

import cn.songm.im.codec.model.ImageMessage;
import cn.songm.im.codec.model.Message;
import cn.songm.im.codec.model.Message.Mtype;
import cn.songm.im.codec.model.MessageContent;
import cn.songm.im.codec.model.TextMessage;
import cn.songm.im.codec.model.UnreadMessage;
import cn.songm.songmq.core.util.JsonUtils;

public class MessageTypeAdapter implements JsonDeserializer<Message> {

    @Override
    public Message deserialize(JsonElement json, Type typeOfT,
            JsonDeserializationContext context) throws JsonParseException {
        JsonObject jObj = json.getAsJsonObject();
        Message message = new Message();
        Class<?> msgClazz = message.getClass();
        try {
            for (Entry<String, JsonElement> entry : jObj.entrySet()) {
                Field f = msgClazz.getDeclaredField(entry.getKey());
                if (f.getModifiers() == 2) {
                    f.setAccessible(true);
                    if (!f.getName().equals("jbody")) {
                        if (f.getType() == String.class) {
                            f.set(message, entry.getValue().getAsString());
                        } else {
                            f.set(message, JsonUtils.getInstance().fromJson(
                                entry.getValue().getAsString(), f.getType()));
                        }
                    }
                }
            }
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        } catch (SecurityException e) {
            e.printStackTrace();
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        message.setJbody(factory(jObj.get("jbody"), message.getType()));

        return message;
    }

    public static MessageContent factory(JsonElement contJson, Mtype mtype) {
        if (contJson == null) return null;
        String jbody = contJson.toString();
        MessageContent content = null;
        switch (mtype) {
        case TEXT:
            content = JsonUtils.getInstance().fromJson(jbody,
                    TextMessage.class);
            break;
        case IMAGE:
            content = JsonUtils.getInstance().fromJson(jbody,
                    ImageMessage.class);
            break;
        case UNREAD:
            content = JsonUtils.getInstance().fromJson(jbody,
                    UnreadMessage.class);
            break;
        default:
            throw new RuntimeException("Message type out");
        }
        return content;
    }
}
