package com.opzoon.vdi.core.ws.vo.entrance;

import java.io.Serializable;

import javax.xml.bind.annotation.XmlRootElement;

import com.opzoon.vdi.core.ws.Services.Response;

/**
 * 登录响应.
 */
@XmlRootElement(name = "response")
public class LoginResponse extends Response<TicketWrapper> implements Serializable {

	private static final long serialVersionUID = 1L;
	
	private TicketWrapper body;

	@Override
	public TicketWrapper getBody() {
		return body;
	}
	@Override
	public void setBody(TicketWrapper body) {
		this.body = body;
	}
	
}