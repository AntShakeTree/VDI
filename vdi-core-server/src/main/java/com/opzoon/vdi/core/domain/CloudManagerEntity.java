package com.opzoon.vdi.core.domain;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * VDI可使用的底层虚拟化管理平台.
 */
@XmlRootElement(name = "cloudManager")
@Entity
@Table(name = "cloudmanager")
public class CloudManagerEntity implements Serializable {

	private static final long serialVersionUID = 1L;
	
	private Integer idcloudmanager;
	private String cloudname;
	private String baseurl;
	private String username;
	private String password;
	private String domain;
	private String clouddrivername;
	private String notes;
	private int status;

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	public Integer getIdcloudmanager() {
		return idcloudmanager;
	}
	public void setIdcloudmanager(Integer idcloudmanager) {
		this.idcloudmanager = idcloudmanager;
	}
	/**
	 * @return 虚拟化管理平台名称.
	 */
	public String getCloudname() {
		return cloudname;
	}
	public void setCloudname(String cloudname) {
		this.cloudname = cloudname;
	}
	/**
	 * @return API路径.
	 */
	public String getBaseurl() {
		return baseurl;
	}
	public void setBaseurl(String baseurl) {
		this.baseurl = baseurl;
	}
	/**
	 * @return 管理员用户名.
	 */
	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
	}
	/**
	 * @return 管理员口令.
	 */
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	/**
	 * @return 管理员域.
	 */
	public String getDomain() {
		return domain;
	}
	public void setDomain(String domain) {
		this.domain = domain;
	}
	/**
	 * @return Cloudstack/Openstack/OpzoonCloud.
	 */
	public String getClouddrivername() {
		return clouddrivername;
	}
	public void setClouddrivername(String clouddrivername) {
		this.clouddrivername = clouddrivername;
	}
	/**
	 * @return 备注.
	 */
	public String getNotes() {
		return notes;
	}
	public void setNotes(String notes) {
		this.notes = notes;
	}
	/**
	 * @return 状态. 参考{@link CloudManagerStatus#CLOUD_MANAGER_STATUS_OK}, {@link CloudManagerStatus#CLOUD_MANAGER_STATUS_ABNORMAL}.
	 */
	@Transient
	public int getStatus() {
		return status;
	}
	public void setStatus(int status) {
		this.status = status;
	}
	
}
