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

@Entity
@Table(name="host")
public class HostEntity {
	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	private Long idhost;
	private String hostname;
	@JoinColumn(name="computepoolid")
	@ManyToOne(cascade=CascadeType.ALL,targetEntity=ComputePoolEntity.class,optional=true,fetch=FetchType.EAGER)
	private ComputePoolEntity computePoolEntity;
	public Long getIdhost() {
		return idhost;
	}
	public void setIdhost(Long idhost) {
		this.idhost = idhost;
	}
	public String getHostname() {
		return hostname;
	}
	public void setHostname(String hostname) {
		this.hostname = hostname;
	}
	public ComputePoolEntity getComputePoolEntity() {
		return computePoolEntity;
	}
	public void setComputePoolEntity(ComputePoolEntity computePoolEntity) {
		this.computePoolEntity = computePoolEntity;
	}
	
}
