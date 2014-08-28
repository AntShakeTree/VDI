package com.vdi.support.desktop.lls.services;

import java.util.List;

import org.springframework.stereotype.Service;

import com.vdi.support.desktop.lls.domain.storage.Storage;

@Service
public interface StorageService {
	List<Storage>  listStorage(Storage Storage);
	Storage getStorage(String StorageId);
}
