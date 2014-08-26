package com.vdi.support.desktop.lls.services.impl;


import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.vdi.common.ParseJSON;
import com.vdi.support.desktop.lls.domain.resource.ComputePool;
import com.vdi.support.desktop.lls.manager.LLSSendMessage;
import com.vdi.support.desktop.lls.services.ComputePoolService;

/**
 * @author mxc
 * 
 */
@Service("computePoolService")
public class ComputePoolServiceImpl implements ComputePoolService {
	static Logger log = LoggerFactory.getLogger(ComputePoolServiceImpl.class);

	@Autowired
	@Qualifier("llsHandle")
	private LLSSendMessage llsSendMessage;

	public List<ComputePool> listComputePool(ComputePool pool) {
		ComputePool poolMessage = new ComputePool();
		if (pool == null) {
			poolMessage.setMapper(new ComputePool());
		} else {
			poolMessage.setMapper(pool);
		}
		poolMessage.setAction(ComputePool.LIST_COMPUTEPOOL_ACTION);
		pool=llsSendMessage.sendMessage(poolMessage, ComputePool.class);
		return 	ParseJSON.convertObjectToDomain(pool.getContent(), ComputePool.getComputePoolListType());
	}

	@Override
	public ComputePool getComputePool(String computepoolId) {
		ComputePool pool = new ComputePool();
		pool.setAction(ComputePool.GET_COMPUTEPOOL_ACTION);
		pool.setComputePoolIdentity(computepoolId);
		return llsSendMessage.sendMessage(pool,ComputePool.class);
	}

}
