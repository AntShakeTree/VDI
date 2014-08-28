package com.vdi.support.desktop.lls.services;

import org.springframework.stereotype.Service;

import com.vdi.support.desktop.lls.domain.vms.VmInstance;
@Service
public interface AsynchronousVmInstanceServices {
	public String createVM(VmInstance vmInstance);
	public String deleteVM(String vmid);
	public String updateVM(VmInstance vmInstance);
	public String startVM(VmInstance vimid);
	public String stopVm(String vmid);
	public String systemStopVM(VmInstance vmInstance);
	public String timeoutSystemStopVM(VmInstance vmInstance);
	public String restartVM(String vmid);
	public String suspendVM(String vmId);
}
