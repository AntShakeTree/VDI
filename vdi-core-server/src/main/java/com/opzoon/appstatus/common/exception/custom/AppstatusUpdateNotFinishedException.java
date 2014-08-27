package com.opzoon.appstatus.common.exception.custom;

import com.opzoon.appstatus.common.exception.AppstatusRestException;

public class AppstatusUpdateNotFinishedException extends AppstatusRestException {

	private static final long serialVersionUID = -7427233765501766386L;
	
	@Override
	public String getMessage() {
		return "<=Appstatus=> Exception: [update not finished Exception]";
	}

}
