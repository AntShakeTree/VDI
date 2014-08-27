package com.opzoon.vdi.core.ws.vo.desktop;

import java.io.Serializable;
import java.util.List;

import javax.xml.bind.annotation.XmlRootElement;

import com.opzoon.vdi.core.domain.Notification;
import com.opzoon.vdi.core.ws.Services.Response;

@XmlRootElement(name = "response")
public class GetNotificationResponse extends Response<List<Notification>> implements Serializable {

	private static final long serialVersionUID = 1L;
	
	private List<Notification> body;
	
	public List<Notification> getBody() {
		return body;
	}
	public void setBody(List<Notification> body) {
		this.body = body;
	}
	
}