package com.opzoon.vdi.core.ws.vo.admin.user;

import java.io.Serializable;
import java.util.List;

import com.opzoon.vdi.core.domain.User;
import com.opzoon.vdi.core.ws.Services.CommonList;

public class UserList extends CommonList<User> implements Serializable {

	private static final long serialVersionUID = 1L;

	private List<User> list;

	@Override
	public List<User> getList() {
		return list;
	}
	@Override
	public void setList(List<User> list) {
		this.list = list;
	}
	
}