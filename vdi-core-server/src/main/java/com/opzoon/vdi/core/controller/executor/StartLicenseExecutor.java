package com.opzoon.vdi.core.controller.executor;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.FileCopyUtils;

import com.opzoon.vdi.core.controller.Controller;
import com.opzoon.vdi.core.controller.TaskInfo;
import com.opzoon.vdi.core.facade.TaskFacade;
import com.opzoon.vdi.core.util.LicenseUtil;

public class StartLicenseExecutor extends ExecutorBase{

	private static final Logger log = LoggerFactory.getLogger(StartLicenseExecutor.class);

	@Autowired
	private Controller controller;

	@Autowired
	private TaskFacade taskFacade;
	
	@Override
	public ExecuteResult execute(TaskInfo task) {
		// TODO Auto-generated method stub
		log.info("receive fingerpring " + task.getPara3() + "  " + taskFacade);
		if(task.getPara3() != null && !task.getPara3().isEmpty())
		{
			LicenseUtil.hardwareInfoSet.add(task.getPara3());
			LicenseUtil.hardwareInfoInFileSet.add(task.getPara3());
			//save set into file
			String hwFileContent = LicenseUtil.getAllFileHardwareInfo();;
			try {
				Writer out = new OutputStreamWriter(new FileOutputStream(LicenseUtil.HARDWARE_FILE_PATH));
				FileCopyUtils.copy(hwFileContent, out);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			try {
				Thread.sleep(3000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			String hwStr = LicenseUtil.getAllHardwareInfo();
			TaskInfo taskInfo = new TaskInfo();
			taskInfo.setExecutorClass(FingerPrintExecutor.class);
			taskInfo.setPara3(hwStr);
			log.info("my finger print is " + hwStr);
			controller.sendTask(taskInfo);
		}
		
		ExecuteResult result = new ExecuteResult();
		result.setErrorCode(0);
		return result;
	}

}
