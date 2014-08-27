/**
 * 
 */
package com.opzoon.ohvc.driver.opzooncloud.domain;

import com.opzoon.vdi.core.domain.VMInstance;

/**
 * @author maxiaochao
 * @version V04
 */
public class OpzoonCloudVMInstanceAttributes extends VMInstance {
	private String display_protocol;
	private boolean use_stunnel;
	private String old_image_type;
	private String oc_os_name;
	private String oc_iso_size;
	private String opzooncloud_iso_size;
	private String nic_model;
	

	public String getDisplay_protocol() {
		return display_protocol;
	}

	public void setDisplay_protocol(String display_protocol) {
		this.display_protocol = display_protocol;
	}

	public boolean isUse_stunnel() {
		return use_stunnel;
	}

	public void setUse_stunnel(boolean use_stunnel) {
		this.use_stunnel = use_stunnel;
	}

	public String getOld_image_type() {
		return old_image_type;
	}

	public void setOld_image_type(String old_image_type) {
		this.old_image_type = old_image_type;
	}

	public String getOc_os_name() {
		return oc_os_name;
	}

	public void setOc_os_name(String oc_os_name) {
		this.oc_os_name = oc_os_name;
	}

	public String getOc_iso_size() {
		return oc_iso_size;
	}

	public void setOc_iso_size(String oc_iso_size) {
		this.oc_iso_size = oc_iso_size;
	}

	public String getOpzooncloud_iso_size() {
		return opzooncloud_iso_size;
	}

	public void setOpzooncloud_iso_size(String opzooncloud_iso_size) {
		this.opzooncloud_iso_size = opzooncloud_iso_size;
	}

	public String getNic_model() {
		return nic_model;
	}

	public void setNic_model(String nic_model) {
		this.nic_model = nic_model;
	}

}
