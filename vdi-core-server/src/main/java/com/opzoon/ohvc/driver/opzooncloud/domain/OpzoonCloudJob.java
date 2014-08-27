package com.opzoon.ohvc.driver.opzooncloud.domain;

import com.opzoon.ohvc.common.Job;

public class OpzoonCloudJob<T> extends Job<T> {
	private String serviceState;

	public String getServiceState() {
		return serviceState;
	}

	public void setServiceState(String serviceState) {
		this.serviceState = serviceState;
	}
	
}
