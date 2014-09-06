package com.vdi.support.desktop.lls.services.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.vdi.support.desktop.lls.domain.vms.VmInstance;
import com.vdi.support.desktop.lls.services.AsynchronousService;
import com.vdi.support.desktop.lls.services.AsynchronousVmInstanceServices;

@Component("asynchronousVmInstanceServices")
public class AsynchronousVmInstanceServicesImpl  implements
		AsynchronousVmInstanceServices {
	@Autowired AsynchronousService asynchrousService;


	@Override
	public String createVM(VmInstance vmInstance) {
		return asynchrousService.excute(vmInstance.setAction(VmInstance.CREATE_VM_ACTION));
	}

	@Override
	public String deleteVM(String vmid) {
		return asynchrousService.excute(new VmInstance().setAction(VmInstance.DELETE_VM_ACTION)
				.setVmIdentity(vmid));
	}

	@Override
	public String updateVM(VmInstance vmInstance) {
		return asynchrousService.excute(vmInstance.setAction(VmInstance.UPDATE_VM_ACTION));
	}

	@Override
	public String startVM(VmInstance vmid) {
		return asynchrousService.excute(vmid.setAction(VmInstance.START_VM_ACTION));
	}

	@Override
	public String stopVm(String vmid) {
		return asynchrousService.excute(new VmInstance().setAction(VmInstance.STOP_VM_ACTION)
				.setVmIdentity(vmid));
	}

	@Override
	public String systemStopVM(VmInstance vmInstance) {
		return asynchrousService.excute(vmInstance.setAction(VmInstance.SYSTEMSTOP_VM_ACTION));
	}

	@Override
	public String timeoutSystemStopVM(VmInstance vmInstance) {
		return asynchrousService.excute(vmInstance
				.setAction(VmInstance.TIMEOUTSYSTEM_VM_STOP_ACTION));
	}

	@Override
	public String restartVM(String vmid) {
		return asynchrousService.excute(new VmInstance().setAction(VmInstance.RESTART_VM_ACTION)
				.setVmIdentity(vmid));
	}

	@Override
	public String suspendVM(String vmid) {
		return asynchrousService.excute(new VmInstance().setAction(VmInstance.SUSPEND_VM_ACTION)
				.setVmIdentity(vmid));
	}

}
