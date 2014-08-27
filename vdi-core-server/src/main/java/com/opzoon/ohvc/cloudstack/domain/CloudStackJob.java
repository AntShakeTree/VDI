package com.opzoon.ohvc.cloudstack.domain;



/**
 * @author tanyunhua
 * @version V04 2012-9-17
 */
public class CloudStackJob {

	private String jobid;
	private Integer jobstatus;
	private Integer jobresultcode;
	private String cmd;
	private String jobresulttype;
	private Object jobresult;
	/**
	 * @return the jobid
	 */
	public String getJobid() {
		return jobid;
	}
	/**
	 * @param jobid the jobid to set
	 */
	public void setJobid(String jobid) {
		this.jobid = jobid;
	}
	/**
	 * @return the jobstatus
	 */
	public Integer getJobstatus() {
		return jobstatus;
	}
	/**
	 * @param jobstatus the jobstatus to set
	 */
	public void setJobstatus(Integer jobstatus) {
		this.jobstatus = jobstatus;
	}
	/**
	 * @return the jobresultcode
	 */
	public Integer getJobresultcode() {
		return jobresultcode;
	}
	/**
	 * @param jobresultcode the jobresultcode to set
	 */
	public void setJobresultcode(Integer jobresultcode) {
		this.jobresultcode = jobresultcode;
	}
	/**
	 * @return the cmd
	 */
	public String getCmd() {
		return cmd;
	}
	/**
	 * @param cmd the cmd to set
	 */
	public void setCmd(String cmd) {
		this.cmd = cmd;
	}
//	/**
//	 * @return the jobresult
//	 */
//	public String getJobresult() {
//		return jobresult;
//	}
//	/**
//	 * @param jobresult the jobresult to set
//	 */
//	public void setJobresult(String jobresult) {
//		this.jobresult = jobresult;
//	}
	/**
	 * @return the jobresulttype
	 */
	public String getJobresulttype() {
		return jobresulttype;
	}
	/**
	 * @param jobresulttype the jobresulttype to set
	 */
	public void setJobresulttype(String jobresulttype) {
		this.jobresulttype = jobresulttype;
	}
	/**
	 * @return the jobresult
	 */
	public Object getJobresult() {
		return jobresult;
	}
	/**
	 * @param jobresult the jobresult to set
	 */
	public void setJobresult(Object jobresult) {
		this.jobresult = jobresult;
	}
	
}
