package com.vdi.gateway;

public  class ConnectionInfo {

	/**
	 */
	public static final int PROTOCOL_UNLIMITED = 0;
	/**
	 */
	public static final int PROTOCOL_HTTP = 1;
	/**
	 */
	public static final int PROTOCOL_HTTPS = 2;

	private String hostname;
	private int hostport;
	private String brokername;
	private int brokerport;
	private int brokerprotocol;
	private String connectionticket;
	
	public String getHostname() {
		return hostname;
	}
	public void setHostname(String hostname) {
		this.hostname = hostname;
	}
	public int getHostport() {
		return hostport;
	}
	public void setHostport(int hostport) {
		this.hostport = hostport;
	}
	public String getBrokername() {
		return brokername;
	}
	public void setBrokername(String brokername) {
		this.brokername = brokername;
	}
	public int getBrokerport() {
		return brokerport;
	}
	public void setBrokerport(int brokerport) {
		this.brokerport = brokerport;
	}
	public int getBrokerprotocol() {
		return brokerprotocol;
	}
	public void setBrokerprotocol(int brokerprotocol) {
		this.brokerprotocol = brokerprotocol;
	}
	public String getConnectionticket() {
		return connectionticket;
	}
	public void setConnectionticket(String connectionticket) {
		this.connectionticket = connectionticket;
	}
	
}