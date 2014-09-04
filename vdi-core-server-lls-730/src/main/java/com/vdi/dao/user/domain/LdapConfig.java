/**
 * Project Name:vdi-core-server-lls
 * File Name:Ldap.java
 * Package Name:com.vdi.dao.user.domain
 * Date:2014年8月15日上午10:15:45
 * Copyright (c) 2014 All Rights Reserved.
 *
 */

package com.vdi.dao.user.domain;

import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.codehaus.jackson.annotate.JsonIgnore;

import com.vdi.common.cache.CacheDomain;
import com.vdi.dao.PageRequest;

@Entity
@Table(name="ldapconfig")
public class LdapConfig extends PageRequest<LdapConfig> implements CacheDomain{
	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	private Integer idldap;
	private String address;
	private int accesstype;
	private String base;
	private String principal;
	private String dns;
	@Transient
	@JsonIgnore
	private String password;
	private int status;
	
	public int getStatus() {
		return status;
	}
	
	public int getAccesstype() {
		return accesstype;
	}

	public void setAccesstype(int accesstype) {
		this.accesstype = accesstype;
	}

	public String getDns() {
		return dns;
	}

	public void setDns(String dns) {
		this.dns = dns;
	}

	public void setStatus(int status) {
		this.status = status;
	}
	@OneToMany(cascade=CascadeType.ALL,mappedBy="ldapConfig",fetch=FetchType.LAZY)
	private List<Organization> organizations;
	
	public List<Organization> getOrganizations() {
		return organizations;
	}
	public void setOrganizations(List<Organization> organizations) {
		this.organizations = organizations;
	}
	private long synctime;
	
	
	public String getAddress() {
		return address;
	}
	public void setAddress(String address) {
		this.address = address;
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
	public long getSynctime() {
		return synctime;
	}
	public void setSynctime(long synctime) {
		this.synctime = synctime;
	}
	@Override
	@Transient
	@JsonIgnore
	public Object getId() {
		return this.getIdldap();
	}
	
	public static final int NORMAL=0;
	public static final int DELETING=501;
	public static final int ERROR=500;
	public static final int SYNC=2;
	public static final int CREATEING = 1;
	public static final int READ_WRITE=636;
	public static final int READONLY=389;
}
