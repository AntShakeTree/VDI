package com.vdi.lls.services;


import java.util.List;

import javax.annotation.Resource;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import test.config.TestConfig;

import com.vdi.support.desktop.lls.domain.Where;
import com.vdi.support.desktop.lls.domain.vms.VmInstance;
import com.vdi.support.desktop.lls.manager.LLSConnection;
import com.vdi.support.desktop.lls.services.VmInstanceService;
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes={TestConfig.class})
public class VmInstanceServiceTest {
	private @Resource(name = "llsHandle")
	LLSConnection llsConnection;
	@Autowired VmInstanceService vmInstanceService;
	@Before
	public void setUp() throws Exception {
	}

	@After
	public void tearDown() throws Exception {
		llsConnection.close();
	}
	
	@Test
	public void testListVmInstance() {
		List<VmInstance> vms =vmInstanceService.listVmInstance((VmInstance) new VmInstance(),new Where().setLimit(10).setOffset(0));
		System.out.println(vms);
		for (VmInstance vmInstance : vms) {
			System.out.println(vmInstance.getVmIdentity());
		}
	}

	@Test
	public void testGetVmInstance() {
//		fail("Not yet implemented");
VmInstance vmInstance=		vmInstanceService.getVmInstance("d5a992a102ea42a1900ca2e5e874150b");
System.out.println(vmInstance.getVmIdentity());
	}

}
