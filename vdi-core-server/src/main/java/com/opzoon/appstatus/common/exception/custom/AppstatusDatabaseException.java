package com.opzoon.appstatus.common.exception.custom;

import com.opzoon.appstatus.common.exception.AppstatusRestException;

/**
 * Appstatus 数据库异常
 * 
 * @author david Date ：2013-11-08
 * @version V0.2.1023（迭代3）
 */
public class AppstatusDatabaseException extends AppstatusRestException
{




	/**
	 * 缺省的串行版本标识
	 */
	private static final long serialVersionUID = -6003245453837323499L;
	



	@Override
	public String getMessage()
	{
		return "<=Appstatus=> Exception: " + super.getMessage() + "[database Exception]";
	}

}
