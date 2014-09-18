package com.vdi.vo.res;

import com.vdi.dao.user.domain.Domain;

public class DomainResponse {
	private Header head;

	private Domain body;

	public Header getHead() {
		return head;
	}

	public void setHead(Header head) {
		this.head = head;
	}

	public Domain getBody() {
		return body;
	}

	public void setBody(Domain body) {
		this.body = body;
	}
	
}
