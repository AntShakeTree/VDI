package com.vdi.vo.req;



public class LoginInfo {
	private String username;
	private String password;
	private String source;
	private String domainguid;
	private int logintype;
	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	public String getSource() {
		return source;
	}
	public void setSource(String source) {
		this.source = source;
	}
	public String getDomainguid() {
		return domainguid;
	}
	public void setDomainguid(String domainguid) {
		this.domainguid = domainguid;
	}
	public int getLogintype() {
		return logintype;
	}
	public void setLogintype(int logintype) {
		this.logintype = logintype;
	}
	
}
