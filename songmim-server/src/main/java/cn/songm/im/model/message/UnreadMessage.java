package cn.songm.im.model.message;

public class UnreadMessage extends NoticeMessage {

    private int number;
    
    public int getNumber() {
        return number;
    }

    public void setNumber(int number) {
        this.number = number;
    }

    @Override
    public String toString() {
        return "UnreadMessage [number=" + number + "]";
    }

}
