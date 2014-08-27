package com.opzoon.vdi.core.ws.vo.admin.user;

import java.io.Serializable;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "idsession")
public class SessionIdWrapper implements Serializable {

	private static final long serialVersionUID = 1L;

	private int idsession;

	public int getIdsession() {
		return idsession;
	}

	public void setIdsession(int idsession) {
		this.idsession = idsession;
	}
	
}