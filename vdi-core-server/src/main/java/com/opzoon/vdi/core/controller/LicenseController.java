package com.opzoon.vdi.core.controller;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.opzoon.appstatus.domain.ClusterState;
import com.opzoon.vdi.core.controller.executor.StartLicenseExecutor;
import com.opzoon.vdi.core.util.LicenseUtil;

@Component("licenseController")
public class LicenseController implements ClusterOpertator{

	private static final Logger log = LoggerFactory.getLogger(LicenseController.class);
	@Autowired
	private Controller controller;
	
//	@PostConstruct
//	public void startLicense()
//	{
//		System.out.println(" controller" + controller);
//		controller.registerClusterOpertator(this);
//	}
	
	private void dealFingerPrint()
	{
		log.info("start vdi for get finger print");
		String fingerPrint = null;
		try {
			fingerPrint = LicenseUtil.getHardwareInfo();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			log.error("deal finger print error: ", e);
			return;
		}
		LicenseUtil.hardwareInfoSet.add(fingerPrint);
		
		LicenseUtil.hardwareInfoInFileSet = LicenseUtil.readHardwareInfo();
		LicenseUtil.hardwareInfoInFileSet.add(fingerPrint);
		
		TaskInfo task = new TaskInfo();
		task.setExecutorClass(StartLicenseExecutor.class);
		task.setPara3(fingerPrint);
		task.setType(1);
		log.info("my finger print is " + fingerPrint);
		controller.sendTask(task);
	}

	public void setController(Controller controller) {
		this.controller = controller;
		System.out.println("setController controller" + controller);
		controller.registerClusterOpertator(this);
	}
	
	@Test
	public void testTaskInfo() throws Exception
	{
		TaskInfo task = new TaskInfo();
		task.setId(3434);
		task.setExecutorClass(StartLicenseExecutor.class);
		task.setPara1("34sefsf");
		task.setPara3("sfs");
		task.setPara5("ssss");
		task.setType(1);
		task.setError("sdkfjskjf,sfkjsdkfj");
		System.out.println(task.toString());
		TaskInfo t = new TaskInfo(task.toString());
		System.out.println(t.getType());
		System.out.println(t.getError());
		System.out.println(t.getPara1());
		System.out.println(t.getPara3());
		System.out.println(t.getPara5());
	}

	@Override
	public void onClusterChange(ClusterState clusterStatus) {
		if(clusterStatus != ClusterState.CLUSTER)
			return;
		Thread t = new Thread(){
			@Override
			public void run() {
				try {
					Thread.sleep(5000);
					dealFingerPrint();
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		};
		t.start();
	}
	
}
