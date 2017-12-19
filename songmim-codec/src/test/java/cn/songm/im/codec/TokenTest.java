package cn.songm.im.codec;

import java.lang.reflect.Field;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

@RunWith(JUnit4.class)
public class TokenTest {

    @Test
    public void testEquals() {
        String uid = "1000";
        Token t1 = new Token(uid, "n_1000", "a_1000", "key");
        Token t2 = new Token(uid, "n_1000", "a_1000", "key");
        Assert.assertEquals(t1, t2);
    }

    @Test
    public void testEqualsNot() {
        Token t1 = new Token("1001", "n_1001", "a_1001", "key");
        Token t2 = new Token("1002", "n_1002", "a_1002", "key");
        Assert.assertFalse(t1.equals(t2));
    }

    @Test
    public void testTokenIdNotNull() {
        Token t = new Token("1000", "n_1000", "a_1000", "key");
        Assert.assertNotNull(t.getTokenId());
    }

    @Test
    public void testToString()
            throws IllegalArgumentException, IllegalAccessException {
        Token tok = new Token("1000", "n_1000", "a_1000", "key");
        Class<?> clazz = tok.getClass();
        Field[] fields = clazz.getDeclaredFields();
        StringBuilder str = new StringBuilder("Token [");
        for (int i = 0; i < fields.length; i++) {
            Field field = fields[i];
            if (field.getModifiers() == 2) {
                field.setAccessible(true);
                str.append(field.getName()).append("=")
                        .append(field.get(tok) == null ? "null"
                                : field.get(tok).toString());
                if (i != fields.length - 1) {
                    str.append(", ");
                }
            }
        }
        str.append("]");
        Assert.assertEquals(str.toString(), tok.toString());
    }
}
