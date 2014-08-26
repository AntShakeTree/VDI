package com.vdi.support.desktop.lls.domain.vms;

import com.vdi.support.desktop.lls.domain.vms.attributes.VmProc;

/**
 * 需求来源 列举模板
 * 
 * @author maxiaochao
 * 
 */
public class Template {
	public static  String CREATE_TEMPLATE_ACTION = "reqGetTemplate";
	public static  String UPDATE_TEMPLATE_ACTION = "updateComputePool";
	private String action;
	private String mapper;//查询条件
	private String vmIdentity;// ：虚拟机id
	private String storageIdentity;// ：虚拟机所在存储id
	private String vmName;// ：虚拟机名字
	private String vmVDisks;// json字符串，表示虚拟机的虚拟硬盘
	private int memorySize;// ：内存大小，单位为MB
	private VmProc vCpu;// json字符串，表示虚拟机的cpu配置，如：{“coreNum”:2}。
	// 取值范围：
	// common：该虚拟机为普通虚拟机
	// link：该虚拟机为link克隆出来的虚拟机
	private String parent;// ：如果虚拟机为link，则为template的id，否则为空
	private boolean isSuspend;// ：是否挂起
	private String status;// ：虚拟机状态。不是虚拟机真实状态，虚拟机的真实状态需要综合虚拟机的status，isSuspend和vmclone表中的数据得出。真实状态会提供专门的接口查询。
	private String defaultRunParams;// ：启动虚拟机时的默认参数，可选。
	private String defaultRunCompute;// ：启动虚拟机时的默认计算池，可选。
	private String templateIdentity;
	public String getVmIdentity() {
		return vmIdentity;
	}

	public void setVmIdentity(String vmIdentity) {
		this.vmIdentity = vmIdentity;
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

	public String getVmVDisks() {
		return vmVDisks;
	}

	public void setVmVDisks(String vmVDisks) {
		this.vmVDisks = vmVDisks;
	}

	public int getMemorySize() {
		return memorySize;
	}

	public void setMemorySize(int memorySize) {
		this.memorySize = memorySize;
	}



	public String getAction() {
		return action;
	}

	public void setAction(String action) {
		this.action = action;
	}

	public String getMapper() {
		return mapper;
	}

	public void setMapper(String mapper) {
		this.mapper = mapper;
	}

	public VmProc getvCpu() {
		return vCpu;
	}

	public void setvCpu(VmProc vCpu) {
		this.vCpu = vCpu;
	}

	public String getParent() {
		return parent;
	}

	public void setParent(String parent) {
		this.parent = parent;
	}

	public boolean isSuspend() {
		return isSuspend;
	}

	public void setSuspend(boolean isSuspend) {
		this.isSuspend = isSuspend;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getDefaultRunParams() {
		return defaultRunParams;
	}

	public void setDefaultRunParams(String defaultRunParams) {
		this.defaultRunParams = defaultRunParams;
	}

	public String getDefaultRunCompute() {
		return defaultRunCompute;
	}

	public void setDefaultRunCompute(String defaultRunCompute) {
		this.defaultRunCompute = defaultRunCompute;
	}

	public String getTemplateIdentity() {
		return templateIdentity;
	}

	public void setTemplateIdentity(String templateIdentity) {
		this.templateIdentity = templateIdentity;
	}
	
}
