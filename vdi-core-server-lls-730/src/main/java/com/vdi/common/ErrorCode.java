package com.vdi.common;

public class ErrorCode {
	public static final int BAD_REQ = 0x101;

	public static final int NO_ERRORS = 0;

	public static final int MULTI_STATUS = 0x101;

	public static final int BAD_REQUEST = 0x002;

	public static final int UNAUTHORIZED = 0x102;

	public static final int FORBIDDEN = 0x103;

	public static final int NOT_FOUND = 0x006;

	public static final int CONFLICT = 0x104;
	public static final int DRIVER_PASS = 0x105;
	public static final int DRIVER_UNTOUCHABLE = 0x106;
	public static final int HYPERVISOR_ABNORMAL = 0x107;
	public static final int LDAP_UNAUTHORIZED = 0x10A;
	public static final int LDAP_ABNORMAL = 0x10B;
	public static final int LDAP_OU_NOT_FOUND = 0x10C;
	public static final int INVALID_PASSWORD = 0x005;
	public static final int HYPERVISOR_NO_ENOUGH_RESOURCES = 0x108;
	public static final int UNKNOWN = 0x001;
	public static final int NOT_YET_IMPLEMENTED = 501;
	public static final int ORIGINAL_PASSWORD_INVALID = 0x109;
	public static final int LICENSE_NO_PUBLIC_KEY = 0x10A;
	public static final int PASSWORD_RESET_NEEDED = 0x115;
	public static final int LICENSE_FINGER_ERROR = 0x10B;
	public static final int LICENSE_ENCRYPT_ERROR = 0x10C;
	public static final int CONNECTION_FULL = 0x10D;
	public static final int LICENSE_DECRYPT_ERROR = 0x10E;
	public static final int LICENSE_DUPLICATE = 0x10F;
	// add by zhanglu 2014-07-01 start
	// 集群时获取不到指纹
	public static final int LICENSE_CLUSTER_TYPE = 0x11A;
	// license过期
	public static final int ESTABLISH_CONNECTION_ERR = 0x11B;
	// 未配置License Server IP
	public static final int LICENSE_SERVER_ERR = 0x11C;
	// 未配置License Server 授权失败
	public static final int LICENSE_SERVER_DOFIANL_ERR = 0x11D;
	// 获取Guid失败
	public static final int UKEY_GUID_ERR = 0x11E;
	// 已经存在有效的license
	public static final int LICENSE_MORE_ERR = 0x11F;
	// add by zhanglu 2014-07-01 end

	public static final int MAX_GROUP = 0x110;

	public static final int PROVISIONING = 0x111;
	public static final int POOL_MAINTAINING = 0x112;
	public static final int DUPE_IP = 0x113;
	public static final int RSA_REQ = 0x114;
	public static final int VDI_NORMAL=0x507;
	
	public static final int NOT_IN_CLUSTER = 0x121;
	public static final int INVALID_SESSION_BASE = 0x150;
	public static final int INVALID_PASSWOED_POLICY = 0x303;
	public static final int INVALID_USERNAME = 0x304;
	public static final int INVALID_AUTACATION_POLICY = 0x401;
	public static final int ORGANIZATION_NULL = 0x701;
	public static final int LDAP_NULL = 0x702;

	/********************* appstatus错误码 ***********************/
	// 数据库连接异常
	public final static int DATABASE_UNTOUCHABLE = 0xe2727102;
	// 操作zookeeper异常
	public final static int ZOOKEEPER_ACTION_FAIL = 0xe2727103;
	// 更新操作未完成
	public final static int UPDATE_NOT_FINISHED = 0xe2727104;
	// error\lost状态主机禁止加入集群
	public final static int FORBID_ADD_CLUSTER = 0xe2727105;
	// Appstatus 认证失败
	public final static int CHECKLOGIN_FAIL = 0xe2727106;

	public final static int CLUSTER_DOWN = 0xe2727107;

	public static final int LDAP_READER_ONLY = 703;

	



}
