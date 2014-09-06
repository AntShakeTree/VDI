package com.vdi.facade.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import com.vdi.common.VDIBeanUtils;
import com.vdi.dao.user.DomainDao;
import com.vdi.dao.user.LdapConfigDao;
import com.vdi.dao.user.domain.Domain;
import com.vdi.dao.user.domain.LdapConfig;
import com.vdi.dao.user.domain.LdapConfigEntity;
import com.vdi.facade.LdapConfigEntityFacad;
import com.vdi.service.user.LdapStateSubject;
import com.vdi.service.user.RemoveLdapConfigObserver;
import com.vdi.service.user.SyncLdapConfigObserver;
import com.vdi.vo.req.LdapConfigIdReq;
import com.vdi.vo.res.Header;
import com.vdi.vo.res.LdapConfigResponse;
import com.vdi.vo.res.ListLdapConfigRespones;
import com.vdi.vo.res.ListLdapConfigRespones.ListLdapConfig;

@Service
public class LdapConfigEntityFacadImpl implements LdapConfigEntityFacad {
	private @Autowired LdapConfigDao ldapConfigDao;
	private @Autowired DomainDao domainDao;
	private @Autowired LdapStateSubject ldapStateSubject;
	private @Autowired SyncLdapConfigObserver ldapConfigSync;
	private @Autowired RemoveLdapConfigObserver removeLdapConfig;

	@Override
	public LdapConfigResponse addLdapConfigEntity(LdapConfigEntity config) {
		LdapConfigResponse response = new LdapConfigResponse();
		Assert.notNull(config);
		Assert.notNull(config.getDomainguid());
		Domain domain = domainDao.get(Domain.class, config.getDomainguid());
		LdapConfig ldapconfig = domain.getConfig();
		VDIBeanUtils.copyPropertiesByNotNull(domain, ldapconfig, null);
		ldapconfig.setBase(config.getBaseurl());
		ldapconfig.setDomain(domain);
		ldapconfig.setStatus(LdapConfig.SYNC);
		
		ldapStateSubject
				.registerStateChangeObserver(ldapConfigSync, ldapconfig);
		ldapConfigDao.save(config);
		ldapconfig.setEntity(config);
		config.setStatus(LdapConfig.SYNC);
		response.setBody(config);
		return response;
	}

	@Override
	public Header removeLdapConfigEntity(LdapConfigIdReq id) {
		Assert.notNull(id);
		Assert.notNull(id.getLdapconfigids());
		for (Integer ldapconfigid : id.getLdapconfigids()) {
			LdapConfigEntity entity =ldapConfigDao.get(LdapConfigEntity.class, ldapconfigid);
			Domain domain = domainDao.get(Domain.class, entity.getDomainguid());
			LdapConfig ldapconfig = new LdapConfig();
			VDIBeanUtils.copyPropertiesByNotNull(domain, ldapconfig, null);
			ldapconfig.setBase(entity.getBaseurl());
			ldapconfig.setDomain(domain);
			ldapconfig.setStatus(LdapConfig.DELETING);
			entity.setStatus(LdapConfig.DELETING);
			ldapconfig.setEntity(entity);
			ldapConfigDao.update(entity);
			ldapStateSubject
					.registerStateChangeObserver(removeLdapConfig, ldapconfig);
		}
		return new Header();
	}

	@Override
	public LdapConfigResponse updateLdap(LdapConfigEntity config) {
		LdapConfigResponse response = new LdapConfigResponse();
		Assert.notNull(config);
		Assert.notNull(config.getDomainguid());
		Domain domain = domainDao.get(Domain.class, config.getDomainguid());
		LdapConfig ldapconfig = new LdapConfig();
		VDIBeanUtils.copyPropertiesByNotNull(domain, ldapconfig, null);
		ldapconfig.setBase(config.getBaseurl());
		ldapconfig.setDomain(domain);
		ldapconfig.setStatus(LdapConfig.SYNC);
		
		ldapStateSubject
				.registerStateChangeObserver(ldapConfigSync, ldapconfig);
		ldapConfigDao.update(config);
		ldapconfig.setEntity(config);
		config.setStatus(LdapConfig.SYNC);
		response.setBody(config);
		return response;
	}

	@Override
	public ListLdapConfigRespones listLdapConfigEntitys(LdapConfigEntity config) {
		ListLdapConfigRespones respones =new ListLdapConfigRespones();
		ListLdapConfig body=new ListLdapConfig();
		respones.setBody(body);
		body.setList(ldapConfigDao.listRequest(config));
		return respones;
	}
}
