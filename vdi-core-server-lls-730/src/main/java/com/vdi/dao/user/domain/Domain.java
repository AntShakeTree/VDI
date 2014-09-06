/**
 * Project Name:vdi-core-server-lls
 * File Name:Domain.java
 * Package Name:com.vdi.dao.user.domain
 * Date:2014年8月15日上午9:27:26
 * Copyright (c) 2014 All Rights Reserved.
 *
 */

package com.vdi.dao.user.domain;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.codehaus.jackson.annotate.JsonIgnore;
import org.codehaus.jackson.map.annotate.JsonSerialize;
import org.codehaus.jackson.map.annotate.JsonSerialize.Inclusion;

import com.vdi.common.cache.CacheDomain;
import com.vdi.dao.PageRequest;

@Entity
@Table(name="domainentity")
@JsonSerialize(include = Inclusion.NON_DEFAULT)
public class Domain extends PageRequest<Domain> implements CacheDomain {

	private static final long serialVersionUID = 1L;

	/**
	 * 域类型: 本地域.
	 */
	public static final int DOMAIN_TYPE_LOCAL = 0x0;
	/**
	 * 域类型: MSAD同步域.
	 */
	public static final int DOMAIN_TYPE_MSAD = 0x1;
	/**
	 * 域类型: ApacheDS同步域.
	 */
	public static final int DOMAIN_TYPE_APACHE = 0x2;

	/**
	 * 状态: 正常.
	 */
	public static final int DOMAIN_STATUS_NORMAL = 0;
	/**
	 * 状态: 维护中.
	 */
	public static final int DOMAIN_STATUS_MAINTAINING = 1;
	/**
	 * 状态: 删除中.
	 */
	public static final int DOMAIN_STATUS_DELETING = 2;
	/**
	 * 状态: 同步中.
	 */
	public static final int DOMAIN_STATUS_SYNCHRONIZING = 3;

	/**
	 * 默认本地域的ID.
	 */
	public static final int DEFAULT_DOMAIN_ID = 0;
	@Id
	private String guid;
	private String domainname;
	private String address;
	private String dns;
	private int accesstype;
	private String principal;
	private int domaintype;
	private String domainnetworkname;
	private String domainservername;
	private int domainserverport;
	private String domainbinddn;
	private String domainbindpass;
	private String notes;
	private int status;
	private int passwordlen;
	private boolean passwordpolicy;

	public int getPasswordlen() {
		return passwordlen;
	}

	public void setPasswordlen(int passwordlen) {
		this.passwordlen = passwordlen;
	}

	public boolean isPasswordpolicy() {
		return passwordpolicy;
	}

	public void setPasswordpolicy(boolean passwordpolicy) {
		this.passwordpolicy = passwordpolicy;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public String getDns() {
		return dns;
	}

	public void setDns(String dns) {
		this.dns = dns;
	}



	public int getAccesstype() {
		return accesstype;
	}

	public void setAccesstype(int accesstype) {
		this.accesstype = accesstype;
	}

	public String getPrincipal() {
		return principal;
	}

	public void setPrincipal(String principal) {
		this.principal = principal;
	}

	public String getGuid() {
		return guid;
	}

	public void setGuid(String guid) {
		this.guid = guid;
	}

	public String getDomainname() {
		return domainname;
	}

	public void setDomainname(String domainname) {
		this.domainname = domainname;
	}

	public int getDomaintype() {
		return domaintype;
	}

	public void setDomaintype(int domaintype) {
		this.domaintype = domaintype;
	}

	public String getDomainnetworkname() {
		return domainnetworkname;
	}

	public void setDomainnetworkname(String domainnetworkname) {
		this.domainnetworkname = domainnetworkname;
	}

	public String getDomainservername() {
		return domainservername;
	}

	public void setDomainservername(String domainservername) {
		this.domainservername = domainservername;
	}

	public int getDomainserverport() {
		return domainserverport;
	}

	public void setDomainserverport(int domainserverport) {
		this.domainserverport = domainserverport;
	}

	public String getDomainbinddn() {
		return domainbinddn;
	}

	public void setDomainbinddn(String domainbinddn) {
		this.domainbinddn = domainbinddn;
	}

	public String getDomainbindpass() {
		return domainbindpass;
	}

	public void setDomainbindpass(String domainbindpass) {
		this.domainbindpass = domainbindpass;
	}

	public String getNotes() {
		return notes;
	}

	public void setNotes(String notes) {
		this.notes = notes;
	}

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	public static long getSerialversionuid() {
		return serialVersionUID;
	}

	@Override
	@JsonIgnore
	@Transient
	public Object getId() {
		return this.getGuid();
	}
	@Transient
	@JsonIgnore
	public LdapConfig getConfig(){
		LdapConfig config = new LdapConfig();
		//VDIBeanUtils.copyPropertiesByNotNull(this, config, null);
		config.setAccesstype(this.accesstype);
		config.setAddress(this.address);config.setDns(this.getDns());
		config.setBase(this.domainbinddn);
		config.setGuid(this.getGuid());
		config.setPassword(this.getDomainbindpass());
		config.setPrincipal(this.getPrincipal());
		return config;
	}
}
