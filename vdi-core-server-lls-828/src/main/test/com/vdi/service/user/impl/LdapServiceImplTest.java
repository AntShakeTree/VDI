/**
 * Project Name:vdi-core-server-lls
 * File Name:LdapServiceImplTest.java
 * Package Name:com.vdi.service.user.impl
 * Date:2014年8月15日下午1:42:28
 * Copyright (c) 2014 All Rights Reserved.
 *
*/

package com.vdi.service.user.impl;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.vdi.dao.user.domain.LdapConfig;
import com.vdi.service.user.LdapService;

import test.config.TestConfig;
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = { TestConfig.class })
public class LdapServiceImplTest {
	private @Autowired LdapService ldapService; 
	@Test
	public void testAddLdapConfig() {
		LdapConfig ldapConfig=new LdapConfig();
		ldapConfig.setBase("dc=domain1,dc=com");
		ldapConfig.setUrl("ldap://20.1.136.193:389");
		ldapConfig.setPrincipal("domain1\\administrator");
		ldapConfig.setPassword("123!@#qwe");
		ldapService.addLdapConfig(ldapConfig);
	}
	@Test
	public void testIsexist() {
		System.out.println(ldapService.isExist("ldap://20.1.136.193:389"));
	}
}
