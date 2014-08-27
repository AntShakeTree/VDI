package com.opzoon.vdi.core.facade;

import static com.opzoon.vdi.core.facade.CommonException.NO_ERRORS;

import com.opzoon.vdi.core.domain.PublicKeyEntity;

public class PublicKeyFacade {
	private DatabaseFacade storageFacade;
	
	public int createPublicKey(PublicKeyEntity publickey) throws CommonException {
		storageFacade.update("delete from PublicKeyEntity");
		storageFacade.persist(publickey);
		return NO_ERRORS;
	}
	
	public PublicKeyEntity getPublicKey()
	{
		return (PublicKeyEntity) storageFacade.findFirst("from PublicKeyEntity");
	}

	public void setStorageFacade(DatabaseFacade storageFacade) {
		this.storageFacade = storageFacade;
	}
}
