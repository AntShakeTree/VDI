package com.vdi.lls.services;

import static org.junit.Assert.fail;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import test.config.TestConfig;

import com.vdi.common.ParseJSON;
import com.vdi.support.desktop.lls.domain.resource.CpuInfo;
import com.vdi.support.desktop.lls.domain.vms.VmInstance;
import com.vdi.support.desktop.lls.domain.vms.attributes.RunParams;
import com.vdi.support.desktop.lls.domain.vms.attributes.VDisk;
import com.vdi.support.desktop.lls.domain.vms.attributes.VNetcard;
import com.vdi.support.desktop.lls.manager.support.VDIQueue;
import com.vdi.support.desktop.lls.services.AsynchronousVmInstanceServices;
import com.vdi.support.desktop.lls.services.TaskService;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = { TestConfig.class })
public class AsynchronousVmInstanceServicesTest {
	@Autowired
	AsynchronousVmInstanceServices asynchronousVmInstanceServices;
	@Autowired
	VDIQueue queue;
	@Autowired
	TaskService taskService;
//	@Autowired LLSJobService jobService;

	@Before
	public void setUp() throws Exception {

	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testCreateVM() {
		VmInstance c = new VmInstance();
		c.setAction(VmInstance.CREATE_VM_ACTION);
		c.setStorageIdentity("local@b784c4b854054706a44f91d9f174119f");
		c.setVmName("maxiaochao" + UUID.randomUUID());
		c.setMemorySize(1024);
		CpuInfo cpuinfo = new CpuInfo();
		cpuinfo.setCoreNum(2);
		c.setvCpu(cpuinfo);
		List<VNetcard> vnetcards = new ArrayList<>();
		VNetcard e = new VNetcard();
		e.setIsDhcp(true);
		vnetcards.add(e);
		c.setvNetcards(vnetcards);
		List<VDisk> vmVDisks = new ArrayList<>();
		VDisk vmdisk = new VDisk();
		vmdisk.setSize(20);
		vmVDisks.add(vmdisk);
		c.setVmVDisks(vmVDisks);
		c.setOs("win7");
		RunParams defaultRunParams = new RunParams();
		List<String> views = new ArrayList<>();
		views.add("spice");
		defaultRunParams.setBootSeq("c");
		defaultRunParams.setViews(views);
		c.setDefaultRunParams(defaultRunParams);
		asynchronousVmInstanceServices.createVM(c);
		// System.out.println();
		try {
			Thread.sleep(10000);
		} catch (InterruptedException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		queue.sendSchdulTask();
	}

	@Test
	public void testDeleteVM() {
		asynchronousVmInstanceServices
				.deleteVM("8605adce653845b193968a1908ff0f3c");
		// asynchronousVmInstanceServices.deleteVM("maxiaochao33fadcac-6a70-4680-822a-30a6e5c1af49");
		queue.sendSchdulTask();
	}

	@Test
	public void testUpdateVM() {
		fail("Not yet implemented");
	}

	@Test
	public void testStartVM() {
		VmInstance vm  =new VmInstance();
		vm.setVmIdentity("f5d9abdd182d4a839a5bcd88e11ab493");
		vm.setComputePoolIdentity("e8349d1f6bc14b55a4492764136fb68f");
		String taskid =	this.asynchronousVmInstanceServices.startVM(vm);
		this.queue.sendSchdulTask();
//		System.out.println(ParseJSON.toJson(jobService.queryJob(taskid)));
	}

	@Test
	public void testStopVm() {
		// this.asynchronousVmInstanceServices.startVM("f5d9abdd182d4a839a5bcd88e11ab493");
		String taskid = this.asynchronousVmInstanceServices
				.stopVm("f5d9abdd182d4a839a5bcd88e11ab493");
		System.out.println(taskid+"=======================");
		this.queue.sendSchdulTask();
//		System.out.println(ParseJSON.toJson(jobService.queryJob(taskid)));
	}

	@Test
	public void testSystemStopVM() {
		fail("Not yet implemented");
	}

	@Test
	public void testTimeoutSystemStopVM() {
		fail("Not yet implemented");
	}

	@Test
	public void testRestartVM() {
		System.out.println(UUID.fromString("aaaaa"));
	}

	@Test
	public void testSuspendVM() {
		fail("Not yet implemented");
	}
}
