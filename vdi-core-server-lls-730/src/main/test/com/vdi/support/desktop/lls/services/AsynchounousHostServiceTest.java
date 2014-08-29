package com.vdi.support.desktop.lls.services;

import static org.junit.Assert.fail;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import test.config.TestConfig;

import com.vdi.common.Session;
import com.vdi.facade.BaseFacad;
import com.vdi.support.desktop.lls.domain.resource.Host;
import com.vdi.support.desktop.lls.domain.task.Task;
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = { TestConfig.class })
public class AsynchounousHostServiceTest {
	private @Autowired BaseFacad baseFacad;
	private @Autowired AsynchounousHostService asynchounousHostService;
	@Test
	public void testCreateHost() {
		Host host =new Host();
		host.setHostName("ceshieryi");
		host.setAddr("20.1.131.31");
		String taskid =asynchounousHostService.createHost(host);
		while(true){
			Task task =(Task) Session.getCache(taskid);
//			syso
//			
//			task.getActionName()
		System.out.println(task.toString());
		}
	}

	@Test
	public void testRemoveHostFromComputePool() {
		fail("Not yet implemented");
	}

	@Test
	public void testDeleteHost() {
		fail("Not yet implemented");
	}

	@Test
	public void testAddHostToComputePool() {
		fail("Not yet implemented");
	}

}
