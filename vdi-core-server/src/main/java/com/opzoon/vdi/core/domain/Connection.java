package com.opzoon.vdi.core.domain;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Transient;

import com.opzoon.vdi.core.cloud.ConnectionManager.ConnectionInfo;

/**
 * 用户访问资源开启的连接.
 */
@Entity
public class Connection implements Serializable {

	private static final long serialVersionUID = 1L;
	
	private Integer idconnection;
	private int sessionid;
	private int resourcetype;
	private int resourceid;
	private String hostname;
	private Integer hostport;
	private String brokername;
	private int brokerport;
	private int brokerprotocol;
	private String connectionticket;
	private Date expire;
	private Integer userid;
	private Integer desktopid;
	private boolean userCanNoLongerOwnThePool;
	private String username;
	private String password;
	private String domain;
	private String resourcename;
	private String tunnelname;
	private Integer tunnelport;
	private RestrictionStrategy restrictionstrategy;
	private boolean toShutdownAfterDestroy;
	private boolean toCloneAfterDestroy = true;

	public void copyFrom(ConnectionInfo connectionInfo) {
		this.setBrokername(connectionInfo.getBrokername());
		this.setConnectionticket(connectionInfo.getConnectionticket());
		this.setHostname(connectionInfo.getHostname());
		this.setBrokerport(connectionInfo.getBrokerport());
		this.setBrokerprotocol(connectionInfo.getBrokerprotocol());
		this.setHostport(connectionInfo.getHostport());
	}
	
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	public Integer getIdconnection() {
		return idconnection;
	}
	public void setIdconnection(Integer idconnection) {
		this.idconnection = idconnection;
	}
	/**
	 * @return 会话ID.
	 */
	public int getSessionid() {
		return sessionid;
	}
	public void setSessionid(int sessionid) {
		this.sessionid = sessionid;
	}
	/**
	 * @return 资源类别. 参考{@link ResourceAssignment#RESOURCE_TYPE_POOL}, {@link ResourceAssignment#RESOURCE_TYPE_APPLICATION}.
	 */
	public int getResourcetype() {
		return resourcetype;
	}
	public void setResourcetype(int resourcetype) {
		this.resourcetype = resourcetype;
	}
	/**
	 * @return 访问资源ID.
	 */
	public int getResourceid() {
		return resourceid;
	}
	public void setResourceid(int resourceid) {
		this.resourceid = resourceid;
	}
	/**
	 * @return 目标主机.
	 */
	public String getHostname() {
		return hostname;
	}
	public void setHostname(String hostname) {
		this.hostname = hostname;
	}
	/**
	 * @return 目标主机端口.
	 */
	public Integer getHostport() {
		return hostport;
	}
	public void setHostport(Integer hostport) {
		this.hostport = hostport;
	}
	/**
	 * @return 转发主机.
	 */
	public String getBrokername() {
		return brokername;
	}
	public void setBrokername(String brokername) {
		this.brokername = brokername;
	}
	/**
	 * @return 转发主机端口.
	 */
	public int getBrokerport() {
		return brokerport;
	}
	public void setBrokerport(int brokerport) {
		this.brokerport = brokerport;
	}
	/**
	 * @return 协议. 参考{@link Connection#PROTOCOL_UNLIMITED}, {@link Connection#PROTOCOL_HTTP}, {@link Connection#PROTOCOL_HTTPS}.
	 */
	public int getBrokerprotocol() {
		return brokerprotocol;
	}
	public void setBrokerprotocol(int brokerprotocol) {
		this.brokerprotocol = brokerprotocol;
	}
	/**
	 * @return 连接令牌.
	 */
	public String getConnectionticket() {
		return connectionticket;
	}
	public void setConnectionticket(String connectionticket) {
		this.connectionticket = connectionticket;
	}
	/**
	 * @return 有效截至时间.
	 */
	public Date getExpire() {
		return expire;
	}
	public void setExpire(Date expire) {
		this.expire = expire;
	}
	/**
	 * @return 用户ID.
	 */
	@Transient
	public Integer getUserid() {
		return userid;
	}
	public void setUserid(Integer userid) {
		this.userid = userid;
	}
	/**
	 * @return 桌面ID. 仅当资源为桌面池时有意义.
	 */
	@Transient
	public Integer getDesktopid() {
		return desktopid;
	}

	public void setDesktopid(Integer desktopid) {
		this.desktopid = desktopid;
	}

	@Transient
	public boolean isUserCanNoLongerOwnThePool() {
		return userCanNoLongerOwnThePool;
	}

	public void setUserCanNoLongerOwnThePool(boolean userCanNoLongerOwnThePool) {
		this.userCanNoLongerOwnThePool = userCanNoLongerOwnThePool;
	}

	@Transient
	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	@Transient
	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	@Transient
	public String getDomain() {
		return domain;
	}

	public void setDomain(String domain) {
		this.domain = domain;
	}

	@Transient
	public String getResourcename() {
		return resourcename;
	}

	public void setResourcename(String resourcename) {
		this.resourcename = resourcename;
	}

	public String getTunnelname() {
		return tunnelname;
	}

	public void setTunnelname(String tunnelname) {
		this.tunnelname = tunnelname;
	}

	public Integer getTunnelport() {
		return tunnelport;
	}

	public void setTunnelport(Integer tunnelport) {
		this.tunnelport = tunnelport;
	}

	@Transient
	public RestrictionStrategy getRestrictionstrategy() {
		return restrictionstrategy;
	}

	public void setRestrictionstrategy(RestrictionStrategy restrictionstrategy) {
		this.restrictionstrategy = restrictionstrategy;
	}

	@Transient
	public boolean isToShutdownAfterDestroy() {
		return toShutdownAfterDestroy;
	}

	public void setToShutdownAfterDestroy(boolean toShutdownAfterDestroy) {
		this.toShutdownAfterDestroy = toShutdownAfterDestroy;
	}

	@Transient
	public boolean isToCloneAfterDestroy() {
		return toCloneAfterDestroy;
	}

	public void setToCloneAfterDestroy(boolean toCloneAfterDestroy) {
		this.toCloneAfterDestroy = toCloneAfterDestroy;
	}
	
}
