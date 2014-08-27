package com.opzoon.ohvc.request;

import org.apache.http.client.methods.HttpGet;
import org.apache.log4j.Logger;

/**
 * GET方式提交的数据
 * 
 * @author maxiaochao
 * 
 */
public class HttpGetRequest extends OpzoonRequestManager<HttpGet> {
	private static final Logger log = Logger.getLogger(HttpGetRequest.class);

	private HttpGetRequest() {
	};

	private HttpGet get;

	private HttpGetRequest(String ip, String url) throws Exception {
		this.get = new HttpGet(url);
		super.init(ip, get);
	}

	public static HttpGetRequest instanceByUrl(String baseurl, String url)
			throws Exception {

		return new HttpGetRequest(baseurl, url);
	}

}
