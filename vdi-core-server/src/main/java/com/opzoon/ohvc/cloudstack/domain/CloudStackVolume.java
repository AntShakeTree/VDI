package com.opzoon.ohvc.cloudstack.domain;

import com.opzoon.ohvc.common.anotation.Required;
import com.opzoon.vdi.core.domain.BaseDomain;

public class CloudStackVolume  extends BaseDomain<CloudStackVolume>{

    @Required
    private String command;
    private String id;
    private String name;
    private Long size;
    private String virtualmachineid;
    private String diskofferingid;
    private String zoneid;
    
	public String getCommand() {
		return command;
	}
	public void setCommand(String command) {
		this.command = command;
	}
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public Long getSize() {
		return size;
	}
	public void setSize(Long size) {
		this.size = size;
	}
	public String getVirtualmachineid() {
		return virtualmachineid;
	}
	public void setVirtualmachineid(String virtualmachineid) {
		this.virtualmachineid = virtualmachineid;
	}
	public String getDiskofferingid() {
		return diskofferingid;
	}
	public void setDiskofferingid(String diskofferingid) {
		this.diskofferingid = diskofferingid;
	}
	public String getZoneid() {
		return zoneid;
	}
	public void setZoneid(String zoneid) {
		this.zoneid = zoneid;
	}
    
}
