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
	try {
		start();
	} catch (IOException e) {
		throw new RuntimeException(e.getMessage());
	}
    }
    
    private String start() throws IOException {
	while (true) {
	    _stdout.print("> ");
	    _stdout.flush();
	    Cli c = null;
	    try {
			c = getCli(_stdin.readLine().trim());
		} catch (NotCliException e) {
			System.out.println(e.getMessage());
			continue;
		}
	    if (c == null) continue;
	    System.out.println(runCli(c));
	}
    }
    
    private class Cli {
    	private Operation operation;
    	private String[] args;
		public Operation getOperation() {
			return operation;
		}
		public void setOperation(Operation operation) {
			this.operation = operation;
		}
		public String[] getArgs() {
			return args;
		}
		public void setArgs(String[] args) {
			this.args = args;
		}
    }
    
    private Cli getCli(String line) throws NotCliException {
    	line = line.trim();
    	if (line.equals("")) {
    		return null;
    	}
    	String[] array = line.split(" ");
    	String[] args = new String[array.length - 1];
    	for (int i = 1; i < array.length; i++) {
    		args[i - 1] = array[i];
    	}
    	Cli c = new Cli();
    	c.setOperation(Operation.instance(array[0]));
    	c.setArgs(args);
    	return c;
    }
    
    private String runCli(Cli c) {
    	String result = "";
    	switch (c.getOperation()) {
			case CONNECT:
				result = c.getOperation().value;
				c.getArgs();
				break;
			case EXIT:
				System.exit(0);
				break;
			default:
				break;
		}
    	return result;
    }
    
    private enum Operation {
    	CONNECT("connect"),
    	EXIT("exit");
    	
    	private String value;
    	
    	private Operation(String v) {
    		this.value = v;
    	}
    	
    	public static Operation instance(String v) throws NotCliException {
    		for (Operation o : values()) {
    			if (o.value.equals(v)) return o;
    		}
    		throw new NotCliException(String.format("command not found: %s", v));
    	}
    }
    
    public static void main(String[] args) {
    	new CliMain();
    }
}
