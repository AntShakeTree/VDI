/**
 * Project Name:vdi-core-server-lls
 * File Name:ComputePoolDaoImplTest.java
 * Package Name:com.vdi.dao.desktop.impl
 * Date:2014年8月11日下午1:33:53
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

import test.config.TestConfig;

import com.vdi.dao.desktop.ComputePoolDao;
import com.vdi.dao.desktop.domain.ComputePoolEntity;
import com.vdi.dao.desktop.domain.HostEntity;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = { TestConfig.class })
public class ComputePoolDaoImplTest {
	private @Autowired ComputePoolDao computePoolDao;

	@Test
	public void testSave() {
	//	fail("Not yet implemented");
		ComputePoolEntity entity = new ComputePoolEntity();
		entity.setComputepoolname("default");
		HostEntity e = new HostEntity();
		e.setHostname("host");
//		e.setComputePoolEntity(entity);
		// entity.s
		entity.getHosts().add(e);
//		entity.set
		computePoolDao.save(entity);
	}

	@Test
	public void testGet() {
		
	}

	@Test
	public void testUpdate() {
		fail("Not yet implemented");
	}

	@Test
	public void testDelete() {
		fail("Not yet implemented");
	}

}
