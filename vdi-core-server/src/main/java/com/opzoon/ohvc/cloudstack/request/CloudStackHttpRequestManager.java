/**  
 * @title: VDIcloudWS v04 CloudStackHttpRequestManager.java 
 * @package com.opzoon.client.cloudstack.request
 * @author maxiaochao
 * @date 2012-9-10
 * @version V04 
 */
package com.opzoon.ohvc.cloudstack.request;

import java.io.IOException;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.ParseException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.util.EntityUtils;
import org.apache.log4j.Logger;

import com.opzoon.ohvc.domain.Certificate;
import com.opzoon.ohvc.request.Request;
import com.opzoon.ohvc.session.Session;
import com.opzoon.vdi.core.domain.BaseDomain;

/**
 * cloudstack 接口管理类
 * 
 * @ClassName: CloudStackHttpRequestManager.java
 * @Description: CloudStackHttpRequestManager.java
 * @author: maxiaochao
 * @date: 2012-9-10
 * @version: V04
 */
public abstract class CloudStackHttpRequestManager<T extends HttpRequestBase> {
	private static Logger log = Logger
			.getLogger(CloudStackHttpRequestManager.class);

	protected String apiUrl;
	private HttpClient client;
	protected T request;

	private HttpResponse response;

	private int code;

	private String finalUrl;

	public void init(String ip) throws Exception {
		client = Request.getClient(ip);
		Certificate certificate = (Certificate) Session.getCertificate(ip);
		this.finalUrl = certificate.getFinalUrl();
		request.addHeader("Cookie", certificate.getCookie());
	}

	/**
	 * 发送请求
	 * 
	 * @return
	 * @throws IOException
	 */
	public String execute() throws IOException {
		String result = "";
		log.info("enter readResult");
		if (response == null) {
			response = client.execute(request);
		}
		try {
			HttpEntity entity = response.getEntity();
			this.code = response.getStatusLine().getStatusCode();
			log.info("readResult : connect status [" + code + "]");
			result = EntityUtils.toString(entity);
			if (code >= 300) {
				if (code == 431) {
					code = 404;
				}
				log.error("connect status is " + code
						+ " server return error message is [" + result + "]");
				throw new IOException("{errorcode:" + code
						+ result.replace("{", ","));
			}
			return result;
		} catch (ParseException e) {
			log.info(e.getMessage());
			throw e;
		} catch (IOException e) {
			log.info(e.getMessage());
			throw e;
		} finally {
			this.releaseRequest(response,client);
		}
	}

	/*
	 * 获得提交URL
	 * 
	 * @see com.opzoon.client.request.Request#getUrl()
	 */
	public String getUrl() {
		// TODO Auto-generated method stub
		return this.finalUrl;
	}

	/**
	 * @return Returns the baseDomain.
	 */
	public BaseDomain<T> getBaseDomain() {
		return baseDomain;
	}

	/**
	 * @return Returns the request.
	 */
	public T getRequest() {
		return request;
	}

	/**
	 * @param request
	 *            The request to set.
	 */
	public abstract void injectRequest(String ip) throws Exception;

	private BaseDomain<T> baseDomain;

	/**
	 * @param baseDomain
	 *            The baseDomain to set.
	 */
	public void setBaseDomain(BaseDomain<T> baseDomain) {
		this.baseDomain = baseDomain;
	}

	/**
	 * @return Returns the response.
	 */
	public HttpResponse getResponse() {
		return response;
	}

	/**
	 * @param response
	 *            The response to set.
	 */
	public void setResponse(HttpResponse response) {
		this.response = response;
	}

	/**
	 * 释放请求 CloudStackHttpRequestManager.releaseRequest()
	 * @param client 
	 * @param response 
	 * 
	 * @return void
	 * @author：maxiaochao 2012-9-17 下午6:07:40
	 */
	public void releaseRequest(HttpResponse response, HttpClient client) {
		 this.request.abort();
		 Request.shutDown(response,client);
	}
}
