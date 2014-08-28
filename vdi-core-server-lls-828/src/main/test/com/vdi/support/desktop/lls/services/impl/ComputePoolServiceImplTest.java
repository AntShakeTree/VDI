/**
 * Project Name:vdi-core-server-lls
 * File Name:ComputePoolServiceImplTest.java
 * Package Name:com.vdi.support.desktop.lls.services.impl
 * Date:2014年8月11日下午3:23:39
 * Copyright (c) 2014 All Rights Reserved.
 *
*/

package com.vdi.support.desktop.lls.services.impl;

import java.util.UUID;

import javax.annotation.Resource;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import test.config.TestConfig;

import com.vdi.support.desktop.lls.domain.resource.ComputePool;
import com.vdi.support.desktop.lls.manager.LLSConnection;
import com.vdi.support.desktop.lls.manager.LLSSendMessage;
import com.vdi.support.desktop.lls.manager.support.LLSQueryLLSQueue;
import com.vdi.support.desktop.lls.services.AsynchronousComputePoolService;
import com.vdi.support.desktop.lls.services.ComputePoolService;
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = { TestConfig.class })
public class ComputePoolServiceImplTest {
@Autowired ComputePoolService computePoolService;
@Autowired AsynchronousComputePoolService asynchronousComputePoolService;
//@Autowired LLSJobService llsJobService;
private @Resource(name = "llsHandle")
LLSConnection llsConnection;
private @Autowired LLSQueryLLSQueue queryLLSQueue;
private @Resource(name = "llsHandle") LLSSendMessage sendMessage;
	@Test
	public void testCreateComputePool() throws InterruptedException {
		llsConnection.connection(null, 0);
		ComputePool computePool=new ComputePool();
		computePool.setComputePoolName(UUID.randomUUID()+"");
		computePool.setDispatchType("seq");
		String jobId =asynchronousComputePoolService.createComputePool(computePool);
		System.out.println(jobId);
		
		while(true){
//			llsJobService.queryJob(jobId);
//			llsq
			queryLLSQueue.sendSchdulTask();
//			System.out.println(llsJobService.queryJob(jobId));
			
			Thread.sleep(5000);
			
		}
	}
	
	@Test
	public void testQ(){
		
	}
}
