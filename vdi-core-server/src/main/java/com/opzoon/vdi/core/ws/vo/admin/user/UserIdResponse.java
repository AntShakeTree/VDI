package com.opzoon.vdi.core.ws.vo.admin.user;

import java.io.Serializable;

import javax.xml.bind.annotation.XmlRootElement;

import com.opzoon.vdi.core.ws.Services.Response;

@XmlRootElement(name = "response")
public class UserIdResponse extends Response<UserIdWrapper> implements Serializable {

	private static final long serialVersionUID = 1L;
	
	private UserIdWrapper body;

	@Override
	public UserIdWrapper getBody() {
		return body;
	}
	@Override
	public void setBody(UserIdWrapper body) {
		this.body = body;
	}
	
}