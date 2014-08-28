/**
 * Project Name:vdi-core-server-lls
 * File Name:GroupDaoTest.java
 * Package Name:com.vdi.dao.user
 * Date:2014年8月17日下午1:12:43
 * Copyright (c) 2014 All Rights Reserved.
 *
*/

package com.vdi.dao.user;

import static org.junit.Assert.*;

import java.util.List;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.vdi.dao.Request;
import com.vdi.dao.user.domain.DeliveryGroup;
import com.vdi.dao.user.domain.User;

import test.config.TestConfig;
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = { TestConfig.class })
public class GroupDaoTest {
	private @Autowired GroupDao groupDao;
	private @Autowired UserDao userDao;
	@Test
	public void testSave() {

		groupDao.save(new DeliveryGroup().setName("交付组1"));
	}

	@Test
	public void testGet() {
//		fail("Not yet implemented");
		groupDao.get(DeliveryGroup.class,1);
		
		
	}

	@Test
	public void testListRequest() {
		List<DeliveryGroup> des =groupDao.listRequest(new Request<DeliveryGroup>() {
		});
		Assert.assertNotNull(des);
		System.out.println(des.size());
	}

	@Test
	public void testDeleteByIds() {
		groupDao.deleteByIds(DeliveryGroup.class,1);
	}

	@Test
	public void testDelete() {
		fail("Not yet implemented");
	}
	@Test
	public void testUpdate(){
		groupDao.update(groupDao.get(DeliveryGroup.class, 2).addUser(this.userDao.get(User.class, 2)));
	}

}
