package cn.songm.im.codec.json;

import java.util.Date;

import com.google.gson.GsonBuilder;

import cn.songm.im.codec.model.Conversation.Ctype;
import cn.songm.im.codec.model.Message;
import cn.songm.im.codec.model.Message.Direction;
import cn.songm.im.codec.model.Message.Mtype;
import cn.songm.songmq.core.util.DateTypeAdapter;
import cn.songm.songmq.core.util.GsonEnumTypeAdapter;
import cn.songm.songmq.core.util.JsonUtils;

public class JsonUtilsInit {

    public static void initialization() {
        JsonUtils.init(new GsonBuilder()
                .registerTypeAdapter(Date.class, new DateTypeAdapter())
                .registerTypeAdapter(Ctype.class, new GsonEnumTypeAdapter<>(Ctype.PRIVATE))
                .registerTypeAdapter(Mtype.class, new GsonEnumTypeAdapter<>(Mtype.TEXT))
                .registerTypeAdapter(Direction.class, new GsonEnumTypeAdapter<>(Direction.SEND))
                .registerTypeAdapter(Message.class, new MessageTypeAdapter())
                );
    }
}
