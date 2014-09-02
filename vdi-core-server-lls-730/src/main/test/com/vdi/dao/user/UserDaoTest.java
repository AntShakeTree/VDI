/**
 * Project Name:vdi-core-server-lls
 * File Name:UserDaoTest.java
 * Package Name:com.vdi.dao.user
 * Date:2014年8月7日下午12:41:20
 * Copyright (c) 2014 All Rights Reserved.
 *
*/

package com.vdi.dao.user;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import test.config.TestConfig;

import com.vdi.dao.Request;
import com.vdi.dao.user.domain.Domain;
import com.vdi.dao.user.domain.Role;
import com.vdi.dao.user.domain.User;
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = { TestConfig.class })
public class UserDaoTest {
		@Autowired private UserDao userDao;
		@Autowired private DomainDao domainDao;
		@Autowired private RoleDao roleDao;
	@Test
	public void testSave() {
		Role admin =roleDao.findOneByKey("authority", Role.ROLE_ADMIN);
		Role userrole =roleDao.findOneByKey("authority", Role.ROLE_ADMIN);
		if(admin==null){
			admin.setAuthority(Role.ROLE_ADMIN);
//			admin.setAuthority(Role.ROLE_USER);
			admin.setParent(0);
			roleDao.save(admin);
		}
		if(userrole==null){
			userrole.setAuthority(Role.ROLE_USER);
			userrole.setParent(admin.getIdrole());
			roleDao.save(userrole);
		}
		
		User user =new User();
		user.setUsername("admin");
		user.setPassword("111111");
		user.setEnabled(true);
		Set<Role> roles=new HashSet<Role>();
		roles.add(admin);
		user.setRoles(roles);
		Domain domain=domainDao.findOneByKey("guid", "asdfghjkllqwertyui");
//		domain.setGuid("asdfghjkllqwertyui");
		domain.setDomaintype(Domain.DOMAIN_TYPE_LOCAL);
		domain.setStatus(Domain.DOMAIN_STATUS_NORMAL);
		domain.setDomainservername("local");
		domain.setDomainserverport(0);
		user.setDomain(domain);
		user.setRoles(roles);		
//		domainDao.save(domain);
		userDao.save(user);

	}
	@Test
	public void deleteUser(){
		List<User> ls =userDao.listRequest(new Request<User>() {
		});
		for (User user : ls) {
			userDao.delete(user);
		}
//		List<Domain> ds =domainDao.listRequest(new Request<Domain>() {
//		});
//		for (Domain domain : ds) {
//			domainDao.delete(domain);
//		}
		
	}
}
