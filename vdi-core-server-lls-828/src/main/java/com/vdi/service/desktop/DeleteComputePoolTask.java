/**
 * Project Name:vdi-core-server-lls
 * File Name:DeleteComputePoolTask.java
 * Package Name:com.vdi.service.desktop
 * Date:2014年8月12日下午1:48:09
 * Copyright (c) 2014 All Rights Reserved.
 *
*/

package com.vdi.service.desktop;

import com.vdi.support.desktop.lls.services.AsynchronousComputePoolService;
import com.vdi.vo.req.DeleteComputePool;
public class DeleteComputePoolTask implements Runnable {
	private DeleteComputePool entity;
	private AsynchronousComputePoolService computePoolService;
	public DeleteComputePoolTask(DeleteComputePool entity,AsynchronousComputePoolService computePoolService) {
		this.entity=entity;
		this.computePoolService=computePoolService;
	}

	@Override
	public void run() {
		computePoolService.deleteComputePool(entity);
	}

}
