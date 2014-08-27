package com.opzoon.ohvc.request;

public class SetHostnameRequest {
	private String hostname;
	private Integer type;
	private String account;
	private String  password;
	private Boolean restart;
	public String getHostname() {
		return hostname;
	}
	public void setHostname(String hostname) {
		this.hostname = hostname;
	}
	public Integer getType() {
		return type;
	}
	public void setType(Integer type) {
		this.type = type;
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
