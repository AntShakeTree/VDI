package com.opzoon.ohvc.driver.opzooncloud.request;

import java.io.IOException;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpEntityEnclosingRequestBase;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.entity.StringEntity;
import org.apache.http.util.EntityUtils;
import org.apache.log4j.Logger;
import org.json.JSONObject;

import com.opzoon.ohvc.common.Constants;
import com.opzoon.ohvc.domain.Certificate;
import com.opzoon.ohvc.domain.ErrorMeesage;
import com.opzoon.ohvc.driver.opzooncloud.common.HeaderUtil;
import com.opzoon.ohvc.request.Request;
import com.opzoon.ohvc.response.ResourceNotMeetException;
import com.opzoon.ohvc.response.UsernameOrPasswordException;
import com.opzoon.ohvc.session.Session;

/**
 * Opzooncloud 请求管理类
 * 
 * @author maxiaochao
 * @version V04
 */
public abstract class OpzoonCloudRequestManager<T extends HttpRequestBase> {
	private static final Logger log = Logger
			.getLogger(OpzoonCloudRequestManager.class);
	private String url;
	private T method;
	public HttpClient httpClient;
	private HttpResponse response;
	private int code;
	private volatile String ip;
	private Certificate certifacate;

	/**
	 * 初始化 HTTP连接 OpzoonCloudRequestManager.init()
	 * 
	 * @param ip
	 * @param method
	 * @return void
	 * @author：maxiaochao 2012-9-17 下午5:59:31
	 */
	public void init(String ip, T method) throws Exception {
		this.ip = ip;
		// 开启认证
		log.info("enter init.");

		httpClient = Request.getClient(ip);
		this.certifacate = Session.getCertificate(ip);
		setHeader(method, ip);
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
		// TODO Auto-generated method stub
		return this.url;
	}

	/**
	 * 提交请求
	 * 
	 * @return
	 * @throws Exception
	 */
	public String execute() throws Exception,ResourceNotMeetException {
		String result = "";
		log.info("enter readResult");
		if (response == null) {
			this.response = this.httpClient.execute(method);
			/**
			 * 0_A_O : please checked
			 * 
			 * 
			 * @author maxiaochao
			 */
//			Header hread = response
//					.getFirstHeader(Constants.VDI_OPZOONCLOUD_HEAD_COOKIE);
//			log.info("please checked update authentication every request logic!["
//					+ hread + "]");
//			if (hread != null) {
//				String cookie = hread.getValue();
//				if (this.certifacate == null) {
//					this.certifacate = Session.getCertificate(this.ip);
//				}
//				this.certifacate.setCookie(cookie);
//				this.certifacate.setStartTimeCount(System.nanoTime());
//				Session.setAuthentication(this.certifacate, this.ip);
//			}

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

				log.error("connect status is " + code
						+ " server return error message is [" + result + "]");

				if (code == 401) {
					throw new UsernameOrPasswordException("{errorcode:" + code
							+ result.replace("{", ","));
				}
				for(String resourceErr:ErrorMeesage.RESOURCE_ERROR){
					if(result.contains(resourceErr)){
						throw new ResourceNotMeetException(result);
					}
				}
				
				
				throw new IOException(result);
			}

			return result;
		} catch (Exception e) {
			log.info(e.getMessage());
			throw e;
		} finally {
			this.releaseRequest(response,httpClient);

		}
	}

	/**
	 * 设置Header OpzoonCloudRequestManager.setHeader()
	 * 
	 * @param t
	 * @return void
	 * @throws Exception
	 * @author：maxiaochao 2012-9-17 下午6:00:29
	 */
	private void setHeader(T t, String ip) throws Exception {
		HeaderUtil.setOpzoonCloudPubilcHeader(t).addHeader("Cookie",
				Session.getCertificate(ip).getCookie());
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
	 * @param httpClient2 
	 * @param response2 
	 * 
	 * @return void
	 * @author：maxiaochao 2012-9-17 下午6:01:09
	 */
	public void releaseRequest(HttpResponse response, HttpClient httpClient) {
		this.method.abort();
		Request.shutDown(response,httpClient);
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
	 * 
	 */
	public String executeJSONStr(String stringData) throws Exception {
		String result = "";
		StringEntity entity;
		try {
			entity = new StringEntity(stringData);
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

}
