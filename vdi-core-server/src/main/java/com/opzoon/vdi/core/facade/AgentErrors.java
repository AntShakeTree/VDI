package com.opzoon.vdi.core.facade;

public abstract class AgentErrors {

	/**
	 * 请求已成功完成
	 */
	public static final int AERR_OK = 0;
	/**
	 * 请求失败（包含所有未知错误）
	 */
	public static final int AERR_FAIL = 0x80000001;
	/**
	 * 请求参数不正确
	 */
	public static final int AERR_PARAM = 0x80000002;
	/**
	 * 任务正在执行
	 */
	public static final int AERR_PENDING = 0x80010001;
	/**
	 * 找不到指定job
	 */
	public static final int AERR_NOT_FOUND_JOB = 0x80010002;
	/**
	 * 响应体内容为空
	 */
	public static final int AERR_NULL_RESPONSE = 0x80010003;
	/**
	 * 需要提供用户名
	 */
	public static final int AERR_NEED_USERNAME = 0x80010004;
	/**
	 * 需要提供密码
	 */
	public static final int AERR_NEED_PASSWORD = 0x80010005;
	/**
	 * 访问IP受限
	 */
	public static final int AERR_ACCESS_DENIED = 0x80020001;
	/**
	 * 主机名无效
	 */
	public static final int AERR_INVALID_COMPUTER = 0x80020002;
	/**
	 * 只能在该域的主域控制器上执行
	 */
	public static final int AERR_NOTPRIMARY = 0x80020003;
	/**
	 * 组已经存在
	 */
	public static final int AERR_GROUP_EXISTS = 0x80020004;
	/**
	 * 用户已经存在
	 */
	public static final int AERR_USER_EXISTS = 0x302;
	/**
	 * 密码不满足密码策略的要求
	 */
	public static final int AERR_PASSWORD_TOO_SHORT = 0x80020006;
	/**
	 * 指定的用户名无效
	 */
	public static final int AERR_BAD_USERNAME = 0x80020007;
	/**
	 * 密码参数无效
	 */
	public static final int AERR_BAD_PASSWORD = 0x80020008;
	/**
	 * 找不到用户名
	 */
	public static final int AERR_USER_NOT_FOUND = 0x80020009;
	/**
	 * 找不到组名
	 */
	public static final int AERR_GROUP_NOT_FOUND = 0x8002000a;
	/**
	 * 该成员不存在，因此不能将其添加到本地组中或从本地组删除
	 */
	public static final int AERR_NO_SUCH_MEMBER = 0x8002000b;
	/**
	 * 指定的帐户名已是此组的成员
	 */
	public static final int AERR_MEMBER_IN_ALIAS = 0x8002000c;
	/**
	 * 无法将新成员加入到本地组中，因为成员的帐户类型错误
	 */
	public static final int AERR_INVALID_MEMBER = 0x8002000d;
	/**
	 * 该操作不能在此特殊的组上执行
	 */
	public static final int AERR_SPEGROUP_OP = 0x8002000e;
	/**
	 * 该操作无法在最新的管理帐户上执行
	 */
	public static final int AERR_LAST_ADMIN = 0x8002000f;
	
	private AgentErrors() {}

}
