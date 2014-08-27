/**  
 * @title: VDIcloudWS v04 CloudStackAuthenticateProxy.java 
 * @package com.opzoon.client.cloudstack.service.imp
 * @author maxiaochao
 * @date 2012-9-13
 * @version V04 
 */
package com.opzoon.ohvc.cloudstack;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.StringTokenizer;
import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.conn.HttpHostConnectException;
import org.apache.http.util.EntityUtils;
import org.apache.log4j.Logger;
import org.json.JSONException;
import org.json.JSONObject;
import com.opzoon.ohvc.cloudstack.domain.CloudStackUser;
import com.opzoon.ohvc.common.Constants;
import com.opzoon.ohvc.domain.Certificate;
import com.opzoon.ohvc.domain.Login;
import com.opzoon.ohvc.request.AuthentateProxy;
import com.opzoon.ohvc.request.Request;
import com.opzoon.ohvc.response.UsernameOrPasswordException;
import com.opzoon.ohvc.session.Session;

/**
 * @ClassName: CloudStackAuthenticateProxy.java
 * @Description: CloudStackAuthenticateProxy.java
 * @author: maxiaochao
 * @date: 2012-9-13
 * @version: V04
 */
public class CloudStackAuthenticateProxy implements AuthentateProxy {
	private static Logger log = Logger
			.getLogger(CloudStackAuthenticateProxy.class);
	// private HttpGet get;
	private Certificate certificate;
	private HttpResponse response;

	public CloudStackAuthenticateProxy() {
	}

	public Header[] getHeaders() {
		return response.getAllHeaders();
	}

	public synchronized Certificate startLogin(String baseUrl, Login login)
			throws Exception, UsernameOrPasswordException {
		Session.removeAuthentication(baseUrl);
		Certificate c = this.start(baseUrl, login);
		Session.distributeLogin(c, baseUrl);
		return c;

	}

	/*
	 * @see
	 * com.opzoon.client.cloudstack.service.CloudStackAuthenticate#start(java
	 * .lang.String, java.lang.String, java.lang.String)
	 */
	public synchronized Certificate start(String baseurl, Login login)
			throws Exception, UsernameOrPasswordException {
		HttpGet get = null;
		HttpClient httpClient = null;
		log.debug("cloudStack login " + login);
		try {
			this.certificate = new Certificate();
			// 设置证书开始时间
			this.certificate.setStartTimeCount(System.nanoTime());
			this.certificate.setSecretkey(Constants.VDI_CS_IP_SECRETKEY);
			getCertificate(baseurl, login.toString(), certificate);
			get = new HttpGet(certificate.getFinalUrl());
			httpClient = Request.getClient(baseurl);
			HttpResponse response = httpClient.execute(get);
			log.debug("NPE=================================================");
			log.debug("get: " + get);
			log.debug("response: " + response);
			log.debug("getStatusLine: " + response.getStatusLine());
			log.debug("getEntity: " + response.getEntity());
			String cookie = response.getFirstHeader(
					Constants.VDI_OPZOONCLOUD_HEAD_COOKIE).getValue();
			this.certificate.setCookie(cookie);
			get.addHeader("Cookie", certificate.getCookie());
			CloudStackUser user = new CloudStackUser();
			CloudStackUser nUser = parseCloudStackLogin(
					EntityUtils.toString(response.getEntity()), user);
			nUser.setUsername(login.getUsername());
			nUser.setPassword(login.getPassword());
			this.certificate.setUser(nUser);
			this.certificate.setProxyName(this.getClass().getName());
			this.certificate.setBaseUrl(baseurl);
			int code = response.getStatusLine().getStatusCode();
			if (code > 300) {
				if (code == 401) {
					throw new UsernameOrPasswordException("{errorCode:" + code
							+ " ,message :authentication is error,username:"
							+ login.getUsername() + ",password:"
							+ login.getPassword() + "}");
				}
				throw new Exception("{errorcode:" + code
						+ " ,message :authentication is error,username:"
						+ login.getUsername() + ",password:"
						+ login.getPassword() + "}");
			}
			Session.setAuthentication(this.certificate, baseurl);
		} catch (Exception e) {
			log.error("::" + e.getMessage());
			if (e instanceof HttpHostConnectException) {
				throw new Exception("{errorCode:404 ,message :\""
						+ e.getMessage() + "\"}");
			}
			throw e;
		} finally {
			get.abort();
			if (response != null) {
				try {
					response.getEntity().getContent().close();
				} catch (Exception e) {

				}
			}
			if (httpClient != null) {
				Request.shutDown(response,httpClient);
			}
		}
		return this.certificate;
	}

	/**
	 * 获得证书 CloudStackAuthenticateProxy.getfinalUrl()
	 * 
	 * @param apiUrl
	 * @param certificate
	 * @return
	 * @return Certificate
	 * @author：maxiaochao 2012-9-13 下午8:16:33
	 */
	private final static Certificate getCertificate(String baseUrl,
			String apiUrl, Certificate certificate) {
		try {
			// Step 1: Make sure your APIKey is URL encoded

			// Step 2: URL encode each parameter value, then sort the parameters
			List<String> sortedParams = new ArrayList<String>();
			StringTokenizer st = new StringTokenizer(apiUrl, "&");
			String url = null;
			boolean first = true;
			while (st.hasMoreTokens()) {
				String paramValue = st.nextToken();
				String param = paramValue.substring(0, paramValue.indexOf("="));
				String value = URLEncoder.encode(paramValue.substring(
						paramValue.indexOf("=") + 1, paramValue.length()),
						"UTF-8");
				if (first) {
					url = param + "=" + value;
					first = false;
				} else {
					url = url + "&" + param + "=" + value;
				}
				sortedParams.add(param.toLowerCase() + "="
						+ value.toLowerCase());
			}
			Collections.sort(sortedParams);

			log.info("Sorted Parameters: " + sortedParams);

			// Step 3: Construct the sorted URL and sign and URL encode the
			// sorted URL with your secret key
			String sortedUrl = null;
			first = true;
			for (String param : sortedParams) {
				if (first) {
					sortedUrl = param;
					first = false;
				} else {
					sortedUrl = sortedUrl + "&" + param;
				}
			}
			log.info("sorted URL : " + sortedUrl);

			// String encodedSignature = OpzoonUtils.signHmacSHA1(sortedUrl,
			// secretKey);
			// certificate.setSignature(encodedSignature);
			String finalUrl = baseUrl + "?" + url;
			certificate.setFinalUrl(finalUrl);
			log.info("final URL : " + finalUrl);
			certificate.setBaseUrl(baseUrl);
			return certificate;

			// http://192.168.31.67:8080/client/api?command=startVirtualMachine&id=8184986e-bd6e-469d-88e9-fe0ca7116112&apiKey=Ah3xoQW1LXj3FNBCt0XIOKA4iPkb1knFRd4fWxAxAOSdbHiadNkwoHeQpBDFjQvgXxNiO9R4Z8CINlBwMk1GsA&signature=VxWf4%2BrIah3DHV%2BsQnlDz7dlzWw%3D
		} catch (Exception e) {
			log.error(e.getMessage());
			return null;
		}
	}

	/**
	 * @return Returns the certificate.
	 */
	public Certificate getCertificate() {
		return certificate;
	}

	private static CloudStackUser parseCloudStackLogin(String readResult,
			CloudStackUser user) {
		try {
			JSONObject jsonObject = new JSONObject(readResult);
			JSONObject object = (JSONObject) jsonObject.get("loginresponse");
			user.setSessionkey(URLEncoder.encode(
					object.getString("sessionkey"), "UTF-8"));
			user.setTimeout(URLEncoder.encode(object.getString("timeout"),
					"UTF-8"));
			return user;
		} catch (JSONException e) {
			e.printStackTrace();
			return null;
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
			return null;
		}
	}
}
