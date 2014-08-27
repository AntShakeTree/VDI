package com.opzoon.vdi.core.controller;

import com.opzoon.appstatus.domain.Node;

public abstract class Controller {
	
	public abstract void registerClusterOpertator(ClusterOpertator operator);
	
	public abstract boolean sendTask(TaskInfo task);

	public abstract void notice(Node node);

	public abstract void noticeMessage(String message);
	
	public static final int TASK_STATUS_INIT = 0;
	public static final int TASK_STATUS_RECEIVED = 1;
	public static final int TASK_STATUS_EXECUTTING = 2;
	public static final int TASK_STATUS_FINISHED = 3;
	public static final int TASK_STATUS_ERROR = 256;
	
//	public static final int TASK_STATUS_TACKOVER = 11;
	
	public static final int ERROR_LENGTH_MAX = 200;
	public static final int MAX_TASK_MININTE = 5; //5分钟没有被接收的任务将进行重新发布。
	
	
}
