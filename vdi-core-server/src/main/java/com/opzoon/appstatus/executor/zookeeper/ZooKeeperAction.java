package com.opzoon.appstatus.executor.zookeeper;


import java.io.File;
import java.io.IOException;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.opzoon.appstatus.common.JavaShellUtil;
import com.opzoon.appstatus.common.AppStatusConstants;
import com.opzoon.appstatus.common.exception.AppstatusExceptionHandle;
import com.opzoon.appstatus.common.exception.AppstatusRestException;
import com.opzoon.appstatus.common.exception.custom.AppstatusZookeeperException;

@Component
public class ZooKeeperAction {

	private static Logger log = Logger.getLogger(ZooKeeperAction.class);
	@Autowired
	private ZooKeeperConfiguration zkConf;
	
	/*public boolean isInitZooKeeper() {
		//TODO 检查zookeeper配置文件是否完成初始化
		log.info("<=AppStatus=> isFinishInit");
		
		return zkConf.isZkConfExist();
	}*/

	public void initZooKeeper() throws IOException {
		//TODO 初始化zookeeper，包括生成zoo.cfg以及给zkServer.sh赋予执行权限
		log.info("<=AppStatus=> initZooKeeper");
		//创建zoo.cfg
		zkConf.createZkConf();
		
		//创建myid和log的目录
		File dataFile = new File(AppStatusConstants.DATA_DIR);
		File logFile = new File(AppStatusConstants.DATA_LOG_DIR);
		if(!dataFile.exists()) dataFile.mkdir();
		if(!logFile.exists()) logFile.mkdir();
		
		//给zkServer.sh加上可执行权限
		if(!ZooKeeperShellUtil.checkExecuteAuthority(AppStatusConstants.ZK_BIN_PATH)) {
			ZooKeeperShellUtil.addExecuteAuthority(AppStatusConstants.ZK_BIN_PATH);
		}
		
	}
	
	public void updateZooKeeper(String servers, long myid) throws AppstatusRestException {
		//TODO 更新zookeeper的配置
		log.info("<=AppStatus=> updateZooKeeper [" + servers + "]");
		initZooKeeperShell(AppStatusConstants.START_SHELL);
		
		//if(!checkResult(JavaShellUtil.executeShell(AppStatusConstants.START_SHELL + " '" + AppStatusConstants.ZK_PATH + "' '" + servers + "' '" + myid + "'"), "STARTED")) {
		if(!checkResult(JavaShellUtil.executeShell(AppStatusConstants.START_SHELL + " " + AppStatusConstants.ZK_PATH + " " + servers + " " + myid), "STARTED")) {
			//throw new ZooKeeperException("zookeeper start failure");
			throw AppstatusExceptionHandle.throwAppstatusException(new AppstatusZookeeperException());
		}
	}
	
	public void stopZooKeeper() throws AppstatusRestException {
		//TODO 关闭zookeeper
		log.info("<=AppStatus=> stopZooKeeper");
		initZooKeeperShell(AppStatusConstants.STOP_SHELL);
		
		//if(!checkResult(JavaShellUtil.executeShell(AppStatusConstants.STOP_SHELL + " '" + AppStatusConstants.ZK_PATH + "'"), "STOPPED")) {
		if(!checkResult(JavaShellUtil.executeShell(AppStatusConstants.STOP_SHELL + " " + AppStatusConstants.ZK_PATH), "STOPPED")) {
			throw AppstatusExceptionHandle.throwAppstatusException(new AppstatusZookeeperException());
		}
	}
	
	public boolean isStandaloneZooKeeper() {
		//TODO 检查zookeeper状态
		log.info("<=AppStatus=> isStandaloneZooKeeper");
		initZooKeeperShell(AppStatusConstants.STATUS_SHELL);
		
		//return checkResult(JavaShellUtil.executeShell(AppStatusConstants.STATUS_SHELL + " '" + AppStatusConstants.ZK_PATH + "'"), "standalone");
		return checkResult(JavaShellUtil.executeShell(AppStatusConstants.STATUS_SHELL + " " + AppStatusConstants.ZK_PATH), "standalone");
	}
	
	public boolean isStartedZooKeeper() {
		//TODO 检查zookeeper状态
		log.info("<=AppStatus=> isStartedZooKeeper");
		initZooKeeperShell(AppStatusConstants.STATUS_SHELL);
		
		return !checkResult(JavaShellUtil.executeShell(AppStatusConstants.STATUS_SHELL), "not running");
	}
	
	private void initZooKeeperShell(String shellPath) {
		//TODO 为shell脚本增加执行权限以及将dos转换为unix
		log.info("<=AppStatus=> initZooKeeperShell");
		
		if(!ZooKeeperShellUtil.checkExecuteAuthority(shellPath)) {
			ZooKeeperShellUtil.addExecuteAuthority(shellPath);
		}
		ZooKeeperShellUtil.changeDosToUnix(shellPath);
	}
	
	private static boolean checkResult(String result, String key) {
		//TODO 检查脚本的运行结果
		log.info("<=AppStatus=> checkResult [" + result + "]");
		
		return (result.indexOf(key) != -1);
	}

	public ZooKeeperConfiguration getZkConf() {
		return zkConf;
	}

	public void setZkConf(ZooKeeperConfiguration zkConf) {
		this.zkConf = zkConf;
	}
	
	
}
