package com.opzoon.vdi.core.ws.vo.entrance;

import java.io.Serializable;
import java.util.List;

import javax.xml.bind.annotation.XmlRootElement;

import com.opzoon.vdi.core.ws.Services.Response;

@XmlRootElement(name = "response")
public class ListAvailableAuthenticationMethodsResponse extends Response<List<AuthenticationMethod>> implements Serializable {

	private static final long serialVersionUID = 1L;
	
	private List<AuthenticationMethod> body;

	@Override
	public List<AuthenticationMethod> getBody() {
		return body;
	}
	@Override
	public void setBody(List<AuthenticationMethod> body) {
		this.body = body;
	}
	
}