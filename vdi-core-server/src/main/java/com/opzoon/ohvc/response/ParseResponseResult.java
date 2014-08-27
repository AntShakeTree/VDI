package com.opzoon.ohvc.response;


/**
 * 返回结果解析类
 * 
 * @author maxiaochao
 * @version V04
 */
public class ParseResponseResult {

	/**
	 * 工具类不需要实例
	 */
	private ParseResponseResult() {
	}

	public static int SUCESS = 0;// 请求失败
	public static int ERR_FAIL = 0x80000001;// 请求失败
	public static int ERR_PARAM = 0x80000002;// 请求参数不正确
	public static int ERR_USER_TICKET_INVALID = 0x80000011;// 用户凭据无效
	public static int ERR_APP_TICKET_INVALID = 0x80000012;// 应用凭据无效
	public static int ERR_LOGIN_PASSWORD = 0x80000101;// 用户密码错误
	public static int ERR_LOGIN_DOMAIN = 0x80000102;// 域名错误
	public static int ERR_LICENSE_EXPIRED = 0x80000103;// 产品授权到期
	public static int ERR_LICENSE_EXCEED = 0x80000104;// 用户连接数达到上限
	public static int ERR_ACL_FAIL_PRIV = 0x80000105;// 访问权限受限
	public static int ERR_ACL_FAIL_IP = 0x80000106;// 访问IP受限
	public static int ERR_START_VM_AGAIN = 0x80000107;// 重复开启
	public static int ERR_STOP_VM_AGAIN = 0x80000108;// 重复关闭
	public static final int ERR_DESTROY_FAIL = 0x80000109;// 销毁实例失败
	public static final int ERR_STATE_FAIL = 0x80000110;// 被操纵的实例状态错误

}
