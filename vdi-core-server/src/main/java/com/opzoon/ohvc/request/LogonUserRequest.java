package com.opzoon.ohvc.request;

public class LogonUserRequest {
	private String username;
	private String password;
	private int brokerprotocol;
	private String domain;
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
	public int getBrokerprotocol() {
		return brokerprotocol;
	}
	public void setBrokerprotocol(int brokerprotocol) {
		this.brokerprotocol = brokerprotocol;
	}
	public String getDomain() {
		return domain;
	}
	public void setDomain(String domain) {
		this.domain = domain;
	}
	
}
