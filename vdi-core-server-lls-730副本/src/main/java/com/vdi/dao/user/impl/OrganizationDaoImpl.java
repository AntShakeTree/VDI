/**
 * Project Name:vdi-core-server-lls
 * File Name:OrganizationDaoImpl.java
 * Package Name:com.vdi.dao.user.impl
 * Date:2014年8月15日下午1:21:35
 * Copyright (c) 2014 All Rights Reserved.
 *
*/

package com.vdi.dao.user.impl;

import org.springframework.stereotype.Repository;

import com.vdi.dao.suport.JPADaoSuport;
import com.vdi.dao.user.OrganizationDao;
import com.vdi.dao.user.domain.Organization;
@Repository
public class OrganizationDaoImpl extends JPADaoSuport<Organization> implements OrganizationDao{

}
