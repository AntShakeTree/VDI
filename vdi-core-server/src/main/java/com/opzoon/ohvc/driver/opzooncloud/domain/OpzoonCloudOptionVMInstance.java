package com.opzoon.ohvc.driver.opzooncloud.domain;

import com.google.gson.Gson;
import com.opzoon.ohvc.common.anotation.Required;
import com.opzoon.vdi.core.domain.BaseDomain;

/**
 * 
 * @author maxiaochao
 * @version V04
 */
public class OpzoonCloudOptionVMInstance {
	public static final String START = "start";
	public static final String STOP = "stop";
	public static final String CLONE = "clone_source";
	public static final String LINK_CLONE = "link_clone";
	private String label;
	private DistributionStrategy distribution_strategy;
	private OpzoonCloudVMInstanceAttributes attributes;
	@Required
	private String option;

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
	public OpzoonCloudOptionVMInstance setOption(String option) {
		this.option = option;
		return this;
	}

	/**
	 * @return Returns the label.
	 */
	public String getLabel() {
		return label;
	}

	/**
	 * @param label
	 *            The label to set.
	 */
	public OpzoonCloudOptionVMInstance setLabel(String label) {
		this.label = label;
		return this;
	}

	public DistributionStrategy getDistribution_strategy() {
		return distribution_strategy;
	}

	public OpzoonCloudOptionVMInstance setDistribution_strategy(
			DistributionStrategy distribution_strategy) {
		this.distribution_strategy = distribution_strategy;
		return this;
	}

	public String bulidJsonString() {
		return new Gson().toJson(this);
	}

	public OpzoonCloudVMInstanceAttributes getAttributes() {
		return attributes;
	}

	public OpzoonCloudOptionVMInstance setAttributes(OpzoonCloudVMInstanceAttributes attributes) {
		this.attributes = attributes;
		return this;
	}
	public static OpzoonCloudVMInstanceAttributes getVNC(){
		OpzoonCloudVMInstanceAttributes	attributes=new OpzoonCloudVMInstanceAttributes();
		attributes.setDisplay_protocol("vnc");
		attributes.setUse_stunnel(false);
		return  attributes;
	}
	public static OpzoonCloudVMInstanceAttributes getSpice(){
		OpzoonCloudVMInstanceAttributes	attributes=new OpzoonCloudVMInstanceAttributes();
		attributes.setDisplay_protocol("spice");
		attributes.setUse_stunnel(false);
		return  attributes;
	}

}
