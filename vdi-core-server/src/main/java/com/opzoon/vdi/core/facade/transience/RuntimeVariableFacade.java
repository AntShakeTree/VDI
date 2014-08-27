package com.opzoon.vdi.core.facade.transience;

import static com.opzoon.vdi.core.facade.FacadeHelper.exists;

import com.opzoon.vdi.core.facade.StorageFacade;

/**
 * 运行时变量相关业务接口.
 */
public class RuntimeVariableFacade {

	private StorageFacade storageFacade;

	public boolean findIfServiceIsStopped() {
		return exists(storageFacade.findFirst(
				"select count(idruntimevariable) from RuntimeVariable where name = ? and value = ?",
				"service.disabled", "y"));
	}

	public void enableService() {
		storageFacade.update(
				"update RuntimeVariable set value = ? where name = ?",
				"n", "service.disabled");
	}

	public void disableService() {
		storageFacade.update(
				"update RuntimeVariable set value = ? where name = ?",
				"y", "service.disabled");
	}
	
	public void setStorageFacade(StorageFacade storageFacade) {
		this.storageFacade = storageFacade;
	}

}
