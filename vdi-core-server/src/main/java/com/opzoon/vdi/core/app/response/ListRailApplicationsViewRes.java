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
import com.opzoon.ohvc.response.RailResponseSupport;
import com.opzoon.vdi.core.app.domain.RailApplicationView;

/**
 * ClassName: ListApplicationsRes
 * @Description: 查询发布状态虚拟应用列表代理类
 * @author David
 * @date 2013-1-30 下午4:28:24
 */
public class ListRailApplicationsViewRes extends RailResponseSupport<List<RailApplicationView>> {
	private Head head;
	private List<RailApplicationView> body;
	private PageRequest<RailApplicationView> page;

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
	public List<RailApplicationView> getBody() {
		return body;
	}


	/**
	 * @param body the body to set
	 */
	public void setBody(List<RailApplicationView> body) {
		this.body = body;
	}


	/** 
	 * @return page 
	 */
	public PageRequest<RailApplicationView> getPage() {
		return page;
	}


	/**
	 * @param page the page to set
	 */
	public void setPage(PageRequest<RailApplicationView> page) {
		this.page = page;
	}


	@Override
	public RailResponse<List<RailApplicationView>> instanceByJson(String reString) {
		Gson gosn = new Gson();
		return gosn.fromJson(reString, this.getClass());
	}

}
