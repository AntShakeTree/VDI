package com.opzoon.vdi.core.ws.vo.desktop;

import java.io.Serializable;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "param")
public class ResourceTypeAndId implements Serializable {

	private static final long serialVersionUID = 1L;

	private int resourcetype;
	private int resourceid;
	private int brokerprotocol;
	
	public int getResourcetype() {
		return resourcetype;
	}
	public void setResourcetype(int resourcetype) {
		this.resourcetype = resourcetype;
	}
	public int getResourceid() {
		return resourceid;
	}
	public void setResourceid(int resourceid) {
		this.resourceid = resourceid;
	}
	public int getBrokerprotocol() {
		return brokerprotocol;
	}
	public void setBrokerprotocol(int brokerprotocol) {
		this.brokerprotocol = brokerprotocol;
	}
	
}