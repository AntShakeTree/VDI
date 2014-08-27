package com.opzoon.vdi.core.controller.executor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.opzoon.vdi.core.controller.TaskInfo;
import com.opzoon.vdi.core.operations.OperationRegistry;

public class CreateDesktopPoolExecutor extends ExecutorBase {
  
  @Autowired
  private OperationRegistry operationRegistry;

	private static final Logger log = LoggerFactory.getLogger(CreateDesktopPoolExecutor.class);

	@Override
	public ExecuteResult execute(TaskInfo task) {
		log.info("--------------------I start execute task " + task.getId() + ", i am " + this.getClass());
		try {
			Thread.sleep(10000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		log.info("-------------------------------executing task " + task.getId());
		try {
			Thread.sleep(30000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		log.info("-------------------------------executing finish " + task.getId());
		ExecuteResult result = new ExecuteResult();
		result.setErrorCode(0);
		return result;
	}
	
}
