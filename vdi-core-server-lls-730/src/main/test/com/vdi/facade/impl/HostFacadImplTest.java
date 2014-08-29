package com.vdi.facade.impl;

import static org.junit.Assert.*;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.vdi.dao.desktop.domain.HostEntity;
import com.vdi.facade.HostFacad;

import test.config.TestConfig;
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = { TestConfig.class })
public class HostFacadImplTest {
	@Autowired HostFacad hostFacad;
	@Test
	public void testListHost() {
		HostEntity entity=new HostEntity();
		entity.setPage(0);
		entity.setPagesize(15);
		hostFacad.listHost(entity);
	}

}
