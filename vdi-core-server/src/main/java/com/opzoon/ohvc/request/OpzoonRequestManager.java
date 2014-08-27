package com.opzoon.ohvc.request;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.Map;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpEntityEnclosingRequestBase;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.entity.StringEntity;
import org.apache.http.util.EntityUtils;
import org.apache.log4j.Logger;
import org.json.JSONObject;

/**
 * @author maxiaochao
 * @version: V04
 * @since V04 2012-10-18
 */
public abstract class OpzoonRequestManager<T extends HttpRequestBase> {

	private static final Logger log = Logger.getLogger(OpzoonRequestManager.class);
	private String url;
	T method;
	public HttpClient httpClient;
	private HttpResponse response;
	private int code;

	/**
	 * 初始化 HTTP连接 OpzoonCloudRequestManager.init()
	 * 
	 * @param ip
	 * @param method
	 * @return void
	 * @author：maxiaochao 2012-9-17 下午5:59:31
	 */
	public void init(String ip, T method) throws Exception {
		log.info("enter init.");
		httpClient = Request.getClient(ip);
		this.method = method;
	}

	/**
	 * 获得平台IP OpzoonCloudRequestManager.getUrl()
	 * 
	 * @return
	 * @return String
	 * @author：maxiaochao 2012-9-17 下午5:59:59
	 */
	public String getUrl() {
		return this.url;
	}

	/**
	 * 提交请求
	 * 
	 * @return
	 * @throws Exception
	 */
	public String execute() throws Exception {
		String result = "";
		log.info("enter readResult");
		if (response == null) {
			this.response = this.httpClient.execute((T) method);
		}
		try {

			HttpEntity entity = null;

			this.code = response.getStatusLine().getStatusCode();
			if (this.code != 204) {
				entity = response.getEntity();
			}
			entity = response.getEntity();
			log.info("readResult : connect status [" + code + "]");
			if (entity != null) {
				result = EntityUtils.toString(entity);
			} else {
				result = code + "";
			}
			if (code >= 300) {

				log.error("connect status is " + code + " server return error message is [" + result + "]");
				throw new IOException("{errorcode:" + code + result.replace("{", ","));
			}
			result=new String(result.getBytes("ISO-8859-1"),"UTF-8");  
			return result;
		} catch (Exception e) {
			log.info(e.getMessage());
			throw e;
		} finally {
			// this.httpClient.getConnectionManager().shutdown();
			this.releaseRequest(response,httpClient);

		}
	}

	public void setUrl(String url) {
		this.url = url;
	}

	/**
	 * @return the code
	 */
	public int getCode() {
		return code;
	}

	/**
	 * 释放请求 OpzoonCloudRequestManager.releaseRequest()
	 * @param httpclient 
	 * @param response 
	 * 
	 * @return void
	 * @author：maxiaochao 2012-9-17 下午6:01:09
	 */
	public void releaseRequest(HttpResponse response, HttpClient httpclient) {
		this.method.abort();
		Request.shutDown(response,httpclient);
	}

	/**
	 * 提交jsonobject格式的请求
	 * 
	 * @param jsonObject
	 * @return
	 * @throws Exception
	 */
	@Deprecated
	public String executeJSON(JSONObject jsonObject) throws Exception {

		return this.executeJSONStr(jsonObject.toString());

	}

	/**
	 * 提交jsonobject封装的String类型的请求请求
	 * 
	 * @param jsonObject
	 * @return
	 * @throws Exception
	 */
	public String executeJSONStr(String stringData) throws Exception {
		String result = "";
		StringEntity entity;
		try {
			entity=new StringEntity(stringData,Charset.forName("UTF-8"));
			HttpEntityEnclosingRequestBase base = (HttpEntityEnclosingRequestBase) method;
			base.setEntity(entity);
			result = this.execute();
		} catch (IOException e) {
			this.releaseRequest(response,httpClient);
			log.error(e.getMessage());
			throw e;
		}
		return result;
	}

	public void addHeader(Map<String, String> paramterStr) {
		for(String key:paramterStr.keySet()){
			method.addHeader(key, paramterStr.get(key)+"");
		}
	}
}
