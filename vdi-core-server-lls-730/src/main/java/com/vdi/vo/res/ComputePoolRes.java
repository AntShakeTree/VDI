package com.vdi.vo.res;

import com.vdi.dao.desktop.domain.ComputePoolEntity;

public class ComputePoolRes implements Response<ComputePoolEntity> {
	private Header head;

	private ComputePoolEntity body;

	public Header getHead() {
		return head;
	}

	public void setHead(Header head) {
		this.head = head;
	}

	public ComputePoolEntity getBody() {
		return body;
	}

	public void setBody(ComputePoolEntity body) {
		this.body = body;
	}
}