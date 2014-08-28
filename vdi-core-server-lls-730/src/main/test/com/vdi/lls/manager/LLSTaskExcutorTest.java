package com.vdi.lls.manager;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import test.config.TestConfig;

import com.vdi.common.Session;
import com.vdi.support.desktop.lls.domain.task.Task;
import com.vdi.support.desktop.lls.manager.support.VDIQueue;
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes={TestConfig.class})
public class LLSTaskExcutorTest {
	@Autowired VDIQueue queue;
	@Before
	public void setUp() throws Exception {
		
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testAddTaskExcutorQueue() {
		queue.addTaskExcutorQueue(new Task().setTaskIdentity("bd32d28c0cfa11e4a3358084f439f58a"));
		queue.sendSchdulTask();
		System.out.println(Session.getCache("bd32d28c0cfa11e4a3358084f439f58a"));
	}

}
