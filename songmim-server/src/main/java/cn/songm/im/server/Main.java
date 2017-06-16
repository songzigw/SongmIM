package cn.songm.im.server;

import cn.songm.im.model.json.JsonUtilsInit;

/**
 * 程序入口
 * 
 * @author zhangsong
 *
 */
public class Main {

    public static void main(String[] args) {
        JsonUtilsInit.initialization();
        new IMContainer().start();
    }
    
}
