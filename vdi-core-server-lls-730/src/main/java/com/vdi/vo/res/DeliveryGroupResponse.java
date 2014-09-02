package com.vdi.vo.res;

import com.vdi.dao.user.domain.DeliveryGroup;

public class DeliveryGroupResponse implements Response<DeliveryGroup> {
	private Header head=new Header();
	private DeliveryGroup body;
	public Header getHead() {
		return head;
	}
	public void setHead(Header head) {
		this.head = head;
	}
	public DeliveryGroup getBody() {
		return body;
	}
	public void setBody(DeliveryGroup body) {
		this.body = body;
	}
	
}
