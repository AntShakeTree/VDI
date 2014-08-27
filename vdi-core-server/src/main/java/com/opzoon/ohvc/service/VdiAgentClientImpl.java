package com.opzoon.ohvc.service;

import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.json.JSONObject;

import com.google.gson.Gson;
import com.google.gson.internal.StringMap;
import com.opzoon.appstatus.common.ParseJSON;
import com.opzoon.ohvc.common.JSONObjectUtils;
import com.opzoon.ohvc.common.Job;
import com.opzoon.ohvc.common.JobStatus;
import com.opzoon.ohvc.common.OpzoonUtils;
import com.opzoon.ohvc.common.RailResponse;
import com.opzoon.ohvc.domain.Agent;
import com.opzoon.ohvc.domain.AgentRailApplication;
import com.opzoon.ohvc.domain.AgentRailApplicationServer;
import com.opzoon.ohvc.domain.Head;
import com.opzoon.ohvc.domain.JobResult;
import com.opzoon.ohvc.domain.ListRailAppRes;
import com.opzoon.ohvc.domain.RailApplicationIcon;
import com.opzoon.ohvc.domain.RailInformation;
import com.opzoon.ohvc.request.HttpPostRequest;
import com.opzoon.ohvc.request.JoinDomainRequest;
import com.opzoon.ohvc.request.JoinWorkgroupRequest;
import com.opzoon.ohvc.request.LogonUserRequest;
import com.opzoon.ohvc.request.SetHostnameRequest;
import com.opzoon.ohvc.response.ApplicationServerResponse;
import com.opzoon.ohvc.response.RailApplicationIconRes;
import com.opzoon.ohvc.response.RailResponseSupport;
import com.opzoon.ohvc.response.Response;
import com.opzoon.ohvc.session.Session;
import com.opzoon.vdi.core.facade.CommonException;

public class VdiAgentClientImpl {
	private static Logger log = Logger.getLogger(VdiAgentClientImpl.class);
//	protected static Set<String> RDPSET = Collections.synchronizedSet(new HashSet<String>(5000));
	private static final ReentrantLock lock = new ReentrantLock();

	public static void main(String[] args) throws Exception {
		deleteUserProfile("20.1.136.184", null, "bbb");
	}

	public static String buildAgentServiceBody(Map<String, String> paraMap) {
		StringBuilder bodyB = new StringBuilder();
		if (paraMap != null) {
			for (Map.Entry<String, String> entry : paraMap.entrySet()) {
				bodyB.append("\"");
				bodyB.append(entry.getKey());
				bodyB.append("\":\"");
				bodyB.append(entry.getValue());
				bodyB.append("\",");
			}
			bodyB.setLength(bodyB.length() - 1);
			bodyB.insert(0, "{");
			bodyB.append("}");
		}
		return bodyB.toString();
	}

	public static String buildAgentUrl(String ip, String method) {
		String url = "http://" + ip + ":" + 58650 + "/vdiagent/services/"
				+ method;
		return url;
	}

	public static Job<SetHostnameRequest> setHostname(String ip,
			String hostname, Integer type, String account, String password,
			boolean restart) throws Exception {
		if (ip == null || type == null || hostname == null) {
			throw new Exception("para error: ip, type, hostname cann't be null");
		}
		if (2 != type && (account == null || password == null)) {
			throw new Exception("para error: need domain account and password ");
		}
		String url = buildAgentUrl(ip, "setHostname");
		Session.removeCache("rdp_" + ip.trim());
		HttpPostRequest post = HttpPostRequest.instanceByUrl(ip, url);
		SetHostnameRequest sethostname = new SetHostnameRequest();
		sethostname.setAccount(account);
		sethostname.setHostname(hostname);
		sethostname.setPassword(password);
		sethostname.setType(type);
		sethostname.setRestart(restart);
		String result = post.executeJSONStr(new Gson().toJson(sethostname));
		String jobid = getJobid(result);
		if (jobid == null || jobid.isEmpty()) {
			throw new Exception("agent return error: not find the jobid");
		}
		Job<SetHostnameRequest> job = new Job<SetHostnameRequest>();
		job.setId(jobid);
		job.setIp(ip);
		job.setStatus(JobStatus.RUNNING);
		job.setAgent(true);
		return job;
	}

	public static void shutdownSystem(String ip) throws Exception {
		String url = buildAgentUrl(ip, "shutdownSystem");
		Session.removeCache("rdp_" + ip.trim());
		HttpPostRequest post = HttpPostRequest.instanceByUrl(ip, url);
		post.execute();
	}

	public static boolean getRDPStatus(String ip) throws Exception {
		boolean isStatus = false;
		Object cacheStatus = Session.getCache("rdp_" + ip.trim());
		log.debug("getRDPStatus return cache status begin." + ip + "_" + isStatus);
		try {
			lock.lock();
			if (cacheStatus != null) {

				if (cacheStatus.equals("error")) {
					isStatus = false;
					Session.removeCache("rdp_" + ip.trim());
					return false;
				}
				isStatus = Boolean.parseBoolean(cacheStatus + "");
			} else {
				try {
					if (StringUtils.isNotEmpty(queryRDPStatus(ip)))
						isStatus = Boolean.parseBoolean(queryRDPStatus(ip));
				} catch (Exception e) {
					Session.removeCache("rdp_" + ip.trim());
					return false;
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			lock.unlock();
		}
		log.debug("getRDPStatus return cache status." + ip + "_" + isStatus);
		return isStatus;
	}

	private static String getJobid(String jsonStr) throws Exception {
		JSONObject jsonObj = new JSONObject(jsonStr);
		JSONObject head = jsonObj.getJSONObject("head");
		if (head == null)
			throw new Exception("agent return error: not find head");
		int errorcode = head.getInt("error");
		if (0 == errorcode) {
			System.out.println(head.getString("jobid"));
			return head.getString("jobid");
		} else {
			log.error("Agent_errorcode ::" + errorcode);
			throw new CommonException(errorcode);
		}
	}

	public static Job<String> restartSystem(String ip) throws Exception {
		String url = buildAgentUrl(ip, "restartSystem");
		Session.removeCache("rdp_" + ip.trim());
		HttpPostRequest post = HttpPostRequest.instanceByUrl(ip, url);
		String result = post.execute();
		String jobid = getJobid(result);
		if (jobid == null || jobid.isEmpty()) {
			throw new Exception("agent return error: not find the jobid");
		}
		Job<String> job = new Job<String>();
		job.setId(jobid);
		job.setStatus(JobStatus.RUNNING);
		job.setIp(ip);
		job.setAgent(true);
		return job;
	}

	// ~queryAsynJob
	@SuppressWarnings("unchecked")
	public static <T> void queryAsyncJobResult(final Job<T> job)
			throws Exception {

		if (job != null) {
			if (job.getId() == null || "".equals(job.getId())
					|| job.getStatus().equals(JobStatus.FAILED)) {
				return;
			}
		}

		final String jobid = job.getId();
		String url = VdiAgentClientImpl.buildAgentUrl(job.getIp(),
				"queryAsyncJobResult");
		HttpPostRequest post = HttpPostRequest.instanceByUrl(job.getIp(), url);
		JSONObject jso = new JSONObject();
		jso.put("jobid", jobid);
		String result = "";
		try {
			result = post.executeJSONStr(jso.toString());
			System.out.println(result);
		} catch (Exception e) {
			if (!(e instanceof IOException)) {
				throw e;
			}
		}
		if (result != null && !result.equals("")) {
			log.debug("query job result [" + result + "]");
			try {
				Agent agent = JSONObjectUtils.parseAgent(result);
				Head head = agent.getHead();
				JobResult body = agent.getBody();
				job.setError(head.getError());

				if (0 != head.getError()) {
					job.setStatus(JobStatus.FAILED);
					throw new Exception("query job failure!");
				} 
				if (body.getJobstatus() == 2) {
					job.setStatus(JobStatus.FAILED);
					job.setError(body.getJobresultcode());
					return;
				} else {
					if (100 == body.getJobprocstatus()) {
						job.setStatus(JobStatus.SUCCESSFUL);
						job.setResult((T) body.getJobresult());
						return;
					} else {
						job.setStatus(JobStatus.RUNNING);
					}
				}
			} catch (Exception e) {
				// 忽略
			}
		}

	}

	public class RDPResult {
		private Head head;
		private RDPStatus body;

		public Head getHead() {
			return head;
		}

		public void setHead(Head head) {
			this.head = head;
		}

		public RDPStatus getBody() {
			return body;
		}

		public void setBody(RDPStatus body) {
			this.body = body;
		}

	}

	public class RDPStatus {
		private int valid;
		private String reason;

		public int getValid() {
			return valid;
		}

		public void setValid(int valid) {
			this.valid = valid;
		}

		public String getReason() {
			return reason;
		}

		public void setReason(String reason) {
			this.reason = reason;
		}

	}

	// Evan
	@SuppressWarnings("rawtypes")
	public static Job joinDomain(String ip, String domainname,
			String domainbinddn, String domainbindpass, boolean restart)
			throws Exception {
		if (ip == null || domainname == null) {
			throw new Exception("para error: ip, domainname cann't be null");
		}
		JoinDomainRequest req = new JoinDomainRequest();
		req.setAccount(domainbinddn);
		req.setDomainname(domainname);
		req.setRestart(restart);
		req.setPassword(domainbindpass);
		String url = buildAgentUrl(ip, "joinDomain");

		HttpPostRequest post = HttpPostRequest.instanceByUrl(ip, url);
		String result = post.executeJSONStr(new Gson().toJson(req));

		String jobid = getJobid(result);
		if (jobid == null || jobid.isEmpty()) {
			throw new Exception("agent return error: not find the jobid");
		}
		Job<JoinDomainRequest> job = new Job<JoinDomainRequest>();
		job.setId(jobid);
		job.setIp(ip);
		job.setStatus(JobStatus.RUNNING);
		job.setAgent(true);
		return job;
	}

	// Evan
	@SuppressWarnings("rawtypes")
	public static Job joinWorkgroup(String ip, String workgroupname,
			String account, String password, boolean restart) throws Exception {
		if (ip == null || workgroupname == null) {
			throw new Exception("para error: ip, workgroupname cann't be null");
		}
		String url = buildAgentUrl(ip, "joinWorkgroup");

		JoinWorkgroupRequest req = new JoinWorkgroupRequest();
		req.setAccount(account);
		req.setPassword(password);
		req.setWorkgroupname(workgroupname);
		req.setRestart(restart);
		HttpPostRequest post = HttpPostRequest.instanceByUrl(ip, url);
		String result = post.executeJSONStr(new Gson().toJson(req));

		String jobid = getJobid(result);
		if (jobid == null || jobid.isEmpty()) {
			throw new Exception("agent return error: not find the jobid");
		}
		Job<JoinWorkgroupRequest> job = new Job<JoinWorkgroupRequest>();
		job.setId(jobid);
		job.setIp(ip);
		job.setStatus(JobStatus.RUNNING);
		job.setAgent(true);
		return job;
	}

	// Evan
	public static Job<String> createUser(String ip, String username,
			String password) throws Exception {
		if (ip == null || username == null) {
			throw new Exception("para error: ip, username cann't be null");
		}
		Map<String, String> paraMap = new HashMap<String, String>();
		paraMap.put("username", username);
		paraMap.put("password", password);
		String body = buildAgentServiceBody(paraMap);
		String url = buildAgentUrl(ip, "createLocalUser");

		log.trace("createUser start: " + ip + ", " + username + ", " + password);

		HttpPostRequest post = HttpPostRequest.instanceByUrl(ip, url);
		String result = post.executeJSONStr(body);

		log.trace("createUser end: " + ip + ", " + username + ", " + password);

		String jobid = getJobid(result);
		if (jobid == null || jobid.isEmpty()) {
			throw new Exception("agent return error: not find the jobid");
		}
		Job<String> job = new Job<String>();
		job.setId(jobid);
		job.setIp(ip);
		job.setStatus(JobStatus.RUNNING);
		job.setAgent(true);
		return job;
	}

	// Evan
	public static Job<String> updateUserPassword(String ip, String username,
			String password) throws Exception {
		if (ip == null || username == null) {
			throw new Exception("para error: ip, username cann't be null");
		}
		Map<String, String> paraMap = new HashMap<String, String>();
		paraMap.put("username", username);
		paraMap.put("password", password);
		String body = buildAgentServiceBody(paraMap);
		String url = buildAgentUrl(ip, "updateLocalUserPassword");

		log.trace("updateUserPassword start: " + ip + ", " + username + ", "
				+ password);

		HttpPostRequest post = HttpPostRequest.instanceByUrl(ip, url);
		String result = post.executeJSONStr(body);

		log.trace("updateUserPassword end: " + ip + ", " + username + ", "
				+ password);

		String jobid = getJobid(result);
		if (jobid == null || jobid.isEmpty()) {
			throw new Exception("agent return error: not find the jobid");
		}
		Job<String> job = new Job<String>();
		job.setId(jobid);
		job.setIp(ip);
		job.setStatus(JobStatus.RUNNING);
		job.setAgent(true);
		return job;
	}

	// Evan
	public static Job<String> logOff(String ip, String domainname,
			String username) throws Exception {
		if (ip == null) {
			throw new Exception("para error: ip cann't be null");
		}
		Map<String, String> paraMap = new HashMap<String, String>();
		if (domainname != null) {
			paraMap.put("domain", domainname);
		}
		paraMap.put("username", username);
		String body = buildAgentServiceBody(paraMap);
		String url = buildAgentUrl(ip, "logoffUser");

		log.trace("logoffUser start: " + ip + ", " + username + ", "
				+ domainname);

		HttpPostRequest post = HttpPostRequest.instanceByUrl(ip, url);
		String result = post.executeJSONStr(body);

		log.trace("logoffUser end: " + ip + ", " + username + ", " + domainname);

		String jobid = getJobid(result);
		if (jobid == null || jobid.isEmpty()) {
			throw new Exception("agent return error: not find the jobid");
		}
		Job<String> job = new Job<String>();
		job.setId(jobid);
		job.setIp(ip);
		job.setStatus(JobStatus.RUNNING);
		job.setAgent(true);
		return job;
	}

	public static Job<String> deleteUser(String ip, String domain,
			String username) throws Exception {
		if (ip == null) {
			throw new Exception("para error: ip cann't be null");
		}
		String body = "";
		if (username != null) {
			Map<String, String> paraMap = new HashMap<String, String>();
			paraMap.put("username", username);
			if (domain != null) {
				paraMap.put("domain", domain);
			}
			body = buildAgentServiceBody(paraMap);
		}
		String url = buildAgentUrl(ip, "deleteUser");

		log.trace("deleteUser start: " + ip + ", " + username);

		HttpPostRequest post = HttpPostRequest.instanceByUrl(ip, url);
		String result = post.executeJSONStr(body);

		log.trace("deleteUser end: " + ip + ", " + username);

		String jobid = getJobid(result);
		if (jobid == null || jobid.isEmpty()) {
			throw new Exception("agent return error: not find the jobid");
		}
		Job<String> job = new Job<String>();
		job.setId(jobid);
		job.setIp(ip);
		job.setStatus(JobStatus.RUNNING);
		job.setAgent(true);
		return job;
	}

	// Evan
	public static Job<String> deleteUserProfile(String ip, String domain,
			String username) throws Exception {
		if (ip == null) {
			throw new Exception("para error: ip cann't be null");
		}
		String body = "";
		if (username != null) {
			Map<String, String> paraMap = new HashMap<String, String>();
			if (domain != null) {
				paraMap.put("domain", domain);
			}
			paraMap.put("username", username);
			body = buildAgentServiceBody(paraMap);
		}
		String url = buildAgentUrl(ip, "deleteUserProfile");

		log.trace("deleteUserProfile start: " + ip + ", " + username);

		HttpPostRequest post = HttpPostRequest.instanceByUrl(ip, url);
		String result = post.executeJSONStr(body);

		log.trace("deleteUserProfile end: " + ip + ", " + username);

		String jobid = getJobid(result);
		if (jobid == null || jobid.isEmpty()) {
			throw new Exception("agent return error: not find the jobid");
		}
		Job<String> job = new Job<String>();
		job.setId(jobid);
		job.setIp(ip);
		job.setStatus(JobStatus.RUNNING);
		job.setAgent(true);
		return job;
	}

	// Evan
	public static Job<String> addUserToLocalGroup(String ip, String username,
			String domainname, String groupname) throws Exception {
		if (ip == null) {
			throw new Exception("para error: ip cann't be null");
		}
		String body = "";
		Map<String, String> paraMap = new HashMap<String, String>();
		paraMap.put("username", username);
		if (domainname != null) {
			paraMap.put("domainname", domainname);
		}
		paraMap.put("groupname", groupname);
		body = buildAgentServiceBody(paraMap);
		String url = buildAgentUrl(ip, "addUserToLocalGroup");

		log.trace("addUserToLocalGroup start: " + ip + ", " + username);

		HttpPostRequest post = HttpPostRequest.instanceByUrl(ip, url);
		String result = post.executeJSONStr(body);

		log.trace("addUserToLocalGroup end: " + ip + ", " + username);

		String jobid = getJobid(result);
		if (jobid == null || jobid.isEmpty()) {
			throw new Exception("agent return error: not find the jobid");
		}
		Job<String> job = new Job<String>();
		job.setId(jobid);
		job.setIp(ip);
		job.setStatus(JobStatus.RUNNING);
		job.setAgent(true);
		return job;
	}

	// Evan
	public static Job<String> deleteUserFromLocalGroup(String ip,
			String username, String domainname, String groupname)
			throws Exception {
		if (ip == null) {
			throw new Exception("para error: ip cann't be null");
		}
		String body = "";
		Map<String, String> paraMap = new HashMap<String, String>();
		paraMap.put("username", username);
		if (domainname != null) {
			paraMap.put("domainname", domainname);
		}
		paraMap.put("groupname", groupname);
		body = buildAgentServiceBody(paraMap);
		String url = buildAgentUrl(ip, "deleteUserFromLocalGroup");

		log.trace("deleteUserFromLocalGroup start: " + ip + ", " + username);

		HttpPostRequest post = HttpPostRequest.instanceByUrl(ip, url);
		String result = post.executeJSONStr(body);

		log.trace("deleteUserFromLocalGroup end: " + ip + ", " + username);

		String jobid = getJobid(result);
		if (jobid == null || jobid.isEmpty()) {
			throw new Exception("agent return error: not find the jobid");
		}
		Job<String> job = new Job<String>();
		job.setId(jobid);
		job.setIp(ip);
		job.setStatus(JobStatus.RUNNING);
		job.setAgent(true);
		return job;
	}
		public static void logonUser(String ip,
				String domainname,String username,  String password,int brokerprotocol)
				throws Exception {
			if (ip == null) {
				throw new Exception("para error: ip cann't be null");
			}
			LogonUserRequest longonuser = new LogonUserRequest();
		
			if (domainname != null) {
				longonuser.setDomain(domainname);
			}
			
			longonuser.setBrokerprotocol(brokerprotocol);
			longonuser.setPassword(password);
			longonuser.setUsername(username);
			String url = buildAgentUrl(ip, "logonUser");

			log.trace("logonUser start: " + ip + ", " + username+","+brokerprotocol);

			HttpPostRequest post = HttpPostRequest.instanceByUrl(ip, url);
			String result = post.executeJSONStr(new Gson().toJson(longonuser));
			log.trace("logonUser end: "+result);
			Response rs = ParseJSON.fromJson(result, Response.class);
			int error =rs.getHead().getError();
			if(error!=0){
				log.error("agent Exception error "+error);
				throw new Exception(error+"");
			}
		}
	/**
	 * @return: ListRailApplications
	 * @param serverIp
	 *            格式例如："10.1.32.93"
	 * @return
	 * @throws Exception
	 *             (IllegalArgumentException : 传递的参数可能不是真正IP地址)
	 */
	public static RailResponse<List<AgentRailApplication>> listRailApplications(
			String serverIp) throws Exception {
		RailResponse<List<AgentRailApplication>> ponse = new ListRailAppRes();
		String url = VdiAgentClientImpl.buildAgentUrl(serverIp,
				"listRailApplications");
		log.info("URL:" + url);
		HttpPostRequest request = HttpPostRequest.instanceByUrl(serverIp, url);
		Map<String, String> paramterStr = new HashMap<String, String>();
		paramterStr.put("Content-Type", "text/plain; charset=UTF-8");
		request.addHeader(paramterStr);
		String reString = request
				.executeJSONStr("{\"servertype\":\"0x101\",\"freshen\":\"false\"}");
		log.info("rail server agent :[" + reString + "]");
		return ponse.instanceByJson(reString);
	}

	/**
	 * @return: ListRailApplications
	 * @param serverIp
	 *            格式例如："10.1.32.93"
	 * @return
	 * @throws Exception
	 *             (IllegalArgumentException : 传递的参数可能不是真正IP地址)
	 */
	public static Job<String> listRailApplicationsFreshen(String serverIp)
			throws Exception {
		String url = VdiAgentClientImpl.buildAgentUrl(serverIp,
				"listRailApplications");
		log.info("URL:" + url);
		HttpPostRequest request = HttpPostRequest.instanceByUrl(serverIp, url);
		Map<String, String> paramterStr = new HashMap<String, String>();
		paramterStr.put("Content-Type", "text/plain; charset=UTF-8");
		request.addHeader(paramterStr);
		String reString = request
				.executeJSONStr("{\"servertype\":\"0x101\",\"freshen\":\"true\"}");
		String jobid = "";
		JSONObject jsonObj = new JSONObject(reString);
		JSONObject head = jsonObj.getJSONObject("head");
		if (head == null)
			throw new Exception("agent return error: not find head");
		int errorcode = head.getInt("error");
		if (errorcode == 0) {
			jobid = head.getString("jobid");
		}

		Job<String> job = new Job<String>();
		job.setError(errorcode);
		job.setId(jobid);
		job.setStatus(JobStatus.RUNNING);
		log.info("rail server agent :[" + reString + "]");
		return job;
	}

	/**
	 * 
	 * Title: getRailApplicationIcon Description:
	 * 
	 * @param applicationId
	 * @param @param applicationid
	 * @param @return
	 * @param @throws Exception
	 * @return RailApplicationIcon
	 * @throws
	 */
	public static RailApplicationIcon getRailApplicationIcon(String serverIp,
			String applicationid) throws Exception {

		String url = VdiAgentClientImpl.buildAgentUrl(serverIp,
				"getRailApplicationIcon");
		log.info("URL:" + url);
		HttpPostRequest request = HttpPostRequest.instanceByUrl(serverIp, url);
		Map<String, String> paramterStr = new HashMap<String, String>();
		paramterStr.put("Content-Type", "text/plain; charset=UTF-8");
		request.addHeader(paramterStr);
		String reString = request.executeJSONStr("{\"applicationid\":\""
				+ applicationid + "\"}");
		log.info("rail server agent :[" + reString + "]");
		Gson g = new Gson();
		RailApplicationIconRes icon = g.fromJson(reString,
				RailApplicationIconRes.class);
		if (icon.getHead().getError() != 0) {
			throw new Exception("Agent failure [error]"
					+ icon.getHead().getError());
		}
		return icon.getBody();
	}

	/**
	 * @throws Exception
	 * @Title: getPerformanceCounter
	 * @param @param parseInt 设定文件
	 * @return void 返回类型
	 * @throws
	 */
	public static RailResponse<AgentRailApplicationServer> getPerformanceCounter(
			String serverIp, int parseInt) throws Exception {
		log.info("getPerformanceCounter :: enter. serverIp==>[	" + serverIp
				+ "]");
		RailResponse<AgentRailApplicationServer> res = new ApplicationServerResponse();
		String url = VdiAgentClientImpl.buildAgentUrl(serverIp,
				"getPerformanceCounter");
		log.info("URL:" + url);
		HttpPostRequest request = HttpPostRequest.instanceByUrl(serverIp, url);
		Map<String, String> paramterStr = new HashMap<String, String>();
		paramterStr.put("Content-Type", "text/plain; charset=UTF-8");
		request.addHeader(paramterStr);
		String reString = request.executeJSONStr("{\"timespan\":" + parseInt
				+ "}");
		log.info("result ::[" + reString + "]");
		return res.instanceByJson(reString);
	}

	/**
	 * @return: ListRailApplications
	 * @param serverIp
	 *            格式例如："10.1.32.93"
	 * @return
	 * @throws Exception
	 *             (IllegalArgumentException : 传递的参数可能不是真正IP地址)
	 */
	@SuppressWarnings("rawtypes")
	public static RailResponse<RailInformation> getHostJoinInformation(
			String serverIp) throws Exception {
		RailResponseSupport<RailInformation> ponse = new RailResponseSupport<RailInformation>();
		String url = VdiAgentClientImpl.buildAgentUrl(serverIp,
				"getHostJoinInformation");
		log.info("getHostJoinInformation ::" + url);
		HttpPostRequest request = HttpPostRequest.instanceByUrl(serverIp, url);
		Map<String, String> paramterStr = new HashMap<String, String>();
		paramterStr.put("Content-Type", "text/plain; charset=UTF-8");
		request.addHeader(paramterStr);
		String reString = request.execute();
		log.info("getHostJoinInformation result " + reString);
		StringMap map = ponse.getJsonMap(reString);
		ponse.setHead(OpzoonUtils.getHeadByString(map.get("head") + ""));
		ponse.setBody(OpzoonUtils.getGson().fromJson(map.get("body") + "",
				RailInformation.class));
		return ponse;
	}



	// real deal getRDPStatus
	private static String queryRDPStatus(String ip) {
		String isStatus = "false";
		String url = buildAgentUrl(ip, "getRDPStatus");
		log.debug("getRDPStatus enter. url " + url);
		HttpPostRequest post;
		try {
			post = HttpPostRequest.instanceByUrl(ip, url);
			String result = post.execute();
			if (result != null) {
				RDPResult rdprResult = new Gson().fromJson(result,
						RDPResult.class);
				RDPStatus status = rdprResult.getBody();
				if (status != null && 0 == status.getValid()) {
					isStatus = "true";
				} else {
					isStatus = "false";
				}
			}
		} catch (Exception e) {
			log.error("getRDPStatus exception [" + e.getMessage() + "]", e);
			isStatus = "error";
		}
		if (isStatus.equals("true")) {
			Session.setCache("rdp_" + ip.trim(), isStatus,20,TimeUnit.SECONDS);
		} else {
			Session.setCache("rdp_" + ip.trim(), isStatus, 30, TimeUnit.SECONDS);
		}
		return isStatus;
	}
	public static  boolean isJointype(String ip){
		String url = buildAgentUrl(ip, "isJointype");
		log.debug("isJointype enter. url " + url);
		HttpPostRequest post;
		try {
			post = HttpPostRequest.instanceByUrl(ip, url);
			String result = post.execute();
			if (result != null) {
				RDPResult rdprResult = new Gson().fromJson(result,
						RDPResult.class);
				RDPStatus status = rdprResult.getBody();
				if (status != null && 0 == status.getValid()) {
					// 判断模板是否加入了域
					RailResponse<RailInformation> rs = getHostJoinInformation(ip);
					log.debug("getRDPStatus :: is join domain? "
							+ rs.getBody().getJointype());
					
					if (3 == rs.getBody().getJointype()) {
						return true;
					}
				} else {
					return false;
				}
			}
		} catch (Exception e) {
			log.error("isJointype exception [" + e.getMessage() + "]", e);
			return false;
		}
	
		return false;
	}
}
