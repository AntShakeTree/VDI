package com.vdi.support.desktop.lls.services.impl;


import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.vdi.common.ParseJSON;
import com.vdi.support.desktop.lls.domain.storage.Storage;
import com.vdi.support.desktop.lls.manager.LLSSendMessage;
import com.vdi.support.desktop.lls.services.StorageService;

@Component("storageService")
public class StorageServiceImpl implements StorageService {
	@Autowired
	@Qualifier("llsHandle")
	private LLSSendMessage llsSendMessage;

	@Override
	public List<Storage> listStorage(Storage storage) {
		Storage storageMessage = new Storage();
		storageMessage.setAction(Storage.GET_STORAGE_ACTION);
	    storage = (storage==null) ? new Storage():storage;
		storageMessage.setMapper(storage);
		storage =llsSendMessage.sendMessage(storage,Storage.class);
		return ParseJSON.convertObjectToDomain(storage.getContent(),Storage.getStorageListType());
	}

	@Override
	public Storage getStorage(String storageId) {
		Storage storage = new Storage();
		storage.setAction(Storage.LIST_STORAGE_ACTION);
		storage.setStorageIdentity(storageId);
		return llsSendMessage.sendMessage(storage,Storage.class);
	}

}
