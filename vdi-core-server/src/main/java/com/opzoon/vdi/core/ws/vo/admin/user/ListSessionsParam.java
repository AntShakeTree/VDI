package com.opzoon.vdi.core.ws.vo.admin.user;

import java.io.Serializable;

import javax.xml.bind.annotation.XmlRootElement;

import com.opzoon.vdi.core.facade.FacadeHelper.PagingInfo;

@XmlRootElement(name = "listParam")
public class ListSessionsParam extends PagingInfo implements Serializable {

	private static final long serialVersionUID = 1L;

	private int userid;
	private int logintype;
	
	public int getUserid() {
		return userid;
	}
	public void setUserid(int userid) {
		this.userid = userid;
	}
	public int getLogintype() {
		return logintype;
	}
	public void setLogintype(int logintype) {
		this.logintype = logintype;
	}
	
}