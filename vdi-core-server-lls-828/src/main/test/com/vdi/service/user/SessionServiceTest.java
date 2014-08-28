/**
 * Project Name:vdi-core-server-lls
 * File Name:SessionServiceTest.java
 * Package Name:com.vdi.service.user
 * Date:2014年8月8日下午4:27:33
 * Copyright (c) 2014 All Rights Reserved.
 *
*/

package com.vdi.service.user;

import static org.junit.Assert.*;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.vdi.dao.user.domain.Session;

import sun.print.resources.serviceui;
import test.config.TestConfig;
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = { TestConfig.class })
public class SessionServiceTest {
	private @Autowired SessionService sessionService;
	@Test
	public void testSaveSession() {
		
//		Session session=new Session();
//		session.setTicket("6FED49C3539C4A4BA25267D01D65A837");
//		session.setUsername("admin");
//		sessionService.saveSession(session);
	}
	@Test
	public void testGets(){
		System.out.println(sessionService.getUsernameByTicket("6FED49C3539C4A4BA25267D01D65A837"));
	}

}
