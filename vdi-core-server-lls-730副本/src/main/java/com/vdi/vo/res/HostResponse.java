package com.vdi.vo.res;

import com.vdi.dao.desktop.domain.HostEntity;

public class HostResponse implements Response<HostEntity> {
	private Header head;
	private HostEntity body;

	public Header getHead() {
		return head;
	}

	public void setHead(Header head) {
		this.head = head;
	}

	public HostEntity getBody() {
		return body;
	}

	public void setBody(HostEntity body) {
		this.body = body;
	}

}
