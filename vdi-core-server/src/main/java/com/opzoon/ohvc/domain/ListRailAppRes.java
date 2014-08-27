/**   
 * @Title: ListRailAppRes.java 
 * @Package com.opzoon.ohvc.response 
 * @Description: 
 * @author David   
 * @date 2013-1-28 下午12:29:04 
 * @version V1.0   
 */
package com.opzoon.ohvc.domain;

import java.util.List;

import com.google.gson.reflect.TypeToken;
import com.opzoon.ohvc.common.OpzoonUtils;
import com.opzoon.ohvc.common.RailResponse;

/**
 * ClassName: ListRailAppRes
 * @Description: ListRailAppRes
 * @author David
 * @date 2013-1-28 下午12:29:04
 */
public class ListRailAppRes implements RailResponse<List<AgentRailApplication>> {

	private Head head;
	private List<AgentRailApplication> body;

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
	public List<AgentRailApplication> getBody() {
		return body;
	}

	/**
	 * @param body
	 *            the body to set
	 */
	public void setBody(List<AgentRailApplication> body) {
		this.body = body;
	}

	public RailResponse<List<AgentRailApplication>> instanceByJson(String json) {
		return OpzoonUtils.getGson().fromJson(json, new TypeToken<ListRailAppRes>() {
		}.getType());
	}

}
