package com.opzoon.vdi.core.support;

public class Configuration {

	private String brokerName;
	private String brokerIP;
	private int brokerPortMin;
	private int brokerPortMax;
	private String httpTunnelName;
	private int httpTunnelPort;
	private String httpsTunnelName;
	private int httpsTunnelPort;

	public String getBrokerName() {
		return brokerName;
	}

	public void setBrokerName(String brokerName) {
		this.brokerName = brokerName;
	}

	public String getBrokerIP() {
		return brokerIP;
	}

	public void setBrokerIP(String brokerIP) {
		this.brokerIP = brokerIP;
	}

	public int getBrokerPortMin() {
		return brokerPortMin;
	}

	public void setBrokerPortMin(int brokerPortMin) {
		this.brokerPortMin = brokerPortMin;
	}

	public int getBrokerPortMax() {
		return brokerPortMax;
	}

	public void setBrokerPortMax(int brokerPortMax) {
		this.brokerPortMax = brokerPortMax;
	}

	public String getHttpTunnelName() {
		return httpTunnelName;
	}

	public void setHttpTunnelName(String httpTunnelName) {
		this.httpTunnelName = httpTunnelName;
	}

	public int getHttpTunnelPort() {
		return httpTunnelPort;
	}

	public void setHttpTunnelPort(int httpTunnelPort) {
		this.httpTunnelPort = httpTunnelPort;
	}

	public String getHttpsTunnelName() {
		return httpsTunnelName;
	}

	public void setHttpsTunnelName(String httpsTunnelName) {
		this.httpsTunnelName = httpsTunnelName;
	}

	public int getHttpsTunnelPort() {
		return httpsTunnelPort;
	}

	public void setHttpsTunnelPort(int httpsTunnelPort) {
		this.httpsTunnelPort = httpsTunnelPort;
	}

}
