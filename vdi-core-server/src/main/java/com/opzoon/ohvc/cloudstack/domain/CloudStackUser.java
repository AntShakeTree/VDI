/**  
 * @title: VDIcloudWS v04 CloudStackUser.java 
 * @package com.opzoon.client.cloudstack.domain
 * @author maxiaochao
 * @date 2012-9-10
 * @version V04 
 */
package com.opzoon.ohvc.cloudstack.domain;

import java.util.ArrayList;

import com.opzoon.ohvc.domain.User;

/**
 * @ClassName: CloudStackUser.javaO
 * @Description: CloudStackUser.java
 * @author: maxiaochao
 * @date: 2012-9-10
 * @version: V04
 */
public class CloudStackUser extends User {
    private String command;
    private ArrayList<String> publicIp;
    private String server;
    private String developerServer;
    private String userId;
    private String apiKey;
    private String secretKey;
    private String encryptedPassword;
    private String cookie;
    private String sessionkey;
    private String timeout;
    /**
     * @return Returns the sessionkey.
     */
    public String getSessionkey() {
	return sessionkey;
    }

    /**
     * @param sessionkey
     *            The sessionkey to set.
     */
    public void setSessionkey(String sessionkey) {
	this.sessionkey = sessionkey;
    }

    /**
     * @return Returns the publicIp.
     */
    public ArrayList<String> getPublicIp() {
	return publicIp;
    }

    /**
     * @return Returns the server.
     */
    public String getServer() {
	return server;
    }

    /**
     * @return Returns the developerServer.
     */
    public String getDeveloperServer() {
	return developerServer;
    }

    /**
     * @return Returns the userId.
     */
    public String getUserId() {
	return userId;
    }

    /**
     * @return Returns the apiKey.
     */
    public String getApiKey() {
	return apiKey;
    }

    /**
     * @return Returns the secretKey.
     */
    public String getSecretKey() {
	return secretKey;
    }

    /**
     * @return Returns the encryptedPassword.
     */
    public String getEncryptedPassword() {
	return encryptedPassword;
    }

    /**
     * @param publicIp
     *            The publicIp to set.
     */
    public void setPublicIp(ArrayList<String> publicIp) {
	this.publicIp = publicIp;
    }

    /**
     * @param server
     *            The server to set.
     */
    public CloudStackUser setServer(String server) {
	this.server = server;
	return this;
    }

    /**
     * @param developerServer
     *            The developerServer to set.
     */
    public void setDeveloperServer(String developerServer) {
	this.developerServer = developerServer;
    }

    /**
     * @param userId
     *            The userId to set.
     */
    public CloudStackUser setUserId(String userId) {
	this.userId = userId;
	return this;
    }

    /**
     * @param apiKey
     *            The apiKey to set.
     */
    public void setApiKey(String apiKey) {
	this.apiKey = apiKey;
    }

    /**
     * @param secretKey
     *            The secretKey to set.
     */
    public void setSecretKey(String secretKey) {
	this.secretKey = secretKey;
    }

    /**
     * @param encryptedPassword
     *            The encryptedPassword to set.
     */
    public void setEncryptedPassword(String encryptedPassword) {
	this.encryptedPassword = encryptedPassword;
    }

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
    public void setCookie(String cookie) {
	this.cookie = cookie;
    }

    /**
     * @return Returns the command.
     */
    public String getCommand() {
	return command;
    }

    /**
     * @param command
     *            The command to set.
     */
    public CloudStackUser setCommand(String command) {
	this.command = command;
	return this;
    }

	public String getTimeout() {
		return timeout;
	}

	public void setTimeout(String timeout) {
		this.timeout = timeout;
	}

}
