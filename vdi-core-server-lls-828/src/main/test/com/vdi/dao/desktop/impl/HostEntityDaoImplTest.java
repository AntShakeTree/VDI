/**
 * Project Name:vdi-core-server-lls
 * File Name:HostEntityDaoImplTest.java
 * Package Name:com.vdi.dao.desktop.impl
 * Date:2014年8月11日下午1:50:47
 * Copyright (c) 2014 All Rights Reserved.
 *
*/

package com.vdi.dao.desktop.impl;

import static org.junit.Assert.*;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.vdi.dao.desktop.HostDao;
import com.vdi.dao.desktop.domain.HostEntity;

import test.config.TestConfig;
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = { TestConfig.class })
public class HostEntityDaoImplTest {
	private @Autowired HostDao hostDao;
	
	@Test
	public void testSave() {
		HostEntity hostEntity =new HostEntity();
//		hostDao.save();
	}

}
