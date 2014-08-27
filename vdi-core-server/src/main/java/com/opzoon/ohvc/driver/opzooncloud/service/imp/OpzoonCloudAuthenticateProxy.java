package com.opzoon.ohvc.driver.opzooncloud.service.imp;

import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.HttpHostConnectException;
import org.apache.http.entity.StringEntity;
import org.apache.log4j.Logger;
import org.json.JSONObject;
import com.opzoon.ohvc.common.Constants;
import com.opzoon.ohvc.domain.Certificate;
import com.opzoon.ohvc.domain.Login;
import com.opzoon.ohvc.domain.User;
import com.opzoon.ohvc.driver.opzooncloud.common.HeaderUtil;
import com.opzoon.ohvc.request.AuthentateProxy;
import com.opzoon.ohvc.request.Request;
import com.opzoon.ohvc.response.UsernameOrPasswordException;
import com.opzoon.ohvc.session.Session;

/**
 * 认证代理
 * 
 * @author maxiaochao
 * @version V04
 */
public class OpzoonCloudAuthenticateProxy implements AuthentateProxy {
	final static Logger log = Logger.getLogger(OpzoonCloudAuthenticateProxy.class);
	// private static final OpzoonCloudAuthenticateProxy INSTANCE = new
	// OpzoonCloudAuthenticateProxy();
	@SuppressWarnings("unused") private volatile boolean isAutenticate = false;

	// 避免重复验证
	/**
	 * 开启认证 OpzoonCloudAuthenticateProxy.start()
	 * 
	 * @param username
	 * @param password
	 * @param domain
	 * @return void
	 * @throws Exception
	 * @author：maxiaochao 2012-9-17 下午6:03:21
	 * 
	 */
	public synchronized Certificate startLogin(String baseUrl, Login login) throws Exception,
			UsernameOrPasswordException {
		Session.removeAuthentication(baseUrl);
		Certificate c = this.start(baseUrl, login);
		Session.distributeLogin(c, baseUrl);
		return c;

	}
	public synchronized Certificate start(String baseUrl, Login login) throws Exception,
			UsernameOrPasswordException {
		Certificate certificate = new Certificate();
		User user = new User();
		String uri = Constants.formatURL(Constants.VDI_OPZOONCLOUD_AUTHENTICATE_URL, baseUrl);
		HttpPost httpmethod = new HttpPost(uri);
		HeaderUtil.setOpzoonCloudPubilcHeader(httpmethod);
		HttpClient httpClient = Request.getClient(baseUrl);
		HttpResponse response =null;
		try {
			JSONObject obj = new JSONObject();
			obj.put("user", login.getUsername());
			obj.put("password", login.getPassword());
			log.info("start :: user [" + login.getUsername() + "] password [" + login.getPassword()
					+ "]");

			this.isAutenticate = true;

			user.setDomainid(login.getDomainId());
			user.setPassword(login.getPassword());
			user.setUsername(login.getUsername());
			// 设置证书开始时间
			certificate.setStartTimeCount(System.nanoTime());
			certificate.setUser(user);
			certificate.setProxyName(this.getClass().getName());
			certificate.setBaseUrl(baseUrl);
			log.info("======david certificate=========" + certificate + "====pass"
					+ user.getPassword() + "==================" + certificate.getBaseUrl());
			Session.setAuthentication(certificate, baseUrl);
			((HttpPost) httpmethod).setEntity(new StringEntity(obj.toString()));
			 response = httpClient.execute((HttpPost) httpmethod);
			Header cookie = response.getFirstHeader(Constants.VDI_OPZOONCLOUD_HEAD_COOKIE);
			int code = response.getStatusLine().getStatusCode();
			certificate.setCookie(cookie.getValue());
			log.info("====david==========" + user.getUsername() + "====pass" + user.getPassword()
					+ "==================" + certificate.getBaseUrl());
			if (code > 300) {
				if (code == 401) {
					throw new UsernameOrPasswordException("{errorCode:" + code
							+ " ,message :authentication is error,username:" + login.getUsername()
							+ ",password:" + login.getPassword() + "}");
				}
				throw new Exception("{errorcode:" + code
						+ " ,message :authentication is error,username:" + login.getUsername()
						+ ",password:" + login.getPassword() + "}");
			}
			Session.setAuthentication(certificate, baseUrl);
			return certificate;
		} catch (Exception e) {
			log.error("::" + e.getMessage());
			if (e instanceof HttpHostConnectException) {
				throw new Exception("{errorCode:404 ,message :\"" + e.getMessage() + "\"}");
			}
			throw e;
		} finally {
			httpmethod.abort();
			Request.shutDown(response,httpClient);
		}
	}

	/**
	 * 
	 * OpzoonCloudAuthenticateProxy
	 */
	public OpzoonCloudAuthenticateProxy() {

	}

}
