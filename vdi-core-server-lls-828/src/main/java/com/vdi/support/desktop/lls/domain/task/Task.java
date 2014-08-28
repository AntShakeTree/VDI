package com.vdi.support.desktop.lls.domain.task;

import java.util.List;

import org.codehaus.jackson.map.annotate.JsonSerialize;
import org.codehaus.jackson.map.annotate.JsonSerialize.Inclusion;
import org.codehaus.jackson.type.TypeReference;

import com.vdi.support.desktop.lls.domain.BasicDomain;
@JsonSerialize(include=Inclusion.NON_DEFAULT)
public class Task extends BasicDomain {
	public static String GET_TASK_ACTION = "reqGetTask";
	public static String LIST_TASK_ACTION = "reqListTask";
	private String status;// ：task状态
	private String actionName;
	private String progress;
	private String action;
	private Task mapper;
	private String taskIdentity;
	private String createBy;
	private String originTaskIdentity;
	private String retryTaskIdentity;
	private Integer rollbackErrorCode;
	private String modifyTime;
	private String createTime;
	private Object params;
	
	public String getCreateTime() {
		return createTime;
	}

	public void setCreateTime(String createTime) {
		this.createTime = createTime;
	}

	public String getTaskIdentity() {
		return taskIdentity;
	}

	public Task setTaskIdentity(String taskIdentity) {
		this.taskIdentity = taskIdentity;
		return this;
	}

	public Task getMapper() {
		return mapper;
	}

	public void setMapper(Task mapper) {
		this.mapper = mapper;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getActionName() {
		return actionName;
	}

	public void setActionName(String actionName) {
		this.actionName = actionName;
	}

	public String getProgress() {
		return progress;
	}

	public void setProgress(String progress) {
		this.progress = progress;
	}

	public String getAction() {
		return action;
	}

	public void setAction(String action) {
		this.action = action;
	}

	public String getCreateBy() {
		return createBy;
	}

	public void setCreateBy(String createBy) {
		this.createBy = createBy;
	}

	public String getOriginTaskIdentity() {
		return originTaskIdentity;
	}

	public void setOriginTaskIdentity(String originTaskIdentity) {
		this.originTaskIdentity = originTaskIdentity;
	}

	public String getRetryTaskIdentity() {
		return retryTaskIdentity;
	}

	public void setRetryTaskIdentity(String retryTaskIdentity) {
		this.retryTaskIdentity = retryTaskIdentity;
	}

	public Integer getRollbackErrorCode() {
		return rollbackErrorCode;
	}

	public void setRollbackErrorCode(Integer rollbackErrorCode) {
		this.rollbackErrorCode = rollbackErrorCode;
	}

	public String getModifyTime() {
		return modifyTime;
	}

	public void setModifyTime(String modifyTime) {
		this.modifyTime = modifyTime;
	}

	public Object getParams() {
		return params;
	}

	public void setParams(Object params) {
		this.params = params;
	}

	public static TypeReference<List<Task>> getTaskListType() {
		// TODO Auto-generated method stub
		return new TypeReference<List<Task>>() {
		};
	}
	public boolean isTaskFinished(Task task) {
		String status = task.getStatus();
		return status.equalsIgnoreCase("successed")
				|| status.equalsIgnoreCase("rollbackSuccessed")
				|| status.equals("rollbackFailed");
	}
	public boolean isTaskSuccess(Task task) {
		String status = task.getStatus();
		return status.equalsIgnoreCase("successed");
	}

	@Override
	public String toString() {
		return "Task [status=" + status + ", actionName=" + actionName
				+ ", progress=" + progress + ", action=" + action + ", mapper="
				+ mapper + ", taskIdentity=" + taskIdentity + ", createBy="
				+ createBy + ", originTaskIdentity=" + originTaskIdentity
				+ ", retryTaskIdentity=" + retryTaskIdentity
				+ ", rollbackErrorCode=" + rollbackErrorCode + ", modifyTime="
				+ modifyTime + ", createTime=" + createTime + ", params="
				+ params + "]"+super.toString();
	}
	
}
