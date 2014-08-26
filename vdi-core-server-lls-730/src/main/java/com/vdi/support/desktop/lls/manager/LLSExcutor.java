package com.vdi.support.desktop.lls.manager;

import org.springframework.stereotype.Service;

import com.vdi.support.desktop.lls.domain.task.Task;

@Service
public interface LLSExcutor {
	public void addTaskExcutorQueue(Task task);
}
