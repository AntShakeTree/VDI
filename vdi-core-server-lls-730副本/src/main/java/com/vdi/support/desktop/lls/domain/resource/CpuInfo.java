package com.vdi.support.desktop.lls.domain.resource;

import org.codehaus.jackson.map.annotate.JsonSerialize;
import org.codehaus.jackson.map.annotate.JsonSerialize.Inclusion;

@JsonSerialize(include=Inclusion.NON_DEFAULT)
public class CpuInfo {
	private int processor;
	private double  cpuMHz;
	private String modelName;
	private int coreNum;
	private String vendor_id;
	public int getCoreNum() {
		return coreNum;
	}
	public void setCoreNum(int coreNum) {
		this.coreNum = coreNum;
	}
	public int getProcessor() {
		return processor;
	}
	public void setProcessor(int processor) {
		this.processor = processor;
	}
	public double getCpuMHz() {
		return cpuMHz;
	}
	public void setCpuMHz(double cpuMHz) {
		this.cpuMHz = cpuMHz;
	}
	public String getModelName() {
		return modelName;
	}
	public void setModelName(String modelName) {
		this.modelName = modelName;
	}
	public String getVendor_id() {
		return vendor_id;
	}
	public void setVendor_id(String vendor_id) {
		this.vendor_id = vendor_id;
	}	
	
}
