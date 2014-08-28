package com.vdi.support.desktop.lls.domain.vms.attributes;

public class Cdrom {
	public static String DISCOVERY_STORAGE_ISOS_ACTION = "discoveryStorageISOs";
	public static String DELETE_ISO_ACTION = "deleteISO";
	private  String	storageIdentity;
	public String getStorageIdentity() {
		return storageIdentity;
	}
	public void setStorageIdentity(String storageIdentity) {
		this.storageIdentity = storageIdentity;
	}
	

}
