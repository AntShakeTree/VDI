/**
 * Project Name:vdi-core-server-lls
 * File Name:OrganizationDaoTest.java
 * Package Name:com.vdi.dao.user
 * Date:2014年8月17日上午11:55:44
 * Copyright (c) 2014 All Rights Reserved.
 *
 */

package com.vdi.dao.user;

import java.util.Set;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.vdi.dao.Request;
import com.vdi.dao.user.domain.Organization;
import com.vdi.service.user.OrganizationService;

import test.config.TestConfig;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = { TestConfig.class })
public class OrganizationDaoTest {

	private @Autowired OrganizationDao organizationDao;
	private @Autowired OrganizationService organiztionService;
	@Test
	public void save() {

		Organization organization = new Organization();
		organization.setOrganizationname("分公司3");
		organization.setParent(organizationDao.get(Organization.class, 1));
		organizationDao.save(organization);
	}

	@Test
	public void findTree() {
		System.out.println(organiztionService.listChildrens(1));
	}
	@Test
	public void findAll(){
		System.out.println(organizationDao.listRequest(new Request<Organization>() {
		}));
	}
	@Test
	public void delete(){
		organizationDao.deleteByIds(Organization.class,1);
	}
}
