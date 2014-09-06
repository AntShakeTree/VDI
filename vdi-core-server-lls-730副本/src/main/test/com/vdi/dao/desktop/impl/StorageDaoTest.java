package com.vdi.dao.desktop.impl;

import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import test.config.TestConfig;

import com.vdi.dao.Request;
import com.vdi.dao.desktop.HostDao;
import com.vdi.dao.desktop.StorageDao;
import com.vdi.dao.desktop.domain.HostEntity;
import com.vdi.dao.desktop.domain.StorageEntity;
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = { TestConfig.class })
public class StorageDaoTest {
	private @Autowired StorageDao storageDao;
	private @Autowired HostDao hostDao;
	@Test
	public void test() {
		StorageEntity entity=new StorageEntity();
		HostEntity hostentity = hostDao.findOneByKey("hostidentity","f0ba85af000e4234832fd3d5526ec5e9");
		entity.setHost(hostentity);
		storageDao.save(entity);
	}
	@Test
	public void delete() {
		List<StorageEntity> ss= storageDao.listRequest(new Request<StorageEntity>() {});
		for (StorageEntity storageEntity : ss) {
			storageDao.delete(storageEntity);
		}
	}
}
