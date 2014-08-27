package com.opzoon.vdi.core.ws.vo.admin.user;

import java.io.Serializable;
import java.util.List;

import javax.xml.bind.annotation.XmlRootElement;

import com.opzoon.vdi.core.ws.Services.MultiStatusResponse;

@XmlRootElement(name = "response")
public class UserIdsResponse extends MultiStatusResponse<UserIdAndError> implements Serializable {

	private static final long serialVersionUID = 1L;
	
	private List<UserIdAndError> body;

	@Override
	public List<UserIdAndError> getBody() {
		return body;
	}
	@Override
	public void setBody(List<UserIdAndError> body) {
		this.body = body;
	}
	
}