package com.opzoon.appstatus.common.exception;


/**
 * AppstatusExceptionCode
 * 
 * @author maxiaochao
 * @version V0.2.1023（迭代3） Date：2013-11-08
 */
public enum AppstatusExceptionCode
{
	/**
	 * Appstatus 成功没有异常
	 */
	Success(ERROR_CODE.SUCCESS),
	/**
	 * Appstatus 未知错误
	 */
	AppstatusUnknowException(ERROR_CODE.UNKNOWN),
	/**
	 * Appstatus 请求参赛错误
	 */
	AppstatusBadRequestException(ERROR_CODE.BAD_REQUEST),
	/**
	 * Appstatus 资源没找到异常
	 */
	AppstatusResourceNoFoundException(ERROR_CODE.NOT_FOUND),
	/**
	 * Appstatus 底层平台异常
	 */
	AppstatusHypervisorAbnormalException(ERROR_CODE.HYPERVISOR_ABNORMAL),
	/**
	 * Appstatus 数据库异常
	 */
	AppstatusDatabaseException(ERROR_CODE.DATABASE_UNTOUCHABLE),
	/**
	 * Appstatus zookeeper异常
	 */
	AppstatusZookeeperExceptin(ERROR_CODE.ZOOKEEPER_ACTION_FAIL),
	/**
	 * Appstatus update操作未完成 
	 */
	AppstatusUpdateNotFinishedException(ERROR_CODE.UPDATE_NOT_FINISHED),
	/**
	 * Appstatus error\lost状态主机禁止加入集群
	 */
	AppstatusForbidAddClusterException(ERROR_CODE.FORBID_ADD_CLUSTER),
	/**
	 * Appstatus 认证失败
	 */
	AppstatusAuthenticateException(ERROR_CODE.CHECKLOGIN_FAIL),
	/**
	 * 
	 */
	AppstatusClusterDownException(ERROR_CODE.CLUSTER_DOWN);
	int errorCode;

	private AppstatusExceptionCode(int errorCode)
	{
		this.errorCode = errorCode;
	}

	public int getErrorCode()
	{
		return errorCode;
	}

	private final static class ERROR_CODE
	{
		/*********************公共错误码***********************/
		// 成功
		public final static int SUCCESS = 0;
		// 未知错误
		public final static int UNKNOWN = 0xe2727001;
		// 请求格式或内容错误
		public final static int BAD_REQUEST = 0xe2727002;
		// 找不到指定资源
		public final static int NOT_FOUND = 0xe2727006;
		
		
		/*********************appstatus错误码***********************/
		// 底层平台异常
		public final static int HYPERVISOR_ABNORMAL = 0xe2727101;
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
		
		public final static int CLUSTER_DOWN= 0xe2727107;

	}
	
}
