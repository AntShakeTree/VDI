package com.vdi.support.desktop.lls.services.impl;

import java.util.List;

import javax.annotation.Resource;

import org.springframework.stereotype.Component;

import com.vdi.support.desktop.lls.domain.vms.attributes.VDisk;
import com.vdi.support.desktop.lls.manager.LLSSendMessage;
import com.vdi.support.desktop.lls.services.VDiskService;

@Component("vdiskService")
public class VDiskServiceImpl implements VDiskService {
	@Resource(name="llsHandle")
	private LLSSendMessage llsSendMessage;

	@Override
	public List<VDisk> listVDisk(VDisk vdisk) {
		VDisk vdiskMessage = new VDisk();
		vdiskMessage.setAction(VDisk.LIST_VDISK_ACTION);
	    vdisk = (vdisk==null) ? new VDisk():vdisk;
		vdiskMessage.setMapper(vdisk);
	
		return null;
	}

	@Override
	public VDisk getVDisk(String vdiskId) {
		VDisk vdisk = new VDisk();
		vdisk.setAction(VDisk.GET_VDISK_ACTION);
		vdisk.setvDiskIdentity(vdiskId);
		return llsSendMessage.sendMessage(vdisk, VDisk.class);
	}

}
