package com.opzoon.vdi.core.domain;

import com.opzoon.ohvc.common.anotation.TargetField;

public class Volume {

	private String id;
	private Long size;
	private String name;
	@TargetField(target="virtualmachineid")
	private String vmid;
	private String status;
	
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public Integer getSize() {
		return (int) (size / (1024 * 1024 * 1024));
	}
//	public void setSize(Integer size) {
//		this.size = size;
//	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}

	public String getVmid() {
		return vmid;
	}
	public void setVmid(String vmid) {
		this.vmid = vmid;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}

}
