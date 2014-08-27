/**   
 * @Title: CreateZkConf.java 
 * Package com.opzoon.appstatus.shell 
 * Description: TODO(用一句话描述该文件做什么) 
 * @author David   
 * @date 2013-8-1 下午4:56:02 
 * @version V1.0   
 */
package com.opzoon.appstatus.executor.zookeeper;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.FileCopyUtils;

import com.opzoon.appstatus.common.AppStatusConstants;

@Component("zkConf")
public class ZooKeeperConfiguration {
	private static Logger log = Logger.getLogger(ZooKeeperConfiguration.class);

	//需要写到配置文件中去
	@Value("#{propertiesReader[tickTime]}")
	private String tickTime;
	@Value("#{propertiesReader[initLimit]}")
	private String initLimit;
	@Value("#{propertiesReader[syncLimit]}")
	private String syncLimit;
	@Value("#{propertiesReader[clientPort]}")
	private String clientPort;
	
	private String dataDir = AppStatusConstants.DATA_DIR;
	private String dataLogDir = AppStatusConstants.DATA_LOG_DIR;

	private static String separator = System.getProperty("line.separator");
	
	/*public ZooKeeperConfiguration() throws IOException {
		Properties prop = new Properties();
		InputStream in = new FileInputStream(AppStatusConstants.ZK_PROP_PATH);
		prop.load(in);
		
		this.tickTime = prop.getProperty("tickTime");
		this.initLimit = prop.getProperty("initLimit");
		this.syncLimit = prop.getProperty("syncLimit");
		this.clientPort = prop.getProperty("clientPort");
		
		this.dataDir = AppStatusConstants.DATA_DIR;
		this.dataLogDir = AppStatusConstants.DATA_LOG_DIR;
	}*/
	
	public void createZkConf() throws IOException {
		//TODO 创建zoo.cfg(要覆盖)
		log.info("<=AppStatus=> createZkConf" + AppStatusConstants.ZK_CONF_PATH);
		
		StringBuilder settings = new StringBuilder();
		settings.append("tickTime=").append(tickTime).append(separator)
				.append("initLimit=").append(initLimit).append(separator)
				.append("syncLimit=").append(syncLimit).append(separator)
				.append("dataDir=").append(dataDir).append(separator)
				.append("dataLogDir=").append(dataLogDir).append(separator)
				.append("clientPort=").append(clientPort).append(separator);
		
		/*if(isZkConfExist()) {
			settings.append(getServerProp(readZkConf()));
		}*/
		
		Writer out = new OutputStreamWriter(new FileOutputStream(AppStatusConstants.ZK_CONF_PATH));
		FileCopyUtils.copy(settings.toString(), out);
		
	}
	
	public String readZkConf() throws IOException {
		//TODO 读取server配置
		log.info("<=AppStatus=> readZkConf");
		
		File file = new File(AppStatusConstants.ZK_CONF_PATH);
		BufferedReader in = new BufferedReader(new FileReader(file));
		StringBuilder settings = new StringBuilder();
		String line = null;
		while(null != (line = in.readLine())) {
			if(!line.startsWith("#")) {
				settings.append(line).append(separator);
			}
		}
		
		return settings.toString();
	}
	
	/*public void updateZkConf() throws IOException {
		//TODO 更新zookeeper
		log.info("<=AppStatus=> updateZkConf");
		
		StringBuilder settings = new StringBuilder();
		settings.append("tickTime=").append(tickTime).append(separator)
				.append("initLimit=").append(initLimit).append(separator)
				.append("syncLimit=").append(syncLimit).append(separator)
				.append("dataDir=").append(dataDir).append(separator)
				.append("dataLogDir=").append(dataLogDir).append(separator)
				.append("clientPort=").append(clientPort).append(separator)
				.append(getServerProp(readZkConf()));
		
		Writer out = new OutputStreamWriter(new FileOutputStream(AppStatusConstants.ZK_CONF_PATH));
		FileCopyUtils.copy(settings.toString(), out);
	}*/
	
	/*private boolean isZkConfExist() {
		//TODO 判断zoo.cfg文件是否已经生成
		log.info("<=AppStatus=> isConfExist");
		
		File file = new File(AppStatusConstants.ZK_CONF_PATH);
		
		return file.exists();
	}
	
	private static String getServerProp(String conf) {
		//TODO 从配置文件中筛选出server配置
		log.info("<=AppStatus=> getServerProp");
		
		StringBuilder servers = new StringBuilder();
		for(String line : conf.split(separator)) {
			if(line.startsWith("server.")) {
				servers.append(line);
			}
		}
		
		return servers.toString();
	}*/
	
}
