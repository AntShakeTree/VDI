package com.vdi.lls.resource.services;

import java.util.List;

import javax.annotation.Resource;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import test.config.TestConfig;

import com.vdi.common.ParseJSON;
import com.vdi.support.desktop.lls.domain.resource.ComputePool;
import com.vdi.support.desktop.lls.manager.LLSConnection;
import com.vdi.support.desktop.lls.manager.LLSSendMessage;
import com.vdi.support.desktop.lls.services.ComputePoolService;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = { TestConfig.class })
public class ComputePoolServiceTest {
	static Logger log = LoggerFactory.getLogger(ComputePoolServiceTest.class);
	
	//
	// // private @Autowired
	// // AutowireCapableBeanFactory beanFactory;
	private @Resource(name = "llsHandle")
	LLSConnection llsConnection;
	private @Resource(name = "llsHandle")
	LLSSendMessage llsSendMessage;
	private @Autowired
	ComputePoolService computePoolService;

	@Before
	public void setUp() throws Exception {
	}

	@Test
	public void testGetComputePool() {
		// fail("Not yet implemented");
		String id = "abv";
		ComputePool pool = computePoolService.getComputePool(id);
		log.warn("{}", ParseJSON.toJson(pool));
		Assert.assertEquals(pool.getComputePoolIdentity(), id);
	}

	@Test
	public void testListComputePool() {
		long start = System.currentTimeMillis();
		for (int i = 0; i <10000; i++) {
			List<ComputePool> pools = computePoolService
					.listComputePool(new ComputePool());
			
			for (ComputePool c : pools) {
				System.out.println(c.getComputePoolIdentity());
			}
		System.out.println(i);
		try {
			Thread.sleep(20*1000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		}
		System.out.println(System.currentTimeMillis()-start);
	}

}
