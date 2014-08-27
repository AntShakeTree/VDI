package com.opzoon.vdi.core.domain;

import com.opzoon.ohvc.common.anotation.TargetField;


public class Template{
	
	public Template()
	{
		
	}

	@TargetField(target="id")
	private String templateId;

	@TargetField(target="displayname")
	private String templatename;
	
	private String protoco;
	/**
	 * @return the templateId
	 */
	public String getTemplateId() {
		return templateId;
	}
	/**
	 * @param templateId the templateId to set
	 */
	public Template setTemplateId(String id) {
		this.templateId = id;
		return this;
	}
	/**
	 * @return the templatename
	 */
	public String getTemplatename() {
		return templatename;
	}
	/**
	 * @param templatename the templatename to set
	 */
	public Template setTemplatename(String name) {
		this.templatename = name;
		return this;
	}
	public String getProtoco() {
		return protoco;
	}
	/**
	 * 
	 * @param protoco
	 * @return Template
	 */
	public Template setProtoco(String protoco) {
		this.protoco = protoco;
		return this;
	}
	@Override
	public String toString() {
		return "Template [templateId=" + templateId + ", templatename="
				+ templatename + ", protoco=" + protoco + "]";
	}
	
	
}
