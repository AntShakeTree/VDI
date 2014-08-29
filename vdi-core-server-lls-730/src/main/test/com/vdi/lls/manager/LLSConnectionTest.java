package com.vdi.lls.manager;

import javax.annotation.Resource;

import org.apache.log4j.Logger;
import org.junit.After;
import org.junit.Before;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import test.config.TestConfig;

import com.vdi.support.desktop.lls.manager.LLSConnection;
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes={TestConfig.class})
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class LLSConnectionTest {
	Logger logger =Logger.getLogger(LLSConnectionTest.class);
	private @Resource(name="llsHandle") LLSConnection connection;
	@Before
	public void setUp() throws Exception {
//		/PropertyConfigurator.configure(Test.class.getClassLoader().getResource("log4j.properties")); 
	}

	@After
	public void tearDown() throws Exception {
		
	}

	@Test
	public void test01Connection() throws Exception {
//		connection.connection(null,null);
	    Thread.sleep(1000);
	    System.out.println(connection.isConnection());
	}

	@Test
	public void test02Close() throws Exception {
		System.out.println("test02Close");
//		connection.close();
	    Thread.sleep(1000);
	    System.out.println(connection.isConnection());
	}
	@Test
	public void testReconnection() {
	}

	
}
