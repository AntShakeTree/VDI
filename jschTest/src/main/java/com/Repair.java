package com;

public  class Repair {
	SSHConncetion conncetion;
	public SSHConncetion getConncetion() {
		return conncetion;
	}

	public void setConncetion(SSHConncetion conncetion) {
		this.conncetion = conncetion;
	}

	public boolean repair(String command) {
		return true;
	}

	public boolean normal(String command) {
		return true;
	}
	public  Repair bulid(String host,String username,String password){return this;}
}
