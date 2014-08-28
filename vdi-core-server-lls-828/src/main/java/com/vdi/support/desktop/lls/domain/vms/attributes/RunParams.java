package com.vdi.support.desktop.lls.domain.vms.attributes;


import java.util.ArrayList;
import java.util.List;

/**
 * 
 * @author mxc
 * 
 */
public class RunParams {
	private List<String> views = new ArrayList<String>(4);
	private String bootSeq;

	public List<String> getViews() {
		return views;
	}

	public void setViews(List<String> views) {
		this.views = views;
	}

	public String getBootSeq() {
		return bootSeq;
	}

	public void setBootSeq(String bootSeq) {
		this.bootSeq = bootSeq;
	}

}
