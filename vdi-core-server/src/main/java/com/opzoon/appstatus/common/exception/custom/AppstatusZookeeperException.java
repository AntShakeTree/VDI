package com.opzoon.appstatus.common.exception.custom;

import com.opzoon.appstatus.common.exception.AppstatusRestException;

public class AppstatusZookeeperException extends AppstatusRestException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 4885259909206325466L;

	@Override
	public String getMessage() {
		return "<=Appstatus=> Exception: [zookeeper action failure Exception]";
	}

}
