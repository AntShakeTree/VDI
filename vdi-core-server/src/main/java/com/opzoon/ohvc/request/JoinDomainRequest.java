package com.opzoon.ohvc.request;

public class JoinDomainRequest {
	private String domainname;
	private String account;
	private String password;
	private Boolean restart;
	public String getDomainname() {
		return domainname;
	}
	public void setDomainname(String domainname) {
		this.domainname = domainname;
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
