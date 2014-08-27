package com.opzoon.ohvc.request;

public class JoinWorkgroupRequest {
	private String workgroupname;
	private String account;
	private String password;
	private Boolean restart;
	public String getWorkgroupname() {
		return workgroupname;
	}
	public void setWorkgroupname(String workgroupname) {
		this.workgroupname = workgroupname;
	}
	public String getAccount() {
		return account;
	}
	public void setAccount(String account) {
		this.account = account;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	public Boolean getRestart() {
		return restart;
	}
	public void setRestart(Boolean restart) {
		this.restart = restart;
	}
	
}
