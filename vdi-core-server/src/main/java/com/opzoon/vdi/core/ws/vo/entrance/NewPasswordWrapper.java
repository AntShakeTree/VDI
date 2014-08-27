package com.opzoon.vdi.core.ws.vo.entrance;

import java.io.Serializable;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "newpassword")
public class NewPasswordWrapper implements Serializable {

	private static final long serialVersionUID = 1L;

	private String oldpassword;
	private String newpassword;

	public String getOldpassword() {
		return oldpassword;
	}

	public void setOldpassword(String oldpassword) {
		this.oldpassword = oldpassword;
	}

	public String getNewpassword() {
		return newpassword;
	}

	public void setNewpassword(String newpassword) {
		this.newpassword = newpassword;
	}
	
}