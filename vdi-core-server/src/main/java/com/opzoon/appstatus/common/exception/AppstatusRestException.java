package com.opzoon.appstatus.common.exception;


/**
 * Appstatus 异常类
 * 
 * @author david Date ：2013-11-08
 * @version V0.2.1023（迭代3）
 */
public class AppstatusRestException extends RuntimeException {
	public AppstatusRestException() {
	}

	public AppstatusRestException(String arg0, Throwable arg1) {
		super(arg0, arg1);
	}

	public AppstatusRestException(String arg0) {
		super(arg0);
	}

	public AppstatusRestException(Throwable arg0) {
		super(arg0);

	}

	private static final long serialVersionUID = -105239594247548881L;

	private int code;

	public int getErrorCode() {
		return code;
	}

	public void setErrorCode(int code) {
		this.code = code;
	}
}
