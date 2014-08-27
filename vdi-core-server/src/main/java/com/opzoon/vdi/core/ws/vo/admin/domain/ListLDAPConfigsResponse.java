package com.opzoon.vdi.core.ws.vo.admin.domain;

import java.io.Serializable;

import javax.xml.bind.annotation.XmlRootElement;

import com.opzoon.vdi.core.ws.Services.Response;

@XmlRootElement(name = "response")
public class ListLDAPConfigsResponse extends Response<LDAPConfigList> implements Serializable {

	private static final long serialVersionUID = 1L;
	
	private LDAPConfigList body;
	
	public LDAPConfigList getBody() {
		return body;
	}
	public void setBody(LDAPConfigList body) {
		this.body = body;
	}
	
}