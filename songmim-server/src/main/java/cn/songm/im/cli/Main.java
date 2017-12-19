package cn.songm.im.cli;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
public class Main {
    public static void main(String[] args) {
        // 返回菜单 
        Object[] BACK = { "B", "回到上级菜单", null };
        // 二级菜单 -- 数学计算 
        Object[][] SXJS_MENU = {
                // 命令符, 描述, 要调用的方法 
                { "1", "求和", "add" },
                { "2", "求差", "sub" },
                BACK,
                { "Q", "退出", "quit" }
                };
        
        // 主菜单 
        Object[][] MENU = {
                // 命令符, 描述, 要调用的方法 
                { "1", "数学计算", SXJS_MENU },
                { "2", "打印消息", "print" },
                { "3", "使用帮助", "help" },
                { "Q", "退出", "quit" }
        };
        
        CommandClient client = new CommandClient(Main.class);
        client.run(MENU);
    }
    /**
     * 求和
     */
    public static void add(){
        try {
            BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
            System.out.print("请输入第一个数字：");
            String one = in.readLine();
            System.out.print("请输入第二个数字：");
            String two = in.readLine();
            
            System.out.println(one + " + " + two + " = " + (Integer.parseInt(one) + Integer.parseInt(two)));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    /**
     * 求差
     */
    public static void sub(){
        try {
            BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
            System.out.print("请输入第一个数字：");
            String one = in.readLine();
            System.out.print("请输入第二个数字：");
            String two = in.readLine();
            
            System.out.println(one + " - " + two + " = " + (Integer.parseInt(one) - Integer.parseInt(two)));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    /**
     * 打印消息
     */
    public static void print(){
        try {
            BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
            System.out.print("请输入要输出的消息：");
            String msg = in.readLine();
            System.out.println(msg);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    /**
     * 帮助
     */
    public static void help(){
        System.out.println("先选择菜单向（括号中的符号），根据提示输入。");
    }
    /**
     * 退出
     */
    public static void quit(){
        System.out.println("Bye!");
        System.exit(0);
    }
    
}
