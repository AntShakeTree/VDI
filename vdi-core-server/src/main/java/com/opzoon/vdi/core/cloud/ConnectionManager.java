package com.opzoon.vdi.core.cloud;

public interface ConnectionManager {
	
	ConnectionInfo establishConnection(String hostname, int hostport, String brokerName, String brokerIP, int brokerPortMin, int brokerPortMax, boolean spice, boolean tunnel);
	
	ConnectionInfo establishConnection(String appservername, String brokerName, String brokerIP);

	int destroyConnection(int brokerport, String hostname, int hostport, String brokerIP, boolean tunnel);
	
	boolean isForwarding(String brokerIP, int brokerPort);
	
	public static class ConnectionInfo {

		/**
		 * 协议: 无限制协议.
		 */
		public static final int PROTOCOL_UNLIMITED = 0;
		/**
		 * 协议: HTTP协议封装.
		 */
		public static final int PROTOCOL_HTTP = 1;
		/**
		 * 协议: HTTPS协议封装.
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

}
