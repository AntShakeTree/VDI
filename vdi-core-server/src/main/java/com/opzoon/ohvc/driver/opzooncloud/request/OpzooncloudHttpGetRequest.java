package com.opzoon.ohvc.driver.opzooncloud.request;

import org.apache.http.client.methods.HttpGet;

/**
 * GET方式提交的数据
 * 
 * @author maxiaochao
 * 
 */
public class OpzooncloudHttpGetRequest extends OpzoonCloudRequestManager<HttpGet> {

	private HttpGet get;

	public OpzooncloudHttpGetRequest(String ip,String url) throws Exception{
		this.get = new HttpGet(url);
		super.init(ip, get);
	}
	
	public OpzooncloudHttpGetRequest(String url) throws Exception{
		this.get = new HttpGet(url);
	}


	public static OpzooncloudHttpGetRequest instanceByUrl(String ip,String url) throws Exception{

		return new OpzooncloudHttpGetRequest(ip,url);
	}


}
