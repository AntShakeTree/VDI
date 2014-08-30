/**
 * Project Name:vdi-core-server-lls
 * File Name:ListComputePool.java
 * Package Name:com.vdi.vo.res
 * Date:2014年8月12日下午12:46:46
 * Copyright (c) 2014 All Rights Reserved.
 *
*/

package com.vdi.vo.res;

import java.util.List;

import com.vdi.dao.PageRequest;
import com.vdi.dao.desktop.domain.ComputePoolEntity;
import com.vdi.vo.res.ListComputePool.ComputePoolList;

public class ListComputePool implements Response<ComputePoolList>{
	
	private Header head;
	
	private ComputePoolList body;
	
	private PageRequest<ComputePoolEntity> page;
	public Header getHead() {
		return head;
	}




	public void setHead(Header head) {
		this.head = head;
	}




	public ComputePoolList getBody() {
		return body;
	}




	public void setBody(ComputePoolList body) {
		this.body = body;
	}




	public PageRequest<ComputePoolEntity> getPage() {
		return page;
	}




	public void setPage(PageRequest<ComputePoolEntity> page) {
		this.page = page;
	}




	public static class ComputePoolList{
		private List<ComputePoolEntity> list;

		public List<ComputePoolEntity> getList() {
			return list;
		}

		public void setList(List<ComputePoolEntity> list) {
			this.list = list;
		}
	
	}
	
}
