package com.vdi.support.desktop.lls.domain.vms;

import java.util.List;

import org.codehaus.jackson.type.TypeReference;

import com.vdi.support.desktop.lls.domain.BasicDomain;
import com.vdi.support.desktop.lls.domain.resource.CpuInfo;
import com.vdi.support.desktop.lls.domain.vms.attributes.RunParams;
import com.vdi.support.desktop.lls.domain.vms.attributes.VDisk;
import com.vdi.support.desktop.lls.domain.vms.attributes.VNetcard;

/**
 * 
 * @author mxc
 * 
 */
public class VmInstance extends BasicDomain{
	public static String CREATE_VM_ACTION = "createVM";
	public static String UPDATE_VM_ACTION = "updateVM";
	public static String DELETE_VM_ACTION = "deleteVM";
	public static String GET_VM_ACTION = "reqGetVM";
	public static String LIST_VM_ACTION = "reqListVM";
	public static String START_VM_ACTION = "startVM";
	public static String STOP_VM_ACTION = "stopVM";
	public static String SYSTEMSTOP_VM_ACTION = "systemStopVM";
	public static String TIMEOUTSYSTEM_VM_STOP_ACTION = "timeoutSystemStopVM";
	public static String RESTART_VM_ACTION = "restartVM";
	public static String SUSPEND_VM_ACTION = "suspendVM";
	public static String TRANSVM2TEMPLATE_ACTION = "transVM2Template";
	public static String LINK_CLONE_VM_ACTION = "linkCloneVM";
	public static String COMPLETE_CLONE_VM_ACTION = "completeCloneTemplate2VM";
	private String storageIdentity;// ：存储的id
	private Integer timeout;
	private String templateIdentity;
	private String action;
	private VmInstance mapper;
	private String vmIdentity;
	private String status;
	private boolean isSuspend;
	private String parent;
	private String vga;
	private String bootSeq;
	
	public String getBootSeq() {
		return bootSeq;
	}

	public void setBootSeq(String bootSeq) {
		this.bootSeq = bootSeq;
	}

	public String getVga() {
		return vga;
	}

	public void setVga(String vga) {
		this.vga = vga;
	}

	public String getParent() {
		return parent;
	}

	public void setParent(String parent) {
		this.parent = parent;
	}

	public boolean getIsSuspend() {
		return isSuspend;
	}

	public void setIsSuspend(boolean isSuspend) {
		this.isSuspend = isSuspend;
	}

	public String getVmIdentity() {
		return vmIdentity;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public VmInstance setVmIdentity(String vmIdentity) {
		this.vmIdentity = vmIdentity;
		return this;
	}

	public VmInstance getMapper() {
		return mapper;
	}

	public void setMapper(VmInstance mapper) {
		this.mapper = mapper;
	}

	public String getAction() {
		return action;
	}

	public VmInstance setAction(String action) {
		this.action = action;
		return this;
	}

	public String getTemplateIdentity() {
		return templateIdentity;
	}

	public void setTemplateIdentity(String templateIdentity) {
		this.templateIdentity = templateIdentity;
	}

	public Integer getTimeout() {
		return timeout;
	}

	public void setTimeout(Integer timeout) {
		this.timeout = timeout;
	}

	private  String vmName;// ：虚拟机的名字
	private Integer memorySize;// ：虚拟机的内存大小，整数，单位MB
	private CpuInfo vCpu;// ：虚拟机的虚拟cpu配置
	private List<VNetcard> vNetcards;// ：虚拟机网卡。为一个集合，集合中的每一个对象表示一个虚拟网卡。每个虚拟网卡只有一个属性,isDhcp:是否想向dhcp服务申请ip。（mac地址系统会自动分配）
	private List<VDisk> vmVDisks;// ：虚拟硬盘信息。json数组，每一对象表示一块硬盘。ide序号按数组中的元素顺序分配。最多只能有
								// 三块硬盘。每个对象只有一个size属性，表示硬盘大小，整数，单位GB
	private String os;// ：操作系统名，可选
	private RunParams defaultRunParams;// ：开启虚拟机时的默认参数。可选
	private String[] view;
	private String defaultRunComputePoolIdentity;// ：开启虚拟机默认使用的计算池。可选
	private String vmType;
	private String computePoolIdentity;
	//：获取对象的起始位置（分页用），整数，可选
	private String cdromIdentity;
	private boolean ha;
	



	public String getCdromIdentity() {
		return cdromIdentity;
	}

	public void setCdromIdentity(String cdromIdentity) {
		this.cdromIdentity = cdromIdentity;
	}

	public boolean isHa() {
		return ha;
	}

	public void setHa(boolean ha) {
		this.ha = ha;
	}

	public String getVmType() {
		return vmType;
	}

	public String getComputePoolIdentity() {
		return computePoolIdentity;
	}

	public void setComputePoolIdentity(String computePoolIdentity) {
		this.computePoolIdentity = computePoolIdentity;
	}

	public void setVmType(String vmType) {
		this.vmType = vmType;
	}

	public String[] getView() {
		return view;
	}

	public void setView(String[] view) {
		this.view = view;
	}

	public String getUPDATE_VM_ACTION() {
		return UPDATE_VM_ACTION;
	}

	public void setUPDATE_VM_ACTION(String uPDATE_VM_ACTION) {
		UPDATE_VM_ACTION = uPDATE_VM_ACTION;
	}

	public String getStorageIdentity() {
		return storageIdentity;
	}

	public void setStorageIdentity(String storageIdentity) {
		this.storageIdentity = storageIdentity;
	}

	public String getVmName() {
		return vmName;
	}

	public void setVmName(String vmName) {
		this.vmName = vmName;
	}

	public Integer getMemorySize() {
		return memorySize;
	}

	public void setMemorySize(Integer memorySize) {
		this.memorySize = memorySize;
	}

	public CpuInfo getvCpu() {
		return vCpu;
	}

	public void setvCpu(CpuInfo vCpu) {
		this.vCpu = vCpu;
	}

	public List<VNetcard> getvNetcards() {
		return vNetcards;
	}

	public void setvNetcards(List<VNetcard> vNetcards) {
		this.vNetcards = vNetcards;
	}

	public List<VDisk> getVmVDisks() {
		return vmVDisks;
	}

	public void setVmVDisks(List<VDisk> vmVDisks) {
		this.vmVDisks = vmVDisks;
	}

	public String getOs() {
		return os;
	}

	public void setOs(String os) {
		this.os = os;
	}

	public RunParams getDefaultRunParams() {
		return defaultRunParams;
	}

	public void setDefaultRunParams(RunParams defaultRunParams) {
		this.defaultRunParams = defaultRunParams;
	}

	public String getDefaultRunComputePoolIdentity() {
		return defaultRunComputePoolIdentity;
	}

	public void setDefaultRunComputePoolIdentity(
			String defaultRunComputePoolIdentity) {
		this.defaultRunComputePoolIdentity = defaultRunComputePoolIdentity;
	}

	public static TypeReference<List<VmInstance>> getVmInstanceListType() {
		return new TypeReference<List<VmInstance>>(){};
	}


}
