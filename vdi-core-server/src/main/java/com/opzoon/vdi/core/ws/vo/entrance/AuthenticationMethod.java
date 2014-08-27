package com.opzoon.vdi.core.ws.vo.entrance;

import java.io.Serializable;
import java.util.List;

public class AuthenticationMethod implements Serializable {

	private static final long serialVersionUID = 1L;

	private String methodname;
	private String methodversion;
	private List<AuthenticationMethodParamDef> params;
	
	public String getMethodname() {
		return methodname;
	}
	public void setMethodname(String methodname) {
		this.methodname = methodname;
	}
	public String getMethodversion() {
		return methodversion;
	}
	public void setMethodversion(String methodversion) {
		this.methodversion = methodversion;
	}
	public List<AuthenticationMethodParamDef> getParams() {
		return params;
	}
	public void setParams(List<AuthenticationMethodParamDef> params) {
		this.params = params;
	}
	
}