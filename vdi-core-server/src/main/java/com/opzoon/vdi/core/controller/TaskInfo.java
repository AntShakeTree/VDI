package com.opzoon.vdi.core.controller;

import java.io.Serializable;

import com.opzoon.vdi.core.controller.executor.ExecutorBase;

public class TaskInfo implements Serializable{

	private static final long serialVersionUID = 1L;
	
	private Integer id;
	private Class<? extends ExecutorBase> executorClass;
	private Integer status = Controller.TASK_STATUS_INIT;
	private Integer executeStatus;
	private String executor;
	private String sender;
	private String para1;
	private String para2;
	private String para3;
	private String para4;
	private String para5;
	private boolean takeOver = false;
	private String error;
	private Integer type = 0; //0: one; //1: all
	
	public TaskInfo()
	{
	}

	public TaskInfo(String taskInfos) throws Exception {
		String[] strs = taskInfos.split(",");
		if (strs == null || strs.length < 12 || strs[1] == null) {
			throw new Exception("not a task");
		}
		this.id = Integer.parseInt(strs[0]);
		executorClass = (Class<? extends ExecutorBase>) Class.forName(strs[1]);

		if (!strs[2].equals("null"))
			this.status = Integer.parseInt(strs[2]);
		if (!strs[3].equals("null"))
			this.executeStatus = Integer.parseInt(strs[3]);
		if (!strs[4].equals("null"))
			this.executor = strs[4];
		if (!strs[5].equals("null"))
			this.sender = strs[5];

		if (!strs[6].equals("null"))
			this.para1 = strs[6];
		if (!strs[7].equals("null"))
			this.para2 = strs[7];
		if (!strs[8].equals("null"))
			this.para3 = strs[8];
		if (!strs[9].equals("null"))
			this.para4 = strs[9];
		if (!strs[10].equals("null"))
			this.para5 = strs[10];
		if (strs[11].equals("true"))
			this.takeOver = true;
		if (!strs[12].equals("null"))
			this.type = Integer.parseInt(strs[12]);
		if (!strs[13].equals("null"))
		{
			this.error = "";
			for(int i = 13; i < strs.length; i++)
			{
				if(i > 13)
					this.error += ",";
				this.error += strs[i];
			}
		}
	}
	
	public TaskInfo(TaskEntity task) throws Exception
	{
		this.id = task.getId();
		this.executorClass = (Class<? extends ExecutorBase>) Class.forName(task.getExecutorClass());
		this.status = task.getStatus();
		this.executeStatus = task.getExecuteStatus();
		this.executor = task.getExecutor();
		this.sender = task.getSender();
		this.para1 = task.getPara1();
		this.para2 = task.getPara2();
		this.para3 = task.getPara3();
		this.para4 = task.getPara4();
		this.para5 = task.getPara5();
		this.error = task.getError();
	}

	public Integer getId() {
		return id;
	}

	/**
	 * @param id
	 *            the id to set
	 */
	public void setId(Integer id) {
		this.id = id;
	}

	public Class<? extends ExecutorBase> getExecutorClass() {
		return executorClass;
	}

	public void setExecutorClass(Class<? extends ExecutorBase> executorClass) {
		this.executorClass = executorClass;
	}

	public Integer getStatus() {
		return status;
	}

	/**
	 * @param status
	 *            the status to set
	 */
	public void setStatus(Integer status) {
		this.status = status;
	}

	/**
	 * @return the executeStatus
	 */
	public Integer getExecuteStatus() {
		return executeStatus;
	}

	/**
	 * @param executeStatus
	 *            the executeStatus to set
	 */
	public void setExecuteStatus(Integer executeStatus) {
		this.executeStatus = executeStatus;
	}

	/**
	 * @return the executor
	 */
	public String getExecutor() {
		return executor;
	}

	/**
	 * @param executor
	 *            the executor to set
	 */
	public void setExecutor(String executor) {
		this.executor = executor;
	}

	/**
	 * @return the sender
	 */
	public String getSender() {
		return sender;
	}

	/**
	 * @param sender
	 *            the sender to set
	 */
	public void setSender(String sender) {
		this.sender = sender;
	}


	/**
	 * @return the para3
	 */
	public String getPara3() {
		return para3;
	}

	/**
	 * @param para3
	 *            the para3 to set
	 */
	public void setPara3(String para3) {
		this.para3 = para3;
	}

	/**
	 * @return the para4
	 */
	public String getPara4() {
		return para4;
	}

	public boolean isTakeOver() {
		return takeOver;
	}

	public void setTakeOver(boolean takeOver) {
		this.takeOver = takeOver;
	}

	public String getPara1() {
		return para1;
	}

	public void setPara1(String para1) {
		this.para1 = para1;
	}

	public String getPara2() {
		return para2;
	}

	public void setPara2(String para2) {
		this.para2 = para2;
	}

	public String getPara5() {
		return para5;
	}

	public void setPara5(String para5) {
		this.para5 = para5;
	}

	/**
	 * @param para4
	 *            the para4 to set
	 */
	public void setPara4(String para4) {
		this.para4 = para4;
	}

	public String getError() {
		return error;
	}

	public void setError(String error) {
		this.error = error;
	}

	public Integer getType() {
		return type;
	}

	public void setType(Integer type) {
		this.type = type;
	}

	public String toString() {
		String str = String.format("%d,%s", id, executorClass.getName());
		str += String.format(",%d,%d,%s,%s", status, executeStatus, executor,
				sender);
		str += String.format(",%s,%s,%s,%s,%s", para1, para2, para3, para4, para5);
		str += String.format(",%s,%d,", this.takeOver, this.type);
		str += String.format("%s", this.error);
		return str;
	}
}