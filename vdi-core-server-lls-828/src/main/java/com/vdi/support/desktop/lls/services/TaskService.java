package com.vdi.support.desktop.lls.services;

import java.util.List;

import org.springframework.stereotype.Service;

import com.vdi.support.desktop.lls.domain.task.Task;
@Service
public interface TaskService {
	Task getTask(String taskId);
	List<Task> listTask(Task task);
}
