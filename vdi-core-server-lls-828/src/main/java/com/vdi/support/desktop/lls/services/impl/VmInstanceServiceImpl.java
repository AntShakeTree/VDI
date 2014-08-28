package com.vdi.support.desktop.lls.services.impl;

import java.util.List;

import javax.annotation.Resource;

import org.springframework.stereotype.Component;

import com.vdi.common.ParseJSON;
import com.vdi.support.desktop.lls.domain.Where;
import com.vdi.support.desktop.lls.domain.vms.VmInstance;
import com.vdi.support.desktop.lls.manager.LLSSendMessage;
import com.vdi.support.desktop.lls.services.VmInstanceService;

@Component("vminstanceService")
public class VmInstanceServiceImpl implements VmInstanceService {
	
	private @Resource(name="llsHandle") LLSSendMessage llsSendMessage;
	
	@Override
	public List<VmInstance> listVmInstance(VmInstance vminstance,Where where) {
		VmInstance vminstanceMessage = new VmInstance();
		vminstanceMessage.setAction(VmInstance.LIST_VM_ACTION);
	    vminstance = (vminstance==null) ? new VmInstance():vminstance;
	    vminstanceMessage.setMapper(vminstance);
		vminstanceMessage.setLimit(where.getLimit());
		vminstanceMessage.setOrderBy(where.getOrderBy());
		vminstanceMessage.setOffset(where.getOffset());
		vminstanceMessage=llsSendMessage.sendMessage(vminstanceMessage, VmInstance.class);
		return ParseJSON.convertObjectToDomain(vminstanceMessage.getContent(),VmInstance.getVmInstanceListType());
	}
	@Override
	public VmInstance getVmInstance(String vminstanceId) {
		VmInstance vminstance = new VmInstance();
		vminstance.setAction(VmInstance.GET_VM_ACTION);
		vminstance.setVmIdentity(vminstanceId);
		return ParseJSON.convertObjectToDomain(llsSendMessage.sendMessage(vminstance,VmInstance.class).getContent(), VmInstance.class);
	}
}
