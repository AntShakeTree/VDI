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
import com.vdi.dao.user.domain.UserMapBridge;
import com.vdi.dao.user.domain.LdapConfigEntity;
import com.vdi.facade.DomainFacad;
import com.vdi.service.user.RemoveOrganizationObserver;
import com.vdi.service.user.UserStateSubject;
import com.vdi.service.user.SyncOrgnazationObserver;
import com.vdi.vo.req.DomainIdsReq;
import com.vdi.vo.res.DomainResponse;
import com.vdi.vo.res.Header;
import com.vdi.vo.res.ListDomainResponse;
import com.vdi.vo.res.ListDomainResponse.ListDomain;

@Service
public class DomainFacadImpl implements DomainFacad {
	private @Autowired DomainDao domainDao;
	private @Autowired UserStateSubject ldapStateSubject;
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
		UserMapBridge config = domain.getConfig();
		LdapSupport.createDomain(config,domain);
		config.setStatus(UserMapBridge.NORMAL);
		config.setGuid(domain.getGuid());
		domain.setDns(this.genneralDns(domain.getAddress()));
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
		UserMapBridge config = new UserMapBridge();
		VDIBeanUtils.copyPropertiesByNotNull(domain, config, null);
		config.setStatus(UserMapBridge.NORMAL);
		config.setGuid(domain.getGuid());
		domainDao.update(dao);
		response.setError(0);
		return response;
	}

	@Override
	public Header deleteDomain(DomainIdsReq req) {
		for (String id : req.getDomainguids()) {
			Domain domain = domainDao.get(Domain.class, id);
			UserMapBridge config = new UserMapBridge();
			domain.setStatus(UserMapBridge.DELETING);
			VDIBeanUtils.copyPropertiesByNotNull(domain, config, null);
			config.setDomain(domain);
			config.setStatus(UserMapBridge.DELETING);
			ldapStateSubject.registerUserStateChangeObserver(deleteOrganization,
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
		UserMapBridge config = domain.getConfig();
		if (domain.getAccesstype() == UserMapBridge.READ_WRITE) {
			try {
				config.setAccesstype(UserMapBridge.READ_WRITE);
				LdapSupport.createDirContext(config);
				return;
			} catch (NamingException e) {
				config.setAccesstype(UserMapBridge.READONLY);
				LdapSupport.createDirContext(config);
				domain.setAccesstype(UserMapBridge.READONLY);
			}
		} else {
			config.setAccesstype(UserMapBridge.READONLY);
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
			UserMapBridge config = new UserMapBridge();
			VDIBeanUtils.copyPropertiesByNotNull(dao, config, null);
			config.setStatus(UserMapBridge.SYNC);
			config.setGuid(dao.getGuid());
			config.setBase(dao.getDomainbinddn());
			config.setDomain(dao);
			config.setBase(ldapConfigEntity.getBaseurl());
			dao.setStatus(UserMapBridge.SYNC);
			domainDao.update(dao);
			ldapStateSubject.registerUserStateChangeObserver(syncOrgnazation,
					config);
		}
		return new Header();
	}

	public String genneralDns(String ip) {
		StringBuilder result = new StringBuilder();
		RuntimeUtils.shell(result, "nslookup " + ip);
		String res = result.toString().trim().toLowerCase();
		res = res.replaceAll("\\s", "");
		String dns =null;
		try {
			String ress = res.substring(res.indexOf("server:") + 7);
			dns= ress.substring(0, ress.indexOf(":")).replaceAll(
					"[a-z]", "");
		} catch (Exception e) {
			return null;
		}
		return dns;
	}
}
