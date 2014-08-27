package com.opzoon.vdi.core.ws.vo.desktop;

import java.io.Serializable;

import javax.xml.bind.annotation.XmlRootElement;

import com.opzoon.vdi.core.domain.RestrictionStrategy;
import com.opzoon.vdi.core.ws.Services.Response;

@XmlRootElement(name = "response")
public class GetRestrictionStrategyResponse extends Response<RestrictionStrategy> implements Serializable {

	private static final long serialVersionUID = 1L;
	
	private RestrictionStrategy body;
	
	public RestrictionStrategy getBody() {
		return body;
	}
	public void setBody(RestrictionStrategy body) {
		this.body = body;
	}
	
}