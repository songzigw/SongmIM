package cn.songm.songmq.core.util;

/**
 * 随机序列处理
 * 
 * @author 张松
 *
 */
public class Sequence {

    private static char[] mark = { '0', '1', '2', '3', '4', '5', '6', '7', '8',
            '9', 'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L',
            'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y',
            'Z' };

    private static Sequence sequence;

    private Sequence() {

    }

    public static Sequence getInstance() {
        if (sequence == null) sequence = new Sequence();
        return sequence;
    }

    public String getSequence(int length) {
        StringBuffer sf = new StringBuffer(length);
        for (int i = 0; i < length; i++) {
            sf.append(mark[(int) ((1 - Math.random()) * mark.length)]);
        }
        return sf.toString();
    }
}
