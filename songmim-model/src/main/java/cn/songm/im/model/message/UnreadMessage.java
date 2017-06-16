package cn.songm.im.model.message;

import cn.songm.im.model.message.Message.Mtype;

public class UnreadMessage extends NoticeMessage {

    private int number;

    private int total;

    public UnreadMessage() {
        super(Mtype.UNREAD);
    }

    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
    }

    public int getNumber() {
        return number;
    }

    public void setNumber(int number) {
        this.number = number;
    }

    @Override
    public String toString() {
        return "UnreadMessage [number=" + number + ", total=" + total
                + ", toString()=" + super.toString() + "]";
    }

}
