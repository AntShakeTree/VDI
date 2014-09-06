package com.vdi.vo.res;

import com.vdi.dao.user.domain.LdapConfigEntity;

public class LdapConfigResponse implements Response<LdapConfigEntity>{
	private Header head=new Header();
	private LdapConfigEntity body;
	public Header getHead() {
		return head;
	}
	public void setHead(Header head) {
		this.head = head;
	}
	public LdapConfigEntity getBody() {
		return body;
	}
	public void setBody(LdapConfigEntity body) {
		this.body = body;
	}
	
}
