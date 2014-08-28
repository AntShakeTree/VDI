package com.vdi.support.desktop.lls.domain.resource;

import java.util.List;

import org.codehaus.jackson.type.TypeReference;

import com.vdi.support.desktop.lls.domain.BasicDomain;

/**
 * 
 * @author mxc
 * 
 */
public class ComputePool extends BasicDomain{
	public static final  String  CREATE_COMPUTEPOOL_ACTION = "createComputePool";
	public static final String  UPDATE_COMPUTEPOOL_ACTION = "updateComputePool";
	public static final String  DELETE_COMPUTEPOOL_ACTION = "deleteComputePool";
	public static final String  GET_COMPUTEPOOL_ACTION = "reqGetComputePool";
	public static final String CREATING = "creating";
	public static final String AVAILABLE = "available";
	public static final String DELETING = "deleting";
	public static final String HOSTADDING = "hostAdding";
	public static final String HOSTREMOVEING = "hostRemoveing";
	public static final String UMOUNTING = "umounting";
	public static final String MOUNTING = "mounting";
	public static final String LIST_COMPUTEPOOL_ACTION = "reqListComputPool";

	
	private String computePoolName;
	private String dispatchType;
	private String device;
	private String action;
	private String computePoolIdentity;
	private ComputePool mapper;// 查询条件
	private String status;
	private Integer rhcsVersion;
	private Boolean isRHCS;
	private int vmProcTotalMem;
	private int hostTotalCpuCoreNum;
	private int vmProcTotalCpuCoreNum;
	private int hostTotalMem;
	private int workHostTotalMem;
	private int workHostTotalCpuCoreNum;
	// private

	public Boolean getIsRHCS() {
		return isRHCS;
	}

	public void setIsRHCS(Boolean isRHCS) {
		this.isRHCS = isRHCS;
	}

	public void setMapper(ComputePool mapper) {
		this.mapper = mapper;
	}

	public String getComputePoolName() {
		return computePoolName;
	}

	public Object getMapper() {
		return mapper;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public Integer getRhcsVersion() {
		return rhcsVersion;
	}

	public void setRhcsVersion(Integer rhcsVersion) {
		this.rhcsVersion = rhcsVersion;
	}

	public Boolean isRHCS() {
		return isRHCS;
	}

	public void setRHCS(Boolean isRHCS) {
		this.isRHCS = isRHCS;
	}

	public void setComputePoolName(String computePoolName) {
		this.computePoolName = computePoolName;
	}

	public String getAction() {
		return action;
	}

	public void setAction(String action) {
		this.action = action;
	}

	public String getComputePoolIdentity() {
		return computePoolIdentity;
	}

	public void setComputePoolIdentity(String computePoolIdentity) {
		this.computePoolIdentity = computePoolIdentity;
	}

	public String getDispatchType() {
		return dispatchType;
	}

	public void setDispatchType(String dispatchType) {
		this.dispatchType = dispatchType;
	}


	public static TypeReference<List<ComputePool>> getComputePoolListType(){
		return new TypeReference<List<ComputePool>>() {
		};
	}

	public String getDevice() {
		return device;
	}

	public void setDevice(String device) {
		this.device = device;
	}

	public int getVmProcTotalMem() {
		return vmProcTotalMem;
	}

	public void setVmProcTotalMem(int vmProcTotalMem) {
		this.vmProcTotalMem = vmProcTotalMem;
	}

	public int getHostTotalCpuCoreNum() {
		return hostTotalCpuCoreNum;
	}

	public void setHostTotalCpuCoreNum(int hostTotalCpuCoreNum) {
		this.hostTotalCpuCoreNum = hostTotalCpuCoreNum;
	}

	public int getVmProcTotalCpuCoreNum() {
		return vmProcTotalCpuCoreNum;
	}

	public void setVmProcTotalCpuCoreNum(int vmProcTotalCpuCoreNum) {
		this.vmProcTotalCpuCoreNum = vmProcTotalCpuCoreNum;
	}

	public int getHostTotalMem() {
		return hostTotalMem;
	}

	public void setHostTotalMem(int hostTotalMem) {
		this.hostTotalMem = hostTotalMem;
	}

	public int getWorkHostTotalMem() {
		return workHostTotalMem;
	}

	public void setWorkHostTotalMem(int workHostTotalMem) {
		this.workHostTotalMem = workHostTotalMem;
	}

	public int getWorkHostTotalCpuCoreNum() {
		return workHostTotalCpuCoreNum;
	}

	public void setWorkHostTotalCpuCoreNum(int workHostTotalCpuCoreNum) {
		this.workHostTotalCpuCoreNum = workHostTotalCpuCoreNum;
	}
	
}
