package com.vdi.facade;

import com.vdi.dao.user.domain.Domain;
import com.vdi.vo.req.DomainIdsReq;
import com.vdi.vo.res.DomainResponse;
import com.vdi.vo.res.Header;
import com.vdi.vo.res.ListDomainResponse;

public interface DomainFacad {
	public ListDomainResponse listDomains(Domain domain);

	public DomainResponse createDomain(Domain domain)  throws Exception;

	public Header updateDomain(Domain domain) throws Exception;

	public Header deleteDomain(DomainIdsReq req);

	public DomainResponse getDomain(DomainIdsReq req);

	public Header syncDomain(DomainIdsReq req);
}
