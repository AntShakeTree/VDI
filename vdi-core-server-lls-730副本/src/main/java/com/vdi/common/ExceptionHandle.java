package com.vdi.common;



/**
 * Appstatus 异常处理类
 * 
 * @author david Date ：2013-11-08
 * @version V0.2.1023（迭代3）
 */
public class ExceptionHandle {
	//~vdicore 
	public static Errors err = Errors.newInstance(2, 1);
	
	public static int changeCustomerExceptionCode(Exception e) {
		int errorcode;
		if (e instanceof NullPointerException
				|| e instanceof java.lang.ClassCastException
				|| e instanceof java.lang.ClassNotFoundException
				|| e instanceof java.lang.NoSuchFieldException
				|| e instanceof java.lang.IndexOutOfBoundsException) {
			errorcode= err.error(ErrorCode.VDI_NORMAL);
		} else if (e instanceof IllegalArgumentException) {
			errorcode = err.error(ErrorCode.BAD_REQ);
		} else {
			errorcode=err.info(ErrorCode.NO_ERRORS);
		}
		return errorcode;
	}
}
