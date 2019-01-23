package cn.songm.songmq.core;

import java.io.Serializable;

/**
 * 主题信息
 * 
 * @author zhangsong
 * @since 0.1, 2017-02-18
 * @version 0.1
 *
 */
public class Topic implements Serializable {

    private static final long serialVersionUID = 2194236956726131934L;

    /** 主题名称 */
    private String name;
    /** 队列模型 */
    private MQueueModel model;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public MQueueModel getModel() {
        return model;
    }

    public void setModel(MQueueModel model) {
        this.model = model;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((name == null) ? 0 : name.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null) return false;
        if (getClass() != obj.getClass()) return false;
        Topic other = (Topic) obj;
        if (name == null) {
            if (other.name != null) return false;
        } else if (!name.equals(other.name)) return false;
        return true;
    }

}
