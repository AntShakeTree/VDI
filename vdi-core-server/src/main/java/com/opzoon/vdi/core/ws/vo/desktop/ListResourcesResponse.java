package com.opzoon.vdi.core.ws.vo.desktop;

import java.io.Serializable;
import java.util.List;

import javax.xml.bind.annotation.XmlRootElement;

import com.opzoon.vdi.core.facade.ResourceFacade.Resource;
import com.opzoon.vdi.core.ws.Services.Response;

@XmlRootElement(name = "response")
public class ListResourcesResponse extends Response<List<Resource>> implements Serializable {

	private static final long serialVersionUID = 1L;
	
	private List<Resource> body;
	
	public List<Resource> getBody() {
		return body;
	}
	public void setBody(List<Resource> body) {
		this.body = body;
	}
	
}