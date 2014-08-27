package com.opzoon.vdi.core.domain;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Transient;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * 桌面池已经部署的桌面及其分配情况.
 */
@XmlRootElement(name = "desktop")
@Entity
public class Desktop implements Serializable {

	private static final long serialVersionUID = 1L;
	
	private Integer iddesktop;
	private int desktoppoolid;
	private String vmid;
	private String vmname;
	private int ownerid;
	private String ipaddress;
	private String ownername;
	private String realname;
	private int status;
	private String desktoppoolname;
	private int desktoppooltype;
	private Integer domainid;
	private String domainname;
  private int vmsource;
  private int assignment;

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	public Integer getIddesktop() {
		return iddesktop;
	}
	public void setIddesktop(Integer iddesktop) {
		this.iddesktop = iddesktop;
	}
	/**
	 * @return 桌面池ID.
	 */
	public int getDesktoppoolid() {
		return desktoppoolid;
	}
	public void setDesktoppoolid(int desktoppoolid) {
		this.desktoppoolid = desktoppoolid;
	}
	/**
	 * @return 虚拟机ID.
	 */
	public String getVmid() {
		return vmid;
	}
	public void setVmid(String vmid) {
		this.vmid = vmid;
	}
	/**
	 * @return 虚拟机名称.
	 */
	public String getVmname() {
		return vmname;
	}
	public void setVmname(String vmname) {
		this.vmname = vmname;
	}
	/**
	 * @return 分配到用户ID, 未分配的桌面或浮动池中的桌面为-1.
	 */
	public int getOwnerid() {
		return ownerid;
	}
	public void setOwnerid(int ownerid) {
		this.ownerid = ownerid;
	}
	public String getIpaddress() {
		return ipaddress;
	}
	public void setIpaddress(String ipaddress) {
		this.ipaddress = ipaddress;
	}
	/**
	 * @return 桌面状态. 参考{@link DesktopStatus#DESKTOP_STATUS_STOPPED}, {@link DesktopStatus#DESKTOP_STATUS_RUNNING}, {@link DesktopStatus#DESKTOP_STATUS_SERVING}, {@link DesktopStatus#DESKTOP_STATUS_CONNECTED}, {@link DesktopStatus#DESKTOP_STATUS_ERROR}.
	 */
	@Transient
	public int getStatus() {
		return status;
	}
	public void setStatus(int status) {
		this.status = status;
	}
	/**
	 * @return 分配的用户名，尚未分配的为空.
	 */
	public String getOwnername() {
		return ownername;
	}
	public void setOwnername(String ownername) {
		this.ownername = ownername;
	}
	public String getDesktoppoolname() {
		return desktoppoolname;
	}
	public void setDesktoppoolname(String desktoppoolname) {
		this.desktoppoolname = desktoppoolname;
	}
	@Transient
	public int getDesktoppooltype() {
		return desktoppooltype;
	}
	public void setDesktoppooltype(int desktoppooltype) {
		this.desktoppooltype = desktoppooltype;
	}
	@Transient
	public Integer getDomainid() {
		return domainid;
	}
	public void setDomainid(Integer domainid) {
		this.domainid = domainid;
	}
	@Transient
	public String getDomainname() {
		return domainname;
	}
	public void setDomainname(String domainname) {
		this.domainname = domainname;
	}
  public int getVmsource()
  {
    return vmsource;
  }
  public void setVmsource(int vmsource)
  {
    this.vmsource = vmsource;
  }
  public int getAssignment()
  {
    return assignment;
  }
  public void setAssignment(int assignment)
  {
    this.assignment = assignment;
  }
public String getRealname() {
	return realname;
}
public void setRealname(String realname) {
	this.realname = realname;
}
	
}
