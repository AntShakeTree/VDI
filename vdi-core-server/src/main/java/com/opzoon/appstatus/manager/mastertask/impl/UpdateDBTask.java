package com.opzoon.appstatus.manager.mastertask.impl;

import com.opzoon.appstatus.common.exception.AppstatusRestException;
import com.opzoon.appstatus.domain.NodeUpdate;
import com.opzoon.appstatus.executor.dao.AppStatusDao;
import com.opzoon.appstatus.manager.mastertask.MasterTask;

public class UpdateDBTask implements MasterTask {
	
	private NodeUpdate nodeUpdate;
	
	private AppStatusDao appStatusDao;
	
	public UpdateDBTask() {
		
	}
	
	public UpdateDBTask(NodeUpdate nodeUpdate, AppStatusDao appStatusDao) {
		this.nodeUpdate = nodeUpdate;
		this.appStatusDao = appStatusDao;
	}

	@Override
	public void process() throws AppstatusRestException {
		appStatusDao.updateNode(nodeUpdate);
	}
	
	@Override
	public String toString() {
		return nodeUpdate.toString();
	}
}
