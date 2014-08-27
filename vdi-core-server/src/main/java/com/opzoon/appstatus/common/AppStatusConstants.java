/**   
 * @Title: ZooKeeperConstant.java 
 * Package com.opzoon.appstatus.zookeeper 
 * Description: TODO(用一句话描述该文件做什么) 
 * @author David   
 * @date 2013-8-15 下午4:14:48 
 * @version V1.0   
 */
package com.opzoon.appstatus.common;

/**
 * ClassName: ZooKeeperConstant Description: 常量
 * 
 * @author zhengyi
 * @date 2013-8-15 下午4:14:48
 * @version V0.2.1023（迭代3）  
 */
public class AppStatusConstants
{

	private final static String BASE_PATH = System.getProperty("appstatus.root");
	
	public final static String ACTIVEMQ_PATH = BASE_PATH + "activemq-data";
	
	public final static String ZK_PATH = BASE_PATH + "WEB-INF/zookeeper";

	public final static String ZK_CONF_PATH = BASE_PATH + "WEB-INF/zookeeper/conf/zoo.cfg";

	public final static String ZK_BIN_PATH = BASE_PATH + "WEB-INF/zookeeper/bin/zkServer.sh";

	public final static String PROPERTIES_PATH = BASE_PATH + "WEB-INF/classes/appstatus.properties";
	
	public final static String ENV_PATH = BASE_PATH + "WEB-INF/classes/env.properties";

	public final static String START_SHELL = BASE_PATH + "WEB-INF/classes/zkStart.sh";

	public final static String STOP_SHELL = BASE_PATH + "WEB-INF/classes/zkStop.sh";

	public final static String STATUS_SHELL = BASE_PATH + "WEB-INF/classes/zkStatus.sh";

	public final static String CHNAGEIPADDRESS_SHELL = BASE_PATH + "WEB-INF/classes/changeIPAddress.sh";
	
	public final static String DATA_DIR = BASE_PATH + "WEB-INF/zookeeper/data";

	public final static String DATA_LOG_DIR = BASE_PATH + "WEB-INF/zookeeper/logs";
	
	//public final static String NODE_PATH = "/vdi/cluster";
	public final static String CLUSTER_PATH = "/vdi/cluster";
	
	public final static String MASTER_PATH = "/vdi/master";
	
	public final static String MESSAGE_PATH = "/vdi/message";
	
	public final static String CONTINUE_PROCESS = "process";
	
	public final static String MASTER_NAME = "master";
	
	public final static String MESSAGE_HEAD = "message_";
	
	public final static int LATCH_TIMEOUT_SECOND = 20;
	
	public final static int MASTER_TIMEOUT_SECOND = 10;

	public final static int TIMER_DELAY_MILLISECOND = 30000;

}
