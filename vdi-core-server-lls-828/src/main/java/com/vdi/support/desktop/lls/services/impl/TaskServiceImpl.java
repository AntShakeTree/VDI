package com.vdi.support.desktop.lls.services.impl;


import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.vdi.common.ParseJSON;
import com.vdi.support.desktop.lls.domain.task.Task;
import com.vdi.support.desktop.lls.manager.LLSSendMessage;
import com.vdi.support.desktop.lls.services.TaskService;

/**
 * @author mxc
 * 
 */
@Service("TaskService")
public class TaskServiceImpl implements TaskService {
	static Logger log = LoggerFactory.getLogger(TaskServiceImpl.class);

	@Autowired
	@Qualifier("llsHandle")
	private LLSSendMessage llsSendMessage;

	public List<Task> listTask(Task task) {
		Task taskMessage = new Task();
		if (task == null) {
			taskMessage.setMapper(new Task());
		} else {
			taskMessage.setMapper(task);
		}
		taskMessage.setAction(Task.LIST_TASK_ACTION);
		task=llsSendMessage.sendMessage(taskMessage, Task.class);
		return 	ParseJSON.convertObjectToDomain(task.getContent(), Task.getTaskListType());
	}

	@Override
	public Task getTask(String TaskId) {
		Task task = new Task();
		task.setAction(Task.GET_TASK_ACTION);
		task.setTaskIdentity(TaskId);
		return llsSendMessage.sendMessage(task,Task.class);
	}

}
