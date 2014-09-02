package com.vdi.vo.req;

import com.vdi.dao.Request;
import com.vdi.dao.user.domain.DeliveryGroup;

public class DeligroupIdReq implements Request<DeliveryGroup> {
	private Integer groupid;

	public Integer getGroupid() {
		return groupid;
	}

	public void setGroupid(Integer groupid) {
		this.groupid = groupid;
	}
	
}
