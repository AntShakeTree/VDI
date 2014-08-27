package com.opzoon.appstatus.domain;


public class NodeConfig {
	private String clientConectionConfig;
	private String serverConfig;

	public String getClientConectionConfig() {
		return clientConectionConfig;
	}

	public NodeConfig setClientConectionConfig(String clientConectionConfig) {
		this.clientConectionConfig = clientConectionConfig;
		return this;
	}

	public String getServerConfig() {
		return serverConfig;
	}

	public NodeConfig setServerConfig(String serverConfig) {
		this.serverConfig = serverConfig;
		return this;
	}

}
