package com.opzoon.vdi.core.ws.vo.admin.user;

import java.io.Serializable;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "param")
public class PrivilegeParam implements Serializable {

	private static final long serialVersionUID = 1L;

	private int userid;
	private int targettype;
	private int targetid;
	
	public int getUserid() {
		return userid;
	}
	public void setUserid(int userid) {
		this.userid = userid;
	}
	public int getTargettype() {
		return targettype;
	}
	public void setTargettype(int targettype) {
		this.targettype = targettype;
	}
	public int getTargetid() {
		return targetid;
	}
	public void setTargetid(int targetid) {
		this.targetid = targetid;
	}
	
}