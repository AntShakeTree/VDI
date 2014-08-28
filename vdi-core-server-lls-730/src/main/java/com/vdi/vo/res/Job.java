package com.vdi.vo.res;

import org.codehaus.jackson.map.annotate.JsonSerialize;
import org.codehaus.jackson.map.annotate.JsonSerialize.Inclusion;

/**
 * 
 * @author ant_shake_tree
 *
 */
@JsonSerialize(include=Inclusion.NON_NULL)
public class Job {
	private String jobid;
	private String progress;
	private int error=0;
	private String status;
	public static final String SUCCESS="success";
	public static final String RUNNING="running";
	public static final String FAIL="error";
	public String getJobid() {
		return jobid;
	}
	public void setJobid(String jobid) {
		this.jobid = jobid;
	}
	public String getProgress() {
		return progress;
	}
	public void setProgress(String progress) {
		this.progress = progress;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public int getError() {
		return error;
	}
	public void setError(int error) {
		this.error = error;
	}
	
}
