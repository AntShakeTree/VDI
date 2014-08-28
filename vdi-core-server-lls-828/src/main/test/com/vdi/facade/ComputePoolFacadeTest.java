/**
 * Project Name:vdi-core-server-lls
 * File Name:ComputePoolFacadeTest.java
 * Package Name:com.vdi.facade
 * Date:2014年8月12日下午12:12:04
 * Copyright (c) 2014 All Rights Reserved.
 *
*/

package com.vdi.facade;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.sun.jndi.url.corbaname.corbanameURLContextFactory;
import com.vdi.dao.desktop.domain.ComputePoolEntity;
import com.vdi.support.desktop.lls.manager.support.LLSQueryLLSQueue;
import com.vdi.vo.req.DeleteComputePool;

import test.config.TestConfig;
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = { TestConfig.class })
public class ComputePoolFacadeTest {
	private @Autowired ComputePoolFacade computePoolFacade;
	@Autowired
	LLSQueryLLSQueue queue;
	@Test
	public void testCreateComputePool() throws InterruptedException {
		ComputePoolEntity entity=new ComputePoolEntity();
		entity.setComputepoolname(UUID.randomUUID()+"");
		entity.setDispatchtype("seq");
		computePoolFacade.createComputePool(entity);
		while(true){
			queue.sendSchdulTask();
			Thread.sleep(5000);
		}
	}
	@Test
	public void testUpdateComputePool() throws InterruptedException {
		
	}
	@Test
	public void testDeleteComputePool() throws InterruptedException {
		DeleteComputePool d =new DeleteComputePool();
	
		List<Long> ss=new ArrayList<Long>();
		ss.add(1l);
		ss.add(2l);
		d.setComputepoolids(ss);
		computePoolFacade.deleteComputePool(d);
		while(true){
			queue.sendSchdulTask();
			Thread.sleep(5000);
		}
	}
}
