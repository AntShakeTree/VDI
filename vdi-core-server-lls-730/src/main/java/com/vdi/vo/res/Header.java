/**
 * Project Name:vdi-core-server-lls
 * File Name:Header.java
 * Package Name:com.vdi.service.Response
 * Date:2014年8月11日下午2:38:15
 * Copyright (c) 2014 All Rights Reserved.
 *
*/

package com.vdi.vo.res;

import org.codehaus.jackson.map.annotate.JsonSerialize;
import org.codehaus.jackson.map.annotate.JsonSerialize.Inclusion;
@JsonSerialize(include=Inclusion.NON_NULL)
public class Header {
	private int error=0;
	private String message;
	private String jobid;
	private String jobstatus;
	public static final String SUCCESS="success";
	public static final String RUNNING="running";
	public static final String FAIL="error";
	public int getError() {
		return error;
	}
	public Header setError(int error) {
		this.error = error;
		return this;
	}
	public String getMessage() {
		return message;
	}
	public void setMessage(String message) {
		this.message = message;
	}
	public String getJobid() {
		return jobid;
	}
	public void setJobid(String jobid) {
		this.jobid = jobid;
	}
	public String getJobstatus() {
		return jobstatus;
	}
	public void setJobstatus(String jobstatus) {
		this.jobstatus = jobstatus;
	}
	
}
