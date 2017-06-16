package cn.songm.im.model.json;

import java.util.Date;

import com.google.gson.GsonBuilder;

import cn.songm.common.utils.DateTypeAdapter;
import cn.songm.common.utils.GsonEnumTypeAdapter;
import cn.songm.common.utils.JsonUtils;
import cn.songm.im.model.message.Message;
import cn.songm.im.model.message.Conversation.Ctype;
import cn.songm.im.model.message.Message.Direction;
import cn.songm.im.model.message.Message.Mtype;

public class JsonUtilsInit {

    public static void initialization() {
        JsonUtils.init(new GsonBuilder()
                .registerTypeAdapter(Date.class, new DateTypeAdapter())
                .registerTypeAdapter(Ctype.class, new GsonEnumTypeAdapter<>(Ctype.PRIVATE))
                .registerTypeAdapter(Mtype.class, new GsonEnumTypeAdapter<>(Mtype.TEXT))
                .registerTypeAdapter(Direction.class, new GsonEnumTypeAdapter<>(Direction.SEND))
                .registerTypeAdapter(Message.class, new MessageTypeAdapter()));
    }
}
