package com.opzoon.ohvc.domain;

import com.opzoon.ohvc.common.RailAppError;

public class Head {
	private Integer error;
	private String message;
	private Integer jobId;
	
	/**
	 * @return the jobId
	 */
	public Integer getJobId() {
		return jobId;
	}

	/**
	 * @param jobId the jobId to set
	 */
	public Head setJobId(Integer jobId) {
		this.jobId = jobId;
		return this;
	}

	/**
	 * @return the message
	 */
	public String getMessage() {
		return message;
	}

	/**
	 * @param message the message to set
	 */
	public Head setMessage(String message) {
		this.message = message;
		return this;
	}

	/**
	 * @return the error
	 */
	public Integer getError() {
		return error;
	}

	/**
	 * @param error the error to set
	 */
	public Head setError(Integer error) {
		this.error = error;
		return this;
	}
	public Head getHeadByError(RailAppError railAppError){
		this.setError(railAppError.getError());
		this.setMessage(railAppError.getMessage());
		return this;
	}
	
}
