package com.opzoon.appstatus.domain;


public class RepairNode {
	private String  host;
	private String password;
	private String username="vdicore";
	private String command;
	private boolean needRepair=true;
	
	
	public boolean isNeedRepair() {
		return needRepair;
	}

	public void setNeedRepair(boolean needRepair) {
		this.needRepair = needRepair;
	}

	public RepairNode(String host,String password){
		this.host=host;
		this.username="vdicore";
		this.password=password;
	}
	
	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getCommand() {
		return command;
	}

	public void setCommand(String command) {
		this.command = command;
	}

	public void setHost(String host) {
		this.host = host;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getHost(){
		return this.host;
	}
	public String getPassword(){
		return this.password;
	}
	public String getUseranme() {
		return this.username;
	}
}
