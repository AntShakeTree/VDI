package com.vdi.dao.desktop.domain;

import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;

import org.codehaus.jackson.annotate.JsonIgnore;

import com.vdi.common.Constants;
import com.vdi.common.cache.CacheDomain;
import com.vdi.dao.PageRequest;
import com.vdi.support.desktop.lls.domain.resource.Host;

@Entity
public class StorageEntity extends PageRequest<StorageEntity> implements CacheDomain{
	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	private Integer idstorage;
	private String storagename;
	private Integer status;
	@JsonIgnore
	private String storageidentity;
//	@ManyToMany(cascade = CascadeType.REFRESH,fetch=FetchType.EAGER)  
//	@JoinTable(name = "storage_host", inverseJoinColumns = @JoinColumn(name = "hostid"), joinColumns = @JoinColumn(name = "storageid"))  	
//	private List<HostEntity> hosts;
	@ManyToOne(cascade=CascadeType.ALL,fetch=FetchType.EAGER)
	@JoinColumn(name = "hostid")
	private HostEntity host;
	private String address;
	private String path;
	private Integer free;
	private Integer totalsize;
	private String storagetype;
	
	public String getStoragetype() {
		return storagetype;
	}
	public void setStoragetype(String storagetype) {
		this.storagetype = storagetype;
	}
	public String getAddress() {
		return address;
	}
	public void setAddress(String address) {
		this.address = address;
	}
	public String getPath() {
		return path;
	}
	public void setPath(String path) {
		this.path = path;
	}
	public Integer getFree() {
		return free;
	}
	public void setFree(Integer free) {
		this.free = free;
	}

	public Integer getTotalsize() {
		return totalsize;
	}
	public void setTotalsize(Integer totalsize) {
		this.totalsize = totalsize;
	}
	public Integer getIdstorage() {
		return idstorage;
	}
	//
	public void setIdstorage(Integer idstorage) {
		this.idstorage = idstorage;
	}
	//
	public String getStoragename() {
		return storagename;
	}
	//
	public void setStoragename(String storagename) {
		this.storagename = storagename;
	}
	//
	public Integer getStatus() {
		return status;
	}
	//
	public void setStatus(Integer status) {
		this.status = status;
	}
	//
	public String getStorageidentity() {
		return storageidentity;
	}
	//
	public void setStorageidentity(String storageidentity) {
		this.storageidentity = storageidentity;
	}

	
	public HostEntity getHost() {
		return host;
	}
	public void setHost(HostEntity host) {
		this.host = host;
	}


	public static final int CREATING=Constants.CREATING;
	public static final int AVAILABLE=Constants.AVAILABLE;
	public static final int DELETING =Constants.DELETING;
	public static final int UMOUNTING=Constants.UMOUNTING;
	public static final int MOUNTING =Constants.MOUNTING;
	public static final int DISCOVING=Constants.RECOVING;
	public static final int ERROR=Constants.ERROR;

	@Override
	public Object getId() {
		return this.idstorage;
	}
	
}
