package com.vdi.facade;

import com.vdi.dao.desktop.domain.StorageEntity;
import com.vdi.vo.req.StorageIdReq;
import com.vdi.vo.res.ListStorage;
import com.vdi.vo.res.StorageResponse;

public interface StorageFacad {
	public ListStorage listStorage(StorageEntity entity);
	public StorageResponse getStorage(StorageIdReq req);
}
