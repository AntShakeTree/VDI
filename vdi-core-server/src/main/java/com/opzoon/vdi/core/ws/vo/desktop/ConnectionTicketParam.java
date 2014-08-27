package com.opzoon.vdi.core.ws.vo.desktop;

import java.io.Serializable;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "param")
public class ConnectionTicketParam implements Serializable {

	private static final long serialVersionUID = 1L;

	private String connectionticket;

	public String getConnectionticket() {
		return connectionticket;
	}
	public void setConnectionticket(String connectionticket) {
		this.connectionticket = connectionticket;
	}
	
}