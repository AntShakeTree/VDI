package com.opzoon.appstatus.common;

import java.io.BufferedReader;
import java.io.InputStreamReader;

import org.apache.log4j.Logger;

/**
 * @ClassName: JavaShellUtil
 * @Description: java调用shell脚本工具
 * @author zhengyi
 * @date
 * @version V0.2.1023（迭代3）  
 */
public class JavaShellUtil {

	private static Logger log = Logger.getLogger(JavaShellUtil.class);

	public static String executeShell(String shellCommand) {
		log.info("<=AppStatus=> executeShell :[" + shellCommand + "]");
		StringBuilder stringBuilder = new StringBuilder();
		BufferedReader bufferedReader = null;
		String resultLine = "";
		Process pid = null;
		try {
		
			//String[] cmd = { "/bin/sh", "-c", shellCommand };
			//String cmd = shellCommand;
			pid = Runtime.getRuntime().exec(shellCommand);
			
			/*log.info("<=AppStatus=> executeShell ProcessBuilder");
			ProcessBuilder builder = new ProcessBuilder(shellCommand.split(" "));
			pid = builder.start();*/

			if (pid != null) {
				bufferedReader = new BufferedReader(new InputStreamReader(
						pid.getInputStream()), 4096);
				pid.waitFor();
				
				String tmpLine = "";
				while ((tmpLine = bufferedReader.readLine()) != null) {
					stringBuilder.append(tmpLine).append("\r\n");
				}
				
				resultLine = stringBuilder.toString();
			}
			
			bufferedReader.close();
		} catch (Exception e) {
			log.error("<=AppStatus=> executeShell" + e);
		}
		
		log.info("<=AppStatus=> executeShell running result[" + resultLine + "] and pid :[" + pid + "]");
		
		return resultLine;
	}

}
