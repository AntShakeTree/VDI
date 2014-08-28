/**
 * Project Name:vdi-core-server-lls
 * File Name:OrganizationServiceImpl.java
 * Package Name:com.vdi.service.user.impl
 * Date:2014年8月17日下午12:24:33
 * Copyright (c) 2014 All Rights Reserved.
 *
*/

package com.vdi.service.user.impl;

import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.vdi.dao.user.OrganizationDao;
import com.vdi.dao.user.domain.Organization;
import com.vdi.service.user.OrganizationService;
@Service
public class OrganizationServiceImpl implements OrganizationService{
	private @Autowired OrganizationDao organizationDao;

	@Override
	@org.springframework.data.annotation.Transient
	public List<Organization> listChildrens(int organizationid) {
	return 	this.organizationDao.listRequest(new Organization().setParent(this.organizationDao.get(Organization.class, organizationid)));
	}

}
