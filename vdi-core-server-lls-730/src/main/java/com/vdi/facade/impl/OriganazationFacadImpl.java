package com.vdi.facade.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.vdi.common.VDIBeanUtils;
import com.vdi.dao.suport.LdapSupport;
import com.vdi.dao.user.DomainDao;
import com.vdi.dao.user.OrganizationDao;
import com.vdi.dao.user.domain.Domain;
import com.vdi.dao.user.domain.LdapConfig;
import com.vdi.dao.user.domain.Organization;
import com.vdi.facade.OriganazationFacad;
import com.vdi.vo.req.OrganizationIdsReq;
import com.vdi.vo.res.Header;
import com.vdi.vo.res.ListOrganizationResponse;
import com.vdi.vo.res.OrganizationResponse;
@Service
public class OriganazationFacadImpl implements OriganazationFacad {
		private @Autowired DomainDao domainDao;
		private @Autowired OrganizationDao organizationDao;
	
	@Override
	public OrganizationResponse createOrganization(Organization organization) throws Exception {
		OrganizationResponse response =new OrganizationResponse();
		Domain domaindaoEntity = domainDao.get(Domain.class,organization.getDomainguid());
		if (domaindaoEntity.getDomaintype()!=Domain.DOMAIN_TYPE_LOCAL) {
			LdapConfig ldapconfig =domaindaoEntity.getConfig();
			VDIBeanUtils.copyPropertiesByNotNull(domaindaoEntity, ldapconfig, null);
			LdapSupport.createOU(ldapconfig, organization);
		}
		organization.setStatus(LdapConfig.NORMAL);
		organizationDao.save(organization);
		return response;
	}

	@Override
	public Header deleteOrganization(OrganizationIdsReq req) throws Exception{
//		OrganizationResponse response =new OrganizationResponse();
//		for (Integer orInteger :req.getOrganizationids()) {
//			Organization	organization= organizationDao.get(Organization.class,orInteger);
//			String domainguid =organization.getDomainguid();
//			Domain domaindaoEntity = domainDao.get(Domain.class,organization.getDomainguid());
//			if (domaindaoEntity.getDomaintype()!=Domain.DOMAIN_TYPE_LOCAL) {
//				LdapConfig ldapconfig =new LdapConfig();
//				VDIBeanUtils.copyPropertiesByNotNull(domaindaoEntity, ldapconfig, null);
//				LdapSupport.createOU(ldapconfig, organization);
//			}
//		}
//		
//		organization.setStatus(LdapConfig.NORMAL);
//		organizationDao.save(organization);

		return null;
	}

	@Override
	public OrganizationResponse updateOrganization(OrganizationIdsReq req) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ListOrganizationResponse listOrganizations() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public OrganizationResponse addUserToOrganization() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public OrganizationResponse deleteUserFromOrganization() {
		// TODO Auto-generated method stub
		return null;
	}

}
