package com.opzoon.vdi.core.facade;

/**
 * 包含错误代码的异常.
 */
@SuppressWarnings("serial")
public class NewCommonException extends Exception {

	/**
	 * 无错误.
	 */
	public static final int NO_ERRORS = 0;
	/**
	 * 部分操作出现错误.
	 */
	public static final int MULTI_STATUS = 0x8000010b;
	/**
	 * 请求格式或内容错误.
	 */
	public static final int BAD_REQUEST = 0x80000002;
	/**
	 * 认证失败.
	 */
	public static final int UNAUTHORIZED = 0x80000101;
	/**
	 * 权限不足.
	 */
	public static final int FORBIDDEN = 0x80000105;
	/**
	 * 找不到指定资源.
	 */
	public static final int NOT_FOUND = 0x80000109;
	/**
	 * 冲突.
	 */
	public static final int CONFLICT = 0x8000010a;
	public static final int DRIVER_PASS = 0x80000101;
	public static final int DRIVER_UNTOUCHABLE = 0x80000102;
	public static final int HYPERVISOR_ABNORMAL = 0x8000010d;
	public static final int LDAP_UNAUTHORIZED = 0x80000101;
	public static final int LDAP_ABNORMAL = 0x80000102;
	public static final int INVALID_PASSWORD = 415;
	public static final int HYPERVISOR_NO_ENOUGH_RESOURCES = 0x8000010c;
	/**
	 * 未知错误.
	 */
	public static final int UNKNOWN = 0x80000001;
	/**
	 * 暂未实现.
	 */
	public static final int NOT_YET_IMPLEMENTED = 501;
	
	private int error;
	
	public NewCommonException (int error) {
		this.error = error;
	}

	public int getError() {
		return error;
	}

}
