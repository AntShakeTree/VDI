package com.vdi.facade;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import test.config.TestConfig;

import com.vdi.dao.desktop.domain.StorageEntity;
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = { TestConfig.class })
public class StorageFacadTest {
private @Autowired StorageFacad storageFacad;
	@Test
	public void testList(){
		StorageEntity entity =new StorageEntity();
		entity.setPage(-1);
		storageFacad.listStorage(entity);
	}
}
