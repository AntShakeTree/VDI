package com.opzoon.vdi.core.ws;

import java.io.Serializable;
import java.util.List;

import javax.xml.bind.annotation.XmlRootElement;

import com.opzoon.vdi.core.domain.Domain;
import com.opzoon.vdi.core.facade.DomainFacade;
import com.opzoon.vdi.core.ws.Services.Response;

/**
 * 域相关业务实现.
 */
public class DomainUsage {
	
	private DomainFacade domainFacade;

	/**
	 * 列举全部域，系统默认存在一个ID为0的本地域.
	 * 
	 * @return 列举域响应.
	 */
	public ListDomainsResponse listDomains() {
		ListDomainsResponse response = new ListDomainsResponse();
		List<Domain> domains = domainFacade.findDomains();
		for (Domain domain : domains) {
			domain.setDomainbinddn(null);
			domain.setDomainbindpass(null);
		}
		response.setBody(domains);
		return response;
	}

	public void setDomainFacade(DomainFacade domainFacade) {
		this.domainFacade = domainFacade;
	}

	@XmlRootElement(name = "response")
	public static class ListDomainsResponse extends Response<List<Domain>> implements Serializable {

		private static final long serialVersionUID = 1L;
		
		private List<Domain> body;
		
		public List<Domain> getBody() {
			return body;
		}
		public void setBody(List<Domain> body) {
			this.body = body;
		}
		
	}

}
