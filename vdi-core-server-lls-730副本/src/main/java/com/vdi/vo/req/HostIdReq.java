package com.vdi.vo.req;

import com.vdi.dao.Request;
import com.vdi.dao.desktop.domain.HostEntity;

public class HostIdReq implements Request<HostEntity> {
	private Integer hostid;

	public Integer getHostid() {
		return hostid;
	}

	public void setHostid(Integer hostid) {
		this.hostid = hostid;
	}
	
}
