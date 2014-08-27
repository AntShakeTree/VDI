/**
 * 
 */
package com.opzoon.ohvc.domain;

import com.opzoon.ohvc.common.anotation.Required;
import com.opzoon.vdi.core.domain.BaseDomain;

/**
 * 用户管理
 * 
 * @author maxiaochao
 * @version V04 2012-9-6
 */
public class User extends BaseDomain<User> {

    @Required
    protected String username;// Username
    @Required
    protected String password;// Password
    protected String domainid;// domain ID that the user belongs to
    
    /**
     * @return the username
     */
    public String getUsername() {

	return username;
    }

    /**
     * @return Returns the password.
     */
    public String getPassword() {
	return password;
    }

    /**
     * @return Returns the domainid.
     */
    public String getDomainid() {
	return domainid;
    }

    /**
     * @param username
     *            The username to set.
     */
    public User setUsername(String username) {
	this.username = username;
	return this;
    }

	public void setPassword(String password) {
		this.password = password;
	}

	public void setDomainid(String domainid) {
		this.domainid = domainid;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((password == null) ? 0 : password.hashCode());
		result = prime * result
				+ ((username == null) ? 0 : username.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		User other = (User) obj;
		if (password == null) {
			if (other.password != null)
				return false;
		} else if (!password.equals(other.password))
			return false;
		if (username == null) {
			if (other.username != null)
				return false;
		} else if (!username.equals(other.username))
			return false;
		return true;
	}

   

}
