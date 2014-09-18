package com.vdi.facade.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.vdi.common.VDIBeanUtils;
import com.vdi.dao.Request;
import com.vdi.dao.suport.LdapSupport;
import com.vdi.dao.user.DomainDao;
import com.vdi.dao.user.OrganizationDao;
import com.vdi.dao.user.UserDao;
import com.vdi.dao.user.domain.Domain;
import com.vdi.dao.user.domain.Organization;
import com.vdi.dao.user.domain.User;
import com.vdi.dao.user.domain.UserMapBridge;
import com.vdi.facade.OriganazationFacad;
import com.vdi.vo.req.OrganizationIdsReq;
import com.vdi.vo.req.OrganizationRequst;
import com.vdi.vo.res.Header;
import com.vdi.vo.res.ListOrganizationResponse;
import com.vdi.vo.res.ListOrganizationResponse.ListOrganization;
import com.vdi.vo.res.OrganizationResponse;
@Service
public class OriganazationFacadImpl implements OriganazationFacad {
		private @Autowired DomainDao domainDao;
		private @Autowired OrganizationDao organizationDao;
		private @Autowired UserDao userDao;
	@Override
	public OrganizationResponse createOrganization(Organization organization) throws Exception {
		OrganizationResponse response =new OrganizationResponse();
		Domain domaindaoEntity = domainDao.get(Domain.class,organization.getDomainguid());
		if (domaindaoEntity.getDomaintype()!=Domain.DOMAIN_TYPE_LOCAL) {
			UserMapBridge ldapconfig =domaindaoEntity.getConfig();
			VDIBeanUtils.copyPropertiesByNotNull(domaindaoEntity, ldapconfig, null);
			LdapSupport.createOU(ldapconfig, organization);
		}
		organization.setStatus(UserMapBridge.NORMAL);
		organizationDao.save(organization);
		return response;
	}

	@Override
	public Header deleteOrganization(OrganizationIdsReq req) throws Exception{
		for (Integer orInteger :req.getOrganizationids()) {
			Organization	organization= organizationDao.get(Organization.class,orInteger);
			Domain domaindaoEntity = domainDao.get(Domain.class,organization.getDomainguid());
			if (domaindaoEntity.getDomaintype()!=Domain.DOMAIN_TYPE_LOCAL) {
				UserMapBridge ldapconfig =new UserMapBridge();
				VDIBeanUtils.copyPropertiesByNotNull(domaindaoEntity, ldapconfig, null);
				LdapSupport.delOU(ldapconfig, organization);				
			}else{
				User request=new User();
				request.setOrganizationid(orInteger);
				List<User> users =userDao.listRequest(request);
				
				for (User user : users) {
					userDao.delete(user);
				}
				organizationDao.delete(organization);
			}
		}
		return new Header();
	}

	@Override
	public OrganizationResponse updateOrganization(OrganizationIdsReq req) {
		return null;
	}

	@Override
	public ListOrganizationResponse listOrganizations(OrganizationRequst req) {
		ListOrganizationResponse response =new ListOrganizationResponse();
		response.getHead().setError(0);
		ListOrganization body=new ListOrganization();
		List<Organization> list=organizationDao.listRequest(request);
		body.setList(list);
		response.setBody(body);
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
