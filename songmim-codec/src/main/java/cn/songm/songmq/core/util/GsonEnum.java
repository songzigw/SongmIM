package cn.songm.songmq.core.util;

public interface GsonEnum<E> {

    String serialize();

    E deserialize(String jsonEnum);
}
