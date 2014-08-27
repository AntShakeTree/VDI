package com.opzoon.vdi.core.ws.vo.admin.domain;

import java.io.Serializable;
import java.util.List;

import com.opzoon.vdi.core.ws.Services.CommonList;

public class LDAPConfigList extends CommonList<LDAPConfig> implements Serializable {

	private static final long serialVersionUID = 1L;

	private List<LDAPConfig> list;

	@Override
	public List<LDAPConfig> getList() {
		return list;
	}
	@Override
	public void setList(List<LDAPConfig> list) {
		this.list = list;
	}
	
}