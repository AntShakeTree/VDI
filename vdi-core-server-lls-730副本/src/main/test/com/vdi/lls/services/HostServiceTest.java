package com.vdi.lls.services;

import javax.annotation.Resource;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import test.config.TestConfig;

import com.vdi.support.desktop.lls.domain.resource.Host;
import com.vdi.support.desktop.lls.manager.LLSConnection;
import com.vdi.support.desktop.lls.manager.LLSSendMessage;
import com.vdi.support.desktop.lls.services.HostService;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = { TestConfig.class })
public class HostServiceTest {
	private @Resource(name = "llsHandle")
	LLSConnection llsConnection;
	private @Resource(name = "llsHandle")
	LLSSendMessage sendMessage;
	private @Autowired HostService hostService;
	@Before
	public void setUp() throws Exception {
	}

	@Test
	public void testListHost() {
		for(Host host:hostService.listHost(new Host())){
			System.out.println(host.getHostIdentity());
		}
		
	}

	@Test
	public void testGetHost() {

		Assert.assertEquals(hostService.getHost("b784c4b854054706a44f91d9f174119f").getHostIdentity(),"b784c4b854054706a44f91d9f174119f");
	}

}
