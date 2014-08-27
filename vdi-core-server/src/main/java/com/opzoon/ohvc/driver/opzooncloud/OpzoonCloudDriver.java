/**
 *
 */
package com.opzoon.ohvc.driver.opzooncloud;

//@TODO 

import java.net.InetAddress;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.log4j.Logger;
import org.json.JSONArray;

import com.opzoon.ohvc.common.Constants;
import com.opzoon.ohvc.common.JSONObjectUtils;
import com.opzoon.ohvc.common.Job;
import com.opzoon.ohvc.common.JobStatus;
import com.opzoon.ohvc.domain.ErrorMeesage;
import com.opzoon.ohvc.domain.Login;
import com.opzoon.ohvc.driver.opzooncloud.domain.DistributionStrategy;
import com.opzoon.ohvc.driver.opzooncloud.domain.OpzoonCloudJob;
import com.opzoon.ohvc.driver.opzooncloud.domain.OpzoonCloudOptionVMInstance;
import com.opzoon.ohvc.driver.opzooncloud.domain.OpzoonCloudVMInstance;
import com.opzoon.ohvc.driver.opzooncloud.domain.OpzoonCloudVMInstanceList;
import com.opzoon.ohvc.driver.opzooncloud.request.HttpDeleteRequest;
import com.opzoon.ohvc.driver.opzooncloud.request.HttpPutRequest;
import com.opzoon.ohvc.driver.opzooncloud.request.OpzoonParseResponseResult;
import com.opzoon.ohvc.driver.opzooncloud.request.OpzooncloudHttpGetRequest;
import com.opzoon.ohvc.driver.opzooncloud.request.OpzooncloudHttpPostRequest;
import com.opzoon.ohvc.driver.opzooncloud.service.imp.OpzoonCloudAuthenticateProxy;
import com.opzoon.ohvc.request.SetHostnameRequest;
import com.opzoon.ohvc.response.ParseResponseResult;
import com.opzoon.ohvc.response.ResourceNotMeetException;
import com.opzoon.ohvc.response.UsernameOrPasswordException;
import com.opzoon.ohvc.service.VdiAgentClientImpl;
import com.opzoon.ohvc.session.ExcecutorUtil;
import com.opzoon.ohvc.session.Session;
import com.opzoon.vdi.core.cloud.CloudManagerSupport;
import com.opzoon.vdi.core.domain.Template;
import com.opzoon.vdi.core.domain.VMInstance;
import com.opzoon.vdi.core.domain.Volume;

/**
 * ClassName: OpzoonCloudVMInstanceServiceImp.java Description:
 * OpzoonCloudVMInstanceServiceImp.java
 * 
 * @author: maxiaochao
 * @date: 2012-9-20
 * @version: V04
 */
public class OpzoonCloudDriver extends CloudManagerSupport {
	private static final Logger log = Logger.getLogger(OpzoonCloudDriver.class);
	private static String START = "start";
	private static String STOP = "stop";
	// private static Lock startOrStopLock=new ReentrantLock();
	private static String monitorListCache = "opv_excutor";
	private static final ConcurrentHashMap<String, Object> session = new ConcurrentHashMap<String, Object>();

	private synchronized void excutorLisvmCache() {
		if (baseUrl == null) {
			return;
		}
		String key = monitorListCache + baseUrl;

		if (session.get(key) != null) {
			return;
		} else {
			ScheduledFuture<?> result = ExcecutorUtil.invokeSchedule(
					new Runnable() {

						@Override
						public void run() {
							setListCache();
						}
					}, 1, 5, TimeUnit.SECONDS);

			session.put(key, result);
		}
	}

	private String baseUrl;

	public String createVMInstanceByJSONString(String jsonString)
			throws Exception {
		String url = Constants.formatURL(
				Constants.VDI_OPZOONCLOUD_CREATE_INSTANCE_URL, this.baseUrl);
		OpzooncloudHttpPostRequest request = OpzooncloudHttpPostRequest
				.instanceByUrl(this.baseUrl, url);

		return request.executeJSONStr(jsonString);
	}

	@Override
	public void login(String username, String password, String domain)
			throws Exception, UsernameOrPasswordException {
		log.info("opzoonDrvier: login username [" + username + "] password ["
				+ password + "]");
		OpzoonCloudAuthenticateProxy proxy = new OpzoonCloudAuthenticateProxy();
		proxy.startLogin(this.baseUrl, new Login().setUsername(username)
				.setPassword(password).setDomainId(domain));

		excutorLisvmCache();
	}

	@Override
	public List<Template> listTemplates() throws Exception {
		log.info("opzoonDrvier listTemplates url==>"
				+ Constants.formatURL(Constants.VDI_OPZOONCLOUD_TEMPS_URL,
						this.baseUrl));
		String result ="";
//		if(link){
		OpzooncloudHttpGetRequest request = OpzooncloudHttpGetRequest
				.instanceByUrl(this.baseUrl, Constants.formatURL(
						Constants.VDI_OPZOONCLOUD_TEMPS_URL, this.baseUrl));
		result = request.execute();
		
		List<OpzoonCloudVMInstance> instances = OpzoonParseResponseResult
				.parseOpzoonCloudVMInstanceList(result).getInstances();
		List<Template> templatess = new ArrayList<Template>();
		for (OpzoonCloudVMInstance instance : instances) {
			// 加过滤条件防止用户偷开模版
			if (instance.getState().equalsIgnoreCase("stopped")) {
				Template template = new Template()
						.setTemplateId(instance.getName())
						.setTemplatename(instance.getLabel())
						.setProtoco(instance.getDisplay_protoco());
				templatess.add(template);
				log.info(template);
			}
		}
		log.debug("opzoonDrvier  listTemplates url==>end");
		return templatess;
	}

	public void setBaseUrl(String baseUrl) {
		this.baseUrl = baseUrl;
	}

	@Override
	public void exitLogin() {
		log.debug("exitLogin=====>" + baseUrl);
		String key = monitorListCache + baseUrl;
		// ~~~停止平台 1、删除缓存，删除节点
		Session.stopSessionByPlatformName(this.baseUrl);
		Object schduledFuture = session.get(key);
		if (schduledFuture != null) {
			((ScheduledFuture<?>) schduledFuture).cancel(true);
		}
		// 删除缓存
		session.remove(key);
	}

	@Override
	public Job<VMInstance> destroyVM(String vmId) throws Exception {
		log.info("opzoonDrvier destroyVM  enter. VMID[" + vmId + "]");
		OpzoonCloudVMInstance in = this.getVM(vmId);
		// vmId = in.getOptionId()!=null ? in.getOptionId() : vmId;
		String opId = in.getOptionId();
		if (opId != null && !"".equals(opId)) {
			vmId = opId;
		}
		HttpDeleteRequest request = HttpDeleteRequest.instanceByUrl(
				this.baseUrl,
				Constants.formatURL(Constants.VDI_OPZOONCLOUD_INSTANCE_URI,
						this.baseUrl) + vmId);
		request.execute();
		Session.removeCache(vmId);
		return new Job<VMInstance>().setStatus(JobStatus.SUCCESSFUL);
	}
	
	/*
	 * @see com.opzoon.vdi.core.cloud.CloudManager#cloneVM(java.lang.String,
	 * java.lang.String)
	 */
	public Job<VMInstance> cloneVM(String templateId, String nameOfNewVM,boolean link)
			throws Exception, ResourceNotMeetException {
		log.info("opzoonDrvier cloneVM" + templateId);
		Job<VMInstance> job = new Job<VMInstance>();
		String param="";
		if(link){
			param = new OpzoonCloudOptionVMInstance()
		    .setOption(OpzoonCloudOptionVMInstance.LINK_CLONE)
			.setLabel(nameOfNewVM)
			.setDistribution_strategy(DistributionStrategy.getInstance())
			.bulidJsonString();}
		else{
			param = new OpzoonCloudOptionVMInstance()
			.setOption(OpzoonCloudOptionVMInstance.CLONE)
			.setLabel(nameOfNewVM)
			.setDistribution_strategy(DistributionStrategy.getInstance())
			.bulidJsonString();
		}
		HttpPutRequest request = HttpPutRequest.instanceByUrl(
				this.baseUrl,
				Constants.formatURL(Constants.VDI_OPZOONCLOUD_TEMP_URI,
						this.baseUrl) + templateId);
		try {
			log.info("opzoonDrvier_clone[" + param + "]");

			String result = request.executeJSONStr(param);
			OpzoonCloudVMInstance instance = null;
			instance = OpzoonParseResponseResult
					.parseOpzoonCloudQueryInstance(result);
			log.warn("opzoonDrvier cloneVM result[" + result + "]");
			job.setId(instance.getCloned_name());
			instance.setId(instance.getCloned_name());
			job.setResult(instance);
		} catch (Exception e) {
			String str = e.getMessage();
			log.error("clone [" + e.getMessage() + "]",e);
			if (str == null) {
				throw e;
			}
			if (str != null
					&& !(str.contains(ErrorMeesage.CLONE_ERRORCODE) || str
							.contains(ErrorMeesage.CLONE_ERRORCODE_ZK))) {
				throw e;
			}
			job.setStatus(JobStatus.RUNNING);
			job.setId("clone," + templateId + "," + nameOfNewVM);
		}
		log.warn("opzoonDrvier cloneVM" + templateId + "status ::"
				+ job.getStatus());
		return job;
	}

	/*
	 * @see
	 * com.opzoon.vdi.core.cloud.CloudManager#queryJobStatus(com.opzoon.common
	 * .Job)
	 */
	@SuppressWarnings("unchecked")
	public <T> void queryJobStatus(final Job<T> job) throws Exception {
		log.debug("opzoonDrvier  queryJobStatus ==>");
		if (job != null) {
			if (job.getId() == null || "".equals(job.getId())

			|| job.getStatus().equals(JobStatus.FAILED)) {
				return;
			}

			if (job.getId().startsWith("clone")) {
				String[] tmp = job.getId().split(",");
				this.cloneVM(tmp[1], tmp[2], (Job<VMInstance>) job);
				return;
			}
		}
		if (job.getIp() != null && !"".equals(job.getIp())) {
			VdiAgentClientImpl.queryAsyncJobResult(job);
		} else {
			this.queryOpzooncloudJob(job);
		}

	}

	/**
	 * query opzoon job.
	 * 
	 * @param job
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	private <T> void queryOpzooncloudJob(final Job<T> job) throws Exception {

		final String jobid = job.getId();
		OpzoonCloudVMInstance instance = getVM(jobid);
		job.setId(instance.getId());
		job.setResult((T) instance);
		String state = instance.getState();

		if (job.getName() != null && job.getName().equals(START)) {
			if (state.trim().equalsIgnoreCase("running")) {
				job.setStatus(JobStatus.SUCCESSFUL);
			} else {
				job.setStatus(JobStatus.RUNNING);
			}
		} else if (job.getName() != null && job.getName().equals(STOP)) {
			if (state.trim().equalsIgnoreCase("stopped")) {
				job.setStatus(JobStatus.SUCCESSFUL);
			} else {
				job.setStatus(JobStatus.RUNNING);
			}
		} else {
			if (state.trim().equalsIgnoreCase("stopped")
					|| state.trim().equalsIgnoreCase("running")) {
				job.setStatus(JobStatus.SUCCESSFUL);
				log.info("opzoonDrvier " + job.getId() + ":"
						+ JobStatus.SUCCESSFUL);
				// break;
			} else if (state.trim().equalsIgnoreCase("error")) {
				job.setStatus(JobStatus.FAILED);
				log.info("opzoonDrvier " + job.getId() + ":" + JobStatus.FAILED);
			} else {
				job.setStatus(JobStatus.RUNNING);

			}
		}
	}

	/*
	 * @see com.opzoon.vdi.core.cloud.CloudManager#stopVM(java.lang.String)
	 */
	@Override
	public Job<VMInstance> stopVM(String vmId) throws Exception {
		log.info("stopVM enter. [" + vmId + "]");
		int result = 0;
		OpzoonCloudJob<VMInstance> opzoonCloudJob = new OpzoonCloudJob<VMInstance>();
		opzoonCloudJob.setServiceState("stopped");

		OpzoonCloudVMInstance validateInstance = this.getVM(vmId);
//		if(validateInstance.geto)
		String opId = validateInstance.getOptionId();
		if (opId != null && !"".equals(opId)) {
			vmId = opId;
		}
		if (!validateInstance.getState().equalsIgnoreCase("running")) {
			return opzoonCloudJob
					.setError(ParseResponseResult.ERR_STOP_VM_AGAIN)
					.setStatus(JobStatus.FAILED).setResult(validateInstance);
		}
		synchronized (this) {
			HttpPutRequest request = HttpPutRequest.instanceByUrl(
					this.baseUrl,
					Constants.formatURL(Constants.VDI_OPZOONCLOUD_INSTANCE_URI,
							this.baseUrl) + vmId);
			int i = 0;
			try {

				request.executeJSONStr(new OpzoonCloudOptionVMInstance()
						.setOption(OpzoonCloudOptionVMInstance.STOP)
						.bulidJsonString().toString());

			} catch (Exception e) {
				String str = e.getMessage();
				if (str != null
						&& !(str.contains(ErrorMeesage.CLONE_ERRORCODE) || str
								.contains(ErrorMeesage.CLONE_ERRORCODE_ZK))) {
					throw e;
				}
				if (++i <= 10) {
					Thread.sleep(5000);
					stopVM(vmId);
				}
			}

		}

		opzoonCloudJob.setId(vmId);
		opzoonCloudJob.setError(result);
		opzoonCloudJob.setName(STOP);
		return opzoonCloudJob;
	}

	/*
	 * @see com.opzoon.vdi.core.cloud.CloudManager#rebootVM(java.lang.String)
	 */
	@Override
	public Job<?> rebootVM(String vmId) throws Exception {
		log.info("rebootVM [" + vmId + "]");
		String key = Constants.JOB_HEADS + vmId;
		Job<?> job = (Job<?>) Session.getCache(key);
		if (job != null) {
			VMInstance instance = (VMInstance) job.getResult();
			if (instance.getState() != null
					&& !instance.getState().equalsIgnoreCase("running")) {
				throw new Exception(
						"{message : \"VM is   status is error!\",status:"
								+ instance.getState() + "}");
			}
		}
		VMInstance instance = this.getVM(vmId);
		if (instance.getState().equalsIgnoreCase("running")) {
			this.stopVM(vmId);
			// try {
			synchronized (this) {

				// this.startOrStopLock.lock();
				while (true) {

					instance = this.getVM(vmId);
					if (instance.getState().equalsIgnoreCase("stopped")) {
						this.startVM(vmId);
						break;
					}
					Thread.sleep(10000);
				}
			}

		} else {
			throw new Exception(
					"{message : \"VM is   status is error!\",status:"
							+ instance.getState() + "}");

		}
		return new Job<VMInstance>().setId(vmId);
	}

	/*
	 * @see com.opzoon.vdi.core.cloud.CloudManager#getVM(java.lang.String)
	 */
	@Override
	public OpzoonCloudVMInstance getVM(String vmId) throws Exception {
		log.info("getVM : vmID[" + vmId + "]");
		if (vmId == null) {
			return null;
		}
		OpzoonCloudVMInstance opzoonCloudVMInstance = (com.opzoon.ohvc.driver.opzooncloud.domain.OpzoonCloudVMInstance) Session
				.getCache(vmId);
		try {
			if (opzoonCloudVMInstance == null) {
				log.info("getVM URL:["
						+ Constants.formatURL(
								Constants.VDI_OPZOONCLOUD_INSTANCE_URI,
								this.baseUrl) + vmId + "]");
				OpzooncloudHttpGetRequest request = OpzooncloudHttpGetRequest
						.instanceByUrl(
								this.baseUrl,
								Constants.formatURL(
										Constants.VDI_OPZOONCLOUD_INSTANCE_URI,
										this.baseUrl)
										+ vmId);
				String result = request.execute();
				opzoonCloudVMInstance = OpzoonParseResponseResult
						.parseOpzoonCloudQueryInstance(result);
			}
			opzoonCloudVMInstance.setOptionId(vmId);
		} catch (Exception e) {
			Session.removeCache(vmId);
			opzoonCloudVMInstance = this.getVMByOriginalName(vmId);
			throw e;
		}
		//
		setVMIp(opzoonCloudVMInstance);
		try {
			opzoonCloudVMInstance.setHost(opzoonCloudVMInstance
					.getVnc().split(":")[0]);
			opzoonCloudVMInstance
					.setPort(Integer.parseInt(opzoonCloudVMInstance
							.getVnc().split(":")[1]));
		} catch (Exception e) {
			String ipLast = opzoonCloudVMInstance.getIp().substring(
					opzoonCloudVMInstance.getIp().lastIndexOf(".") + 1);
			String ipStart = opzoonCloudVMInstance.getIp().substring(0,
					opzoonCloudVMInstance.getIp().lastIndexOf("."));
			int ipLastI = Integer.parseInt(ipLast) - 1;
			opzoonCloudVMInstance.setHost(ipStart + "." + ipLastI);
			opzoonCloudVMInstance.setPort(5900);
		}

		log.info("getVM :: vmId[" + vmId + "] state ["
				+ opzoonCloudVMInstance.getState() + "]");
		excutorLisvmCache();
		return opzoonCloudVMInstance;
	}

	private OpzoonCloudVMInstance getVMByOriginalName(String vmId)
			throws Exception {
		vmId = vmId.substring(vmId.lastIndexOf("/") + 1);
		OpzooncloudHttpGetRequest request = OpzooncloudHttpGetRequest
				.instanceByUrl(
						this.baseUrl,
						Constants.formatURL(
								Constants.VDI_OPZOONCLOUD_INSTANCE_URI,
								this.baseUrl)
								+ "?original_name=" + vmId);
		String result = request.execute();
		List<OpzoonCloudVMInstance> instances = OpzoonParseResponseResult
				.parseOpzoonCloudVMInstanceList(result).getInstances();
		log.debug("getVMByoriginalName ==>"+result);
		if (instances.size() == 0) {
			return null;
		}
		OpzoonCloudVMInstance instance = instances.get(0);
		instance.setOptionId(instance.getName());
		if (instances.size() > 1) {
			instance.setId(vmId);
			instance.setName(instance.getLabel());
			instance.setState("error");
			return instance;
		} else {
			instance.setId(vmId);
			instance.setName(instance.getLabel());
			setVMIp(instance);
			return instance;
		}
	}

	/*
	 * @see com.opzoon.vdi.core.cloud.CloudManager#startVM(java.lang.String)
	 */
	@Override
	public Job<VMInstance> startVM(String vmId) throws Exception {
		log.info("startVM enter. ["+vmId+"]");
		int result = 0;
		OpzoonCloudJob<VMInstance> job = new OpzoonCloudJob<VMInstance>();
		job.setServiceState("running");
		OpzoonCloudVMInstance validateInstance = this.getVM(vmId);
//		if(validateInstance.geto)
		String opId = validateInstance.getOptionId();
		if (opId != null && !"".equals(opId)) {
			vmId = opId;
		}
		
		String url = Constants.formatURL(
				Constants.VDI_OPZOONCLOUD_INSTANCE_URI, this.baseUrl) + vmId;
		if (!validateInstance.getState().equalsIgnoreCase("stopped")) {
			return job.setError(ParseResponseResult.ERR_START_VM_AGAIN)
					.setStatus(JobStatus.FAILED).setResult(validateInstance);
		}
		synchronized (this) {
			HttpPutRequest request = HttpPutRequest.instanceByUrl(this.baseUrl,
					url);
			int i = 0;
			try {
				String param = new OpzoonCloudOptionVMInstance()
						.setOption(OpzoonCloudOptionVMInstance.START)
						.setAttributes(OpzoonCloudOptionVMInstance.getSpice())
						.bulidJsonString();
				log.debug("startVM :::::"+param);
				request.executeJSONStr(param);
			} catch (Exception e) {
				String str = e.getMessage();
				if (str != null
						&& !(str.contains(ErrorMeesage.CLONE_ERRORCODE) || str
								.contains(ErrorMeesage.CLONE_ERRORCODE_ZK))) {
					throw e;
				}

				Thread.sleep(5000);
				if (++i <= 10) {
					startVM(vmId);
				}
			}
		}
		job.setName(START);
		job.setId(vmId).setError(result).setStatus(JobStatus.RUNNING);
		return job;
	}

	/**
	 * 
	 * @throws Exception
	 */
	public static OpzoonCloudVMInstanceList parseOpzoonCloudVMInstanceList(
			String resultData) throws Exception {

		OpzoonCloudVMInstanceList vs = JSONObjectUtils.parseOpzoonInstance(
				resultData, OpzoonCloudVMInstanceList.class);
		JSONArray jsonArray = vs.getResult();
		List<OpzoonCloudVMInstance> ls = new ArrayList<OpzoonCloudVMInstance>();
		vs.setInstances(ls);
		for (int i = 0; i < jsonArray.length(); i++) {
			OpzoonCloudVMInstance instance = JSONObjectUtils
					.parseOpzoonInstance(jsonArray.get(i).toString(),
							OpzoonCloudVMInstance.class);
			if (instance.getState().equalsIgnoreCase("stopped")) {

				ls.add(instance);

			}
		}
		return vs;
	}

	@Override
	public boolean getRdpStatus(String vmId) throws Exception {
		log.trace("");
		VMInstance vm = this.getVM(vmId);
		VdiAgentClientImpl.getRDPStatus(vm.getIpaddress());
		return VdiAgentClientImpl.getRDPStatus(vm.getIpaddress());
	}

	@SuppressWarnings("unchecked")
	@Override
	public Job<SetHostnameRequest> setHostname(String vmId, String hostname,
			Integer type, String account, String password, boolean restart)
			throws Exception {
		VMInstance vmInstance = this.getVM(vmId);
		@SuppressWarnings("rawtypes")
		Job job = VdiAgentClientImpl.setHostname(vmInstance.getIpaddress(),
				hostname, type, account, password, restart);
		job.setAgent(true);
		return job;
	}

	@Override
	public void shutdownSystem(String vmId) throws Exception {
		VMInstance vmInstance = this.getVM(vmId);
		VdiAgentClientImpl.shutdownSystem(vmInstance.getIpaddress());
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	public Job<String> restartSystem(String vmId) throws Exception {
		VMInstance vmInstance = this.getVM(vmId);
		Job job = VdiAgentClientImpl.restartSystem(vmInstance.getIpaddress());
		return job;
	}

	// Evan
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public Job<String> joinDomain(String vmId, String domainname,
			String domainbinddn, String domainbindpass, boolean restart)
			throws Exception {
		VMInstance vmInstance = this.getVM(vmId);
		Job job = VdiAgentClientImpl.joinDomain(vmInstance.getIpaddress(),
				domainname, domainbinddn, domainbindpass, restart);
		job.setAgent(true);
		return job;
	}

	// Evan
	public Job<String> createUser(String vmId, String username, String password)
			throws Exception {
		VMInstance vmInstance = this.getVM(vmId);
		return VdiAgentClientImpl.createUser(vmInstance.getIpaddress(),
				username, password);
	}

	// Evan
	public Job<String> updateUserPassword(String vmId, String username,
			String password) throws Exception {
		VMInstance vmInstance = this.getVM(vmId);
		return VdiAgentClientImpl.updateUserPassword(vmInstance.getIpaddress(),
				username, password);
	}

	// Evan
	public Job<String> deleteUser(String vmId, String username)
			throws Exception {
		VMInstance vmInstance = this.getVM(vmId);

		return VdiAgentClientImpl.deleteUser(vmInstance.getIpaddress(),
				username, null);

	}

	/**
	 * 
	 * @author:maxiaochao 2012-11-1
	 * @param templateId
	 * @param nameOfNewVM
	 * @param job
	 * @return
	 * @throws Exception
	 */
	private Job<VMInstance> cloneVM(String templateId, String nameOfNewVM,
			Job<VMInstance> job) throws Exception {
		log.info("cloneVM ==>[" + templateId + "] name :[" + nameOfNewVM + "]");
		String param = new OpzoonCloudOptionVMInstance()
				.setOption(OpzoonCloudOptionVMInstance.LINK_CLONE)
				.setLabel(nameOfNewVM)
				.setDistribution_strategy(DistributionStrategy.getInstance())
				.bulidJsonString();
		HttpPutRequest request = HttpPutRequest.instanceByUrl(
				this.baseUrl,
				Constants.formatURL(Constants.VDI_OPZOONCLOUD_TEMP_URI,
						this.baseUrl) + templateId);
		try {
			String result = request.executeJSONStr(param);
			OpzoonCloudVMInstance instance = null;

			instance = OpzoonParseResponseResult
					.parseOpzoonCloudQueryInstance(result);
			job.setId(instance.getCloned_name());
			instance.setId(instance.getCloned_name());
			job.setResult(instance);
		} catch (Exception e) {
			String str = e.getMessage();

			if (str == null) {
				throw e;
			}
			if (str != null
					&& !(str.contains(ErrorMeesage.CLONE_ERRORCODE) || str
							.contains(ErrorMeesage.CLONE_ERRORCODE_ZK))) {
				throw e;
			}
			job.setStatus(JobStatus.RUNNING);
			job.setId("clone," + templateId + "," + nameOfNewVM);
		}

		log.info(job.getStatus());
		return job;
	}

	@Override
	public void deleteVolume(String storageid) throws Exception {
	}

	@Override
	public Job<?> eraseVolume(String storageid) throws Exception {
		Job<?> job = new Job<String>();
		job.setStatus(JobStatus.SUCCESSFUL);
		return job;
	}

	@Override
	public List<Volume> listVolumes(String volumeIdForQuerying)
			throws Exception {
		List<Volume> volumes = new LinkedList<Volume>();
		return volumes;
	}

	@Override
	public Job<?> detachVolume(String storageid) throws Exception {
		Job<?> job = new Job<String>();
		job.setStatus(JobStatus.SUCCESSFUL);
		return job;
	}

	@Override
	public Job<?> attachVolume(String storageid, String vmid) throws Exception {
		Job<?> job = new Job<String>();
		job.setStatus(JobStatus.SUCCESSFUL);
		return job;
	}

	@Override
	public Job<?> createVolume(String format, int size) throws Exception {
		Job<String> job = new Job<String>();
		job.setStatus(JobStatus.SUCCESSFUL);
		job.setResult("dummy"
				+ Integer.toHexString((int) (Math.random() * 100000000)));
		return job;
	}

	/**
	 * 
	 * @param @param ip
	 * @param @return
	 * @return boolean
	 * @throws
	 * @author : david
	 * @since : v1.0.0.0
	 * @date : 2012-12-18 娑撳﹤宕�1:06:55
	 */
	private boolean isInetAddress(String ip) {
		String reg = "\\b((?!\\d\\d\\d)\\d+|1\\d\\d|2[0-4]\\d|25[0-5])\\.((?!\\d\\d\\d)\\d+|1\\d\\d|2[0-4]\\d|25[0-5])\\.((?!\\d\\d\\d)\\d+|1\\d\\d|2[0-4]\\d|25[0-5])\\.((?!\\d\\d\\d)\\d+|1\\d\\d|2[0-4]\\d|25[0-5])\\b";
		if (!matches(ip, reg)) {
			try {
				String add = InetAddress.getByName(ip).getHostAddress();
				return matches(add, reg);
			} catch (Exception e) {
				return false;
			}
		}

		return true;
	}

	/**
	 * Title: matches : d
	 * 
	 * @param value
	 * @param regular
	 * @param
	 * @return boolean
	 * @throws
	 */
	private boolean matches(String value, String regular) {
		Pattern pattern = Pattern.compile(regular);
		Matcher matcher = pattern.matcher(value); // 娴犮儵鐛欑拠锟�7.400.600.2娑撹桨绶�
		return matcher.matches();
	}

	/*
	 * <p>Title: resetVM</p> <p>Description: </p>
	 * 
	 * @param arg0
	 * 
	 * @return
	 * 
	 * @see com.opzoon.vdi.core.cloud.CloudManager#resetVM(java.lang.String)
	 */

	@Override
	public Job<?> resetVM(String templateId, String id) throws Exception {
		log.info("resetVM==> [" + templateId + "] id==>[" + id + "]");
		OpzoonCloudVMInstance opzoonCloudVMInstance = this.getVM(id);
		boolean tab = true;
		while (tab) {
			try {
				this.destroyVM(id);
				tab = false;
			} catch (Exception e) {
				tab = true;
				Thread.sleep(5000);
			}
		}
		return this.cloneVM(templateId, opzoonCloudVMInstance.getLabel(),true);
	}

	@Override
	public Job<String> logOff(String vmid, String domainname, String username)
			throws Exception {
		log.info("logOff==> [" + vmid + "] id==>[" + domainname + "] username["
				+ username + "]");
		VMInstance vmInstance = this.getVM(vmid);
		return VdiAgentClientImpl.logOff(vmInstance.getIpaddress(), domainname,
				username);
	}

	public void setListCache() {

		OpzooncloudHttpGetRequest request;
		try {

			request = OpzooncloudHttpGetRequest.instanceByUrl(this.baseUrl,
					Constants.formatURL(
							Constants.VDI_OPZOONCLOUD_INSTANCES_URL,
							this.baseUrl));
			log.info("opzoonDrvier setListCache url==>"
					+ Constants.formatURL(
							Constants.VDI_OPZOONCLOUD_INSTANCES_URL,
							this.baseUrl));
			String result = request.execute();
			List<OpzoonCloudVMInstance> instances = OpzoonParseResponseResult
					.parseOpzoonCloudVMInstanceList(result).getInstances();
			for (OpzoonCloudVMInstance instance : instances) {
				// 转换
				instance.setId(instance.getName());
				instance.setName(instance.getLabel());
				String id = instance.getId();
				setVMIp(instance);
				try {
					instance.setHost(instance.getVnc().split(":")[0]);
					instance.setPort(Integer.parseInt(instance.getVnc().split(
							":")[1]));
				} catch (Exception e) {
					String ipLast = instance.getIp().substring(
							instance.getIp().lastIndexOf(".") + 1);
					String ipStart = instance.getIp().substring(0,
							instance.getIp().lastIndexOf("."));
					int ipLastI = Integer.parseInt(ipLast) - 1;
					instance.setHost(ipStart + "." + ipLastI);
					instance.setPort(5900);
				}

				log.debug("opzoonDrvier setListCache url==>" + id + ":"
						+ instance.getState());
				Session.setCache(id, instance,5,TimeUnit.SECONDS);
			}
			Thread.sleep(5000);
		} catch (Exception e) {
			log.error(e.getMessage());
		}
	}

	@Override
	public Job<String> deleteUserProfile(String vmid, String domain,
			String username) throws Exception {
		VMInstance vmInstance = this.getVM(vmid);
		return VdiAgentClientImpl.deleteUserProfile(vmInstance.getIpaddress(),
				domain, username);
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public Job<String> joinWorkgroup(String vmid, String workgroupname,
			String account, String password, boolean restart) throws Exception {
		VMInstance vmInstance = this.getVM(vmid);
		Job job = VdiAgentClientImpl.joinWorkgroup(vmInstance.getIpaddress(),
				workgroupname, account, password, restart);
		job.setAgent(true);
		return job;
	}

	/**
	 * Inner public tool's method that is capacity of set ip for vmInstance.
	 * 
	 * @param instance
	 * @throws Exception
	 */
	private void setVMIp(OpzoonCloudVMInstance instance) throws Exception {
		if (instance!=null&&instance.getIp() != null && instance.getIp().equals("0.0.0.0")) {
			String ip = "0.0.0.0";
			ip_lable: for (int j = 0; j < Integer.MAX_VALUE; j++) {
				JSONArray jsonarray = instance.getAttributes()
						.getJSONObject("network")
						.getJSONObject("opzooncloud_vcable-eth" + j)
						.getJSONArray("address");
				for (int i = 0; i < jsonarray.length(); i++) {
					ip = jsonarray.getString(i);
					boolean isip = this.isInetAddress(ip);
					if (isip) {
						break ip_lable;
					}
				}
			}
			instance.setIp(ip);
			instance.setIpaddress(ip);
		}
		log.debug("OpzoonCloudVMInstance "+instance);
	}
}