package com.opzoon.vdi.core.ws.vo.desktop;

import java.io.Serializable;

import javax.xml.bind.annotation.XmlRootElement;

//add by tanyunhua , for find desktop connection count;  end ------------
@XmlRootElement(name = "param")
public class ResourceParam implements Serializable {

	private static final long serialVersionUID = 1L;

	private int resourcetype;
	private int resourceid;
	private Integer volumeid;
	
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
	public Integer getVolumeid() {
		return volumeid;
	}
	public void setVolumeid(Integer volumeid) {
		this.volumeid = volumeid;
	}
	
}