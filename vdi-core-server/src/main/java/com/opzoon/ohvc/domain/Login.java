/**  
 * @title: VDIcloudWS v04 Login.java 
 * @package com.opzoon.client.cloudstack.domain
 * @author maxiaochao
 * @date 2012-9-13
 * @version V04 
 */
package com.opzoon.ohvc.domain;

import com.opzoon.ohvc.common.Constants;
import com.opzoon.ohvc.common.anotation.MD5;
import com.opzoon.ohvc.common.anotation.Required;
import com.opzoon.vdi.core.domain.BaseDomain;

/**
 * @ClassName: Login.java
 * @Description: Login.java
 * @author: maxiaochao
 * @date: 2012-9-13
 * @version: V04
 */
public class Login extends BaseDomain<Login>{
    @Required
    private String command = Constants.VDI_CS_API_LOGIN;
    @Required
    private String username;
    @Required
    @MD5
    private String password;
    private String domain;
    private String domainId;
    private String response="json";
    /**
     * @return Returns the domain.
     */
    public String getDomain() {
	return domain;
    }

    /**
     * @param domain
     *            The domain to set.
     */
    public void setDomain(String domain) {
	this.domain = domain;
    }


    /**
     * @return Returns the username.
     */
    public String getUsername() {
	return username;
    }

    /**
     * @return Returns the domainId.
     */
    public String getDomainId() {
	return domainId;
    }

    /**
     * @param domainId
     *            The domainId to set.
     */
    public Login setDomainId(String domainId) {
	this.domainId = domainId;
	return this;
    }

    /**
     * @return Returns the password.
     */
    public String getPassword() {
	return password;
    }



    /**
     * @return Returns the command.
     */
    public String getCommand() {
        return command;
    }

    /**
     * @param command The command to set.
     */
    public Login setCommand(String command) {
        this.command = command;
        return this;
    }

    /**
     * @param username
     *            The username to set.
     */
    public Login setUsername(String username) {
	this.username = username;
	return this;
    }

    /**
     * @param password
     *            The password to set.
     */
    public Login setPassword(String password) {
	this.password = password;
	return this;
    }

    /**
     * @return Returns the response.
     */
    public String getResponse() {
        return response;
    }

    /**
     * @param response The response to set.
     */
    public Login setResponse(String response) {
        this.response = response;
        return this;
    }
    public static void main(String[] args) {
		System.out.println(new Login());
	}
}
