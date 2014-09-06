package com.vdi.facade.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.vdi.dao.desktop.StorageDao;
import com.vdi.dao.desktop.domain.StorageEntity;
import com.vdi.dao.desktop.domain.build.StorageBuild;
import com.vdi.facade.StorageFacad;
import com.vdi.support.desktop.lls.domain.storage.Storage;
import com.vdi.support.desktop.lls.services.StorageService;
import com.vdi.vo.req.StorageIdReq;
import com.vdi.vo.res.Header;
import com.vdi.vo.res.ListStorage;
import com.vdi.vo.res.ListStorage.StorageList;
import com.vdi.vo.res.StorageResponse;
@Service
public class StorageFacadImpl implements StorageFacad {
	
	private @Autowired StorageDao storageDao;
	private @Autowired StorageService storageService;
	@Override
	public ListStorage listStorage(StorageEntity entity) {
		Header head=new Header();
		int error=0;
		ListStorage res = new ListStorage();
		StorageList body=new StorageList();
		List<StorageEntity> cs =storageDao.listRequest(entity);
		body.setList(cs);
		head.setError(error);
		res.setHead(head);
		res.setBody(body);
		res.setPage(entity);
		return res;
	}
	@Override
	public StorageResponse getStorage(StorageIdReq req) {
		StorageResponse response =new StorageResponse();
		Header head=new Header();
		head.setError(0);
		response.setHead(head);
		StorageEntity entity = storageDao.get(StorageEntity.class, req.getStorageid());
		if(entity==null){
			return response;
		}
		
		String identity  =entity.getStorageidentity();
		
		Storage storage= storageService.getStorage(identity);
		if(storage==null){
			entity.setStatus(StorageEntity.ERROR);	
			return response;
		}else{
			entity=new StorageBuild(entity, storage).entity_address().entity_free().entity_path().entity_storageidentity().entity_storagename().entity_totalize().entity_status().buildEntity();
		}
		
		return response;
	}
}
