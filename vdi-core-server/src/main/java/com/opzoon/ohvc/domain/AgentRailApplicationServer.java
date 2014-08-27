/**   
 * @Title: RailApplicationServer.java 
 * @Package com.opzoon.vdi.core.domain 
 * @Description: TODO(用一句话描述该文件做什么) 
 * @author david   
 * @date 2013-1-26 上午11:23:29 
 * @version V1.0   
 */
package com.opzoon.ohvc.domain;

import java.io.Serializable;

import com.opzoon.ohvc.common.Regular;
import com.opzoon.ohvc.common.anotation.Required;

/**
 * ClassName: RailApplicationServer
 * 
 * @Description: 虚拟应用服务器的实体类
 * @author David
 * @date 2013-1-26 上午11:23:29
 */
public class AgentRailApplicationServer implements Serializable, Comparable<AgentRailApplicationServer> {

	private static final long serialVersionUID = 6531463316905506085L;
	public static int RAIL_APPLICATION_SERVER_STATUS_STOPPED = 0;// ：关闭（RAIL_APPLICATION_SERVER_STATUS_STOPPED）
	public static int RAIL_APPLICATION_SERVER_STATUS_RUNNING = 1;// ：不提供RAIL服务（RAIL_APPLICATION_SERVER_STATUS_RUNNING）
	public static int RAIL_APPLICATION_SERVER_STATUS_SERVING = 2;// ：可提供RAIL服务（RAIL_APPLICATION_SERVER_STATUS_SERVING）
	public static int RAIL_APPLICATION_SERVER_STATUS_ERROR = 256;// ：错误（RAIL_APPLICATION_SERVER_STATUS_ERROR）
	public static int RAIL_APPLICATION_SERVER_STATUS_DELEING = 257;// ：正在删除 （RAIL_APPLICATION_SERVER_STATUS_DELEING）
	public static String RAIL_JOIN_TYPE_UNKNOWN = "unknown";
	public static String RAIL_JOIN_TYPE_DOMAIN = "domain";
	public static String RAIL_JOIN_TYPE_GROUP = "group";
	public static String RAIL_JOIN_TYPE_UNJOINED = "unjoined";

	// 主键
	private Integer idapplicationserver;// id
	@Required(regular = { Regular.INETADDRESS, Regular.UNREPEATABLE })
	private String servername;// 虚拟应用服务器名称
	private int servertype;
	private String notes;
	//
	private int timespan;
	private String cpuload;// CPU负载（百分比）
	private String memload;// 内存负载（百分比）
	private String networkload;// 网络负载
	private int connections;// 当前连接数
	private int appinstalled;// 发布虚拟应用数量（Agent报告的虚拟应用数量）
	private int apppublished;// 安装虚拟应用数量（管理员确认发布的虚拟应用数量）
	private int status = RAIL_APPLICATION_SERVER_STATUS_SERVING;
	private String jointype;// 主机所在组织的类型 unknown：未知状 unjoined：没有加入任何组织 group：加入了工作组 domain：加入了域
	private String joinname = "";// 所在域或工作组的名字

	private String applicationpath;

	/**
	 * @return joinname
	 */
	public String getJoinname() {
		return joinname;
	}

	/**
	 * @return applicationpath
	 */
	public String getApplicationpath() {
		return applicationpath;
	}

	/**
	 * @param applicationpath
	 *            the applicationpath to set
	 */
	public void setApplicationpath(String applicationpath) {
		this.applicationpath = applicationpath;
	}

	/**
	 * @return jointype
	 */
	public String getJointype() {
		return jointype;
	}

	/**
	 * @param jointype
	 *            the jointype to set
	 */
	public void setJointype(String jointype) {
		this.jointype = jointype;
	}

	/**
	 * @param joinname
	 *            the joinname to set
	 */
	public void setJoinname(String joinname) {
		this.joinname = joinname;
	}

	/**
	 * @return status
	 */
	public int getStatus() {
		return status;
	}

	/**
	 * @param status
	 *            the status to set
	 */
	public void setStatus(int status) {
		this.status = status;
	}

	/**
	 * @return connections
	 */
	public int getConnections() {
		return connections;
	}

	/**
	 * @param connections
	 *            the connections to set
	 */
	public void setConnections(int connections) {
		this.connections = connections;
	}

	/**
	 * @return appinstalled
	 */
	public int getAppinstalled() {
		return appinstalled;
	}

	/**
	 * @param appinstalled
	 *            the appinstalled to set
	 */
	public void setAppinstalled(int appinstalled) {
		this.appinstalled = appinstalled;
	}

	/**
	 * @return apppublished
	 */
	public int getApppublished() {
		return apppublished;
	}

	/**
	 * @param apppublished
	 *            the apppublished to set
	 */
	public void setApppublished(int apppublished) {
		this.apppublished = apppublished;
	}

	/**
	 * @return idapplicationserver
	 */
	public Integer getIdapplicationserver() {
		return idapplicationserver;
	}

	/**
	 * @param idapplicationserver
	 *            the idapplicationserver to set
	 */
	public void setIdapplicationserver(Integer idapplicationserver) {
		this.idapplicationserver = idapplicationserver;
	}

	/**
	 * @return servername
	 */
	public String getServername() {
		return servername;
	}

	/**
	 * @param servername
	 *            the servername to set
	 */
	public void setServername(String servername) {
		this.servername = servername;
	}

	/**
	 * @return servertype
	 */
	public int getServertype() {
		return servertype;
	}

	/**
	 * @param servertype
	 *            the servertype to set
	 */
	public void setServertype(int servertype) {
		this.servertype = servertype;
	}

	/**
	 * @return notes
	 */
	public String getNotes() {
		return notes;
	}

	/**
	 * @param notes
	 *            the notes to set
	 */
	public void setNotes(String notes) {
		this.notes = notes;
	}

	/**
	 * @return timespan
	 */
	public int getTimespan() {
		return timespan;
	}

	/**
	 * @param timespan
	 *            the timespan to set
	 */
	public void setTimespan(int timespan) {
		this.timespan = timespan;
	}

	/**
	 * @return cpuload
	 */
	public String getCpuload() {
		return cpuload;
	}

	/**
	 * @param cpuload
	 *            the cpuload to set
	 */
	public void setCpuload(String cpuload) {
		this.cpuload = cpuload;
	}

	/**
	 * @return memload
	 */
	public String getMemload() {
		return memload;
	}

	/**
	 * @param memload
	 *            the memload to set
	 */
	public void setMemload(String memload) {
		this.memload = memload;
	}

	/**
	 * @return networkload
	 */
	public String getNetworkload() {
		return networkload;
	}

	/**
	 * @param networkload
	 *            the networkload to set
	 */
	public void setNetworkload(String networkload) {
		this.networkload = networkload;
	}

	@Override
	public int compareTo(AgentRailApplicationServer o) {
		return this.connections - o.connections;
	}

}
