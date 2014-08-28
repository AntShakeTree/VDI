/**
 * Project Name:vdi-core-server-lls
 * File Name:TaskHandle.java
 * Package Name:com.vdi.support.desktop.lls.manager.support
 * Date:2014年8月12日上午11:25:47
 * Copyright (c) 2014 All Rights Reserved.
 *
 */

package com.vdi.support.desktop.lls.manager.support;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.vdi.common.ParseJSON;
import com.vdi.common.Session;
import com.vdi.dao.desktop.ComputePoolDao;
import com.vdi.dao.desktop.domain.ComputePoolEntity;
import com.vdi.support.desktop.lls.domain.resource.ComputePool;
import com.vdi.support.desktop.lls.domain.task.Task;
import com.vdi.support.desktop.lls.services.ComputePoolService;

@Component
public class TaskHandle {
	@Autowired
	ComputePoolDao computePoolDao;
	@Autowired
	ComputePoolService computePoolService;

	public void handle(Task task) {
		String taskid = task.getTaskIdentity();
		switch (task.getActionName()) {
		case "createComputePool":
			if (task.getErrorCode() != null && task.getErrorCode() != 0) {
				computePoolDao.excuteHql(
						"update ComputePoolEntity set error=? where taskid=?",
						task.getErrorCode(), taskid);
				Session.removeCache(task.getTaskIdentity());
				return;
			}
			ComputePool b = ParseJSON.convertObjectToDomain(task.getContent(),
					ComputePool.class);
			String cid = b.getComputePoolIdentity();
			ComputePool b2 = this.computePoolService.getComputePool(cid);
			b2 = ParseJSON.convertObjectToDomain(b2.getContent(),
					ComputePool.class);
			// ~~
			ComputePoolEntity computePoolEntity =computePoolDao.findOneByKey("computepoolname", b2.getComputePoolName());
				computePoolEntity.setComputepoolname(b2.getComputePoolName());
				computePoolEntity.setComputePoolIdentity(cid);
				computePoolEntity.setMemoryamount(b2.getWorkHostTotalMem());
				computePoolEntity.setMemoryrest(b2.getWorkHostTotalMem()
						- b.getVmProcTotalMem());
				computePoolEntity.setCpuamount(b2.getWorkHostTotalCpuCoreNum());
				computePoolEntity.setCpurest(b2.getWorkHostTotalCpuCoreNum()
						- b.getHostTotalCpuCoreNum());
			computePoolDao.update(computePoolEntity);
			break;
		default:
			break;
		}
	}
}
