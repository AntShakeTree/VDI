package com.opzoon.vdi.core.domain;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * 异步任务.
 */
@XmlRootElement(name = "asyncJob")
@Entity
public class AsyncJob implements Serializable {

	private static final long serialVersionUID = 1L;

	/**
	 * 任务状态代码: 任务仍在执行.
	 */
	public static final int ASYNC_JOB_STATUS_RUNNING = 0;
	/**
	 * 任务状态代码: 任务执行成功, jobresultcode为0.
	 */
	public static final int ASYNC_JOB_STATUS_SUCCESS = 1;
	/**
	 * 任务状态代码: 任务执行失败, jobresultcode返回错误代码.
	 */
	public static final int ASYNC_JOB_STATUS_FAILURE = 2;
	
	private Integer jobid;
	private String cmd;
	private Date createtime;
	private int jobstatus;
	private int jobprocstatus;
	private int jobresultcode;
	private String handle;
	private String jobresult;

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	public Integer getJobid() {
		return jobid;
	}
	public void setJobid(Integer jobid) {
		this.jobid = jobid;
	}
	/**
	 * @return 发起任务的命令.
	 */
	public String getCmd() {
		return cmd;
	}
	public void setCmd(String cmd) {
		this.cmd = cmd;
	}
	/**
	 * @return 发起任务的时间.
	 */
	public Date getCreatetime() {
		return createtime;
	}
	public void setCreatetime(Date createtime) {
		this.createtime = createtime;
	}
	/**
	 * @return 任务状态代码. 参考{@link AsyncJob#ASYNC_JOB_STATUS_RUNNING}, {@link AsyncJob#ASYNC_JOB_STATUS_SUCCESS}, {@link AsyncJob#ASYNC_JOB_STATUS_FAILURE}.
	 */
	public int getJobstatus() {
		return jobstatus;
	}
	public void setJobstatus(int jobstatus) {
		this.jobstatus = jobstatus;
	}
	/**
	 * @return 任务进度信息, 0~100
	 */
	public int getJobprocstatus() {
		return jobprocstatus;
	}
	public void setJobprocstatus(int jobprocstatus) {
		this.jobprocstatus = jobprocstatus;
	}
	/**
	 * @return 任务执行错误码. 0表示无错误.
	 */
	public int getJobresultcode() {
		return jobresultcode;
	}
	public void setJobresultcode(int jobresultcode) {
		this.jobresultcode = jobresultcode;
	}
	/**
	 * @return 执行任务的线程/对象.
	 */
	public String getHandle() {
		return handle;
	}
	public void setHandle(String handle) {
		this.handle = handle;
	}
	/**
	 * @return 任务结果.
	 */
	public String getJobresult() {
		return jobresult;
	}
	public void setJobresult(String jobresult) {
		this.jobresult = jobresult;
	}
	
}
