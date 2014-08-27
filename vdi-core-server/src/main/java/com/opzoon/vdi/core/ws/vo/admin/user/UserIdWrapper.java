package com.opzoon.vdi.core.ws.vo.admin.user;

import java.io.Serializable;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "iduser")
public class UserIdWrapper implements Serializable {

	private static final long serialVersionUID = 1L;

	private int iduser;
	
	public int getIduser() {
		return iduser;
	}
	public void setIduser(int iduser) {
		this.iduser = iduser;
	}
	
}