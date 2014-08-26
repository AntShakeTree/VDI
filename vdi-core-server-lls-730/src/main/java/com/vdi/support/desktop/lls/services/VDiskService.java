package com.vdi.support.desktop.lls.services;

import java.util.List;

import org.springframework.stereotype.Service;

import com.vdi.support.desktop.lls.domain.vms.attributes.VDisk;
@Service
public interface VDiskService {
	List<VDisk> listVDisk(VDisk pool);
	VDisk getVDisk(String VDiskpoolId);
}
