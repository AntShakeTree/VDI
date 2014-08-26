package com.vdi.gateway;



import static com.vdi.gateway.StringUtils.randomString;

import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Queue;
import java.util.UUID;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.TimeUnit;

import org.slf4j.LoggerFactory;





public class LocalConnectionManager  {

	final static org.slf4j.Logger LOG = LoggerFactory
			.getLogger(LocalConnectionManager.class);
	private Queue<ServerSocket> serverSocketHolder = new ConcurrentLinkedQueue<ServerSocket>();

	public void init() {
		/*
		 * BLOODY_TIPS 'service network restart' without modifying sysctl.conf
		 * will erase the command below.
		 */
		RuntimeUtils.shell(null, "echo 1 > /proc/sys/net/ipv4/ip_forward");
		RuntimeUtils.shell(null, "iptables -t filter -F");
		RuntimeUtils.shell(null, "iptables -t nat -F");
		RuntimeUtils
				.shell(null, "iptables -t nat -A POSTROUTING -j MASQUERADE");
	}

	public ConnectionInfo establishConnection(String hostname, int hostport,
			String brokerName, String brokerIP, boolean spice, boolean tunnel) {
		int port = this.bindAvailablePort();
		if (port < 0) {
			return null;
		}
		String ip = this.ip(brokerIP);
		if (brokerName.length() < 1) {
			brokerName = ip;
		}
		int error = this.forward(ip, port, hostname, hostport, tunnel);
		if (numberNotEquals(error, 0)) {
			return null;
		}
		ConnectionInfo connectionInfo = new ConnectionInfo();
		connectionInfo.setBrokername(brokerName);
		connectionInfo.setBrokerport(port);
		connectionInfo.setBrokerprotocol(ConnectionInfo.PROTOCOL_UNLIMITED);
		connectionInfo.setConnectionticket(randomString(64));
		connectionInfo.setHostname(hostname);
		connectionInfo.setHostport(hostport);
		return connectionInfo;
	}

	private boolean numberNotEquals(int error, int i) {
		return error!=i;
	}

	public ConnectionInfo establishConnection(String appservername,
			String brokerName, String brokerIP) {
		int port = this.bindAvailablePort();
		String hostname = appservername;
		int hostport = 3389;
		String ip = this.ip(brokerIP);
		if (brokerName.length() < 1) {
			brokerName = ip;
		}
		int error = this.forward(ip, port, hostname, hostport, false);
		if (numberNotEquals(error, 0)) {
			return null;
		}
		ConnectionInfo connectionInfo = new ConnectionInfo();
		connectionInfo.setBrokername(brokerName);
		connectionInfo.setBrokerport(port);
		connectionInfo.setBrokerprotocol(ConnectionInfo.PROTOCOL_UNLIMITED);
		connectionInfo.setConnectionticket(randomString(64));
		connectionInfo.setHostname(hostname);
		connectionInfo.setHostport(hostport);
		return connectionInfo;
	}

	public int destroyConnection(int brokerport, String hostname, int hostport,
			String brokerIP, boolean tunnel) {
		int error = this.unforward(brokerport, hostname, hostport, brokerIP,
				tunnel);
		if (numberNotEquals(error, 0)) {
			LOG.warn("Errors on INPUT D: {}", error);
		}
		for (Iterator<ServerSocket> iterator = serverSocketHolder.iterator(); iterator
				.hasNext();) {
			ServerSocket serverSocket = iterator.next();
			if (numberEquals(serverSocket.getLocalPort(), brokerport)) {
				try {
					serverSocket.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				// TODO Try-catch syn error?
				iterator.remove();
				break;
			}
		}
		return 0;
	}

	private boolean numberEquals(int src, int target) {
		// TODO Auto-generated method stub
		return src==target;
	}

	public boolean isForwarding(String brokerIP, int brokerPort) {
		Socket s = null;
		try {
			s = new Socket(InetAddress.getByName(brokerIP), brokerPort);
			return true;
		} catch (Exception e) {
			return false;
		} finally {
			if (s != null) {
				try {
					s.close();
				} catch (IOException e) {
				}
			}
		}
	}

	private String ip(String brokerIP) {
		if (brokerIP.length() > 0) {
			return brokerIP;
		}
		// TODO IPV4 Only.
		String ip = "127.0.0.1";
		StringBuilder ipResult = new StringBuilder();
		int error = RuntimeUtils
				.shell(ipResult,
						"ifconfig|grep 'Bcast.*Mask'|awk '{print $2}'|awk -F : '{print $2}'");
		if (numberEquals(error, 0)) {
			ip = ipResult.toString().trim();
			if (ip.indexOf('\n') > -1) {
				// Getting the 1st IP from multiple IPs.
				ip = ip.substring(0, ip.indexOf('\n')).trim();
			}
		}
		return ip;
	}



	private int bindAvailablePort() {
		try {
			ServerSocket serverSocket = new ServerSocket();
			serverSocket.bind(null);
			int brokerport = serverSocket.getLocalPort();
			serverSocketHolder.add(serverSocket);
			return brokerport;
		} catch (IOException e) {
			e.printStackTrace();
			return -1;
		}

	}

	private int forward(String ip, int brokerport, String hostname,
			int hostport, boolean tunnel) {
		int error = delayClear(brokerport, hostname, hostport, ip);
		if (tunnel) {
			error = RuntimeUtils.shell(
					null,
					StringUtils.strcat("iptables -t nat -A OUTPUT -p tcp -d ", ip,
							" --dport ", brokerport, " -j DNAT --to ",
							hostname, ":", hostport));
			if (numberNotEquals(error, 0)) {
				return error;
			}
		} else {
			error = RuntimeUtils.shell(
					null,
					StringUtils.strcat("iptables -t nat -A PREROUTING -p tcp -d ", ip,
							" --dport ", brokerport, " -j DNAT --to ",
							hostname, ":", hostport));
			if (numberNotEquals(error, 0)) {
				return error;
			}
		}
		return 0;
	}

	

	public int delayClear(Integer brokerport, String hostname,
			Integer hostport, String brokerIP) {

		String iptables = StringUtils.strcat("iptables -D FORWARD -s ", hostname,
				" -j REJECT");
		int error = -1;
		StringBuilder result = new StringBuilder("");

		do {
			error = RuntimeUtils.shell(result, iptables);
		} while (error == 0);
		String cleanInput = StringUtils.strcat("iptables -D INPUT -s ", hostname,
				" -j REJECT");
		do {
			error = RuntimeUtils.shell(result, cleanInput);
		} while (error == 0);
		return error;
	}

	private int unforward(int brokerport, String hostname, int hostport,
			String brokerIP, boolean tunnel) {
		if (tunnel) {
			int error = RuntimeUtils.shell(null,
					StringUtils.strcat("iptables -A INPUT -s ", hostname, " -j REJECT"));
			if (numberNotEquals(error, 0)) {
				// TODO Auto-generated catch block
			}
			error = RuntimeUtils.shell(
					null,
					StringUtils.strcat("iptables -t nat -D OUTPUT -p tcp -d ",
							this.ip(brokerIP), " --dport ", brokerport,
							" -j DNAT --to ", hostname, ":", hostport));

			if (numberNotEquals(error, 0)) {
				LOG.warn("Errors on INPUT D: {}", error);
			}
			Monitor monitor = new Monitor();
			monitor.setClassname(LocalConnectionManager.class.getName());
			monitor.setMethod("delayClear");
			List<Object> ps = new ArrayList<Object>();
			ps.add(brokerport);
			ps.add(hostname);
			ps.add(hostport);
			ps.add(brokerIP);
			monitor.setParameters(ps);
			try {
				Session.whenDelayNotice(UUID.randomUUID() + "", monitor, 10,
						TimeUnit.SECONDS);
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			}
			return error;
		} else {
			int error = RuntimeUtils.shell(null,
					StringUtils.strcat("iptables -A FORWARD -s ", hostname, " -j REJECT"));
			if (numberNotEquals(error, 0)) {
				LOG.warn("Errors on FORWARD: {}", error);
			}
			return RuntimeUtils.shell(
					null,
					StringUtils.strcat("iptables -t nat -D PREROUTING -p tcp -d ",
							this.ip(brokerIP), " --dport ", brokerport,
							" -j DNAT --to ", hostname, ":", hostport));
		}
	}
	
}
