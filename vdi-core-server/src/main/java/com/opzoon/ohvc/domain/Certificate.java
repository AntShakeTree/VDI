/**
 * 
 */
package com.opzoon.ohvc.domain;

import com.opzoon.vdi.core.domain.BaseDomain;

/**
 * 证书
 * 
 * @author maxiaochao
 * @version V04 2012-9-6
 */
public class Certificate extends BaseDomain<Certificate> {
	// private String apikey;
	// private String signature;
	private String secretkey;
	private User user;
	private String finalUrl;
	private String baseUrl;
	private String cookie;
	private long startTimeCount;
	private long endTimeCount;
	private String proxyName;

	/**
	 * @return Returns the apikey.
	 */
	// public String getApikey() {
	// return apikey;
	// }

	/**
	 * @return Returns the cookie.
	 */
	public String getCookie() {
		return cookie;
	}

	/**
	 * @param cookie
	 *            The cookie to set.
	 */
	public Certificate setCookie(String cookie) {
		this.cookie = cookie;
		return this;
	}


	/**
	 * @return Returns the secretkey.
	 */
	public String getSecretkey() {
		return secretkey;
	}

	/**
	 * @return Returns the user.
	 */
	public User getUser() {
		return user;
	}

	/**
	 * @return Returns the finalUrl.
	 */
	public String getFinalUrl() {
		return finalUrl;
	}

	/**
	 * @param secretkey
	 *            The secretkey to set.
	 */
	public Certificate setSecretkey(String secretkey) {
		this.secretkey = secretkey;
		return this;
	}

	/**
	 * @param user
	 *            The user to set.
	 */
	public Certificate setUser(User user) {
		this.user = user;
		return this;
	}

	/**
	 * @param finalUrl
	 *            The finalUrl to set.
	 */
	public Certificate setFinalUrl(String finalUrl) {
		this.finalUrl = finalUrl;
		return this;
	}

	/**
	 * @return the baseUrl
	 */
	public String getBaseUrl() {
		return baseUrl;
	}

	/**
	 * @param baseUrl
	 *            the baseUrl to set
	 */
	public void setBaseUrl(String baseUrl) {
		this.baseUrl = baseUrl;
	}

	/**
	 * @return Returns the startTimeCount.
	 */
	public long getStartTimeCount() {
		return startTimeCount;
	}

	/**
	 * @return Returns the endTimeCount.
	 */
	public long getEndTimeCount() {
		return endTimeCount;
	}

	/**
	 * @param startTimeCount
	 *            The startTimeCount to set.
	 */
	public void setStartTimeCount(long startTimeCount) {
		this.startTimeCount = startTimeCount;
	}

	/**
	 * @param endTimeCount
	 *            The endTimeCount to set.
	 */
	public void setEndTimeCount(long endTimeCount) {
		this.endTimeCount = endTimeCount;
	}

	/**
	 * @return Returns the proxyName.
	 */
	public String getProxyName() {
		return proxyName;
	}

	/**
	 * @param proxyName
	 *            The proxyName to set.
	 */
	public void setProxyName(String proxyName) {
		this.proxyName = proxyName;
	}

}
