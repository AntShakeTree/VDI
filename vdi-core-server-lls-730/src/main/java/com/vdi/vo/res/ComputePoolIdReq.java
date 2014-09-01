package com.vdi.vo.res;

import com.vdi.dao.Request;
import com.vdi.dao.desktop.domain.ComputePoolEntity;

public class ComputePoolIdReq implements Request<ComputePoolEntity>{
	
	private Integer computepoolid;

	public Integer getComputepoolid() {
		return computepoolid;
	}

	public void setComputepoolid(Integer computepoolid) {
		this.computepoolid = computepoolid;
	}
	
}
