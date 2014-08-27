package com.opzoon.appstatus.common.exception.custom;

import com.opzoon.appstatus.common.exception.AppstatusRestException;

/**
 * Appstatus 资源没有找到异常
 * 
 * @author david Date ：2013-11-08
 * @version V0.2.1023（迭代3）
 */
public class AppstatusResourceNoFoundException extends AppstatusRestException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 5236595007601934167L;

	@Override
	public String getMessage() {
		return "<=Appstatus=> Exception: " + super.getMessage() + "[Resource not found Exception]";
	}

}
