package com.opzoon.client.opzooncloudservice;

/*
 * Copyright reserved 2009 by roxinf Co. Ltd.     
 * test.opzoon.client.domain
 * Author：maxiaochao
 * Date：2012-9-26
 */

import java.util.Iterator;
import java.util.List;

import com.opzoon.ohvc.common.Job;
import com.opzoon.ohvc.common.JobStatus;
import com.opzoon.ohvc.driver.opzooncloud.OpzoonCloudDriver;
import com.opzoon.ohvc.request.HttpGetRequest;
import com.opzoon.ohvc.request.HttpPostRequest;
import com.opzoon.vdi.core.domain.Template;
import com.opzoon.vdi.core.domain.VMInstance;

/**
 * TestService Author：maxiaochao 2012-9-26 下午3:32:42
 */
public class TestService {
	// CloudStackServiceImp service = new CloudStackServiceImp();
	com.opzoon.vdi.core.cloud.CloudManager service;
	String vmId = "/opzoon/administrator/3e37307c-aae7-4203-8dfa-0c8d0b124560";
	// String username;
	// String password;
	// String vmId =
	// "/opzoon/administrator/17626312-5a02-4fa3-9c61-9c8c3a75a90b";
	String password;
	String username;
	// String password="opzoon";
	// String username="admin";
	String baseIp;

	// String baseIp = "http://192.188.46.5";

	// String baseIp="http://192.188.46.5";

	public static void main(String[] args) throws Exception {

		final TestService test = new TestService();
		test.initService(2);
		test.login();
		List<Template> ts = test.listTemplates();
		for (Template t : ts) {
			System.out.println(t.getTemplatename());
			System.out.println(t.getTemplateId());
		}
		test.cloneVM("/opzoon/administrator/568d875e-895e-4270-9a0e-23f2ccc17df1","mxc",false);
		System.out.println("==========listT=====/opzoon/administrator/fa0a895b-3255-4e7e-be71-2f5854058e0c=============");
		// test.rebootVM();
		// test.restartSystem();
		//

		// test.destroy("/opzoon/administrator/5652b1e2-e4cb-489d-8ae1-ad25ff5910d6");
		// test.cloneVM();
		// for (int i = 0; i < 10; i++) {
		// new Thread(new Runnable() {
		//
		// @Override
		// public void run() {
		// // TODO Auto-generated method stub
		// try {
		// test.cloneVM();
		// } catch (Exception e) {
		// // TODO Auto-generated catch block
		// e.printStackTrace();
		// }
		// }
		// }).start();
		// }

	}

	/**
	 * @throws Exception
	 * @Title: destroy
	 * @Description: TODO(这里用一句话描述这个方法的作用)
	 * @param @param templateId 设定文件
	 * @return void 返回类型
	 * @throws
	 */
	private void destroy(String templateId) throws Exception {
		// TODO Auto-generated method stub
		service.destroyVM(templateId);
	}

	private void cloneVM(String tempid,String label,boolean link) throws Exception {
		// TODO Auto-generated method stub
		Job job = service.cloneVM(tempid,
			label, link);

		while (true) {

			Thread.sleep(5000);
			// job.setId("123");
			service.queryJobStatus(job);
			System.out.println(job.getStatus());
			if (!job.getStatus().equals(JobStatus.RUNNING)) {
				break;
			}
		}
	}

	private void getRDPStauts() throws Exception {
		System.out.println(service.getRdpStatus(vmId));

	}

	private void restartSystem() throws Exception {
		// TODO Auto-generated method stub
		String vm = "/opzoon/administrator/849be3a6-cfa9-496e-a64c-1add06bbf053";
		// String ip = "20.2.10.5";

		Job job = service.restartSystem(vm);

		while (true) {
			try {
				Thread.sleep(10000);
				service.queryJobStatus(job);
				System.out.println("ID: " + job.getId() + ", status:"
						+ job.getStatus() + ", ip" + job.getIp());
				if (job.getStatus().equals(JobStatus.SUCCESSFUL)
						|| job.getStatus().equals(JobStatus.FAILED)) {

					break;
				}
			} catch (Exception e) {
				e.printStackTrace();
			}

		}
	}

	public void testSethostname() throws Exception {
		String ip = "20.1.22.30";
		String vm = "/opzoon/administrator/d5ee6b50-3979-492c-ad43-36b2c039763b";
		Job job = service.setHostname(vmId, "maxc", 2, null, ip, true);
		while (true) {
			try {
				Thread.sleep(10000);
				service.queryJobStatus(job);
				System.out.println("ID: " + job.getId() + ", status:"
						+ job.getStatus() + ", ip" + job.getIp());
				if (job.getStatus().equals(JobStatus.SUCCESSFUL)
						|| job.getStatus().equals(JobStatus.FAILED)) {
					break;

				}
			} catch (Exception e) {
				e.printStackTrace();
			}

		}
	}

	private void login2() throws Exception {
		// TODO Auto-generated method stub
		service.setBaseUrl(baseIp);

		service.login("aaa", "aaa", null);

	}

	public void initService(int type) {
		switch (type) {
		case 1:
			baseIp = "http://20.1.202.15";
			service = new OpzoonCloudDriver();
			username = "/opzoon/administrator";
			password = "Admin@opzoon.com";
			break;
		case 2:
			baseIp = "http://20.2.32.5";
			service = new OpzoonCloudDriver();
			username = "/opzoon/administrator";
			password = "Admin@opzoon.com";
			break;
		case 3:
			baseIp = "http://20.2.10.5";
			service = new OpzoonCloudDriver();
			username = "/opzoon/administrator";
			password = "Admin@opzoon.com";
			break;
		default:
			baseIp = "http://20.1.129.5";
			service = new OpzoonCloudDriver();
			// username = "/root/root/";
			// password = "opzooncloud";
			username = "opzoon/administrator";
			password = "Admin@opzoon.com";
			break;
		}
	}

	private void getVM(String id) throws Exception {
		// TODO Auto-generated method stub
		VMInstance instance = service.getVM(id);
		System.out.println(instance.getId());
	}

	public void startVM() throws Exception {

		Job job = service.startVM(vmId);

		// while (true) {
		//
		// service.queryJobStatus(job);
		// System.out.println(job.getStatus());
		// if (!job.getStatus().equals(JobStatus.RUNNING)) {
		// break;
		// }
		// Thread.sleep(1000);
		// }
	}

	public void stopVM() throws Exception {

		Job job = service.stopVM(vmId);
		while (true) {

			service.queryJobStatus(job);
			System.out.println(job.getStatus());
			if (!job.getStatus().equals(JobStatus.RUNNING)) {
				break;
			}
			Thread.sleep(1000);
		}
	}

	public void rebootVM() throws Exception {
		String vmid = "/opzoon/administrator/64a9b494-0ded-4bdd-8ff8-2f109b58379b";
		Job job = service.startVM(vmid);

		while (true) {

			service.queryJobStatus(job);
			System.out.println(job.getStatus());
			if (!job.getStatus().equals(JobStatus.RUNNING)) {
				break;
			}
			Thread.sleep(1000);
		}
		Job<?> job2 = service.rebootVM(vmid);
		while (true) {

			service.queryJobStatus(job2);
			System.out.println(job2.getStatus());
			if (!job2.getStatus().equals(JobStatus.RUNNING)) {
				break;
			}
			Thread.sleep(1000);
		}
	}

	public List<Template> listTemplates() throws Exception {
		System.out.println("=========listTemplates========");
		return service.listTemplates(true);

	}

	public void login() throws Exception {
		service.setBaseUrl(baseIp);

		service.login(username, password, null);

	}

	public static void testPares() {

	}

	public void cloneVm() throws Exception {
		Job job = service.cloneVM(vmId, "vdi_clone_xp", true);
		service.queryJobStatus(job);
		// while (true) {
		// service.queryJobStatus(job);
		// System.out.println(job.getStatus());
		// if (!job.getStatus().equals(JobStatus.RUNNING)) {
		// break;
		// }
		// Thread.sleep(1000);
		// }

	}

	public void testAgrent() throws Exception {
		String vmId = "/opzoon/administrator/1b8cadfa-f801-493e-83a4-56c5f6de38bb";
		VMInstance instance = service.getVM(vmId);
		System.out.println(instance.getIpaddress());
		// String baseUrl
		// ="http://"+instance.getIpaddress()+":58650/vdiagent/services/getRdpStatus";
		String baseUrl = "http://" + instance.getIpaddress()
				+ ":58650/vdiagent/services/restartSystem";
		// OpzooncloudHttpGetRequest get =new
		// OpzooncloudHttpGetRequest(baseUrl);
		// System.out.println(get.execute());
		HttpGetRequest get = HttpGetRequest.instanceByUrl(
				instance.getIpaddress(), baseUrl);
		// System.out.println(get.execute());
		HttpPostRequest post = HttpPostRequest.instanceByUrl(
				instance.getIpaddress(), baseUrl);
		System.out.println(post.execute() + "==================");
	}

	public void testAddstorage() throws Exception {
		String vmId = "/opzoon/administrator/b242321b-fbc9-4355-9181-39b59c2fe750";
		VMInstance instance = service.getVM(vmId);
		System.out.println(instance.getIpaddress());
		// String baseUrl
		// ="http://"+instance.getIpaddress()+":58650/vdiagent/services/getRdpStatus";
		String baseUrl = "http://" + instance.getIpaddress()
				+ ":58650/vdiagent/services/restartSystem";
		// OpzooncloudHttpGetRequest get =new
		// OpzooncloudHttpGetRequest(baseUrl);
		// System.out.println(get.execute());
		HttpGetRequest get = HttpGetRequest.instanceByUrl(
				instance.getIpaddress(), baseUrl);
		// System.out.println(get.execute());
		HttpPostRequest post = HttpPostRequest.instanceByUrl(
				instance.getIpaddress(), baseUrl);
		System.out.println(post.execute() + "==================");
	}

}
