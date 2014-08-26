package com.vdi.support.desktop.lls.domain.resource;

import java.util.List;

import org.codehaus.jackson.annotate.JsonIgnore;
import org.codehaus.jackson.type.TypeReference;

import com.vdi.support.desktop.lls.domain.BasicDomain;

public class Host extends BasicDomain {
	public static String CREATE_HOST_ACTION = "createHost";
	public static String UPDATE_HOST_ACTION = "updateHost";
	public static String DELETE_HOST_ACTION = "deleteHost";
	public static String GET_HOST_ACTION = "reqGetHost";
	public static String LIST_HOST_ACTION = "reqListHost";
	private String action;
	private Host mapper;// 查询条件
	private String computePoolIdentity;
	private String addTime;
	private String addr;
	private String role;
	private List<CpuInfo> cpuInfo;
	private String hostName;
	private String hostIdentity;
	private String status;
	private long totalMem;
	private int cpuCoreNum;
	@JsonIgnore
	private Integer _userId;

	public long getTotalMem() {
		return totalMem;
	}

	public void setTotalMem(long totalMem) {
		this.totalMem = totalMem;
	}

	public String getStatus() {
		return status;
	}

	public Integer get_userId() {
		return _userId;
	}

	public void set_userId(Integer _userId) {
		this._userId = _userId;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getAction() {
		return action;
	}

	public void setAction(String action) {
		this.action = action;
	}

	public Host getMapper() {
		return mapper;
	}

	public String getComputePoolIdentity() {
		return computePoolIdentity;
	}

	public void setComputePoolIdentity(String computePoolIdentity) {
		this.computePoolIdentity = computePoolIdentity;
	}

	public void setMapper(Host host) {
		this.mapper = host;
	}

	public String getHostName() {
		return hostName;
	}

	public void setHostName(String hostName) {
		this.hostName = hostName;
	}

	public String getHostIdentity() {
		return hostIdentity;
	}

	public void setHostIdentity(String hostIdentity) {
		this.hostIdentity = hostIdentity;
	}
//
//	public static Type getHostListType() {
//		TypeToken<List<Host>> type = new TypeToken<List<Host>>() {
//		};
//		return type.getType();
//	}

	public String getAddTime() {
		return addTime;
	}

	public void setAddTime(String addTime) {
		this.addTime = addTime;
	}

	public String getAddr() {
		return addr;
	}

	public void setAddr(String addr) {
		this.addr = addr;
	}

	public String getRole() {
		return role;
	}

	public void setRole(String role) {
		this.role = role;
	}

	public List<CpuInfo> getCpuInfo() {
		return cpuInfo;
	}

	public void setCpuInfo(List<CpuInfo> cpuInfo) {
		this.cpuInfo = cpuInfo;
	}

	public static TypeReference<List<Host>> getListHostType() {
		return new org.codehaus.jackson.type.TypeReference<List<Host>>() {};
	}

	public int getCpuCoreNum() {
		return cpuCoreNum;
	}

	public void setCpuCoreNum(int cpuCoreNum) {
		this.cpuCoreNum = cpuCoreNum;
	}
	
}
