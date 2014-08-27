package com.opzoon.vdi.core.controller;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "task")
public class TaskEntity implements Serializable
{
	/*
	 * 	CREATE TABLE `task` (
				  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT 'ID',
				  `executorClass` VARCHAR(1024) NOT NULL COMMENT '执行类全名',
				  `status` int(11) NOT NULL COMMENT '任务状态. 0：初始化; 1：有服务器接收; 2：正在执行; 3：执行完成; 256：执行发生错误',
				  `executeStatus` int(11) DEFAULT NULL COMMENT '任务执行状态. 由执行类自定义',
				  `executor` char(127) DEFAULT NULL COMMENT '任务接收者',
				  `sender` char(127) DEFAULT NULL COMMENT '任务发出者',
				  `para1` VARCHAR(1024) DEFAULT NULL COMMENT '参数1',
				  `para2` VARCHAR(1024) DEFAULT NULL COMMENT '参数2',
				  `para3` VARCHAR(1024) DEFAULT NULL COMMENT '参数3',
				  `para4` VARCHAR(1024) DEFAULT NULL COMMENT '参数4',
				  `para5` VARCHAR(1024) DEFAULT NULL COMMENT '参数5',
				  `error` VARCHAR(1024) DEFAULT NULL COMMENT '错误信息',
				  `createTime` datetime DEFAULT NULL COMMENT '发起任务的时间', 
				  PRIMARY KEY (`id`)
				) ENGINE=MyISAM AUTO_INCREMENT=1 DEFAULT CHARSET=utf8;
				*/
	private static final long serialVersionUID = 1L;

	private Integer id;
	private String executorClass;
	private Integer status = Controller.TASK_STATUS_INIT;
	private Integer executeStatus;
	private String executor;
	private String sender;
	private String para1;
	private String para2;
	private String para3;
	private String para4;
	private String para5;
	private String error;
	private Date createTime;

	public TaskEntity()
	{
		super();
	}

	public TaskEntity(TaskInfo taskInfo)
	{
		this.id = taskInfo.getId();
		this.executorClass = taskInfo.getExecutorClass().getName();
		this.status = taskInfo.getStatus();
		this.executeStatus = taskInfo.getExecuteStatus();
		this.executor = taskInfo.getExecutor();
		this.sender = taskInfo.getSender();
		this.para1 = taskInfo.getPara1();
		this.para2 = taskInfo.getPara2();
		this.para3 = taskInfo.getPara3();
		this.para4 = taskInfo.getPara4();
		this.para5 = taskInfo.getPara5();
		this.error = taskInfo.getError();
	}

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	public Integer getId()
	{
		return id;
	}

	public void setId(Integer id)
	{
		this.id = id;
	}

	public String getExecutorClass()
	{
		return executorClass;
	}

	public void setExecutorClass(String executorClass)
	{
		this.executorClass = executorClass;
	}

	public Integer getStatus()
	{
		return status;
	}

	public void setStatus(Integer status)
	{
		this.status = status;
	}

	public Integer getExecuteStatus()
	{
		return executeStatus;
	}

	public void setExecuteStatus(Integer executeStatus)
	{
		this.executeStatus = executeStatus;
	}

	public String getExecutor()
	{
		return executor;
	}

	public void setExecutor(String executor)
	{
		this.executor = executor;
	}

	public String getSender()
	{
		return sender;
	}

	public void setSender(String sender)
	{
		this.sender = sender;
	}


	public String getPara3()
	{
		return para3;
	}

	public void setPara3(String para3)
	{
		this.para3 = para3;
	}

	public String getPara4()
	{
		return para4;
	}

	public void setPara4(String para4)
	{
		this.para4 = para4;
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

	public String getError()
	{
		return error;
	}

	public void setError(String error)
	{
		this.error = error;
	}

	public Date getCreateTime() {
		return createTime;
	}

	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}
	
}
