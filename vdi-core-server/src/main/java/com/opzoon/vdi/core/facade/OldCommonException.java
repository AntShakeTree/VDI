package com.opzoon.vdi.core.facade;

/**
 * 包含错误代码的异常.
 */
@SuppressWarnings("serial")
public class OldCommonException extends Exception {

	/**
	 * 无错误.
	 */
	public static final int NO_ERRORS = 0;
	/**
	 * 部分操作出现错误.
	 */
	public static final int MULTI_STATUS = 207;
	/**
	 * 请求格式或内容错误.
	 */
	public static final int BAD_REQUEST = 400;
	/**
	 * 认证失败.
	 */
	public static final int UNAUTHORIZED = 401;
	/**
	 * 权限不足.
	 */
	public static final int FORBIDDEN = 403;
	/**
	 * 找不到指定资源.
	 */
	public static final int NOT_FOUND = 404;
	/**
	 * 冲突.
	 */
	public static final int CONFLICT = 409;
	public static final int DRIVER_PASS = 410;
	public static final int DRIVER_UNTOUCHABLE = 411;
	public static final int HYPERVISOR_ABNORMAL = 412;
	public static final int LDAP_UNAUTHORIZED = 413;
	public static final int LDAP_ABNORMAL = 414;
	public static final int INVALID_PASSWORD = 415;
	public static final int HYPERVISOR_NO_ENOUGH_RESOURCES = 416;
	/**
	 * 未知错误.
	 */
	public static final int UNKNOWN = 500;
	/**
	 * 暂未实现.
	 */
	public static final int NOT_YET_IMPLEMENTED = 501;
	
	private int error;
	
	public OldCommonException (int error) {
		this.error = error;
	}

	public int getError() {
		return error;
	}

}
