package com.opzoon.vdi.core.ws.vo.domain;

import java.io.Serializable;
import java.util.List;

import javax.xml.bind.annotation.XmlRootElement;

import com.opzoon.vdi.core.domain.Domain;
import com.opzoon.vdi.core.ws.Services.Response;

@XmlRootElement(name = "response")
public class ListDomainsResponse extends Response<List<Domain>> implements Serializable {

	private static final long serialVersionUID = 1L;
	
	private List<Domain> body;
	
	public List<Domain> getBody() {
		return body;
	}
	public void setBody(List<Domain> body) {
		this.body = body;
	}
	
}