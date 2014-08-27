package com.opzoon.vdi.core.ws.vo.entrance;

import java.io.Serializable;

public class AuthenticationMethodParamDef implements Serializable {

	private static final long serialVersionUID = 1L;

	private String paramname;
	private int paramtype;
	
	public String getParamname() {
		return paramname;
	}
	public void setParamname(String paramname) {
		this.paramname = paramname;
	}
	public int getParamtype() {
		return paramtype;
	}
	public void setParamtype(int paramtype) {
		this.paramtype = paramtype;
	}
}