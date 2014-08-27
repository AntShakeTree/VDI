package com.opzoon.appstatus.domain.res;

import com.opzoon.ohvc.domain.Head;


public class AppStatusResponse<T>
{
	/**
	 * AppStatus标准响应头信息
	 */
	private Head head;
	/**
	 * AppStatus标准响应体信息
	 */
	private Body<T> body;
	
	public Head getHead() {
		return head;
	}
	public void setHead(Head head) {
		this.head = head;
	}
	public Body<T> getBody() {
		return body;
	}
	public void setBody(Body<T> body) {
		this.body = body;
	}
	@Override
	public String toString() {
		return "AppStatusResponse [head=" + head + ", body=" + body + "]";
	}
	
	
}