package com.opzoon.vdi.core.ws.vo.desktop;

import java.io.Serializable;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "param")
public class ResourceTypeParam implements Serializable {

	private static final long serialVersionUID = 1L;

	private Integer userid;
	private int resourcetype;
	
	public Integer getUserid() {
		return userid;
	}
	public void setUserid(Integer userid) {
		this.userid = userid;
	}
	public int getResourcetype() {
		return resourcetype;
	}
	public void setResourcetype(int resourcetype) {
		this.resourcetype = resourcetype;
	}
	
}