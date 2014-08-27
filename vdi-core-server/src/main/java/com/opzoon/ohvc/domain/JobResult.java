package com.opzoon.ohvc.domain;

public class JobResult {
	
	private Object jobresult;
	private String cmd;
	private int jobstatus;
	private int jobprocstatus;
	private int jobresultcode;
	public Object getJobresult() {
		return jobresult;
	}
	public void setJobresult(Object jobresult) {
		this.jobresult = jobresult;
	}
	public String getCmd() {
		return cmd;
	}
	public void setCmd(String cmd) {
		this.cmd = cmd;
	}
	public int getJobstatus() {
		return jobstatus;
	}
	public void setJobstatus(int jobstatus) {
		this.jobstatus = jobstatus;
	}
	public int getJobprocstatus() {
		return jobprocstatus;
	}
	public void setJobprocstatus(int jobprocstatus) {
		this.jobprocstatus = jobprocstatus;
	}
	public int getJobresultcode() {
		return jobresultcode;
	}
	public void setJobresultcode(int jobresultcode) {
		this.jobresultcode = jobresultcode;
	}
	
}
