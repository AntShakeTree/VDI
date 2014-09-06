package test.vdi.lls.unixsocket;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.type.TypeReference;
import org.junit.After;
import org.junit.Test;

import com.vdi.common.ParseJSON;
import com.vdi.support.desktop.lls.domain.resource.ComputePool;
import com.vdi.support.desktop.lls.domain.resource.CpuInfo;
import com.vdi.support.desktop.lls.domain.resource.Host;
import com.vdi.support.desktop.lls.domain.storage.Storage;
import com.vdi.support.desktop.lls.domain.task.Task;
import com.vdi.support.desktop.lls.domain.vms.VmInstance;
import com.vdi.support.desktop.lls.domain.vms.attributes.RunParams;
import com.vdi.support.desktop.lls.domain.vms.attributes.VDisk;
import com.vdi.support.desktop.lls.domain.vms.attributes.VNetcard;
import com.vdi.support.desktop.lls.manager.support.LLSLocalNIOHandle;

public class LLSNIOSorketClient {
	LLSLocalNIOHandle handle = new LLSLocalNIOHandle();



	@After
	public void teardown() {
		handle.close();
	}

	@Test
	public void testListHost() throws JsonGenerationException,
			JsonMappingException, IOException {
		Host host = new Host();
		host.setAction(Host.LIST_HOST_ACTION);
		host.setMapper(new Host());
		host = handle.sendMessage(host, Host.class);
		// ParseJSON.toJson(host.getContent());

		String cs = ParseJSON.toJson(host.getContent());
		System.out.println(cs);
		List<Host> hosts = ParseJSON.getJson().readValue(cs,
				new TypeReference<List<Host>>() {
				});
		for (Host host2 : hosts) {
			System.out.println(host2.getHostIdentity());
		}
		handle.close();
	}

	@Test
	public void reqGetComputePool() {
		ComputePool c = new ComputePool();
		c.setAction(ComputePool.GET_COMPUTEPOOL_ACTION);
		c.setComputePoolIdentity("e8349d1f6bc14b55a4492764136fb68f");
		c = handle.sendMessage(c, ComputePool.class);
		String cs = ParseJSON.toJson(c);
		System.out.println(cs);
		reqGetStorage();
	}

	@Test
	public void reqGetStorage() {
		Storage c = new Storage();
		c.setAction(Storage.GET_STORAGE_ACTION);
		c.setStorageIdentity("local@b784c4b854054706a44f91d9f174119f");
		c = handle.sendMessage(c, Storage.class);
		String cs = ParseJSON.toJson(c);
		System.out.println(cs);
	}

	@Test
	public void reqListStorage() {
		Storage c = new Storage();
		c.setAction(Storage.LIST_STORAGE_ACTION);
		c = handle.sendMessage(c, Storage.class);
		String cs = ParseJSON.toJson(c);
		System.out.println(cs);

	}

	@Test
	public void reqListVM() throws InterruptedException {
		VmInstance c = new VmInstance();
		c.setAction(VmInstance.LIST_VM_ACTION);
		c.setMapper(new VmInstance());
		c = handle.sendMessage(c, VmInstance.class);
		String cs = ParseJSON.toJson(c);
		System.out.println(cs);
		Thread.sleep(10000);
		reqListStorage();
	}

	@Test
	public void reqCreateVM() {
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
		System.out.println(ParseJSON.toJson(c));
		c = handle.sendMessage(c, VmInstance.class);
		String cs = ParseJSON.toJson(c);
		System.out.println(cs);
	}

	// @Test
	// public void reqGetStorage() {
	// Storage c = new Storage();
	// c.setAction(Storage.GET_STORAGE_ACTION);
	// c.setStorageIdentity("local@b784c4b854054706a44f91d9f174119f");
	// c = handle.sendMessage(c, Storage.class);
	// String cs = ParseJSON.toJson(c);
	// System.out.println(cs);
	// }
	@Test
	public void queryJob() {
		Task c = new Task();
		c.setAction(Task.GET_TASK_ACTION);
		c.setTaskIdentity("6acb11c40c5c11e4ad3d8084f439f58a");
		c = handle.sendMessage(c, Task.class);
		String cs = ParseJSON.toJson(c);
		System.out.println(cs);
	}

	@Test
	public void generalTask() throws JsonParseException, JsonMappingException,
			IOException {
		ConfigUtil.loadConfigFileByPath("/test.properties");
		String task = ConfigUtil.getKey("task");
		System.out.println(task);
		HashMap<String, Object> map = ParseJSON.getJson().readValue(task,
				new TypeReference<HashMap<String, Object>>() {});
		for (String key : map.keySet()) {
			System.out.println(key + "===============================");
			Object object = map.get("content");
			Task task2=	ParseJSON.getJson().convertValue(object, Task.class);
			System.out.println(task2);
		}
	}
	@Test
	public void generalCpuInfoArray(){
		ConfigUtil.loadConfigFileByPath("/test.properties");
		String task = ConfigUtil.getKey("array");
		List<CpuInfo> task2=	ParseJSON.getJson().convertValue(task, new TypeReference<List<CpuInfo>>() {
		});

	}
	@Test public void listVMS(){
		VmInstance v =new VmInstance();
		v.setAction(VmInstance.LIST_VM_ACTION);
		v.setMapper(new VmInstance());
		v =handle.sendMessage(v, VmInstance.class);
		System.out.println(ParseJSON.toJson(v));
	}
}
