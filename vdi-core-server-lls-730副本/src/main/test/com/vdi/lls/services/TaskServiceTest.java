package com.vdi.lls.services;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import test.config.TestConfig;

import com.vdi.common.ParseJSON;
import com.vdi.support.desktop.lls.domain.task.Task;
import com.vdi.support.desktop.lls.services.TaskService;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = { TestConfig.class })
public class TaskServiceTest {
	@Autowired
	TaskService taskService;

	@Before
	public void setUp() throws Exception {

	}

	@After
	public void tearDown() throws Exception {
	
	}

	@Test
	public void testGetTask() {
	Task t=	taskService.getTask("2add0e6a0d2311e4be6e8084f439f58a");
	System.out.println(ParseJSON.toJson(t));
	}

}
