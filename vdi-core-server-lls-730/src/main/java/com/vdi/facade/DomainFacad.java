package com.vdi.facade;

import com.vdi.dao.user.domain.Domain;
import com.vdi.vo.res.ListDomainResponse;

public interface DomainFacad {
	ListDomainResponse listDomains(Domain domain);
}
