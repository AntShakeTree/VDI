package com.opzoon.vdi.core.ws.admin;

import static com.opzoon.vdi.core.facade.CommonException.BAD_REQUEST;
import static com.opzoon.vdi.core.facade.CommonException.UNKNOWN;
import static com.opzoon.vdi.core.util.ConditionUtils.numberNotEquals;

import java.io.Serializable;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

import javax.xml.bind.annotation.XmlRootElement;

import org.springframework.beans.factory.annotation.Autowired;

import com.opzoon.appstatus.manager.handle.IPConfigHandle;
import com.opzoon.vdi.core.facade.FacadeHelper.PagingInfo;
import com.opzoon.vdi.core.facade.UserFacade;
import com.opzoon.vdi.core.facade.transience.RuntimeVariableFacade;
import com.opzoon.vdi.core.util.RuntimeUtils;
import com.opzoon.vdi.core.ws.Services.NullResponse;
import com.opzoon.vdi.core.ws.Services.Response;

/**
 * 系统管理业务实现.
 */
public class SystemManagement {
	
	private UserFacade userFacade;
	private RuntimeVariableFacade runtimeVariableFacade;
	@Autowired
	private IPConfigHandle clusterIPConfigHandle;

	public NullResponse shutdownSystem() {
		NullResponse response = new NullResponse();
		int error = RuntimeUtils.shell(
				null,
				"shutdown -h now");
		if (numberNotEquals(error, 0) && numberNotEquals(error, 128 + 15)) {
			response.getHead().setError(UNKNOWN);
			return response;
		}
		return response;
	}

	public NullResponse rebootSystem() {
		NullResponse response = new NullResponse();
		int error = RuntimeUtils.shell(
				null,
				"shutdown -r now");
		if (numberNotEquals(error, 0) && numberNotEquals(error, 128 + 15)) {
			response.getHead().setError(UNKNOWN);
			return response;
		}
		return response;
	}

	public NullResponse stopService() {
		NullResponse response = new NullResponse();
		runtimeVariableFacade.disableService();
		userFacade.cleanAllSessions();
		return response;
	}

	public NullResponse restartService() {
		NullResponse response = new NullResponse();
		userFacade.cleanAllSessions();
		runtimeVariableFacade.enableService();
		return response;
	}

	public NullResponse configureSystem(
			ConfigureSystemParam configureSystemParam) {
		NullResponse response = new NullResponse();
		if (configureSystemParam.getHostname() != null
				&& !configureSystemParam.getHostname().matches("^[0-9a-zA-Z]+$")) {
			response.getHead().setError(BAD_REQUEST);
			return response;
		}
		if (configureSystemParam.getDnsserver() != null) {
			for (String dnsserver : configureSystemParam.getDnsserver()) {
				if (!dnsserver.matches("^[0-9a-zA-Z/\\.]+$")) {
					response.getHead().setError(BAD_REQUEST);
					return response;
				}
			}
		}
		if (configureSystemParam.getNtpserver() != null) {
			for (String ntpserver : configureSystemParam.getNtpserver()) {
				if (!ntpserver.matches("^[0-9a-zA-Z/\\.]+$")) {
					response.getHead().setError(BAD_REQUEST);
					return response;
				}
			}
		}
		if (configureSystemParam.getHostname() != null) {
			int error = RuntimeUtils.shell(
					null,
					"hostname ?",
					configureSystemParam.getHostname());
			if (numberNotEquals(error, 0)) {
				response.getHead().setError(UNKNOWN);
				return response;
			}
		}
		if (configureSystemParam.getDnsserver() != null && configureSystemParam.getDnsserver().length > 0) {
			int error = RuntimeUtils.shell(
					null,
					"sed -i.bak -e /^nameserver\\ /d /etc/resolv.conf");
			if (numberNotEquals(error, 0)) {
				response.getHead().setError(UNKNOWN);
				return response;
			}
			for (String dnsserver : configureSystemParam.getDnsserver()) {
				error = RuntimeUtils.shell(
						null,
						"echo 'nameserver ?' >> /etc/resolv.conf",
						dnsserver);
				if (numberNotEquals(error, 0)) {
					response.getHead().setError(UNKNOWN);
					return response;
				}
			}
		}
		if (configureSystemParam.getNtpserver() != null && configureSystemParam.getNtpserver().length > 0) {
			int error = RuntimeUtils.shell(
							null,
							"sed -i.bak -e /^server\\ /d /etc/ntp.conf");
			if (numberNotEquals(error, 0)) {
				response.getHead().setError(UNKNOWN);
				return response;
			}
			for (String ntpserver : configureSystemParam.getDnsserver()) {
				error = RuntimeUtils.shell(
						null,
						"echo 'server ? iburst' >> /etc/ntp.conf",
						ntpserver);
				if (numberNotEquals(error, 0)) {
					response.getHead().setError(UNKNOWN);
					return response;
				}
			}
		}
		return response;
	}

	public ListNetworkAdaptersResponse listNetworkAdapters() {
		ListNetworkAdaptersResponse response = new ListNetworkAdaptersResponse();
		Map<String, String> routes = this.getRoutes();
		if (routes == null) {
			response.getHead().setError(UNKNOWN);
			return response;
		}
		StringBuilder ifconfigResult = new StringBuilder();
		/*
		 * Output example:
		 * 
		 * eth0
		 *  192.168.44.132 255.255.255.0
		 * eth1
		 */
		int error = RuntimeUtils.shell(
				ifconfigResult,
				"ifconfig | grep -E 'Ethernet|Bcast' | sed -E 's/(^[0-9a-zA-Z]+) +Link encap:Ethernet.*/\\1/' | sed -E 's/ +inet addr:([0-9\\.]+).+Mask:([0-9\\.]+)/ \\1 \\2/'");
		if (numberNotEquals(error, 0)) {
			response.getHead().setError(UNKNOWN);
			return response;
		}
		response.setBody(new LinkedList<NetworkAdapter>());
		StringTokenizer st = new StringTokenizer(ifconfigResult.toString(), "\r\n");
		NetworkAdapter networkAdapter = null;
		while (st.hasMoreTokens()) {
			String line = st.nextToken();
			if (line.startsWith(" ")) {
				StringTokenizer stLine = new StringTokenizer(line);
				networkAdapter.setIpaddr(stLine.nextToken());
				networkAdapter.setNetmask(stLine.nextToken());
			} else {
				String ethname = line;
				networkAdapter = new NetworkAdapter();
				response.getBody().add(networkAdapter);
				networkAdapter.setIdnetworkadapter(ethname);
				networkAdapter.setGateway(routes.get(ethname));
			}
		}
		return response;
	}

	public NullResponse configureNetworkAdapter(NetworkAdapter networkAdapter) {
		String oldIPAddress = "";
		if (IPConfigHandle.getNetworkAdapterInfo() != null)
		{
			oldIPAddress = IPConfigHandle.getNetworkAdapterInfo().getIpaddr();
		}
		
		NullResponse response = new NullResponse();
		if (networkAdapter.getIdnetworkadapter() == null
				|| !networkAdapter.getIdnetworkadapter().matches("^[0-9a-zA-Z]+$")) {
			response.getHead().setError(BAD_REQUEST);
			return response;
		}
		if (networkAdapter.getIpaddr() != null) {
			if (!networkAdapter.getIpaddr().matches("^[0-9\\.]+$")) {
				response.getHead().setError(BAD_REQUEST);
				return response;
			}
			if (networkAdapter.getNetmask() == null
					|| !networkAdapter.getNetmask().matches("^[0-9\\.]+$")) {
				response.getHead().setError(BAD_REQUEST);
				return response;
			}
		}
		if (networkAdapter.getGateway() != null
				&& !networkAdapter.getGateway().matches("^[0-9\\.]+$")) {
			response.getHead().setError(BAD_REQUEST);
			return response;
		}
		int error = 0;
		if (networkAdapter.getIpaddr() != null) {
			error = RuntimeUtils.shell(
					null,
					"sed -e s/IPADDR=.*/IPADDR=?/g /etc/sysconfig/network-scripts/ifcfg-? > /tmp/net && mv -f /tmp/net /etc/sysconfig/network-scripts/ifcfg-?",
					networkAdapter.getIpaddr(),
					networkAdapter.getIdnetworkadapter(),
					networkAdapter.getIdnetworkadapter());
			if (numberNotEquals(error, 0)) {
				response.getHead().setError(UNKNOWN);
				return response;
			}
			error = RuntimeUtils.shell(
					null,
					"sed -e s/NETMASK=.*/NETMASK=?/g /etc/sysconfig/network-scripts/ifcfg-? > /tmp/net && mv -f /tmp/net /etc/sysconfig/network-scripts/ifcfg-?",
					networkAdapter.getNetmask(),
					networkAdapter.getIdnetworkadapter(),
					networkAdapter.getIdnetworkadapter());
			if (numberNotEquals(error, 0)) {
				response.getHead().setError(UNKNOWN);
				return response;
			}
			if (networkAdapter.getGateway() != null) {
				error = RuntimeUtils.shell(
						null,
						"sed -e s/GATEWAY=.*/GATEWAY=?/g /etc/sysconfig/network-scripts/ifcfg-? > /tmp/net && mv -f /tmp/net /etc/sysconfig/network-scripts/ifcfg-?",
						networkAdapter.getGateway(),
						networkAdapter.getIdnetworkadapter(),
						networkAdapter.getIdnetworkadapter());
				if (numberNotEquals(error, 0)) {
					response.getHead().setError(UNKNOWN);
					return response;
				}
			}
		} else {
			error = RuntimeUtils.shell(
					null,
					"dhclient ?",
					networkAdapter.getIdnetworkadapter());
		}
		if (numberNotEquals(error, 0)) {
			response.getHead().setError(UNKNOWN);
			return response;
		}
		error = RuntimeUtils.shell(
				null,
				"service network restart");
		if (numberNotEquals(error, 0)) {
			response.getHead().setError(UNKNOWN);
			return response;
		}
		RuntimeUtils.shell(
				null,
				"echo 1 > /proc/sys/net/ipv4/ip_forward");
		RuntimeUtils.shell(
				null,
				"iptables -t filter -F");
		RuntimeUtils.shell(
				null,
				"iptables -t nat -F");
		RuntimeUtils.shell(
				null,
				"iptables -t nat -A POSTROUTING -j MASQUERADE");
		
		if (!clusterIPConfigHandle.changeClusterHostIP(oldIPAddress, networkAdapter.getIpaddr()))
		{
			response.getHead().setError(UNKNOWN);
			return response;
		}
		
		return response;
	}
	
	public void setUserFacade(UserFacade userFacade) {
		this.userFacade = userFacade;
	}

	public void setRuntimeVariableFacade(RuntimeVariableFacade runtimeVariableFacade) {
		this.runtimeVariableFacade = runtimeVariableFacade;
	}

	private Map<String, String> getRoutes() {
		StringBuilder netstatResult = new StringBuilder();
		/*
		 * Output example:
		 * 
		 * 192.168.44.2 eth0
		 */
		int error = RuntimeUtils.shell(
				netstatResult,
				"netstat -nr | grep -E '^0.0.0.0' | awk '{print $2,$8}'");
		if (numberNotEquals(error, 0)) {
			return null;
		}
		Map<String, String> routes = new HashMap<String, String>();
		StringTokenizer st = new StringTokenizer(netstatResult.toString(), "\r\n");
		while (st.hasMoreTokens()) {
			String line = st.nextToken();
			StringTokenizer stLine = new StringTokenizer(line);
			String route = stLine.nextToken();
			String ethname = stLine.nextToken();
			routes.put(ethname, route);
		}
		return routes;
	}
	
	private String bcast(String ip, String mask) {
		StringBuilder sb = new StringBuilder();
		StringTokenizer stIp = new StringTokenizer(ip, ".");
		StringTokenizer maskIp = new StringTokenizer(mask, ".");
		for (int i = 0; i < 4; i++) {
			String ipPart = stIp.nextToken();
			String maskPart = maskIp.nextToken();
			sb.append(Integer.parseInt(ipPart) | (256 + ~Integer.parseInt(maskPart)));
			if (i < 3) {
				sb.append('.');
			}
		}
		return sb.toString();
	}

	@XmlRootElement(name = "listParam")
	public static class ConfigureSystemParam extends PagingInfo implements Serializable {

		private static final long serialVersionUID = 1L;

		private String hostname;
		private String[] dnsserver;
		private String[] ntpserver;
		
		public String getHostname() {
			return hostname;
		}
		public void setHostname(String hostname) {
			this.hostname = hostname;
		}
		public String[] getDnsserver() {
			return dnsserver;
		}
		public void setDnsserver(String[] dnsserver) {
			this.dnsserver = dnsserver;
		}
		public String[] getNtpserver() {
			return ntpserver;
		}
		public void setNtpserver(String[] ntpserver) {
			this.ntpserver = ntpserver;
		}
		
	}

	@XmlRootElement(name = "response")
	public static class ListNetworkAdaptersResponse extends Response<List<NetworkAdapter>> implements Serializable {

		private static final long serialVersionUID = 1L;
		
		private List<NetworkAdapter> body;
		
		public List<NetworkAdapter> getBody() {
			return body;
		}
		public void setBody(List<NetworkAdapter> body) {
			this.body = body;
		}
		
	}
	
	public static class NetworkAdapter implements Serializable {

		private static final long serialVersionUID = 1L;

		private String idnetworkadapter;
		private String ipaddr;
		private String netmask;
		private String gateway;
		
		public String getIdnetworkadapter() {
			return idnetworkadapter;
		}
		public void setIdnetworkadapter(String idnetworkadapter) {
			this.idnetworkadapter = idnetworkadapter;
		}
		public String getIpaddr() {
			return ipaddr;
		}
		public void setIpaddr(String ipaddr) {
			this.ipaddr = ipaddr;
		}
		public String getNetmask() {
			return netmask;
		}
		public void setNetmask(String netmask) {
			this.netmask = netmask;
		}
		public String getGateway() {
			return gateway;
		}
		public void setGateway(String gateway) {
			this.gateway = gateway;
		}
		
	}

}
