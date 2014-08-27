package com.opzoon.vdi.core.ws.vo.desktop;

import java.io.Serializable;

import javax.xml.bind.annotation.XmlRootElement;

import com.opzoon.vdi.core.domain.Connection;
import com.opzoon.vdi.core.ws.Services.Response;

@XmlRootElement(name = "response")
public class ConnectionResponse extends Response<Connection> implements Serializable {

	private static final long serialVersionUID = 1L;
	
	private Connection body;
	
	public Connection getBody() {
		return body;
	}
	public void setBody(Connection body) {
		this.body = body;
	}
	
}