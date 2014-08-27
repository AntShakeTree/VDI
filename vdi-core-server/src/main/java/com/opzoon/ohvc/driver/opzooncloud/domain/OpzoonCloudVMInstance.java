/**
 * 
 */
package com.opzoon.ohvc.driver.opzooncloud.domain;

import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.opzoon.ohvc.common.JSONObjectUtils;
import com.opzoon.vdi.core.domain.VMInstance;

/**
 * @author maxiaochao
 * @version V04
 */
public class OpzoonCloudVMInstance extends VMInstance {
	private String id;
	private String option;
	private JSONArray tags;
	private String account;
	private String optionId;
	private JSONObject networking;
	private String name;
	private Integer entry;
	private JSONArray placement_requirements;
	private String ip;
	private JSONObject other_attributes;
	private Boolean virtio;
	private String uri;
	private String disk_attach;
	private String cloned_name;
	private String label;
	private String imagelist;
	private String nvc;
	private String site;
	private JSONObject attributes;
	private JSONObject distribution_strategy;
	private Integer boot_index;
	private Integer import_index;
	private JSONArray storage_attachments;
	private String vnc;
	private String start_time;
	private JSONArray attached_disks;
	private String err_msg;
	// 是否是spice
	private String display_protoco="spice";

	public String getDisplay_protoco() {
		return display_protoco;
	}

	public void setDisplay_protoco(String display_protoco) {
		this.display_protoco = display_protoco;
	}

	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * @param name
	 *            the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * @return the ip
	 */
	public String getIp() {
		return ip;
	}

	/**
	 * @return Returns the vnc.
	 */
	public String getVnc() {
		return vnc;
	}

	/**
	 * @param vnc
	 *            The vnc to set.
	 */
	public void setVnc(String vnc) {
		this.vnc = vnc;
	}

	/**
	 * @param ip
	 *            the ip to set
	 */
	public void setIp(String ip) {
		this.ip = ip;
	}

	/**
	 * @return Returns the start_time.
	 */
	public String getStart_time() {
		return start_time;
	}

	/**
	 * @param start_time
	 *            The start_time to set.
	 */
	public void setStart_time(String start_time) {
		this.start_time = start_time;
	}

	/**
	 * @return the other_attributes
	 */
	public JSONObject getOther_attributes() {
		return other_attributes;
	}

	/**
	 * @param other_attributes
	 *            the other_attributes to set
	 */
	public void setOther_attributes(JSONObject other_attributes) {
		this.other_attributes = other_attributes;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	/**
	 * @return the virtio
	 */
	public Boolean isVirtio() {
		return virtio;
	}

	/**
	 * @param virtio
	 *            the virtio to set
	 */
	public void setVirtio(Boolean virtio) {
		this.virtio = virtio;
	}

	/**
	 * @return the uri
	 */
	public String getUri() {
		return uri;
	}

	/**
	 * @param uri
	 *            the uri to set
	 */
	public void setUri(String uri) {
		this.uri = uri;
	}

	/**
	 * @return the disk_attach
	 */
	public String getDisk_attach() {
		return disk_attach;
	}

	/**
	 * @param disk_attach
	 *            the disk_attach to set
	 */
	public void setDisk_attach(String disk_attach) {
		this.disk_attach = disk_attach;
	}

	/**
	 * @return the imagelist
	 */
	public String getImagelist() {
		return imagelist;
	}

	/**
	 * @param imagelist
	 *            the imagelist to set
	 */
	public void setImagelist(String imagelist) {
		this.imagelist = imagelist;
	}

	/**
	 * @return the nvc
	 */
	public String getNvc() {
		return nvc;
	}

	/**
	 * @param nvc
	 *            the nvc to set
	 */
	public void setNvc(String nvc) {
		this.nvc = nvc;
	}

	/**
	 * @return the site
	 */
	public String getSite() {
		return site;
	}

	/**
	 * @param site
	 *            the site to set
	 */
	public void setSite(String site) {
		this.site = site;
	}

	/**
	 * @return the attributes
	 */
	public JSONObject getAttributes() {
		return attributes;
	}

	/**
	 * @param attributes
	 *            the attributes to set
	 */
	public void setAttributes(JSONObject attributes) {
		this.attributes = attributes;
	}

	/**
	 * @param storage_attachments
	 *            the storage_attachments to set
	 */
	public void setStorage_attachments(
			List<OpzoonCloudStorageAttachment> storage_attachments) {
		this.storage_attachments = new JSONArray();
		try {
			for (Integer i = 0; i < storage_attachments.size(); i++) {
				this.storage_attachments.put(i, storage_attachments.get(i)
						.buildJSONObject());
			}

		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

	/**
	 * @return the storage_attachments
	 */
	public JSONArray getStorage_attachments() {

		return storage_attachments;
	}

	/**
	 * @return the boot_index
	 */
	public Integer getBoot_index() {
		return boot_index;
	}

	/**
	 * @param boot_index
	 *            the boot_index to set
	 */
	public void setBoot_index(Integer boot_index) {
		this.boot_index = boot_index;
	}

	public JSONObject getNetworking() {
		return networking;
	}

	public void setNetworking(JSONObject networking) {
		this.networking = networking;
	}

	/**
	 * @return the placement_requirements
	 */
	public JSONArray getPlacement_requirements() {
		return placement_requirements;
	}

	/**
	 * @param placement_requirements
	 *            the placement_requirements to set
	 */
	public void setPlacement_requirements(JSONArray placement_requirements) {
		this.placement_requirements = placement_requirements;
	}

	/**
	 * @return the account
	 */
	public String getAccount() {
		return account;
	}

	/**
	 * @param account
	 *            the account to set
	 */
	public void setAccount(String account) {
		this.account = account;
	}

	/**
	 * @return the label
	 */
	public String getLabel() {
		return label;
	}

	/**
	 * @param label
	 *            the label to set
	 */
	public OpzoonCloudVMInstance setLabel(String label) {
		this.label = label;
		return this;
	}

	/**
	 * @return the import_index
	 */
	public Integer getImport_index() {
		return import_index;
	}

	/**
	 * @param import_index
	 *            the import_index to set
	 */
	public void setImport_index(Integer import_index) {
		this.import_index = import_index;
	}

	/**
	 * @return the entry
	 */
	public Integer getEntry() {
		return entry;
	}

	/**
	 * @param entry
	 *            the entry to set
	 */
	public void setEntry(Integer entry) {
		this.entry = entry;
	}

	/**
	 * @return the option
	 */
	public String getOption() {
		return option;
	}

	/**
	 * @param option
	 *            the option to set
	 */
	public OpzoonCloudVMInstance setOption(String option) {
		this.option = option;
		return this;
	}

	/**
	 * 构建JSONOBJECT
	 */
	public JSONObject buildJSONObject() {
		return JSONObjectUtils.buildJSONObject(this);
	}

	/**
	 * @return Returns the cloned_name.
	 */
	public String getCloned_name() {
		return cloned_name;
	}

	/**
	 * @param cloned_name
	 *            The cloned_name to set.
	 */
	public void setCloned_name(String cloned_name) {
		this.cloned_name = cloned_name;
	}

	public JSONArray getTags() {
		return tags;
	}

	public void setTags(JSONArray tags) {
		this.tags = tags;
	}

	public Boolean getVirtio() {
		return virtio;
	}

	public void setStorage_attachments(JSONArray storage_attachments) {
		this.storage_attachments = storage_attachments;
	}

	public JSONObject getDistribution_strategy() {
		return distribution_strategy;
	}

	public void setDistribution_strategy(JSONObject distribution_strategy) {
		this.distribution_strategy = distribution_strategy;
	}

	public JSONArray getAttached_disks() {
		return attached_disks;
	}

	public void setAttached_disks(JSONArray attached_disks) {
		this.attached_disks = attached_disks;
	}

	public String getErr_msg() {
		return err_msg;
	}

	public void setErr_msg(String err_msg) {
		this.err_msg = err_msg;
	}

	@Override
	public int hashCode() {

		return this.name.hashCode();
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == this) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (!(obj instanceof OpzoonCloudVMInstance)) {
			return false;
		}
		if (((OpzoonCloudVMInstance) obj).getName() != null
				&& ((OpzoonCloudVMInstance) obj).getName().equals(this.name)) {
			return true;
		} else {
			return false;
		}
	}

	public String getOptionId() {
		return optionId;
	}

	public void setOptionId(String optionId) {
		this.optionId = optionId;
	}

	@Override
	public String toString() {
		return "OpzoonCloudVMInstance [id=" + id + ", option=" + option
				+ ", tags=" + tags + ", account=" + account + ", optionId="
				+ optionId + ", networking=" + networking + ", name=" + name
				+ ", entry=" + entry + ", placement_requirements="
				+ placement_requirements + ", ip=" + ip + ", other_attributes="
				+ other_attributes + ", virtio=" + virtio + ", uri=" + uri
				+ ", disk_attach=" + disk_attach + ", cloned_name="
				+ cloned_name + ", label=" + label + ", imagelist=" + imagelist
				+ ", nvc=" + nvc + ", site=" + site + ", attributes="
				+ attributes + ", distribution_strategy="
				+ distribution_strategy + ", boot_index=" + boot_index
				+ ", import_index=" + import_index + ", storage_attachments="
				+ storage_attachments + ", vnc=" + vnc + ", start_time="
				+ start_time + ", attached_disks=" + attached_disks
				+ ", err_msg=" + err_msg + ", display_protoco="
				+ display_protoco + "]";
	}

}
