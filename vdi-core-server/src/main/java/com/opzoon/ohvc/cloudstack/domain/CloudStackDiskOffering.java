package com.opzoon.ohvc.cloudstack.domain;

public class CloudStackDiskOffering {

	private String id;
	private String name;
	private Boolean iscustomized;
	
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
	public Boolean isIscustomized() {
		return iscustomized;
	}
	public void setIscustomized(Boolean iscustomized) {
		this.iscustomized = iscustomized;
	}
	
}
