package cn.songm.im.model;

import java.lang.reflect.Field;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import junit.framework.Assert;

@RunWith(JUnit4.class)
public class ProtocolTest {

    private static final Logger LOG = LoggerFactory
            .getLogger(ProtocolTest.class);
    public static final short VERSION = 1;
    public static final short HEADER_LEN = 20;

    @Test
    public void testToString()
            throws IllegalArgumentException, IllegalAccessException {
        Protocol pro = new Protocol();
        Class<?> clazz = pro.getClass();
        Field[] fields = clazz.getDeclaredFields();
        StringBuilder str = new StringBuilder("Protocol [");
        for (int i = 0; i < fields.length; i++) {
            Field field = fields[i];
            if (field.getModifiers() == 2) {
                field.setAccessible(true);
                str.append(field.getName()).append("=")
                        .append(field.get(pro) == null ? "null"
                                : field.get(pro).toString());
                if (i != fields.length - 1) {
                    str.append(", ");
                }
            }
        }
        str.append("]");
        LOG.info(str.toString());
        LOG.info(pro.toString());
        Assert.assertEquals(str.toString(), pro.toString());
    }

}
