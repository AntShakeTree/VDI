package com.opzoon.appstatus.common.exception.custom;

import com.opzoon.appstatus.common.exception.AppstatusRestException;

public class AppstatusForbidAddClusterException extends AppstatusRestException {

	private static final long serialVersionUID = -4171019105611860335L;
	
	@Override
	public String getMessage() {
		return "<=Appstatus=> Exception: [forbid this host add cluster]";
	}

}
