package com.opzoon.ohvc.driver.opzooncloud.common;

import java.text.MessageFormat;

/**
 * 
 * @author maxiaochao 常量
 */
public class Constants {
	public static final String VDI_OPZOONCLOUD_VMINSTANCE_ETH0_PREPERTIES_1 = "seclists";
	public static final String VDI_OPZOONCLOUD_VMINSTANCE_RELATIONSHIPS_QNAME = "relationships";
	public static final String ADI_OPZOONCLOUD_URI_KYE = "vdi.opzooncloud.uri";

	public static final String VDI_OPZOONCLOUD_HEAD_COOKIE = ConfigUtil
			.getBykey("vdi.opzooncloud.head.cookie");

	public static final String VDI_OPZOONCLOUD_AUTHENTICATE_URL = ConfigUtil
			.getBykey("vdi.opzooncloud.authenticate.url");
	public static final String VDI_OPZOONCLOUD_AUTHENTICATE_USER = ConfigUtil
			.getBykey("vdi.opzooncloud.authenticate.user");
	public static final String VDI_OPZOONCLOUD_CREATE_INSTANCE_URL = ConfigUtil
			.getBykey("vdi.opzooncloud.create.instance.url");
	// 查询所有的URL
	public static final String VDI_OPZOONCLOUD_INSTANCES_URL = ConfigUtil
			.getBykey("vdi.opzooncloud.instances.url");
	public static final String VDI_OPZOONCLOUD_AUTHENTICATE_PASSWORD = ConfigUtil
			.getBykey("vdi.opzooncloud.authenticate.password");
	public static final String ADI_OPZOONCLOULD_MESSAGE_RESULT = "result";
	// 操作vminstance uri
	public static final String VDI_OPZOONCLOUD_INSTANCE_URI = ConfigUtil
			.getBykey("vdi.opzooncloud.instance.uri");
	public static final String VDI_OPZOONCLOUD_VM_SATUS_STOP = "stopped";

	public static final String VDI_CS_HOST = ConfigUtil.getBykey("vdi.cs.host");
	public static final String VDI_CS_API_LOGIN = "login";
	public static final String VDI_CS_IP_KEY = "vdi.cs.ip";
	// public static final String VDI_CS_APIKEY = ConfigUtil.getBykey("apiKey");
	public static final String VDI_CS_IP_SECRETKEY = ConfigUtil
			.getBykey("secretKey");
	public static final int SCHEDULEDPOOL_SIZE = Integer.parseInt(ConfigUtil
			.getBykey("platform.size"));
	public static final String JOB_HEADS = "JOB_";

	/**
	 * 不允许实例化
	 */
	private Constants() {
	}

	/**
	 * format url Constants.FormatURL()
	 * 
	 * @param src
	 *            .properties config url
	 * @param ip
	 * @return
	 * @return String
	 * @author：maxiaochao 2012-9-19 下午4:56:17
	 */
	public static String formatURL(String src, String baseurl) {
		return MessageFormat.format(src, baseurl);
	}
}
