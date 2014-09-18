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
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
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
	private Integer parent;
	@ManyToMany(cascade=CascadeType.REFRESH,targetEntity=DeliveryGroup.class,mappedBy="origanazations")
	private Set<DeliveryGroup> groups;
	private String guid;
	private String binddn;
	private int level=0;
	private String fullname;
	private String domainguid;
	@JsonIgnore
	private Integer ldapconfigid;
	private int status;
	
	public Integer getLdapconfigid() {
		return ldapconfigid;
	}

	public void setLdapconfigid(Integer ldapconfigid) {
		this.ldapconfigid = ldapconfigid;
	}

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	public Integer getParent() {
		return parent;
	}

	public void setParent(Integer parent) {
		this.parent = parent;
	}

	public String getDomainguid() {
		return domainguid;
	}

	public void setDomainguid(String domainguid) {
		this.domainguid = domainguid;
	}

	public String getFullname() {
		return fullname;
	}

	public void setFullname(String fullname) {
		this.fullname = fullname;
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

	public String getBinddn() {
		return binddn;
	}

	public void setBinddn(String binddn) {
		this.binddn = binddn;
	}

}
