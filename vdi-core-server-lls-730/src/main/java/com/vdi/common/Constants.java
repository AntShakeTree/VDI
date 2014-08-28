package com.vdi.common;

import com.vdi.common.ConfigUtil;

/*
 * mxc
 * 
 */
public class Constants {
	//lls config
	public static final int  LLS_SORKET_TIMEOUT=Integer.parseInt(ConfigUtil.getLLSConfigByKey("lls.sorcket.timeout"));
	public static final String LLS_UNIX_SORCKET_FILE="/root/unixSockets/interfaceServer";
	public static final long LLS_SORCKET_CON_RETRY_TIMES = 3;
	public static final long LLS_SORCKET_CON_RATE_TIMES = 10;
	public static final String LLS_SORCKET_ADDRESS = ConfigUtil.getLLSConfigByKey("lls.sorcket.address");
	public static final int LLS_SORCKET_PORT = Integer.parseInt(ConfigUtil.getLLSConfigByKey("lls.sorcket.port"));
	public static final int CREATING = 501;
	public static final int AVAILABLE = 1;
	public static final int DELETING = 502;
	public static final int ERROR = 500;
}
