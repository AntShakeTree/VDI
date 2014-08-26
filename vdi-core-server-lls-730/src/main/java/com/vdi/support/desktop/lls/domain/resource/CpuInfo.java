package com.vdi.support.desktop.lls.domain.resource;

import org.codehaus.jackson.map.annotate.JsonSerialize;
import org.codehaus.jackson.map.annotate.JsonSerialize.Inclusion;

@JsonSerialize(include=Inclusion.NON_DEFAULT)
public class CpuInfo {
	private int processor;
	private double  cpuMHz;
	private String modelName;
	private int coreNum;
	
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
}
