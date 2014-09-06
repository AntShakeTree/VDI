//package com.vdi.service.user.impl;
//
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Service;
//
//import com.vdi.common.VDIBeanUtils;
//import com.vdi.dao.suport.LdapHelp;
//import com.vdi.dao.user.DomainDao;
//import com.vdi.dao.user.LdapConfigDao;
//import com.vdi.dao.user.domain.Domain;
//import com.vdi.dao.user.domain.LdapConfig;
//import com.vdi.service.user.CreateDomainService;
//import com.vdi.service.user.LdapStateSubject;
//import com.vdi.service.user.OrganizationService;
//@Service
//public class CreatDomainServiceImpl implements CreateDomainService{
//	private @Autowired DomainDao domainDao;
//	private @Autowired LdapConfigDao ldapConfigDao;
//	private @Autowired OrganizationService organizationService;
//	@Override
//	public void whenLdapStateChangeUpdateByLdapconfig(LdapStateSubject subject,LdapConfig config) {
//	
//		
//		if(config.getStatus()!=LdapConfig.CREATEING){
//			return;
//		}
//		Domain domain =LdapHelp.buildDomain(config);
//		domain.setStatus(Domain.DOMAIN_STATUS_SYNCHRONIZING);
//		Domain dao=domainDao.get(Domain.class,domain.getGuid());
//		if(dao!=null){
//			VDIBeanUtils.copyPropertiesByNotNull(domain, dao, null);
//			domainDao.update(dao);
//		}else{
//			domainDao.save(domain);
//		}
//		config.setDomain(domain);
//		config.setStatus(LdapConfig.SYNC);
//		subject.registerStateChangeObserver(organizationService,config);
//	}
//
//}
