/**
 * Project Name:vdi-core-server-lls
 * File Name:Ldap.java
 * Package Name:com.vdi.dao.user.domain
 * Date:2014年8月15日上午10:15:45
 * Copyright (c) 2014 All Rights Reserved.
 *
 */

package com.vdi.dao.user.domain;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;

@Entity
@Table(name="ldapconfig")
public class LdapConfig {
	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	private Integer idldap;
	private String url;
	private String base;
	private String principal;
	@Transient
	private String password;
	private String domainguid;
	private long synctime;
	public String getUrl() {
		return url;
	}
	public void setUrl(String url) {
		this.url = url;
	}
	public String getBase() {
		return base;
	}
	public void setBase(String base) {
		this.base = base;
	}
	
	public String getPrincipal() {
		return principal;
	}
	public void setPrincipal(String principal) {
		this.principal = principal;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	public Integer getIdldap() {
		return idldap;
	}
	public void setIdldap(Integer idldap) {
		this.idldap = idldap;
	}
	public String getDomainguid() {
		return domainguid;
	}
	public void setDomainguid(String domainguid) {
		this.domainguid = domainguid;
	}
	public long getSynctime() {
		return synctime;
	}
	public void setSynctime(long synctime) {
		this.synctime = synctime;
	}
	
}
