package com.opzoon.vdi.core.ws.vo.admin.user;

import java.io.Serializable;

import javax.xml.bind.annotation.XmlRootElement;

import com.opzoon.vdi.core.facade.FacadeHelper.PagingInfo;

@XmlRootElement(name = "listParam")
public class ListUsersParam extends PagingInfo implements Serializable {

	private static final long serialVersionUID = 1L;

	private int iduser;
	private int usertype;
	private int domainid;
	private int organizationid;
  private int groupid;
  private int norsa;
	
	public int getIduser() {
		return iduser;
	}
	public void setIduser(int iduser) {
		this.iduser = iduser;
	}
	public int getUsertype() {
		return usertype;
	}
	public void setUsertype(int usertype) {
		this.usertype = usertype;
	}
	public int getDomainid() {
		return domainid;
	}
	public void setDomainid(int domainid) {
		this.domainid = domainid;
	}
	public int getOrganizationid() {
		return organizationid;
	}
	public void setOrganizationid(int organizationid) {
		this.organizationid = organizationid;
	}
	public int getGroupid() {
		return groupid;
	}
	public void setGroupid(int groupid) {
		this.groupid = groupid;
	}
  public int getNorsa()
  {
    return norsa;
  }
  public void setNorsa(int norsa)
  {
    this.norsa = norsa;
  }
	
}