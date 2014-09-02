package com.vdi.vo.res;

import com.vdi.dao.user.domain.LdapConfig;

public class LdapConfigResponse implements Response<LdapConfig>{
	private Header head;
	private LdapConfig body;
	public Header getHead() {
		return head;
	}
	public void setHead(Header head) {
		this.head = head;
	}
	public LdapConfig getBody() {
		return body;
	}
	public void setBody(LdapConfig body) {
		this.body = body;
	}
	
}
