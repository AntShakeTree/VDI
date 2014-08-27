package com.opzoon.vdi.core.facade.transience;

import com.opzoon.vdi.core.facade.StorageFacade;

/**
 * 桌面状态相关业务接口.
 */
public class CloudManagerStatusFacade {

	private StorageFacade storageFacade;

	public int findStatus(int idcloudmanager) {
		return (Integer) storageFacade.findFirst(
				"select status from CloudManagerStatus where idcloudmanager = ?",
				idcloudmanager);
	}

	/*
	public void createNewStatus(int idcloudmanager) {
		CloudManagerStatus cloudManagerStatus = new CloudManagerStatus();
		cloudManagerStatus.setIdcloudmanager(idcloudmanager);
		cloudManagerStatus.setStatus(CLOUD_MANAGER_STATUS_OK);
		storageFacade.persist(cloudManagerStatus);
	}
	*/

	public void deleteStatus(int idcloudmanager) {
		storageFacade.update(
				"delete from CloudManagerStatus where idcloudmanager = ?",
				idcloudmanager);
	}

	public void markAsOK(int idcloudmanager) {
//		CloudManagerStatus cloudManagerStatus = storageFacade.load(CloudManagerStatus.class, idcloudmanager);
//		if (cloudManagerStatus != null) {
//			cloudManagerStatus.setStatus(CLOUD_MANAGER_STATUS_OK);
//			storageFacade.merge(cloudManagerStatus);
//		}
	}

	public void markAsAbnormal(int idcloudmanager) {
//		CloudManagerStatus cloudManagerStatus = storageFacade.load(CloudManagerStatus.class, idcloudmanager);
//		if (cloudManagerStatus != null) {
//			cloudManagerStatus.setStatus(CLOUD_MANAGER_STATUS_ABNORMAL);
//			storageFacade.merge(cloudManagerStatus);
//		}
	}

	public void setStorageFacade(StorageFacade storageFacade) {
		this.storageFacade = storageFacade;
	}

}
