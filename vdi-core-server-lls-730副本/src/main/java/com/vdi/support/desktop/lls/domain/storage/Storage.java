package com.vdi.support.desktop.lls.domain.storage;


import java.util.List;

import org.codehaus.jackson.type.TypeReference;

import com.vdi.support.desktop.lls.domain.BasicDomain;
import com.vdi.support.desktop.lls.domain.storage.attributes.StorageParams;

/**
 * 
 * @author mxc
 *
 */
public class Storage extends BasicDomain{

	public static  String CREATE_STORAGE_ACTION = "createStorage";
	public static String UPDATE_STORAGE_ACTION = "updateStorage";
	public static String DELETE_STORAGE_ACTION = "deleteStorage";
	public static String GET_STORAGE_ACTION =  "reqGetStorage";
	public static String LIST_STORAGE_ACTION = "reqListStorage";
	public static String UMOUNT_STORAGE_ACTION = "umountStorage";
	public static String MOUNT_STORAGE_ACTION = "mountStorage";
	private String action;
	private Storage mapper;//查询条件
	private String storageName;
	private int free;
	private String storageIdentity;
	private String	storageType;
	private StorageParams storageParams;
	private String status;
	private String hostIdentity;
	private Integer totalSize;
	public Storage getMapper() {
		return mapper;
	}
	
	public int getFree() {
		return free;
	}

	public void setFree(int free) {
		this.free = free;
	}

	public String getAction() {
		return action;
	}

	public void setAction(String action) {
		this.action = action;
	}

	public void setMapper(Storage mapper) {
		this.mapper = mapper;
	}
	public String getStorageName() {
		return storageName;
	}
	public void setStorageName(String storageName) {
		this.storageName = storageName;
	}
	public String getStorageType() {
		return storageType;
	}
	public void setStorageType(String storageType) {
		this.storageType = storageType;
	}
	public StorageParams getStorageParams() {
		return storageParams;
	}
	public void setStorageParams(StorageParams storageParams) {
		this.storageParams = storageParams;
	}

	public static TypeReference<List<Storage>> getStorageListType() {
		return new TypeReference<List<Storage>>() {
		};
	}

	public String getStorageIdentity() {
		return storageIdentity;
	}

	public void setStorageIdentity(String storageIdentity) {
		this.storageIdentity = storageIdentity;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getHostIdentity() {
		return hostIdentity;
	}

	public void setHostIdentity(String hostIdentity) {
		this.hostIdentity = hostIdentity;
	}

	public Integer getTotalSize() {
		return totalSize;
	}

	public void setTotalSize(Integer totalSize) {
		this.totalSize = totalSize;
	}
	//status
	public static final String CREATING = "creating";
	public static final String AVAILABLE="available";
	public static final String DELETING="deleting";
	public static final String MOUNTING="mounting";
	public static final String UMOUNTING="umounting";
	public static final String DISCOVING="discoving";
	
}
