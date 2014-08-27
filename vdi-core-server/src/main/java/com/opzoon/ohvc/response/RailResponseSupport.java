/**   
 * @Title: RailResponseSupport.java 
 * @Package com.opzoon.ohvc.core.domain 
 * @Description: 通用的response support 
 * @author david   
 * @date 2013-1-28 下午12:31:46 
 * @version V1.0   
 */
package com.opzoon.ohvc.response;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.internal.StringMap;
import com.opzoon.ohvc.common.RailResponse;
import com.opzoon.ohvc.domain.Head;

/**
 * ClassName: RailResponseSupport
 * @Description: 通用的返回
 * @author david
 * @date 2013-1-28 下午12:31:46
 */
public class RailResponseSupport<T> implements RailResponse<T> {
	private Head head;
	private T body;

	/**
	 * @return head
	 */
	public Head getHead() {
		return head;
	}

	/**
	 * @param head
	 *            the head to set
	 */
	public void setHead(Head head) {
		this.head = head;
	}

	/**
	 * @return body
	 */
	public T getBody() {
		return body;
	}

	/**
	 * @param body
	 *            the body to set
	 */
	public void setBody(T body) {
		this.body = body;
	}

	/*
	 * (非 Javadoc) <p>Title: instanceByJson</p> <p>Description: </p>
	 * @param reString
	 * @return
	 * @see com.opzoon.ohvc.common.RailResponse#instanceByJson(java.lang.String)
	 */

	@Override
	public RailResponse<T> instanceByJson(String reString) {
		return this;
	}

	@SuppressWarnings("rawtypes")
	public StringMap getJsonMap(String reString) {
		GsonBuilder builder = new GsonBuilder();
		Gson gson = builder.create();
		StringMap fromJson = gson.fromJson(reString, StringMap.class);
		return fromJson;
	}


}
