package com.opzoon.vdi.core.domain;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

import org.springframework.web.bind.annotation.ResponseBody;

/**
 * 系统支持的域, 系统默认存在一个ID为0的本地域.
 */
@Entity
public class Domain  implements Serializable {

	private static final long serialVersionUID = 1L;

	/**
	 * 域类型: 本地域.
	 */
	public static final int DOMAIN_TYPE_LOCAL = 0x0;
	/**
	 * 域类型: MSAD同步域.
	 */
	public static final int DOMAIN_TYPE_MSAD = 0x1;
	/**
	 * 域类型: ApacheDS同步域.
	 */
	public static final int DOMAIN_TYPE_APACHE = 0x2;

	/**
	 * 状态: 正常.
	 */
	public static final int DOMAIN_STATUS_NORMAL = 0;
	/**
	 * 状态: 维护中.
	 */
	public static final int DOMAIN_STATUS_MAINTAINING = 1;
	/**
	 * 状态: 删除中.
	 */
	public static final int DOMAIN_STATUS_DELETING = 2;
	/**
	 * 状态: 同步中.
	 */
	public static final int DOMAIN_STATUS_SYNCHRONIZING = 3;

	/**
	 * 默认本地域的ID.
	 */
	public static final int DEFAULT_DOMAIN_ID = 0;
	
	private Integer iddomain;
	private int domaintype;
	private String guid="";
	private String domainname;
	private String domainnetworkname;
	private String domainservername;
	private int domainserverport;
	private String domainbinddn;
	private String domainbindpass;
	private String notes;
	private int status;
	private Integer ownerthread;
	
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	public Integer getIddomain() {
		return iddomain;
	}
	public void setIddomain(Integer iddomain) {
		this.iddomain = iddomain;
	}
	/**
	 * @return 域类型. 参考{@link Domain#DOMAIN_TYPE_LOCAL}, {@link Domain#DOMAIN_TYPE_MSAD}, {@link Domain#DOMAIN_TYPE_APACHE}.
	 */
	public int getDomaintype() {
		return domaintype;
	}
	public void setDomaintype(int domaintype) {
		this.domaintype = domaintype;
	}
  public String getGuid()
  {
    return guid;
  }
  public void setGuid(String guid)
  {
    this.guid = guid;
  }
	/**
	 * @return 域名称.
	 */
	public String getDomainname() {
		return domainname;
	}
	public void setDomainname(String domainname) {
		this.domainname = domainname;
	}
	/**
	 * @return 域网络名称.
	 */
	public String getDomainnetworkname() {
		return domainnetworkname;
	}
	public void setDomainnetworkname(String domainnetworkname) {
		this.domainnetworkname = domainnetworkname;
	}
	/**
	 * @return 域服务器地址.
	 */
	public String getDomainservername() {
		return domainservername;
	}
	public void setDomainservername(String domainservername) {
		this.domainservername = domainservername;
	}
	/**
	 * @return 域服务器端口.
	 */
	public int getDomainserverport() {
		return domainserverport;
	}
	public void setDomainserverport(int domainserverport) {
		this.domainserverport = domainserverport;
	}
	/**
	 * @return 域服务器用户名.
	 */
	public String getDomainbinddn() {
		return domainbinddn;
	}
	public void setDomainbinddn(String domainbinddn) {
		this.domainbinddn = domainbinddn;
	}
	/**
	 * @return 域服务器口令.
	 */
	public String getDomainbindpass() {
		return domainbindpass;
	}
	public void setDomainbindpass(String domainbindpass) {
		this.domainbindpass = domainbindpass;
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
	 * @return 状态. 0 正常; 1 维护中; 2 删除中; 3 同步中.
	 */
	public int getStatus() {
		return status;
	}
	public void setStatus(int status) {
		this.status = status;
	}
	/**
	 * @return 当前正在操作的线程ID.
	 */
	public Integer getOwnerthread() {
		return ownerthread;
	}
	public void setOwnerthread(Integer ownerthread) {
		this.ownerthread = ownerthread;
	}
	
}
