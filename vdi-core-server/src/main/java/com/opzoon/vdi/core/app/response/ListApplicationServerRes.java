/**   
 * @Title: ListApplicationServerRes.java 
 * @Package com.opzoon.ohvc.response 
 * @Description: TODO 
 * @author David   
 * @date 2013-1-30 下午4:28:24 
 * @version V1.0   
 */
package com.opzoon.vdi.core.app.response;

import java.util.List;

import com.google.gson.Gson;
import com.opzoon.ohvc.common.RailResponse;
import com.opzoon.ohvc.domain.Head;
import com.opzoon.ohvc.request.PageRequest;

/**
 * ClassName: ListApplicationServerRes
 * @Description: TODO
 * @author David
 * @date 2013-1-30 下午4:28:24
 */
public class ListApplicationServerRes implements RailResponse<List<com.opzoon.vdi.core.app.domain.RailApplicationServer>> {
	private Head head;
	private List<com.opzoon.vdi.core.app.domain.RailApplicationServer> body;
	private PageRequest<com.opzoon.vdi.core.app.domain.RailApplicationServer> page;
	

	/** 
	 * @return page 
	 */
	public PageRequest<com.opzoon.vdi.core.app.domain.RailApplicationServer> getPage() {
		return page;
	}


	/**
	 * @param page the page to set
	 */
	public void setPage(PageRequest<com.opzoon.vdi.core.app.domain.RailApplicationServer> page) {
		this.page = page;
	}


	/** 
	 * @return head 
	 */
	public Head getHead() {
		return head;
	}


	/**
	 * @param head the head to set
	 */
	public void setHead(Head head) {
		this.head = head;
	}


	/** 
	 * @return body 
	 */
	public List<com.opzoon.vdi.core.app.domain.RailApplicationServer> getBody() {
		return body;
	}


	/**
	 * @param body the body to set
	 */
	public void setBody(List<com.opzoon.vdi.core.app.domain.RailApplicationServer> body) {
		this.body = body;
	}


	@Override
	public RailResponse<List<com.opzoon.vdi.core.app.domain.RailApplicationServer>> instanceByJson(String reString) {
		Gson gson = new Gson();
		return gson.fromJson(reString, this.getClass());
	}

}
