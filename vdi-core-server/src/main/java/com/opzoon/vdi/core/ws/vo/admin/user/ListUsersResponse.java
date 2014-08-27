package com.opzoon.vdi.core.ws.vo.admin.user;

import java.io.Serializable;

import javax.xml.bind.annotation.XmlRootElement;

import com.opzoon.vdi.core.ws.Services.Response;

@XmlRootElement(name = "response")
public class ListUsersResponse extends Response<UserList> implements Serializable {

	private static final long serialVersionUID = 1L;
	
	private UserList body;
	
	public UserList getBody() {
		return body;
	}
	public void setBody(UserList body) {
		this.body = body;
	}
	
}