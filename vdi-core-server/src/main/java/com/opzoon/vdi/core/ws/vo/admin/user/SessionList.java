package com.opzoon.vdi.core.ws.vo.admin.user;

import java.io.Serializable;
import java.util.List;

import com.opzoon.vdi.core.domain.Session;
import com.opzoon.vdi.core.ws.Services.CommonList;

public class SessionList extends CommonList<Session> implements Serializable {

	private static final long serialVersionUID = 1L;

	private List<Session> list;
	
	@Override
	public List<Session> getList() {
		return list;
	}
	@Override
	public void setList(List<Session> list) {
		this.list = list;
	}
	
}