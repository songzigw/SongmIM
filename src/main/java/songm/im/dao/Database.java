
package songm.im.dao;

public interface Database {

    public static enum IM implements Tables {
        /** 会话消息 */
        SIM_MESSAGE,
    }
    
    /**
     * 会话消息字段
     * 
     * @author 张松
     * 
     */
    public static enum MessageF implements Fields {
        /** 消息ID */
        MSGID,
        /** 会话类型 */
        CONV,
        /** 消息类型 */
        TYPE,
        /** 发送方 */
        FROM,
        /** 接收方 */
        TO,
        /** 创建时间 */
        CREATED,
        /** 修改时间 */
        UPDATED,
    }

}
