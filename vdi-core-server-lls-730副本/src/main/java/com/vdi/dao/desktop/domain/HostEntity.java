/**
 * Project Name:vdi-core-server-lls
 * File Name:HostEntity.java
 * Package Name:com.vdi.dao.desktop.domain
 * Date:2014年8月11日下午1:27:20
 * Copyright (c) 2014 All Rights Reserved.
 *
*/

package com.vdi.dao.desktop.domain;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.codehaus.jackson.annotate.JsonBackReference;
import org.codehaus.jackson.annotate.JsonIgnore;
import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.codehaus.jackson.annotate.JsonManagedReference;

import com.vdi.common.Constants;
import com.vdi.common.cache.CacheDomain;
import com.vdi.dao.PageRequest;
import com.vdi.dao.Request;

@Entity
@Table(name="host")
@JsonIgnoreProperties(value={"hostidentity"})
public class HostEntity extends PageRequest<HostEntity> implements CacheDomain{
	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	private Long idhost;
	private String hostname;
	private String address;
	@JsonIgnore
	private String hostidentity;
	private Integer status;

	@ManyToOne
	@JoinColumn(name="computepoolid")
	@JsonIgnore
	private ComputePoolEntity computePoolEntity;
	public String getHostidentity() {
		return hostidentity;
	}
	public void setHostidentity(String hostidentity) {
		this.hostidentity = hostidentity;
	}
	
	public String getAddress() {
		return address;
	}
	public void setAddress(String address) {
		this.address = address;
	}


	public Long getIdhost() {
		return idhost;
	}
	public void setIdhost(Long idhost) {
		this.idhost = idhost;
	}

	
	public ComputePoolEntity getComputePoolEntity() {
		return computePoolEntity;
	}
	public void setComputePoolEntity(ComputePoolEntity computePoolEntity) {
		this.computePoolEntity = computePoolEntity;
	}
	@Override
	@JsonIgnore
	public Object getId() {
		return idhost;
	}


	public String getHostname() {
		return hostname;
	}
	public void setHostname(String hostname) {
		this.hostname = hostname;
	}
	
	
	
	public Integer getStatus() {
		return status;
	}
	public void setStatus(Integer status) {
		this.status = status;
	}
	public static final int CREATING=Constants.CREATING;
	public static final int FREE=Constants.AVAILABLE;
	public static final int DELETING=Constants.DELETING;
	public static final int WORK=2;
	public static final  int DISCON=510;
	public static final int WORKDIS=507;
	public static final  int FREEDIS=508;
	public static final int RECOVING=Constants.RECOVING;
	public static final int ERROR=Constants.ERROR;
}
