package com.opzoon.vdi.core.facade;

/**
 * 鍖呭惈閿欒浠ｇ爜鐨勫紓甯�
 */
@SuppressWarnings("serial")
public class CommonException extends Exception {

  /**
   * 鏃犻敊璇�
   */
  public static final int NO_ERRORS = 0;
  /**
   * 閮ㄥ垎鎿嶄綔鍑虹幇閿欒.
   */
  public static final int MULTI_STATUS = 0x101;
  /**
   * 璇锋眰鏍煎紡鎴栧唴瀹归敊璇�
   */
  public static final int BAD_REQUEST = 0x002;
  /**
   * 璁よ瘉澶辫触.
   */
  public static final int UNAUTHORIZED = 0x102;
  /**
   * 鏉冮檺涓嶈冻.
   */
  public static final int FORBIDDEN = 0x103;
  /**
   * 鎵句笉鍒版寚瀹氳祫婧�
   */
  public static final int NOT_FOUND = 0x006;
  /**
   * 鍐茬獊.
   */
  public static final int CONFLICT = 0x104;
  public static final int DRIVER_PASS = 0x105;
  public static final int DRIVER_UNTOUCHABLE = 0x106;
  public static final int HYPERVISOR_ABNORMAL = 0x107;
  public static final int LDAP_UNAUTHORIZED = 0x10A;
  public static final int LDAP_ABNORMAL = 0x10B;
  public static final int LDAP_OU_NOT_FOUND = 0x10C;
  public static final int INVALID_PASSWORD = 0x005;
  public static final int HYPERVISOR_NO_ENOUGH_RESOURCES = 0x108;
  /**
   * 鏈煡閿欒.
   */
  public static final int UNKNOWN = 0x001;
  /**
   * 鏆傛湭瀹炵幇.
   */
  public static final int NOT_YET_IMPLEMENTED = 501;
  /**
   * 鍘熷瘑鐮侀敊璇�
   */
  public static final int ORIGINAL_PASSWORD_INVALID = 0x109;
  public static final int LICENSE_NO_PUBLIC_KEY = 0x10A;
  public static final int PASSWORD_RESET_NEEDED = 0x115;
  public static final int LICENSE_FINGER_ERROR = 0x10B;
  public static final int LICENSE_ENCRYPT_ERROR = 0x10C;
  public static final int CONNECTION_FULL = 0x10D;
  public static final int LICENSE_DECRYPT_ERROR = 0x10E;
  public static final int LICENSE_DUPLICATE = 0x10F;
	//add by zhanglu 2014-07-01 start
	//集群时获取不到指纹
	public static final int LICENSE_CLUSTER_TYPE = 0x11A;
	//license过期
	public static final int ESTABLISH_CONNECTION_ERR = 0x11B;
	//未配置License Server IP
	public static final int LICENSE_SERVER_ERR = 0x11C;
	//未配置License Server 授权失败
	public static final int LICENSE_SERVER_DOFIANL_ERR = 0x11D;
	//获取Guid失败
	public static final int UKEY_GUID_ERR = 0x11E;
	//已经存在有效的license
	public static final int LICENSE_MORE_ERR = 0x11F;
	//add by zhanglu 2014-07-01 end
  
  public static final int MAX_GROUP = 0x110;

  public static final int PROVISIONING = 0x111;
  public static final int POOL_MAINTAINING = 0x112;
  public static final int DUPE_IP = 0x113;
  public static final int RSA_REQ = 0x114;

  public static final int NOT_IN_CLUSTER = 0x121;
  public static final int INVALID_SESSION_BASE = 0x150;
  public static final int INVALID_PASSWOED_POLICY = 0x303;
  public static final int INVALID_AUTACATION_POLICY = 0x401;
  private int error;
  
  public CommonException (int error) {
    this.error = error;
  }

  public int getError() {
    return error;
  }

}