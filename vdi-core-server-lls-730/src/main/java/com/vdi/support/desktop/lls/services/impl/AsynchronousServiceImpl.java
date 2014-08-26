/**
 * Project Name:vdi-core-server-lls
 * File Name:AsynchronousServiceImpl.java
 * Package Name:com.vdi.support.desktop.lls.services.impl
 * Date:2014年8月11日下午3:50:03
 * Copyright (c) 2014 All Rights Reserved.
 *
*/

package com.vdi.support.desktop.lls.services.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.vdi.common.ParseJSON;
import com.vdi.support.desktop.lls.domain.task.Task;
import com.vdi.support.desktop.lls.manager.LLSExcutor;
import com.vdi.support.desktop.lls.manager.LLSSendMessage;
import com.vdi.support.desktop.lls.services.AsynchronousService;
@Service
public class AsynchronousServiceImpl implements AsynchronousService{
	private static Logger LOGGER = LoggerFactory
			.getLogger(AsynchronousVmInstanceServicesImpl.class);
	private @Autowired @Qualifier("llsHandle") LLSSendMessage sendMessage;
	private @Autowired LLSExcutor excutor;
	public  String excute(Object vmInstance) {
		Task task = sendMessage.sendMessage(vmInstance, Task.class);
		task = ParseJSON.convertObjectToDomain(task.getContent(), Task.class);
		LOGGER.info("task : {}", task.getTaskIdentity());
		String taskid=task.getTaskIdentity();
		excutor.addTaskExcutorQueue(task);
		return taskid;
	}
}
