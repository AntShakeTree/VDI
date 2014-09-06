package com.vdi.facade;

import static org.junit.Assert.fail;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import test.config.TestConfig;

import com.vdi.dao.user.domain.Domain;
import com.vdi.vo.res.ListDomainResponse;
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = { TestConfig.class })
public class DomainFacadTest {
	private @Autowired DomainFacad domainFacad;
	@Test
	public void testListDomains() {
		ListDomainResponse res=this.domainFacad.listDomains(new Domain());
		res.getBody().getList();
	}

	@Test
	public void testCreateDomain() throws Exception {
		Domain domain=new Domain();
		domain.setDomainbinddn("dc=cws,dc=com");
		domain.setAddress("20.2.100.110");
		domain.setPrincipal("cws\\administrator");
		domain.setDomainbindpass("123.com");
		domain.setAccesstype(636);
		this.domainFacad.createDomain(domain);
	}

	@Test
	public void testUpdateDomain() {
		fail("Not yet implemented");
	}

	@Test
	public void testDeleteDomain() {
		fail("Not yet implemented");
	}

	@Test
	public void testGetDomain() {
		fail("Not yet implemented");
	}

	@Test
	public void testSyncDomain() {
		fail("Not yet implemented");
	}

}
