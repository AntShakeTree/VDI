package com.vdi.vo.req;

import com.vdi.dao.Request;
import com.vdi.dao.user.domain.DeliveryGroup;

public class DeliveryAndUserIdReq implements Request<DeliveryGroup> {
	private Integer userid;
	private Integer groupid;
	public Integer getUserid() {
		return userid;
	}
	public void setUserid(Integer userid) {
		this.userid = userid;
	}
	public Integer getGroupid() {
		return groupid;
	}
	public void setGroupid(Integer groupid) {
		this.groupid = groupid;
	}
	
}
