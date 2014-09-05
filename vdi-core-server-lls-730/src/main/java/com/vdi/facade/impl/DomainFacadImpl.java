package com.vdi.facade.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import com.vdi.common.VDIBeanUtils;
import com.vdi.dao.user.DomainDao;
import com.vdi.dao.user.domain.Domain;
import com.vdi.dao.user.domain.LdapConfig;
import com.vdi.facade.DomainFacad;
import com.vdi.service.user.DeleteOrganization;
import com.vdi.service.user.LdapStateSubject;
import com.vdi.service.user.SyncOrgnazation;
import com.vdi.vo.req.DomainIdsReq;
import com.vdi.vo.res.DomainResponse;
import com.vdi.vo.res.Header;
import com.vdi.vo.res.ListDomainResponse;
import com.vdi.vo.res.ListDomainResponse.ListDomain;

@Service
public class DomainFacadImpl implements DomainFacad {
	private @Autowired DomainDao domainDao;
	private @Autowired LdapStateSubject ldapStateSubject;
	private @Autowired DeleteOrganization deleteOrganization;
	private @Autowired SyncOrgnazation syncOrgnazation;
	@Override
	public ListDomainResponse listDomains(Domain domain) {
		ListDomainResponse response = new ListDomainResponse();
		List<Domain> list = domainDao.listRequest(domain);
		ListDomain body = new ListDomain();
		body.setList(list);
		response.setBody(body);
		response.setPage(domain);
		return response;
	}

	@Override
	public DomainResponse createDomain(Domain domain) {
		this.validateDomain(domain);
		DomainResponse response = new DomainResponse();
		LdapConfig config = new LdapConfig();
		VDIBeanUtils.copyPropertiesByNotNull(domain, config, null);
		config.setStatus(LdapConfig.NORMAL);
		config.setGuid(domain.getGuid());
		domainDao.save(domain);
		response.setBody(domain);
		return response;
	}

	@Override
	public Header updateDomain(Domain domain) {
		this.validateDomain(domain);
		Assert.notNull(domain.getGuid());
		Domain dao = domainDao.get(Domain.class, domain.getGuid());
		Assert.notNull(dao);
		//
		VDIBeanUtils.copyPropertiesByNotNull(domain, dao, null);
		//
		Header response = new Header();
		LdapConfig config = new LdapConfig();
		VDIBeanUtils.copyPropertiesByNotNull(domain, config, null);
		config.setStatus(LdapConfig.NORMAL);
		config.setGuid(domain.getGuid());
		domainDao.update(dao);
		response.setError(0);
		return response;
	}

	@Override
	public Header delteDomain(DomainIdsReq req) {
		for (String id : req.getDomainguids()) {
			Domain domain = domainDao.get(Domain.class, id);
			LdapConfig config = new LdapConfig();
			domain.setStatus(LdapConfig.DELETING);
			VDIBeanUtils.copyPropertiesByNotNull(domain, config, null);
			config.setDomain(domain);
			config.setStatus(LdapConfig.DELETING);
			ldapStateSubject.registerStateChangeObserver(deleteOrganization, config);
		}
		return new Header();
	}

	@Override
	public DomainResponse getDomain(DomainIdsReq req) {
		Assert.notNull(req.getDomainguid());
		DomainResponse response =new DomainResponse();
		response.setBody(this.domainDao.get(Domain.class, req.getDomainguid()));
		return response;
	}

	private void validateDomain(Domain domain) {
		Assert.notNull(domain);
		Assert.notNull(domain.getDns());
		Assert.notNull(domain.getDomainbindpass());
		Assert.notNull(domain.getPrincipal());
		Assert.notNull(domain.getDomainbinddn());
		Assert.notNull(domain.getAccesstype());
		Assert.notNull(domain.getAddress());
		// 验证 dns
	}

	@Override
	public Header syncDomain(DomainIdsReq req) {
		Assert.notNull(req.getDomainguid());
		Domain dao =domainDao.get(Domain.class,req.getDomainguid());
		LdapConfig config =new LdapConfig();
		VDIBeanUtils.copyPropertiesByNotNull(dao, config, null);
		config.setStatus(LdapConfig.SYNC);
		config.setGuid(dao.getGuid());
		config.setBase(dao.getDomainbinddn());
		config.setDomain(dao);
		dao.setStatus(LdapConfig.SYNC);
		domainDao.update(dao);
		ldapStateSubject.registerStateChangeObserver(syncOrgnazation, config);
		return new Header();
	}
}
