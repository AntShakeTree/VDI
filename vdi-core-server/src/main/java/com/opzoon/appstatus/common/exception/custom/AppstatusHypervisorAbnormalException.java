package com.opzoon.appstatus.common.exception.custom;

import com.opzoon.appstatus.common.exception.AppstatusRestException;

public class AppstatusHypervisorAbnormalException extends AppstatusRestException {

	private static final long serialVersionUID = 5180233202234500630L;

	@Override
	public String getMessage() {
		return "<=Appstatus=> Exception: " + super.getMessage() + "[Hypervisor Abnormal Exception]";
	}

}
