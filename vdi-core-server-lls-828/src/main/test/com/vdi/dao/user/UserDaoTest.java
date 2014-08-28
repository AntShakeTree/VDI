/**
 * Project Name:vdi-core-server-lls
 * File Name:UserDaoTest.java
 * Package Name:com.vdi.dao.user
 * Date:2014年8月7日下午12:41:20
 * Copyright (c) 2014 All Rights Reserved.
 *
*/

package com.vdi.dao.user;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import test.config.TestConfig;

import com.vdi.dao.user.domain.Role;
import com.vdi.dao.user.domain.User;
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = { TestConfig.class })
public class UserDaoTest {
		@Autowired private UserDao UserDao;
	@Test
	public void testSave() {
		User domain =new User();
		domain.setEnabled(true);
		domain.setUsername("admin");
		domain.setPassword("111111");
		Role e=new Role();
		e.setAuthority("ADMIN");
		
		domain.getRoles().add(e);
		Role e2=new Role();
		e2.setAuthority("USER");
		domain.getRoles().add(e2);
		UserDao.save(domain);
	}

}
