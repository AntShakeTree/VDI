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

import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.util.StringUtils;

import test.config.TestConfig;

import com.vdi.dao.Request;
import com.vdi.dao.desktop.ComputePoolDao;
import com.vdi.dao.desktop.domain.ComputePoolBuild;
import com.vdi.dao.desktop.domain.ComputePoolEntity;
import com.vdi.dao.desktop.domain.HostEntity;
import com.vdi.support.desktop.lls.domain.resource.ComputePool;

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
		List<ComputePoolEntity> es =computePoolDao .listRequest(new Request<ComputePoolEntity>() {
		});
		for (ComputePoolEntity computePoolEntity : es) {
			computePoolDao.delete(computePoolEntity);
		}
	}
	
	@Test
	public void testUPdate() {
		List<ComputePoolEntity> cs =computePoolDao .listRequest(new Request<ComputePoolEntity>() {
		});
		ComputePool computePool =new ComputePool();
		computePool.setComputePoolIdentity("4d1f1045fe45486b9bdf1eec0180bc35");
		String identity="4d1f1045fe45486b9bdf1eec0180bc35";
		computePool.setStatus(ComputePool.AVAILABLE);
		for (ComputePoolEntity computePoolEntity : cs) {
			System.out.println(computePoolEntity.getComputepoolidentity());
			if(!StringUtils.isEmpty(identity)){
				if(computePoolEntity.getComputepoolidentity().equals(identity)){
					computePoolEntity=new ComputePoolBuild(computePoolEntity, computePool).entity_computePoolIdentity().entity_cpuamount().entity_cpurest().entity_dispatchtype().entity_memoryamount().entity_memoryrest().entity_status().bulidComputePoolEntity();
					computePoolDao.update(computePoolEntity);
				}
			}	
		}		
	}

	@Test
	public void testDelete2() {
		computePoolDao.delete(computePoolDao.get(ComputePoolEntity.class, 63));
	}

}
