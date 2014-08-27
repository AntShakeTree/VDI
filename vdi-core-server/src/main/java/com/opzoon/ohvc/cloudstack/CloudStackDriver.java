/**
 * 
 */
package com.opzoon.ohvc.cloudstack;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.json.JSONObject;

import com.opzoon.ohvc.cloudstack.domain.CloudStackAsyncJob;
import com.opzoon.ohvc.cloudstack.domain.CloudStackDiskOffering;
import com.opzoon.ohvc.cloudstack.domain.CloudStackJob;
import com.opzoon.ohvc.cloudstack.domain.CloudStackTemplate;
import com.opzoon.ohvc.cloudstack.domain.CloudStackVMInstance;
import com.opzoon.ohvc.cloudstack.domain.CloudStackVolume;
import com.opzoon.ohvc.cloudstack.domain.CloudStackZone;
import com.opzoon.ohvc.cloudstack.request.CloudStackHttpGetRequest;
import com.opzoon.ohvc.cloudstack.request.CloudStackHttpRequestManager;
import com.opzoon.ohvc.cloudstack.util.JsonUtil;
import com.opzoon.ohvc.common.Job;
import com.opzoon.ohvc.common.JobStatus;
import com.opzoon.ohvc.domain.Login;
import com.opzoon.ohvc.request.SetHostnameRequest;
import com.opzoon.ohvc.service.VdiAgentClientImpl;
import com.opzoon.vdi.core.cloud.CloudManager;
import com.opzoon.vdi.core.domain.Template;
import com.opzoon.vdi.core.domain.VMInstance;
import com.opzoon.vdi.core.domain.Volume;

/**
 * cloud stack鎺ュ彛瀹炵幇绫�
 * 
 * @author maxiaochao
 * @version V04 2012-9-6
 */
public class CloudStackDriver implements CloudManager {
	private static Logger log = Logger.getLogger(CloudStackDriver.class);
	private String baseUrl;
	private Map<String, String> hostidIpMap = new HashMap<String, String>();
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see com.opzoon.vdi.core.cloud.CloudManager#login(java.lang.String,
	 * java.lang.String, java.lang.String)
	 */
	@Override
	public void login(String username, String password, String domain)
			throws Exception {
		CloudStackAuthenticateProxy proxy = new CloudStackAuthenticateProxy();
		log.debug("cloudstack---------------------------------------login-------------------");
		log.debug("cloudstack--baseUrl-------------------["+this.baseUrl+"-------username"+username+"password"+password);
		proxy.startLogin(this.baseUrl, new Login().setUsername(username)
				.setPassword(password).setResponse("json"));
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.opzoon.vdi.core.cloud.CloudManager#setUrl(java.lang.String)
	 */
	@Override
	public void setBaseUrl(String baseUrl) {
		this.baseUrl = baseUrl;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.opzoon.vdi.core.cloud.CloudManager#startDesketop(java.lang.String,
	 * com.opzoon.vdi.core.cloud.Authentication)
	 */
	@SuppressWarnings("rawtypes")
	@Override
	public Job<String> startVM(String vmId) throws Exception {
		CloudStackHttpRequestManager request = new CloudStackHttpGetRequest(
				this.baseUrl, new CloudStackVMInstance().setCommand(
						"startVirtualMachine").setId(vmId));
		String jobidStr = request.execute();
		// log.info(jobidStr);
		String jobid = JsonUtil.getJobid(jobidStr);

		Job<String> jobStartVm = new Job<String>(String.class);
		jobStartVm.setId(jobid);
		jobStartVm.setStatus(JobStatus.RUNNING);

		return jobStartVm;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.opzoon.vdi.core.cloud.CloudManager#stopDesketop(java.lang.String,
	 * com.opzoon.vdi.core.cloud.Authentication)
	 */
	@Override
	public Job<VMInstance> stopVM(String vmId) throws Exception {
		// TODO Auto-generated method stub
		CloudStackHttpRequestManager request = new CloudStackHttpGetRequest(
				this.baseUrl, new CloudStackVMInstance().setCommand(
						"stopVirtualMachine").setId(vmId));
		String jobidStr = request.execute();
		String jobid = JsonUtil.getJobid(jobidStr);

		Job<VMInstance> jobStopVM = new Job<VMInstance>();
		jobStopVM.setId(jobid);
		jobStopVM.setStatus(JobStatus.RUNNING);
		return jobStopVM;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.opzoon.vdi.core.cloud.CloudManager#rebootDesketop(java.lang.String,
	 * com.opzoon.vdi.core.cloud.Authentication)
	 */
	@Override
	public Job<VMInstance> rebootVM(String vmId) throws Exception {
		// TODO Auto-generated method stub
		CloudStackHttpRequestManager request = new CloudStackHttpGetRequest(
				this.baseUrl, new CloudStackVMInstance().setCommand(
						"rebootVirtualMachine").setId(vmId));
		String jobidStr = request.execute();
		log.info(jobidStr);
		String jobid = JsonUtil.getJobid(jobidStr);
		Job<VMInstance> jobRebootVM = new Job<VMInstance>();
		jobRebootVM.setId(jobid);
		jobRebootVM.setStatus(JobStatus.RUNNING);

		return jobRebootVM;
	}

	/*
	 * @see
	 * com.opzoon.vdi.core.cloud.CloudManager#destroyDesketop(java.lang.String,
	 * com.opzoon.vdi.core.cloud.Authentication)
	 */
	@Override
	public Job<VMInstance> destroyVM(String vmId) throws Exception {
		// TODO Auto-generated method stub
		CloudStackHttpRequestManager request = new CloudStackHttpGetRequest(
				this.baseUrl, new CloudStackVMInstance().setCommand(
						"destroyVirtualMachine").setId(vmId));
		String jobidStr = request.execute();
		log.info(jobidStr);
		String jobid = JsonUtil.getJobid(jobidStr);

		Job<VMInstance> jobDestroyVM = new Job<VMInstance>();
		jobDestroyVM.setId(jobid);
		jobDestroyVM.setStatus(JobStatus.RUNNING);
		return jobDestroyVM;
	}

	/*
	 * 鏌ヨadmin鍚嶄笅鐨凾emplate
	 * 
	 * @see com.opzoon.vdi.core.cloud.CloudManager#listTemplates()
	 */
	@SuppressWarnings("rawtypes")
	@Override
	public List<Template> listTemplates() throws Exception {

		String groupId = getVMGroupId("template");
		if (groupId == null || groupId.isEmpty())
			return new ArrayList<Template>();

		CloudStackHttpRequestManager request = new CloudStackHttpGetRequest(
				this.baseUrl, new CloudStackVMInstance().setCommand(
						"listVirtualMachines").setGroupid(groupId));
		String result = request.execute();

		List<Template> csTemplateList = JsonUtil.getObjectList(result,
				"virtualmachine", Template.class);
		if (csTemplateList == null) {
			return new ArrayList<Template>();
		}
		return csTemplateList;
	}

	@Override
	public Job<VMInstance> cloneVM(String vmId, String nameOfNewVM,boolean link)
			throws Exception {
		CloudStackVMInstance vm = getInstance(vmId);
		Job<VMInstance> jobCloneVM = new Job<VMInstance>(VMInstance.class);
		if (vm == null) {
			jobCloneVM.setStatus(JobStatus.FAILED);
			return jobCloneVM;
		}

		CloudStackVMInstance csVM = new CloudStackVMInstance();
		csVM.setCommand("deployVirtualMachine");
		csVM.setServiceofferingid(vm.getServiceofferingid());
		csVM.setTemplateId(vm.getTemplateid());
		csVM.setZoneid(vm.getZoneid());
		csVM.setHypervisor(vm.getHypervisor());
		csVM.setDisplayname(nameOfNewVM);
		csVM.setName(nameOfNewVM);

		CloudStackHttpRequestManager request = new CloudStackHttpGetRequest(
				this.baseUrl, csVM);
		String jobidStr = request.execute();
		log.info(jobidStr);
		String jobid = JsonUtil.getJobid(jobidStr);
		jobCloneVM.setId(jobid);
		jobCloneVM.setName("virtualmachine");
		jobCloneVM.setStatus(JobStatus.RUNNING);

		return jobCloneVM;
	}

	@Override
	public <T> void queryJobStatus(Job<T> job) throws Exception {
		if (job.isAgent()) {
			VdiAgentClientImpl.queryAsyncJobResult(job);
		} else {
			this.queryCSJobStatus(job);
		}
	}

	private <T> void queryCSJobStatus(Job<T> job) throws Exception {

		CloudStackHttpRequestManager request = new CloudStackHttpGetRequest(
				this.baseUrl, new CloudStackAsyncJob().setCommand(
						"queryAsyncJobResult").setJobId(job.getId()));
		String jobStr = request.execute();
		System.out.println(jobStr);

		JSONObject resultJson = new JSONObject(jobStr);
		String key = (String) resultJson.keys().next();
		JSONObject jsonJob = resultJson.getJSONObject(key);

		CloudStackJob csjob = (CloudStackJob) JsonUtil.getObjectByJSONObject(
				jsonJob, CloudStackJob.class);
		JobStatus js = null;
		switch (csjob.getJobstatus()) {
		case 0:
			js = JobStatus.RUNNING;
			break;
		case 1:
			js = JobStatus.SUCCESSFUL;
			break;
		case 2:
			js = JobStatus.FAILED;
			break;
		}
		job.setStatus(js);

		if (job.getName() != null && js == JobStatus.SUCCESSFUL
				&& "object".equals(csjob.getJobresulttype())) {
			JSONObject jsonObj = (JSONObject) csjob.getJobresult();

			System.out.println(job.classType());
			Object obj = JsonUtil.getObjectByJSONObject(
					jsonObj.getJSONObject(job.getName()), job.classType());

			System.out.println(obj);
			job.setResult((T) obj);
		}
	}

	@Override
	public VMInstance getVM(String vmId) throws Exception {
		CloudStackHttpRequestManager request = new CloudStackHttpGetRequest(
				this.baseUrl, new CloudStackVMInstance().setCommand(
						"listVirtualMachines").setId(vmId));
		String result = request.execute();
		List<VMInstance> vmList = JsonUtil.getObjectList(result,
				"virtualmachine", VMInstance.class);

		if (vmList == null || vmList.size() <= 0)
			return null;

		VMInstance vm = vmList.get(0);
		
		String hostip = hostidIpMap.get(vm.getHostid());
		if(hostip == null && vm.getHostid() != null && !vm.getHostid().equals("0"))
		{
			CloudStackHttpRequestManager listHostRequest = new CloudStackHttpGetRequest(
					this.baseUrl, new CloudStackVMInstance().setCommand(
							"listHosts").setId(vm.getHostid()));
			result = listHostRequest.execute();
			List<Host> hosts = JsonUtil.getObjectList(result, "host", Host.class);
			if(hosts.size() > 0)
			{
				hostidIpMap.put(hosts.get(0).getId(), hosts.get(0).getIpaddress());
				vm.setHost(hosts.get(0).getIpaddress());
			}
		}
		else if(hostip != null)
		{
			vm.setHost(hostip);
		}
		return vm;
	}

	public String getVMGroupId(String groupName) throws Exception {
		if (groupName == null || groupName.isEmpty())
			return "";
		CloudStackVMInstance template = new CloudStackVMInstance();
		template.setCommand("listInstanceGroups");
		template.setName(groupName);
		CloudStackHttpRequestManager request = new CloudStackHttpGetRequest(
				this.baseUrl, template);
		String result = request.execute();

		List<CloudStackTemplate> templateList = JsonUtil.getObjectList(result,
				"instancegroup", CloudStackTemplate.class);
		if (templateList != null && templateList.size() > 0) {
			for (CloudStackTemplate t : templateList) {
				if (groupName.equals(t.getName())) {
					return t.getId();
				}
			}
		}

		return "";
	}

	public CloudStackVMInstance getInstance(String vmId) throws Exception {
		CloudStackHttpRequestManager request = new CloudStackHttpGetRequest(
				this.baseUrl, new CloudStackVMInstance().setCommand(
						"listVirtualMachines").setId(vmId));
		String result = request.execute();
		List<CloudStackVMInstance> vmList = JsonUtil.getObjectList(result,
				"virtualmachine", CloudStackVMInstance.class);
		if (vmList == null || vmList.size() <= 0)
			return null;
		return vmList.get(0);
	}

	/*
	 * @see com.opzoon.vdi.core.cloud.CloudManager#exitLogin()
	 */
	@Override
	public void exitLogin() throws Exception {
		// TODO Auto-generated method stub

	}

	public void shutdownSystem(String vmId) throws Exception {

		VMInstance vm = this.getVM(vmId);
		if (vm == null)
			throw new Exception("not find the vm by vmid: " + vmId);
		VdiAgentClientImpl.shutdownSystem(vm.getIpaddress());
	}

	public Job<String> restartSystem(String vmId) throws Exception {

		VMInstance vm = this.getVM(vmId);
		if (vm == null)
			throw new Exception("not find the vm by vmid: " + vmId);
		return VdiAgentClientImpl.restartSystem(vm.getIpaddress());
	}

	public boolean getRdpStatus(String vmId) throws Exception {
		VMInstance vm = getVM(vmId);
		if (vm == null)
			throw new Exception("not find the vm by vmid: " + vmId);
		return VdiAgentClientImpl.getRDPStatus(vm.getIpaddress());
	}

	@Override
	public Job<SetHostnameRequest> setHostname(String vmId, String hostname, Integer type,
			String account, String password, boolean restart) throws Exception {
		// TODO Auto-generated method stub
		VMInstance vm = getVM(vmId);
		if (vm == null)
			throw new Exception("not find the vm by vmid: " + vmId);
		return VdiAgentClientImpl.setHostname(vm.getIpaddress(), hostname,
				type, account, password, restart);
	}

	// Evan
	public Job<String> joinDomain(String vmId, String domainname, String domainbinddn,
			String domainbindpass, boolean restart) throws Exception {
		VMInstance vmInstance = this.getVM(vmId);
		Job job = VdiAgentClientImpl.joinDomain(vmInstance.getIpaddress(), domainname, domainbinddn, domainbindpass, restart);
		job.setAgent(true);
		return job;
	}

	// Evan
	public Job<String> createUser(String vmId, String username, String password)
			throws Exception {
		VMInstance vmInstance = this.getVM(vmId);
		return VdiAgentClientImpl.createUser(vmInstance.getIpaddress(), username, password);
	}

	// Evan
	public Job<String> updateUserPassword(String vmId, String username, String password)
			throws Exception {
		VMInstance vmInstance = this.getVM(vmId);
		return VdiAgentClientImpl.updateUserPassword(vmInstance.getIpaddress(), username, password);
	}

	// Evan
	public Job<String> deleteUser(String vmId, String username)
			throws Exception {
		VMInstance vmInstance = this.getVM(vmId);
		return VdiAgentClientImpl.deleteUser(vmInstance.getIpaddress(), username, null);
	}

	@Override
	public void deleteVolume(String storageid) throws Exception {
		
		CloudStackVolume volume = new CloudStackVolume();
		volume.setCommand("deleteVolume");
		volume.setId(storageid);
		CloudStackHttpRequestManager request = new CloudStackHttpGetRequest(
				this.baseUrl, volume);
		String result = request.execute();
	}
	
	@Override
	public Job<Volume> eraseVolume(String storageid) throws Exception {
		List<Volume> volumeList = listVolumes(storageid);
		if(volumeList == null || volumeList.size() <= 0)
		{
			log.error("the volume id is not found in CloudStack " + storageid);
			return null;
		}
		Volume volume = volumeList.get(0);
		deleteVolume(storageid);
		
		return createVolume(volume.getName(), volume.getSize());
	}

	@Override
	public List<Volume> listVolumes(String storageid)
			throws Exception {
		CloudStackVolume volume = new CloudStackVolume();
		volume.setCommand("listVolumes");
		volume.setId(storageid);
		CloudStackHttpRequestManager request = new CloudStackHttpGetRequest(baseUrl, volume);
		String result = request.execute();
		System.out.println(result);
		List<Volume> volumeList = JsonUtil.getObjectList(result,
				"volume", Volume.class);
		return volumeList;
	}

	@Override
	public Job<?> detachVolume(String storageid) throws Exception {
		CloudStackVolume volume = new CloudStackVolume();
		volume.setCommand("detachVolume");
		volume.setId(storageid);
		CloudStackHttpRequestManager request = new CloudStackHttpGetRequest(baseUrl, volume);
		String result = request.execute();
		
		String jobid = JsonUtil.getJobid(result);
		Job<String> jobDetachVolume = new Job<String>();
		jobDetachVolume.setId(jobid);
		jobDetachVolume.setStatus(JobStatus.RUNNING);
		jobDetachVolume.setResult(storageid);
		return jobDetachVolume;
	}

	@Override
	public Job<?> attachVolume(String storageid, String vmid) throws Exception {
		CloudStackVolume volume = new CloudStackVolume();
		volume.setCommand("attachVolume");
		volume.setId(storageid);
		volume.setVirtualmachineid(vmid);
		CloudStackHttpRequestManager request = new CloudStackHttpGetRequest(baseUrl, volume);
		String result = request.execute();
		System.out.println(result);
		
		String jobid = JsonUtil.getJobid(result);
		Job<String> jobAttachVolume = new Job<String>();
		jobAttachVolume.setId(jobid);
		jobAttachVolume.setStatus(JobStatus.RUNNING);
		jobAttachVolume.setResult(storageid);
		return jobAttachVolume;
	}
	
	public String getCustomDiskOfferingId() throws Exception {
		CloudStackVolume volume = new CloudStackVolume();
		volume.setCommand("listDiskOfferings");
		volume.setName("Custom");
		CloudStackHttpRequestManager request = new CloudStackHttpGetRequest(baseUrl, volume);
		String result = request.execute();

		List<CloudStackDiskOffering> offeringList = JsonUtil.getObjectList(result,
				"diskoffering", CloudStackDiskOffering.class);
		
		if(offeringList == null || offeringList.size() <= 0)
			return null;
		
		for(CloudStackDiskOffering offering: offeringList)
		{
			if(offering.isIscustomized())
				return offering.getId();
		}
		return null;
	}
	public String getZoneId() throws Exception {
		CloudStackVolume volume = new CloudStackVolume();
		volume.setCommand("listZones");
		CloudStackHttpRequestManager request = new CloudStackHttpGetRequest(baseUrl, volume);
		String result = request.execute();
		List<CloudStackZone> zoneList = JsonUtil.getObjectList(result,
				"zone", CloudStackZone.class);
		
		if(zoneList == null || zoneList.size() <= 0)
			return null;
		if(zoneList.size() == 1)
			return zoneList.get(0).getId();
		for(CloudStackZone zoon: zoneList)
		{
			if(zoon.getName().endsWith("opz"))
				return zoon.getId();
		}
		return null;
	}

	@Override
	public Job<Volume> createVolume(String name, int size) throws Exception {
		String diskOfferingId = getCustomDiskOfferingId();
		String zoneId = getZoneId();
		if(diskOfferingId == null || zoneId == null)
		{
			log.error("not found the named Custom disk offerings or zone which name end with 'opz' ");
			return null;
		}
		
		CloudStackVolume volume = new CloudStackVolume();
		volume.setCommand("createVolume");
		volume.setName(name);
//		volume.setSize(String.format("%d", size));
		volume.setSize((long)size);
		volume.setDiskofferingid(diskOfferingId);
		volume.setZoneid(zoneId);
		
		CloudStackHttpRequestManager request = new CloudStackHttpGetRequest(baseUrl, volume);
		String result = request.execute();
		System.out.println(result);
		
		String jobid = JsonUtil.getJobid(result);
		Job<Volume> jobAttachVolume = new Job<Volume>(Volume.class);
		jobAttachVolume.setName("volume");
		jobAttachVolume.setId(jobid);
		jobAttachVolume.setStatus(JobStatus.RUNNING);
		return jobAttachVolume;
	}

	/* (闈�Javadoc) 
	* <p>Title: resetVM</p> 
	* <p>Description: </p> 
	* @param templateId
	* @param vmid
	* @return
	* @throws Exception 
	* @see com.opzoon.vdi.core.cloud.CloudManager#resetVM(java.lang.String, java.lang.String) 
	*/
	
	public Job<?> resetVM(String templateId, String vmid) throws Exception {
		VMInstance vmInstance=	this.getVM(vmid);
		this.destroyVM(vmid);
		return this.cloneVM(templateId, vmInstance.getName(),true);
	}

	@Override
	public Job<String> logOff(String vmid, String domainname, String username)
			throws Exception {
		VMInstance vmInstance = this.getVM(vmid);
		return VdiAgentClientImpl.logOff(vmInstance.getIpaddress(), domainname, username);
	}
	
	public static class Host{
		private String id;
		private String ipaddress;
		public String getId() {
			return id;
		}
		public void setId(String id) {
			this.id = id;
		}
		public String getIpaddress() {
			return ipaddress;
		}
		public void setIpaddress(String ipaddress) {
			this.ipaddress = ipaddress;
		}
	}

	// Evan
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public Job<String> joinWorkgroup(String vmid, String workgroupname, String account, String password, boolean restart)
			throws Exception {
		VMInstance vmInstance = this.getVM(vmid);
		Job job = VdiAgentClientImpl.joinWorkgroup(vmInstance.getIpaddress(),
				workgroupname, account, password, restart);
		job.setAgent(true);
		return job;
	}

	// Evan
	@Override
	public Job<String> deleteUserProfile(String vmid, String domain,
			String username) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

}
