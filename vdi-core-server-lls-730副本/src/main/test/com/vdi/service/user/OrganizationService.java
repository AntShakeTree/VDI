/**
 * Project Name:vdi-core-server-lls
 * File Name:OrganizationService.java
 * Package Name:com.vdi.service.user
 * Date:2014年8月17日下午12:24:13
 * Copyright (c) 2014 All Rights Reserved.
 *
*/

package com.vdi.service.user;

import java.util.List;

import org.springframework.stereotype.Service;

import com.vdi.dao.user.domain.Organization;

@Service
public interface OrganizationService {
	List<Organization> listChildrens(int organizationid);
}
