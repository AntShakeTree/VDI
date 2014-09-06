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

public class UserMapBridge{

	
	private Integer idldap;
	private String address;
	private int accesstype;
	private String base;
	private String principal;
	private String dns;
	private String password;
	private int status;
	private String guid;
	private Domain domain;
	private LdapConfigEntity entity;
	private User user;
	private Organization organization;
	private List<User> users;
	
	public List<User> getUsers() {
		return users;
	}

	public void setUsers(List<User> users) {
		this.users = users;
	}

	public Organization getOrganization() {
		return organization;
	}

	public void setOrganization(Organization organization) {
		this.organization = organization;
	}

	public LdapConfigEntity getEntity() {
		return entity;
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public void setEntity(LdapConfigEntity entity) {
		this.entity = entity;
	}

	public Domain getDomain() {
		return domain;
	}

	public void setDomain(Domain domain) {
		this.domain = domain;
	}

	public String getGuid() {
		return guid;
	}

	public void setGuid(String guid) {
		this.guid = guid;
	}

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
	
	public static final int NORMAL=0;
	public static final int DELETING=501;
	public static final int ERROR=500;
	public static final int SYNC=2;
	public static final int SYNCING=3;

	public static final int CREATEING = 1;
	public static final int READ_WRITE=636;
	public static final int READONLY=389;
	public static final int SYNC_USER=4;
}
