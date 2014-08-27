package com.opzoon.vdi.core.ws.vo.desktop;

import java.io.Serializable;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "param")
public class ForceWrapper implements Serializable {

	private static final long serialVersionUID = 1L;

	private boolean force;
	private Integer volumeid;

	public boolean isForce() {
		return force;
	}
	public void setForce(boolean force) {
		this.force = force;
	}
	public Integer getVolumeid() {
		return volumeid;
	}
	public void setVolumeid(Integer volumeid) {
		this.volumeid = volumeid;
	}
	
}