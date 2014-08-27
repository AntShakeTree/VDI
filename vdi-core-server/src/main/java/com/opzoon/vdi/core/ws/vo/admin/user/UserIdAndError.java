package com.opzoon.vdi.core.ws.vo.admin.user;

import java.io.Serializable;

public class UserIdAndError implements Serializable {

	private static final long serialVersionUID = 1L;

	private int iduser;
	private int error;
	
	public UserIdAndError(int iduser, int error) {
		this.iduser = iduser;
		this.error = error;
	}
	
	public int getIduser() {
		return iduser;
	}
	public void setIduser(int iduser) {
		this.iduser = iduser;
	}
	public int getError() {
		return error;
	}
	public void setError(int error) {
		this.error = error;
	}
	
}