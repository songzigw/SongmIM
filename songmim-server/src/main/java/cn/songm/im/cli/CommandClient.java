package cn.songm.im.cli;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
@SuppressWarnings("unchecked")
public class CommandClient {
    private String title = "Java命令行客户端";
    private Class listenClass = this.getClass();
    private BufferedReader _stdin;
    private FormattingPrintWriter _stdout;
    // 默认主菜单
    private Object[][] MENU = {
        // 命令符, 描述, 要调用的方法
        { "1", "欢迎", "welcome" },
        { "Q", "退出", "quit" } 
        };
    public CommandClient(Class clazz) {
        if(clazz != null){
            this.listenClass = clazz;
        }
        
        _stdin = new BufferedReader(new InputStreamReader(System.in));
        _stdout = new FormattingPrintWriter(System.out, true);
        _stdout.println("/n==== " + this.title + " ====");
    }
    
    public CommandClient(String title, Class clazz) {
        if(clazz != null){
            this.listenClass = clazz;
        }
        _stdin = new BufferedReader(new InputStreamReader(System.in));
        _stdout = new FormattingPrintWriter(System.out, true);
        if(title != null && !title.trim().equals("")){
            this.title = title;
        }
        _stdout.println("/n==== " + this.title + " ====");
    }
    
    public void setMenu(Object[][] menu){
        if(menu != null){
            this.MENU = menu;
        }
    }
    
    @SuppressWarnings("unchecked")
    private Method findMethod(String name) {
        Class cl = this.listenClass;
        Method method = null;
        while (method == null) {
            try {
                method = cl.getDeclaredMethod(name, null);
            } catch (NoSuchMethodException e) {
                System.out.println("no method define");
                cl = cl.getSuperclass();
                if (cl == null) {
                    e.printStackTrace();
                    System.exit(1);
                }
            }
        }
        
        return method;
    }
    private String getChoice(String choices) throws IOException {
        while (true) {
            _stdout.print("> ");
            _stdout.flush();
            String line = _stdin.readLine().trim();
            if (line.length() == 1) {
                int choice = Character.toUpperCase(line.charAt(0));
                int index = choices.indexOf(choice);
                if (index != -1)
                    return choices.substring(index, index + 1);
            }
            _stdout.println("/n*** 必须选择以下选项之一： " + choices);
        }
    }
    // 选择菜单选项，动态调用某个方法
    public void doMenu(Object[][] menu, boolean main) {
        this.doMenu(title, this.MENU, main);
    }
    @SuppressWarnings("unchecked")
    public void doMenu(String pTitle, Object[][] menu, boolean main) {
        synchronized (System.in) {
            Map actions = new HashMap();
            StringBuffer sb = new StringBuffer(menu.length);
            for (int i = 0; i < menu.length; i++) {
                Object mnemonic = menu[i][0];
                sb.append(mnemonic);
                Object action = menu[i][2];
                if (action instanceof String)
                    action = findMethod((String) action);
                actions.put(mnemonic, action);
            }
            String choices = sb.toString();
            while (true) {
                try {
                    String mnemonic;
                    _stdout.clearTabs();
                    _stdout.println("/n---   " + pTitle + "   ---");
                    _stdout.println("/n请选择菜单项:");
                    for (int i = 0; i < menu.length; i++)
                        _stdout.println("[" + menu[i][0] + "]  " + menu[i][1]);
                    // Get the user's selection.
                    mnemonic = getChoice(choices);
                    // System.out.println("mnemonic = " + mnemonic);
                    for (int i = 0; i < menu.length; i++) {
                        Object[] entry = menu[i];
                        if (entry[0].equals(mnemonic)) {
                            Object action = actions.get(mnemonic);
                            if (action == null) {
                                return;
                            } else if (action instanceof Method) {
                                // System.out.println("selected,will do");
                                // Cast required to suppress JDK1.5 varargs
                                // compiler warning.
                                ((Method) action).invoke(this, (Object[]) null);
                            } else {
                                doMenu((String) entry[1], (Object[][]) action, false);
                            }
                        }
                    }
                } catch (Exception e) {
                    Throwable t = e;
                    if (e instanceof InvocationTargetException)
                        t = ((InvocationTargetException) e)
                                .getTargetException();
                    _stdout.println("/n发生异常: " + t);
                }
            }// while end
        }
    }
    
    public void run(Object[][] menu){
        this.setMenu(menu);
        this.doMenu(menu, true);
    }
    
    public void welcome(){
        System.out.println("欢迎使用Java命令行客户端！");
    }
    
    public void quit() {
        System.exit(0);
    }
    
    public static void main(String[] args) {
        CommandClient client = new CommandClient(null);
        client.doMenu(null, true);
    }
}
