package com.opzoon.ohvc.driver.opzooncloud.domain;

import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.opzoon.ohvc.common.JSONObjectUtils;
import com.opzoon.vdi.core.domain.BaseDomain;

/**
 * networking 子属性 网卡
 * 
 * @author maxiaochao
 *  @version V04
 */
public class OpzoonCloudEth extends BaseDomain<OpzoonCloudEth>{
	private String vethernet;
	private JSONArray seclists;

	/**
	 * @return the seclists
	 */
	public JSONArray getSeclists() {
		return seclists;
	}

	/**
	 * @param seclists
	 *            the seclists to set
	 */
	public OpzoonCloudEth setSeclists(List<String> seclists) {
		if (seclists != null) {
			this.seclists = new JSONArray();
			try {
				for (int i = 0; i < seclists.size(); i++) {
					this.seclists.put(i, seclists.get(i));
				}
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
		return this;
	}

	/**
	 * @return the vethernet
	 */
	public String getVethernet() {
		return vethernet;
	}

	/**
	 * @param vethernet
	 *            the vethernet to set
	 */
	public OpzoonCloudEth setVethernet(String vethernet) {
		this.vethernet = vethernet;
		return this;
	}
	
	public JSONObject buildJSONObject() {
		return JSONObjectUtils.buildJSONObject(this);
	}
}
