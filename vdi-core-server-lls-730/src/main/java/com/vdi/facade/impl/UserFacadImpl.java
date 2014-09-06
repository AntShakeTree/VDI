//package com.vdi.facade.impl;
//
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Service;
//import org.springframework.util.Assert;
//
//import com.vdi.common.Constants;
//import com.vdi.common.ErrorCode;
//import com.vdi.common.ExceptionHandle;
//import com.vdi.common.VDIBeanUtils;
//import com.vdi.dao.suport.LdapSupport;
//import com.vdi.dao.user.DomainDao;
//import com.vdi.dao.user.OrganizationDao;
//import com.vdi.dao.user.UserDao;
//import com.vdi.dao.user.domain.Domain;
//import com.vdi.dao.user.domain.Organization;
//import com.vdi.dao.user.domain.User;
//import com.vdi.facade.UserFacad;
//import com.vdi.vo.req.UserReq;
//import com.vdi.vo.res.UserResponse;
//@Service
//public class UserFacadImpl implements UserFacad {
//	private @Autowired UserDao userDao;
//	private @Autowired DomainDao domainDao;
//	private @Autowired OrganizationDao organizationDao;
//	@Override
//	public UserResponse createUser(UserReq req) {
//		UserResponse response=new UserResponse();
//		User user =new User();
//		Assert.notNull(req);
//		if(req.getDomainguid()==null){
//			req.setDomainguid(Constants.LOCAL_DOMAIN_GUID);
//		}
//		
//		Domain domain =domainDao.get(Domain.class, req.getDomainguid());
//		VDIBeanUtils.copyPropertiesByNotNull(req,user,null);
//		user.setDomainguid(domain.getGuid());
//		Organization organization =organizationDao.get(Organization.class, req.getOrganizationid());
//		if(organization==null){
//			response.getHead().setError(ExceptionHandle.err.error(ErrorCode.ORGANIZATION_NULL));
//			return response;
//		}
//		if(!req.getDomainguid().equals(Constants.LOCAL_DOMAIN_GUID)){
//			if(organization.getLdapConfig()==null){
//				response.getHead().setError(ExceptionHandle.err.error(ErrorCode.LDAP_NULL));
//				return response;	
//			}
//			user.setOrganization(organization);
//			LdapSupport.createUser(user);
//		}
//		userDao.save(user);
//		response.setBody(user);
//		return response;
//	}
//
//	@Override
//	public void updateUser() {
//		
//	}
//
//	@Override
//	public void updatePassword() {
//		// TODO Auto-generated method stub
//
//	}
//
//	@Override
//	public void removeUser() {
//		// TODO Auto-generated method stub
//
//	}
//
//	@Override
//	public void listUsers() {
//		// TODO Auto-generated method stub
//
//	}
//
//	@Override
//	public void getUser() {
//		// TODO Auto-generated method stub
//
//	}
//
//}
