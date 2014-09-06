package com.vdi.support.desktop.lls.domain.vms.attributes;

import org.codehaus.jackson.map.annotate.JsonSerialize;
import org.codehaus.jackson.map.annotate.JsonSerialize.Inclusion;

@JsonSerialize(include=Inclusion.NON_DEFAULT)
public class VNetcard {
	private boolean isDhcp;

	public boolean getIsDhcp() {
		return isDhcp;
	}

	public void setIsDhcp(boolean isDhcp) {
		this.isDhcp = isDhcp;
	}

}
