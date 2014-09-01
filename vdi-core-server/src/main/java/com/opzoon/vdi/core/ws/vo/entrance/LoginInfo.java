package com.opzoon.vdi.core.ws.vo.entrance;

import java.io.Serializable;
import java.util.List;

import javax.xml.bind.annotation.XmlRootElement;

/**
 * 登录信息.
 */
@XmlRootElement(name = "loginInfo")
public class LoginInfo implements Serializable {

	private static final long serialVersionUID = 1L;

	private String username;
	private String password;
	private String source;
	private List<AuthenticationMethodParamValue> params;
	// FIXME delete it.
	private int domainid;
	private String domainguid;
	private int logintype;
	
	public String getSource() {
		return source;
	}
	public void setSource(String source) {
		this.source = source;
	}
	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	public List<AuthenticationMethodParamValue> getParams() {
		return params;
	}
	public void setParams(List<AuthenticationMethodParamValue> params) {
		this.params = params;
	}
	public int getDomainid()
  {
    return domainid;
  }
  public void setDomainid(int domainid)
  {
    this.domainid = domainid;
  }
  public String getDomainguid() {
		return domainguid;
	}
	public void setDomainguid(String domainguid) {
		this.domainguid = domainguid;
	}
	public int getLogintype() {
		return logintype;
	}
	public void setLogintype(int logintype) {
		this.logintype = logintype;
	}
	
}