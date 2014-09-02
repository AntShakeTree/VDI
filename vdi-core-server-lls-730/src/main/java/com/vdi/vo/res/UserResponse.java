package com.vdi.vo.res;

import com.vdi.dao.user.domain.User;

public class UserResponse {
private Header head;
private User body;
public Header getHead() {
	return head;
}
public void setHead(Header head) {
	this.head = head;
}
public User getBody() {
	return body;
}
public void setBody(User body) {
	this.body = body;
}

}
