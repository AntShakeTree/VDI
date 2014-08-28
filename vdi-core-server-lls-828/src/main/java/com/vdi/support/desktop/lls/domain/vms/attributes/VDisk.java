package com.vdi.support.desktop.lls.domain.vms.attributes;

import java.lang.reflect.Type;

import com.vdi.support.desktop.lls.domain.BasicDomain;

public class VDisk extends BasicDomain{
	public static String CREATE_VDISK_ACTION = "createVDisk";
	public static String UPDATE_VDISK_ACTION = "updateVDisk";
	public static String DELETE_VDISK_ACTION = "deleteVDisk";
	public static String GET_VDISK_ACTION = "reqGetVDisk";
	public static String LIST_VDISK_ACTION = "reqListVDisk";
	public static String VM_HOST_ADD_VDISK_ACTION = "vmHotAddVDisk";
	public static String VM_HOST_DEL_VDISK_ACTION = "vmHotDelVDisk";
	private String action;
	private VDisk mapper;// 查询条件
	private int index;
	private String vDiskStorageName;
	private String vDiskName;
	private String vmIdentity;// ：虚拟机的id
	private String vDiskIdentity;// ：虚拟硬盘的id

	public String getVmIdentity() {
		return vmIdentity;
	}

	public void setVmIdentity(String vmIdentity) {
		this.vmIdentity = vmIdentity;
	}

	public String getvDiskIdentity() {
		return vDiskIdentity;
	}

	public void setvDiskIdentity(String vDiskIdentity) {
		this.vDiskIdentity = vDiskIdentity;
	}

	public String getvDiskStorageName() {
		return vDiskStorageName;
	}

	public int getIndex() {
		return index;
	}

	public void setIndex(int index) {
		this.index = index;
	}

	public void setvDiskStorageName(String vDiskStorageName) {
		this.vDiskStorageName = vDiskStorageName;
	}

	public String getvDiskName() {
		return vDiskName;
	}

	public void setvDiskName(String vDiskName) {
		this.vDiskName = vDiskName;
	}

	public String getAction() {
		return action;
	}

	public void setAction(String action) {
		this.action = action;
	}

	public VDisk getMapper() {
		return mapper;
	}

	public void setMapper(VDisk mapper) {
		this.mapper = mapper;
	}

	private int size;// G

	public int getSize() {
		return size;
	}

	public void setSize(int size) {
		this.size = size;
	}

	public static Type getVDiskListType() {
		// TODO Auto-generated method stub
		return null;
	}

}
