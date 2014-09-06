package com.vdi.support.desktop.lls.services;


import java.util.List;

import org.springframework.stereotype.Service;

import com.vdi.support.desktop.lls.domain.resource.ComputePool;
@Service
public interface ComputePoolService {
	List<ComputePool> listComputePool(ComputePool pool);
	ComputePool getComputePool(String computepoolId);
}
