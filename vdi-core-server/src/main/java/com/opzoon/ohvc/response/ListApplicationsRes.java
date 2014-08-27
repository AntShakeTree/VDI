/**   
 * @Title: ListApplicationServerRes.java 
 * @Package com.opzoon.ohvc.response 
 * @Description: TODO 
 * @author David   
 * @date 2013-1-30 下午4:28:24 
 * @version V1.0   
 */
package com.opzoon.ohvc.response;

import java.util.List;

import com.google.gson.Gson;
import com.opzoon.ohvc.common.RailResponse;
import com.opzoon.ohvc.domain.Head;
import com.opzoon.ohvc.domain.AgentRailApplication;
import com.opzoon.ohvc.request.PageRequest;

/**
 * ClassName: ListApplicationsRes
 * @Description: 查询虚拟应用列表代理类
 * @author David
 * @date 2013-1-30 下午4:28:24
 */
public class ListApplicationsRes implements RailResponse<List<AgentRailApplication>> {
	private Head head;
	private List<AgentRailApplication> body;
	private PageRequest<AgentRailApplication> page;


	/** 
	 * @return page 
	 */
	public PageRequest<AgentRailApplication> getPage() {
		return page;
	}

	/**
	 * @param page the page to set
	 */
	public void setPage(PageRequest<AgentRailApplication> page) {
		this.page = page;
	}

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

	@Override
	public RailResponse<List<AgentRailApplication>> instanceByJson(String reString) {
		Gson gosn = new Gson();
		return gosn.fromJson(reString, this.getClass());
	}

}
