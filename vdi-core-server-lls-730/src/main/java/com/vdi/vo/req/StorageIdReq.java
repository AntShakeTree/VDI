package com.vdi.vo.req;

import com.vdi.dao.Request;
import com.vdi.dao.desktop.domain.StorageEntity;

public class StorageIdReq implements Request<StorageEntity>{
	private Integer storageid;

	public Integer getStorageid() {
		return storageid;
	}

	public void setStorageid(Integer storageid) {
		this.storageid = storageid;
	}
	
}
