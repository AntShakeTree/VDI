package com.vdi.support.desktop.lls.services;

import java.util.List;

import org.springframework.stereotype.Service;

import com.vdi.support.desktop.lls.domain.Where;
import com.vdi.support.desktop.lls.domain.vms.VmInstance;
@Service
public interface VmInstanceService {
	List<VmInstance> listVmInstance(VmInstance vminstance,Where where);
	VmInstance getVmInstance(String VmInstanceId);
}
