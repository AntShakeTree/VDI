package com.opzoon.appstatus.common.exception.custom;

import com.opzoon.appstatus.common.exception.AppstatusRestException;

public class AppstatusAuthenticateException extends AppstatusRestException
{


	/**
	 * 缺省串行版本标识
	 */
	private static final long serialVersionUID = 1L;

	@Override
	public String getMessage()
	{
		return "<=Appstatus=> Exception: [AppStatus authenticate Exception]";
	}

}
