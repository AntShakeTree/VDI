/**
 * Project Name:vdi-core-server-lls
 * File Name:ComputePool.java
 * Package Name:com.vdi.dao.desktop.domain
 * Date:2014年8月11日上午11:23:13
 * Copyright (c) 2014 All Rights Reserved.
 *
*/

package com.vdi.dao.desktop.domain;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.codehaus.jackson.map.annotate.JsonSerialize;
import org.codehaus.jackson.map.annotate.JsonSerialize.Inclusion;

import com.vdi.common.Constants;
import com.vdi.common.cache.CacheDomain;
import com.vdi.dao.PageRequest;

@Entity
@Table(name="computepool")
@JsonSerialize(include=Inclusion.NON_DEFAULT)
@JsonIgnoreProperties({"taskid","computePoolIdentity"})
public class ComputePoolEntity extends PageRequest<ComputePoolEntity> implements CacheDomain{
	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	private Long idcomputepool;
	@Column(nullable=false,unique=true)
	private String computepoolname;
	private int cpuamount;
	private int cpurest;
	private int memoryamount;
	private int memoryrest;
	private String dispatchtype;
	private String taskid;
	private String note;
	private String computePoolIdentity;
	private int status;
	@OneToMany(cascade=CascadeType.ALL,mappedBy="computePoolEntity", orphanRemoval=true,targetEntity=HostEntity.class,fetch=FetchType.EAGER)
	private List<HostEntity> hosts=new ArrayList<HostEntity>();
	

	public int getStatus() {
		return status;
	}
	public void setStatus(int status) {
		this.status = status;
	}
	public String getComputepoolname() {
		return computepoolname;
	}
	public void setComputepoolname(String computepoolname) {
		this.computepoolname = computepoolname;
	}
	public int getCpuamount() {
		return cpuamount;
	}
	public void setCpuamount(int cpuamount) {
		this.cpuamount = cpuamount;
	}
	public int getCpurest() {
		return cpurest;
	}
	public void setCpurest(int cpurest) {
		this.cpurest = cpurest;
	}
	public int getMemoryamount() {
		return memoryamount;
	}
	public void setMemoryamount(int memoryamount) {
		this.memoryamount = memoryamount;
	}
	public int getMemoryrest() {
		return memoryrest;
	}
	public void setMemoryrest(int memoryrest) {
		this.memoryrest = memoryrest;
	}
	public List<HostEntity> getHosts() {
		return hosts;
	}
	public void setHosts(List<HostEntity> hosts) {
		this.hosts = hosts;
	}
	@Override
	public Object getId() {
		return this.getIdcomputepool();
	}
	public String getDispatchtype() {
		return dispatchtype;
	}
	public void setDispatchtype(String dispatchtype) {
		this.dispatchtype = dispatchtype;
	}
	public String getTaskid() {
		return taskid;
	}
	public void setTaskid(String taskid) {
		this.taskid = taskid;
	}


	public String getNote() {
		return note;
	}
	public void setNote(String note) {
		this.note = note;
	}
	public String getComputePoolIdentity() {
		return computePoolIdentity;
	}
	public void setComputePoolIdentity(String computePoolIdentity) {
		this.computePoolIdentity = computePoolIdentity;
	}
	public Long getIdcomputepool() {
		return idcomputepool;
	}
	public void setIdcomputepool(Long idcomputepool) {
		this.idcomputepool = idcomputepool;
	}
	public static final int CREATING = Constants.CREATING;
	public static final int AVAILABLE = Constants.AVAILABLE;
	public static final int DELETING = Constants.DELETING;
	public static final int HOSTADDING = 503;
	public static final int HOSTREMOVEING = 504;
	public static final int UMOUNTING = 505;
	public static final int MOUNTING = 506;
	public static final int ERROR=Constants.ERROR;
} 
	
//	
	
	
