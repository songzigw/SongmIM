package cn.songm.im.cli;

public class Test {

    public static void main(String[] args) {
	CliClient c = new CliClient("127.0.0.1", 17185);
	
	try {
	    c.connect();
	} catch (InterruptedException e) {
	    e.printStackTrace();
	}
    }
}
