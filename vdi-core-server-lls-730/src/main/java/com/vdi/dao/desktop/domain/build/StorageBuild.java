package com.vdi.dao.desktop.domain.build;


import static org.springframework.util.StringUtils.isEmpty;

import org.springframework.util.Assert;

import com.vdi.dao.desktop.domain.StorageEntity;
import com.vdi.support.desktop.lls.domain.storage.Storage;
import com.vdi.support.desktop.lls.domain.storage.attributes.StorageParams;

public class StorageBuild extends Storage {
	public Storage storage;
	private StorageEntity entity;

	public StorageBuild(StorageEntity entity, Storage storage) {
		Assert.notNull(entity);
		Assert.notNull(storage);
		this.entity = entity;
		this.storage = storage;
	}

	public StorageBuild lls_storageName() {
		if(!isEmpty(entity.getStoragename())){
			storage.setStorageName(entity.getStoragename());
		}
		return this;
	}

	public StorageBuild lls_free() {
		storage.setFree(entity.getFree());
		return this;
	}

	public StorageBuild lls_storageIdentity() {
		if(!isEmpty(entity.getStorageidentity())){
			storage.setStorageIdentity(entity.getStorageidentity());
		}
		return this;
	}

	public StorageBuild lls_storageType() {
		if(!isEmpty(entity.getStoragetype())){
			storage.setStorageType(entity.getStoragetype());
		}
		return this;
	}

	public StorageBuild lls_storageParams() {
		StorageParams params=new StorageParams();
		if(!isEmpty(entity.getAddress())){
			params.setAddr(entity.getAddress());
		}
		if(!isEmpty(entity.getPath())){
			params.setPath(entity.getPath());
		}
		storage.setStorageParams(params);
		return this;
	}

	

	public StorageBuild lls_hostIdentity() {
		if(entity.getHost()!=null&&!isEmpty(entity.getHost().getHostidentity())){
			storage.setHostIdentity(entity.getHost().getHostidentity());
		}
		return this;
	}

	public StorageBuild lls_totalSize() {
		if(entity.getTotalsize()!=null)
			storage.setTotalSize(entity.getTotalsize());
		return this;
	}

	public StorageBuild entity_storagename() {
		if(!isEmpty(storage.getStorageName())){
			entity.setStoragename(storage.getStorageName());
		}
		return this;
	}

	public StorageBuild entity_status() {
		switch (storage.getStatus()) {
		case Storage.CREATING:
			entity.setStatus(StorageEntity.CREATING);
			break;
		case Storage.AVAILABLE:
			entity.setStatus(StorageEntity.AVAILABLE);
			break;
		case Storage.DELETING:
			entity.setStatus(StorageEntity.DELETING);
			break;
		case Storage.MOUNTING:
			entity.setStatus(StorageEntity.MOUNTING);
			break;
		case Storage.UMOUNTING:
			entity.setStatus(StorageEntity.UMOUNTING);
			break;
		default:
			break;
		}
		return this;
	}

	public StorageBuild entity_storageidentity() {
		if(!isEmpty(storage.getStorageIdentity())){
			entity.setStorageidentity(storage.getStorageIdentity());
		}
		return this;
	}

	

	public StorageBuild entity_address() {
		if(storage.getStorageParams()!=null&&!isEmpty(storage.getStorageParams().getAddr())){
			entity.setAddress(storage.getStorageParams().getAddr());
		}
		return this;
	}

	public StorageBuild entity_path() {
		if(storage.getStorageParams()!=null&&!isEmpty(storage.getStorageParams().getPath())){
			entity.setAddress(storage.getStorageParams().getPath());
		}
		return this;
	}

	public StorageBuild entity_free() {
		entity.setFree(storage.getFree());
		return this;
	}

	public StorageBuild entity_totalize() {
		entity.setTotalsize(storage.getTotalSize());
		return this;
	}

	public Storage buildLLSDomain() {
		return this.storage;
	}

	public StorageEntity buildEntity() {
		return this.entity;
	}
}
