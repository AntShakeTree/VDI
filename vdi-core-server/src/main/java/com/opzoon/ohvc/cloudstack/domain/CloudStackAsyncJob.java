package com.opzoon.ohvc.cloudstack.domain;

import com.opzoon.ohvc.common.anotation.Required;
import com.opzoon.vdi.core.domain.BaseDomain;
/**
 * 
* @ClassName: CloudStackAsyncJob.java
* @Description: CloudStackAsyncJob.java 
* @author: tanyunhua 
* @date: 2012-9-17
* @version: V04
 */
public class CloudStackAsyncJob extends BaseDomain<CloudStackAsyncJob>{

	@Required
	private String command;
	@Required
	private String jobId;
	/**
	 * @return the command
	 */
	public String getCommand() {
		return command;
	}
	/**
	 * @param command the command to set
	 */
	public CloudStackAsyncJob setCommand(String command) {
		this.command = command;
		return this;
	}
	/**
	 * @return the jobId
	 */
	public String getJobId() {
		return jobId;
	}
	/**
	 * @param jobId the jobId to set
	 */
	public CloudStackAsyncJob setJobId(String jobId) {
		this.jobId = jobId;
		return this;
	}
	
}
