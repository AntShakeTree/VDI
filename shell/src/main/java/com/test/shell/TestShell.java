package com.test.shell;

import java.io.IOException;

public class TestShell {
	public static void main(String[] args) throws IOException, InterruptedException {
		for(int i=0; i<1000;i++ ){
			Runtime.getRuntime().exec("ping www.baidu.com -t");
			Thread.sleep(5000);
		}
	}
}
