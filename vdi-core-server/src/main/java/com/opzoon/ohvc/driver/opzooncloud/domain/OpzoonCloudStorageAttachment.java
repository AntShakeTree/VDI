package com.opzoon.ohvc.driver.opzooncloud.domain;

import org.json.JSONObject;

import com.opzoon.ohvc.common.JSONObjectUtils;
import com.opzoon.vdi.core.domain.BaseDomain;

public class OpzoonCloudStorageAttachment  extends BaseDomain<OpzoonCloudStorageAttachment>{
	// "storage_attachments" : [{"index": 1, "type": "local", "name_or_size":
	// 2500}],
	private int index = 1;
	private String type = "local";
	private String name_or_size;

	/**
	 * @return the index
	 */
	public int getIndex() {
		return index;
	}

	/**
	 * @param index
	 *            the index to set
	 */
	public OpzoonCloudStorageAttachment setIndex(int index) {
		this.index = index;
		return this;
	}

	/**
	 * @return the type
	 */
	public String getType() {
		return type;
	}

	/**
	 * @param type
	 *            the type to set
	 */
	public OpzoonCloudStorageAttachment setType(String type) {
		this.type = type;
		return this;
	}

	/**
	 * @return the name_or_size
	 */
	public String getName_or_size() {
		return name_or_size;
	}

	/**
	 * @param string
	 *            the name_or_size to set
	 */
	public OpzoonCloudStorageAttachment setName_or_size(String string) {
		this.name_or_size = string;
		return this;
	}

	@Override
	public JSONObject buildJSONObject() {
		// TODO Auto-generated method stub
		return JSONObjectUtils.buildJSONObject(this);
	}

}