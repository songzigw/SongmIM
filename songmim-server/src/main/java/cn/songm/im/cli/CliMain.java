package cn.songm.im.cli;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

/***
 * 命令行客户端主程序入口
 * 命令行客户端链接服务器，只能限于服务器本地
 * 计算机上的端客户端访问，不允许远程访问。
 * 
 * @author zhangsong
 *
 */
public class CliMain {

    private static final String TITLE = "SongmIM命令行程序";
    
    private FormattingPrintWriter _stdout;
    private BufferedReader _stdin;
    
    public CliMain() {
	_stdin = new BufferedReader(new InputStreamReader(System.in));
	_stdout = new FormattingPrintWriter(System.out, true);
	_stdout.println(TITLE);
    }
    
    private String getChoice(String choices) throws IOException {
	while (true) {
	    _stdout.print("> ");
	    _stdout.flush();
	    String line = _stdin.readLine().trim();
	}
    }
    
    public static void main(String[] args) {

    }
}
