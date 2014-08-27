package com.opzoon.ohvc.driver.opzooncloud.common;

import org.apache.http.client.methods.HttpRequestBase;

/**
 * 
 * @author maxiaochao
 * 
 */
public class HeaderUtil {

	public static HttpRequestBase setOpzoonCloudPubilcHeader(
			HttpRequestBase httpmethod) {
		httpmethod.addHeader("Content-Type", "application/opzooncloud-v2+json");
		httpmethod.addHeader("Accept-Encoding", "gzip;q=1.0, identity; q=0.5");
		httpmethod.addHeader("Accept", "application/opzooncloud-v2+json");
		return httpmethod;
	}

}
 