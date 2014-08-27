package com.opzoon.vdi.core.ws.vo.admin.user;

import java.io.Serializable;

import javax.xml.bind.annotation.XmlRootElement;

import com.opzoon.vdi.core.ws.Services.Response;

@XmlRootElement(name = "response")
public class ListSessionsResponse extends Response<SessionList> implements Serializable {

	private static final long serialVersionUID = 1L;
	
	private SessionList body;
	
	public SessionList getBody() {
		return body;
	}
	public void setBody(SessionList body) {
		this.body = body;
	}
	
}