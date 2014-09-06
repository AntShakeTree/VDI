//
//package com.vdi.service.user.impl;
//
//import java.util.List;
//
//import org.springframework.stereotype.Service;
//
//import com.vdi.dao.suport.LdapHelp;
//import com.vdi.dao.user.domain.Domain;
//import com.vdi.dao.user.domain.LdapConfig;
//import com.vdi.dao.user.domain.Organization;
//import com.vdi.service.user.LdapStateSubject;
//import com.vdi.service.user.OrganizationService;
//
//@Service
//public class OrgnizationServiceImpl implements OrganizationService {
//
//	
//	@Override
//	public void whenLdapStateChangeUpdateByLdapconfig(LdapStateSubject subject,LdapConfig config) {
//		if(config.getStatus()!=LdapConfig.SYNC){
//			return;
//		}
//		//sync orgs
//		List<Organization> list =LdapHelp.getAllOrgnizations(config);
//		//发动同步人
//		Domain domain =config.getDomain();
//		
//		List<Organization> daos =domain.getOrganizations();
//		//更新数据库
//		
//		for (Organization organization : daos) {
////			organization.getFullname().
//		}
//		
//	}
//
//	@Override
//	public List<Organization> listChildrens(int organizationid) {
//		return null;
//	}
//
//}
