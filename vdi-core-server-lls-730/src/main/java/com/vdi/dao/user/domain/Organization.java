/**
 * Project Name:vdi-core-server-lls
 * File Name:Organization.java
 * Package Name:com.vdi.dao.user.domain
 * Date:2014年8月14日下午3:34:21
 * Copyright (c) 2014 All Rights Reserved.
 *
 */

package com.vdi.dao.user.domain;

import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Transient;

import org.codehaus.jackson.annotate.JsonIgnore;

import com.vdi.common.cache.CacheDomain;
import com.vdi.dao.Request;

@Entity
public class Organization implements Request<Organization>, CacheDomain {
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Integer idorganization;
	private String organizationname;
	@ManyToOne(fetch = FetchType.EAGER)
	// 这里设置JoinColum设置了外键的名字，并且parent是关系维护端
	@JoinColumn(name = "parent")
	private Organization parent;
	@OneToMany(cascade = { CascadeType.ALL }, mappedBy = "organization", targetEntity = User.class, fetch = FetchType.LAZY)
	private Set<User> users;
	@OneToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER, mappedBy = "parent")
	private Set<Organization> childrens;
	@ManyToMany(cascade=CascadeType.REFRESH,targetEntity=DeliveryGroup.class,mappedBy="origanazations")
	private Set<DeliveryGroup> groups;
	private String guid;
	private String distinguishedName;
	private int level=0;
	@ManyToOne
	@JoinColumn(name="ldapconfigid")
	private LdapConfig ldapConfig;
	
	public LdapConfig getLdapConfig() {
		return ldapConfig;
	}

	public void setLdapConfig(LdapConfig ldapConfig) {
		this.ldapConfig = ldapConfig;
	}

	public Set<DeliveryGroup> getGroups() {
		return groups;
	}

	public void setGroups(Set<DeliveryGroup> groups) {
		this.groups = groups;
	}

	public int getLevel() {
		return level;
	}

	public void setLevel(int level) {
		this.level = level;
	}

	public Integer getIdorganization() {
		return idorganization;
	}

	public void setIdorganization(Integer idorganization) {
		this.idorganization = idorganization;
	}

	public String getOrganizationname() {
		return organizationname;
	}

	public void setOrganizationname(String organizationname) {
		this.organizationname = organizationname;
	}

	public Organization getParent() {
		return parent;
	}

	public Organization setParent(Organization parent) {
		this.parent = parent;
		return this;
	}

	public Set<User> getUsers() {
		return users;
	}

	public void setUsers(Set<User> users) {
		this.users = users;
	}

	public Set<Organization> getChildrens() {
		return childrens;
	}

	public void setChildrens(Set<Organization> childrens) {
		this.childrens = childrens;
	}

	@Override
	@Transient
	@JsonIgnore
	public Object getId() {
		return this.getIdorganization();
	}

	public String getGuid() {
		return guid;
	}

	public void setGuid(String guid) {
		this.guid = guid;
	}
	@Transient
	public String getDistinguishedName() {
		return distinguishedName;
	}

	public void setDistinguishedName(String distinguishedName) {
		this.distinguishedName = distinguishedName;
	}

}
