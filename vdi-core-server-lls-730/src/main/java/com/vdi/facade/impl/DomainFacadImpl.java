package com.vdi.facade.impl;

import java.util.List;

import javax.naming.NamingException;
import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import com.vdi.common.ErrorCode;
import com.vdi.common.ExceptionHandle;
import com.vdi.common.RuntimeUtils;
import com.vdi.common.VDIBeanUtils;
import com.vdi.dao.suport.LdapSupport;
import com.vdi.dao.user.DomainDao;
import com.vdi.dao.user.LdapConfigDao;
import com.vdi.dao.user.domain.Domain;
import com.vdi.dao.user.domain.LdapConfig;
import com.vdi.dao.user.domain.LdapConfigEntity;
import com.vdi.facade.DomainFacad;
import com.vdi.service.user.RemoveOrganizationObserver;
import com.vdi.service.user.LdapStateSubject;
import com.vdi.service.user.SyncOrgnazationObserver;
import com.vdi.vo.req.DomainIdsReq;
import com.vdi.vo.res.DomainResponse;
import com.vdi.vo.res.Header;
import com.vdi.vo.res.ListDomainResponse;
import com.vdi.vo.res.ListDomainResponse.ListDomain;

@Service
public class DomainFacadImpl implements DomainFacad {
	private @Autowired DomainDao domainDao;
	private @Autowired LdapStateSubject ldapStateSubject;
	private @Autowired RemoveOrganizationObserver deleteOrganization;
	private @Autowired SyncOrgnazationObserver syncOrgnazation;
	private @Autowired LdapConfigDao ldapConfigDao;

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
	@Transactional
	public DomainResponse createDomain(Domain domain) throws Exception {
		DomainResponse response = new DomainResponse();
		int or = domain.getAccesstype();
		this.validateDomain(domain);
		int vafter = domain.getAccesstype();
		if (or != vafter) {
			response.getHead().setError(
					ExceptionHandle.err.warn(ErrorCode.LDAP_READER_ONLY));
		}
		LdapConfig config = domain.getConfig();
		domain = LdapSupport.createDomain(config);
		config.setStatus(LdapConfig.NORMAL);
		config.setGuid(domain.getGuid());
		domain.setDns(domain.getDns());
		domainDao.save(domain);
		response.setBody(domain);
		return response;
	}

	@Override
	public Header updateDomain(Domain domain) throws Exception {
		// this.validateDomain(domain);
		Header response = new Header();
		int or = domain.getAccesstype();
		this.validateDomain(domain);
		int vafter = domain.getAccesstype();
		if (or != vafter) {
			response.setError(ExceptionHandle.err
					.warn(ErrorCode.LDAP_READER_ONLY));
		}
		Assert.notNull(domain.getGuid());
		Domain dao = domainDao.get(Domain.class, domain.getGuid());
		Assert.notNull(dao);
		//
		VDIBeanUtils.copyPropertiesByNotNull(domain, dao, null);
		//
		LdapConfig config = new LdapConfig();
		VDIBeanUtils.copyPropertiesByNotNull(domain, config, null);
		config.setStatus(LdapConfig.NORMAL);
		config.setGuid(domain.getGuid());
		domainDao.update(dao);
		response.setError(0);
		return response;
	}

	@Override
	public Header deleteDomain(DomainIdsReq req) {
		for (String id : req.getDomainguids()) {
			Domain domain = domainDao.get(Domain.class, id);
			LdapConfig config = domain.getConfig();
			domain.setStatus(LdapConfig.DELETING);
			config.setDomain(domain);
			config.setStatus(LdapConfig.DELETING);
			ldapStateSubject.registerStateChangeObserver(deleteOrganization,
					config);
		}
		return new Header();
	}

	@Override
	public DomainResponse getDomain(DomainIdsReq req) {
		Assert.notNull(req.getDomainguid());
		DomainResponse response = new DomainResponse();
		response.setBody(this.domainDao.get(Domain.class, req.getDomainguid()));
		return response;
	}

	private void validateDomain(Domain domain) throws NamingException {
		Assert.notNull(domain);
		// Assert.notNull(domain.getDns());
		Assert.notNull(domain.getDomainbindpass());
		Assert.notNull(domain.getPrincipal());
		Assert.notNull(domain.getDomainbinddn());
		Assert.notNull(domain.getAccesstype());
		Assert.notNull(domain.getAddress());
		Assert.notNull(this.genneralDns(domain.getAddress()));
		LdapConfig config = domain.getConfig();
		if (domain.getAccesstype() == LdapConfig.READ_WRITE) {
			try {
				config.setAccesstype(LdapConfig.READ_WRITE);
				LdapSupport.createDirContext(config);
				return;
			} catch (NamingException e) {
				config.setAccesstype(LdapConfig.READONLY);
				LdapSupport.createDirContext(config);
				domain.setAccesstype(LdapConfig.READONLY);
			}
		} else {
			config.setAccesstype(LdapConfig.READONLY);
			LdapSupport.createDirContext(config);
		}

	}

	@Override
	public Header syncDomain(DomainIdsReq req) {
		Assert.notNull(req.getDomainguid());
		Domain dao = domainDao.get(Domain.class, req.getDomainguid());
		LdapConfigEntity entity = new LdapConfigEntity();
		entity.setDomainguid(req.getDomainguid());
		List<LdapConfigEntity> es = ldapConfigDao.listRequest(entity);
		for (LdapConfigEntity ldapConfigEntity : es) {
			LdapConfig config = dao.getConfig();
			config.setStatus(LdapConfig.SYNC);
			config.setGuid(dao.getGuid());
			config.setBase(dao.getDomainbinddn());
			config.setDomain(dao);
			config.setBase(ldapConfigEntity.getBaseurl());
			dao.setStatus(LdapConfig.SYNC);
			domainDao.update(dao);
			ldapStateSubject.registerStateChangeObserver(syncOrgnazation,
					config);
		}
		return new Header();
	}

	public boolean genneralDns(String ip) {
		return true;
	}
}
