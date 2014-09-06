package com.vdi.vo.res;

public class LoginResponse {
	private Header head=new Header();
	private TicketWrapper body;
	
	public TicketWrapper getBody() {
		return body;
	}

	public void setBody(TicketWrapper body) {
		this.body = body;
	}

	public Header getHead() {
		return head;
	}

	public void setHead(Header head) {
		this.head = head;
	}
	
}
