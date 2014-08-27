package com.opzoon.appstatus.common.exception.custom;

import com.opzoon.appstatus.common.exception.AppstatusRestException;

/**
 * Appstatus 请求参赛异常
 * @author david
 * Date ：2013-11-08
 * @version V0.2.1023（迭代3）
 */
public class AppstatusBadRequestException extends AppstatusRestException {



	/**
	 * 
	 */
	private static final long serialVersionUID = 8917331911657545306L;

	@Override
	public String getMessage() {
		return "<=Appstatus=> Exception: " + super.getMessage() + "[Bad request Exception]";
	}
	
}
