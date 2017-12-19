package cn.songm.im.codec;

import java.lang.reflect.Field;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

@RunWith(JUnit4.class)
public class SessionTest {

    @Test
    public void testToString()
            throws IllegalArgumentException, IllegalAccessException {
        Token token = new Token("1000", "n_1000", "a_1000", "key");
        Session ses = new Session(token.getUid(), token.getTokenId());
        Class<?> clazz = ses.getClass();
        Field[] fields = clazz.getDeclaredFields();
        StringBuilder str = new StringBuilder("Session [");
        for (int i = 0; i < fields.length; i++) {
            Field field = fields[i];
            if (field.getModifiers() == 2) {
                field.setAccessible(true);
                str.append(field.getName()).append("=")
                        .append(field.get(ses) == null ? "null"
                                : field.get(ses).toString());
                if (i != fields.length - 1) {
                    str.append(", ");
                }
            }
        }
        str.append("]");
        Assert.assertEquals(str.toString(), ses.toString());
    }
}
