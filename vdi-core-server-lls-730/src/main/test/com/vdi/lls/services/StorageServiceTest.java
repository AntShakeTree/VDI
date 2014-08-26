package com.vdi.lls.services;

import javax.annotation.Resource;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import test.config.TestConfig;

import com.vdi.common.ParseJSON;
import com.vdi.support.desktop.lls.domain.storage.Storage;
import com.vdi.support.desktop.lls.manager.LLSConnection;
import com.vdi.support.desktop.lls.manager.LLSSendMessage;
import com.vdi.support.desktop.lls.services.StorageService;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = { TestConfig.class })
public class StorageServiceTest {
	private static final Storage Storage = null;
	private @Resource(name = "llsHandle")
	LLSConnection llsConnection;
	private @Resource(name = "llsHandle")
	LLSSendMessage sendMessage;
	private @Autowired
	StorageService storageService;

	@Before
	public void setUp() throws Exception {
	}

	@After
	public void tearDown() throws Exception {
		llsConnection.close();
	}

	@Test
	public void testListStorage() {

		for (Storage iterable_element : storageService.listStorage(Storage)) {
			// System.out.println(iterable_element.getContent());
			System.out.println(iterable_element.getStorageIdentity());
		}
	}

	@Test
	public void testGetStorage() {
		Storage storage =	storageService.getStorage("local@b784c4b854054706a44f91d9f174119f");
		System.out.println(ParseJSON.toJson(storage));
	}

}
