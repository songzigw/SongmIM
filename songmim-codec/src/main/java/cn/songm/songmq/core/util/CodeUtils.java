package cn.songm.songmq.core.util;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import sun.misc.BASE64Encoder;

/**
 * 编码帮助类
 * 
 * @author zhangsong
 *
 */
@SuppressWarnings("restriction")
public class CodeUtils {

    /**
     * SHA-1 数字摘要
     * <p>
     * SHA<sup>15</sup> 的全称是 Secure Hash Algorithm，安全散列算法。 SHA-1
     * 算法生成的摘要信息长度是160位，由于生成的摘要信息 更长，运算过程更加复杂，在相同的硬件上，SHA-1 运行速度比 MD5更慢，但是更为安全。
     * </p>
     * <p>
     * 由于计算机的摘要转换成字符串，可能会生成一些无法显示和网络 传输的控制字符，因此，我们需要对生成的摘要字符串进行编码，
     * 常用的编码方式包括十六进制与Base64编码，当前采用16进制编码。
     * </p>
     * 
     * @param text
     * @return
     */
    public static String sha1(String text) {
        MessageDigest md = null;
        String outStr = null;
        try {
            md = MessageDigest.getInstance("SHA-1");
            byte[] digest = md.digest(text.getBytes());
            outStr = bytes2hex(digest);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
        return outStr;
    }

    public static String md5(String text) {
        MessageDigest md = null;
        String outStr = null;
        try {
            md = MessageDigest.getInstance("MD5");
            byte[] digest = md.digest(text.getBytes());
            outStr = bytes2hex(digest);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
        return outStr;
    }

    public static String encode64(byte[] bytes) {
        BASE64Encoder encoder = new BASE64Encoder();
        return encoder.encode(bytes);
    }

    /**
     * 16进制编码
     * <p>
     * 数据类型byte，在java中是作为最小的数字类处理的，占8个位， 因此它的值被定义在[-128, 127]区间内，也就是signed byte。
     * </p>
     * 
     * @param bytes
     * @return
     */
    public static String bytes2hex(byte[] bytes) {
        StringBuilder hex = new StringBuilder();
        for (int i = 0; i < bytes.length; i++) {
            byte b = bytes[i];
            boolean negative = false;
            if (b < 0) negative = true;
            int bAbs = Math.abs(b);
            if (negative) bAbs = bAbs | 0x80;
            String temp = Integer.toHexString(bAbs & 0xFF);
            if (temp.length() == 1) {
                hex.append("0");
            }
            hex.append(temp.toLowerCase());
        }
        return hex.toString();
    }

    /**
     * 16进制解码
     * 
     * @param hex
     * @return
     */
    public static byte[] hex2bytes(String hex) {
        byte[] bytes = new byte[hex.length() / 2];
        for (int i = 0; i < hex.length(); i = i + 2) {
            String subStr = hex.substring(i, i + 2);
            boolean negative = false;
            int intPar = Integer.parseInt(subStr, 16);
            if (intPar > 127) negative = true;
            if (intPar == 128) {
                intPar = -128;
            } else if (negative) {
                intPar = 0 - (intPar & 0x7F);
            }
            byte b = (byte) intPar;
            bytes[i / 2] = b;
        }
        return bytes;
    }

    private static final String[] hex = { "0", "1", "2", "3", "4", "5", "6",
            "7", "8", "9", "a", "b", "c", "d", "e", "f" };

    public static String byteArrayToHexString(byte[] byteArray) {
        StringBuffer sb = new StringBuffer();
        for (byte b : byteArray) {
            sb.append(byteToHexChar(b));
        }
        return sb.toString();
    }

    private static Object byteToHexChar(byte b) {
        int n = b;
        if (n < 0) {
            n = 256 + n;
        }
        int d1 = n / 16;
        int d2 = n % 16;
        return hex[d1] + hex[d2];
    }
}
