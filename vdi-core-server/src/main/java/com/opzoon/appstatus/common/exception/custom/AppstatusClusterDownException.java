package com.opzoon.appstatus.common.exception.custom;

import com.opzoon.appstatus.common.exception.AppstatusRestException;

public class AppstatusClusterDownException extends AppstatusRestException{
	/**
	 * 缺省的串行版本标识
	 */
	private static final long serialVersionUID = -6003245453837323492L;
	



	@Override
	public String getMessage()
	{
		return "<=Appstatus=> Exception: " + super.getMessage() + "[database Exception]";
	}
}
