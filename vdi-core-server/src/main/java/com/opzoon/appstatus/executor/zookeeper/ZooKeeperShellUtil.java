/**   
* @Title: ZooKeeperShell.java 
* Package com.opzoon.appstatus.zookeeper 
* Description: TODO(用一句话描述该文件做什么) 
* @author David   
* @date 2013-8-9 上午9:14:15 
* @version V1.0   
*/
package com.opzoon.appstatus.executor.zookeeper;

import org.apache.log4j.Logger;

import com.opzoon.appstatus.common.JavaShellUtil;

public class ZooKeeperShellUtil {
	
	private static Logger log = Logger.getLogger(ZooKeeperShellUtil.class);
	
	public static boolean checkExecuteAuthority(String shellPath) {
		//TODO 检查shell脚本是否用可执行权限
		log.info("<=AppStatus=> checkExecuteAuthority shellPath=[" + shellPath + "]");
		
		return 'x' == JavaShellUtil.executeShell("ls -al " + shellPath).charAt(3);
	}
	
	public static void addExecuteAuthority(String shellPath) {
		//TODO 给shell脚本可执行权限
		log.info("<=AppStatus=> addExecuteAuthority shellPath=[" + shellPath + "]");
		
		JavaShellUtil.executeShell("chmod +x " + shellPath);
	}
	
	public static void changeDosToUnix(String shellPath) {
		//TODO 将shell从dos转为unix
		log.info("<=AppStatus=> changeDosToUnix shellPath=[" + shellPath + "]");
		
		//JavaShellUtil.executeShell("sed -i s/\\^M//g " + shellPath);
		//JavaShellUtil.executeShell("sed -i s/.$//g " + shellPath);
		JavaShellUtil.executeShell("sed -i s/\\r//g " + shellPath);
	}
	
}
