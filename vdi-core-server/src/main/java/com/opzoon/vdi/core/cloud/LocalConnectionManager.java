package com.opzoon.vdi.core.cloud;

import static com.opzoon.vdi.core.cloud.ConnectionManager.ConnectionInfo.PROTOCOL_UNLIMITED;
import static com.opzoon.vdi.core.facade.CommonException.NO_ERRORS;
import static com.opzoon.vdi.core.util.ConditionUtils.numberEquals;
import static com.opzoon.vdi.core.util.ConditionUtils.numberNotEquals;
import static com.opzoon.vdi.core.util.StringUtils.randomString;
import static com.opzoon.vdi.core.util.StringUtils.strcat;

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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.opzoon.ohvc.session.Monitor;
import com.opzoon.ohvc.session.Session;
import com.opzoon.vdi.core.util.RuntimeUtils;

public class LocalConnectionManager implements ConnectionManager {

	private static final Logger log = LoggerFactory
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

	@Override
	public ConnectionInfo establishConnection(String hostname, int hostport,
			String brokerName, String brokerIP, int brokerPortMin,
			int brokerPortMax, boolean spice, boolean tunnel) {
		int port = this.bindAvailablePort(brokerPortMin, brokerPortMax);
		if (port < 0) {
			return null;
		}
		log.trace("Forwarding port {}", Integer.valueOf(port));
		String ip = this.ip(brokerIP);
		if (brokerName.length() < 1) {
			brokerName = ip;
		}
		int error = this.forward(ip, port, hostname, hostport, tunnel);
		if (numberNotEquals(error, 0)) {
			// TODO Auto-generated catch block
			return null;
		}
		log.trace("Forwarded port {}", Integer.valueOf(port));
		ConnectionInfo connectionInfo = new ConnectionInfo();
		connectionInfo.setBrokername(brokerName);
		connectionInfo.setBrokerport(port);
		connectionInfo.setBrokerprotocol(PROTOCOL_UNLIMITED);
		connectionInfo.setConnectionticket(randomString(64));
		connectionInfo.setHostname(hostname);
		connectionInfo.setHostport(hostport);
		return connectionInfo;
	}

	@Override
	public ConnectionInfo establishConnection(String appservername,
			String brokerName, String brokerIP) {
		int port = this.bindAvailablePort(0, 65536);
		String hostname = appservername;
		int hostport = 3389;
		log.trace("Forwarding port {}", Integer.valueOf(port));
		String ip = this.ip(brokerIP);
		if (brokerName.length() < 1) {
			brokerName = ip;
		}
		int error = this.forward(ip, port, hostname, hostport, false);
		if (numberNotEquals(error, 0)) {
			// TODO Auto-generated catch block
			return null;
		}
		log.trace("Forwarded port {}", Integer.valueOf(port));
		ConnectionInfo connectionInfo = new ConnectionInfo();
		connectionInfo.setBrokername(brokerName);
		connectionInfo.setBrokerport(port);
		connectionInfo.setBrokerprotocol(PROTOCOL_UNLIMITED);
		connectionInfo.setConnectionticket(randomString(64));
		connectionInfo.setHostname(hostname);
		connectionInfo.setHostport(hostport);
		return connectionInfo;
	}

	@Override
	public int destroyConnection(int brokerport, String hostname, int hostport,
			String brokerIP, boolean tunnel) {
		int error = this.unforward(brokerport, hostname, hostport, brokerIP,
				tunnel);
		if (numberNotEquals(error, 0)) {
			// TODO Auto-generated catch block
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
		return NO_ERRORS;
	}

	@Override
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

	public static void main(String[] args) throws IOException {
		// ServerSocket serverSocket = new ServerSocket(1266);
		// serverSocket.close();
		// serverSocket = new ServerSocket(9999);
		// int min = 5;
		// int max = 10;
		// int start = (int) (Math.random() * (max - min));
		// for (int i = 0, c = max - min; i < c; i++) {
		// System.out.println(min + (start + i) % c);
		// }
		// for(java.lang.reflect.Method m:new
		// LocalConnectionManager().getClass().getDeclaredMethods()){
		// System.err.println(m.getName());
		//
		// }

		// new LocalConnectionManager().destroyConnection(1132, "20.1.203.34",
		// 3389,"", false);
	}

	private int bindAvailablePort(int brokerPortMin, int brokerPortMax) {
		int start = (int) (Math.random() * (brokerPortMax - brokerPortMin));
		// for (int i = 0, c = brokerPortMax - brokerPortMin; i < c; i++) {
		// try {
		// int brokerport = brokerPortMin + (start + i) % c;
		// ServerSocket serverSocket = new ServerSocket(brokerport);
		// serverSocketHolder.add(serverSocket);
		// return brokerport;
		// } catch (IOException e) {
		// // TODO Auto-generated catch block
		// e.printStackTrace();
		// }
		// TODO Warn no resource.
		// return -1;
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
					strcat("iptables -t nat -A OUTPUT -p tcp -d ", ip,
							" --dport ", brokerport, " -j DNAT --to ",
							hostname, ":", hostport));
			if (numberNotEquals(error, 0)) {
				return error;
			}
		} else {
			error = RuntimeUtils.shell(
					null,
					strcat("iptables -t nat -A PREROUTING -p tcp -d ", ip,
							" --dport ", brokerport, " -j DNAT --to ",
							hostname, ":", hostport));
			if (numberNotEquals(error, 0)) {
				return error;
			}
		}
		return 0;
	}

	// private int unforward(int brokerport, String hostname, int hostport,
	// String brokerIP, boolean tunnel) {
	// log.debug("=======================>"+brokerport+"," +hostname+"," +
	// hostport+","
	// + brokerIP+"," +tunnel);
	// if (tunnel) {
	// return RuntimeUtils.shell(
	// null,
	// strcat("iptables -t nat -D OUTPUT -p tcp -d ",
	// this.ip(brokerIP), " --dport ", brokerport,
	// " -j DNAT --to ", hostname, ":", hostport));
	// } else {
	// int error = RuntimeUtils.shell(null,
	// strcat("iptables -A FORWARD -s ", hostname, " -j REJECT"));
	// if (numberNotEquals(error, 0)) {
	// // TODO Auto-generated catch block
	// log.warn("Errors on FORWARD: {}", error);
	// }

	// // try {
	// // Session.whenDelayNotice(UUID.randomUUID()+"", monitor,
	// 3,TimeUnit.MINUTES);
	// // } catch (IllegalAccessException e) {
	// // // TODO Auto-generated catch block
	// // e.printStackTrace();
	// // }
	// return RuntimeUtils.shell(
	// null,
	// strcat("iptables -t nat -D PREROUTING -p tcp -d ",
	// this.ip(brokerIP), " --dport ", brokerport,
	// " -j DNAT --to ", hostname, ":", hostport));
	// }
	// }

	public int delayClear(Integer brokerport, String hostname,
			Integer hostport, String brokerIP) {

		String iptables = strcat("iptables -D FORWARD -s ", hostname,
				" -j REJECT");
		log.debug("delayClear ::" + iptables);
		int error = -1;
		StringBuilder result = new StringBuilder("");

		do {
			error = RuntimeUtils.shell(result, iptables);
			log.debug("delayClear ::" + result);
		} while (error == 0);
		String cleanInput = strcat("iptables -D INPUT -s ", hostname,
				" -j REJECT");
		log.debug("delayClear ::" + cleanInput);
		do {
			error = RuntimeUtils.shell(result, cleanInput);
			log.debug("delayClear ::" + result);
		} while (error == 0);
		log.debug("delayClear  result::" + result);
		return error;
	}

	private int unforward(int brokerport, String hostname, int hostport,
			String brokerIP, boolean tunnel) {
		if (tunnel) {
			int error = RuntimeUtils.shell(null,
					strcat("iptables -A INPUT -s ", hostname, " -j REJECT"));
			if (numberNotEquals(error, 0)) {
				// TODO Auto-generated catch block
				log.warn("Errors on INPUT: {}", error);
			}
			error = RuntimeUtils.shell(
					null,
					strcat("iptables -t nat -D OUTPUT -p tcp -d ",
							this.ip(brokerIP), " --dport ", brokerport,
							" -j DNAT --to ", hostname, ":", hostport));

			if (numberNotEquals(error, 0)) {
				// TODO Auto-generated catch block
				log.warn("Errors on INPUT D: {}", error);
			}
			// RuntimeUtils.shell(
			// null,
			// strcat("iptables -D INPUT -s ", hostname, " -j REJECT"));
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
					strcat("iptables -A FORWARD -s ", hostname, " -j REJECT"));
			if (numberNotEquals(error, 0)) {
				// TODO Auto-generated catch block
				log.warn("Errors on FORWARD: {}", error);
			}
			return RuntimeUtils.shell(
					null,
					strcat("iptables -t nat -D PREROUTING -p tcp -d ",
							this.ip(brokerIP), " --dport ", brokerport,
							" -j DNAT --to ", hostname, ":", hostport));
		}
	}

}
