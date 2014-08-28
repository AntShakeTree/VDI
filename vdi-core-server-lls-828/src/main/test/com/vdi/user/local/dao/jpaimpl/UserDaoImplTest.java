/**
 * Project Name:vdi-core-server-lls
 * File Name:UserDaoImplTest.java
 * Package Name:com.vdi.user.local.dao.jpaimpl
 * Date:2014年8月6日下午5:43:03
 * Copyright (c) 2014 All Rights Reserved.
 *
*/

package com.vdi.user.local.dao.jpaimpl;

import java.util.Set;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import test.config.TestConfig;

import com.vdi.dao.user.UserDao;
import com.vdi.dao.user.domain.Role;
import com.vdi.dao.user.domain.User;
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = { TestConfig.class })
public class UserDaoImplTest {
	private  @Autowired UserDao userDao;
	
	@Test
	public void save() {
//		fail("Not yet implemented");
		User user =new User();
		user.setEnabled(true);
//		user.setRoles();
		user.getRoles().add(new Role().setAuthority("ADMIN"));
		user.setPassword("111111");
		user.setUsername("admin");
		userDao.save(user);
	}
	@Test
	public void getU(){
		System.out.println(userDao.findOneByKey("username","admin"));
	}
	

}
