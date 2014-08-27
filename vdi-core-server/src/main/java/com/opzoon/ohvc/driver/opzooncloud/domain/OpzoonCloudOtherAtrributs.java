package com.opzoon.ohvc.driver.opzooncloud.domain;

import org.json.JSONObject;

import com.opzoon.ohvc.common.JSONObjectUtils;
import com.opzoon.vdi.core.domain.BaseDomain;

/**
 * 
 * @author maxiaochao
 * @version V04
 */
public class OpzoonCloudOtherAtrributs extends BaseDomain<OpzoonCloudOtherAtrributs>{
	private double cpus ;
	private int ram = 400;
	private int io = 250;

	/**
	 * @return the cpus
	 */
	public double getCpus() {
		return cpus;
	}

	/**
	 * @param d the cpus to set
	 */
	public OpzoonCloudOtherAtrributs setCpus(double cpus) {
		this.cpus = cpus;
		return this;
	}

	/**
	 * @return the ram
	 */
	public int getRam() {
		return ram;
	}

	/**
	 * @param ram the ram to set
	 */
	public OpzoonCloudOtherAtrributs setRam(int ram) {
		this.ram = ram;
		return this;
	}

	/**
	 * @return the io
	 */
	public int getIo() {
		return io;
	}

	/**
	 * @param io the io to set
	 */
	public OpzoonCloudOtherAtrributs setIo(int io) {
		this.io = io;
		return this;
	}

	@Override
	public JSONObject buildJSONObject() {
		// TODO Auto-generated method stub
		return JSONObjectUtils.buildJSONObject(this);
	}




}