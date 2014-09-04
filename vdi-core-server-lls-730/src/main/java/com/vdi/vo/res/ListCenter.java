package com.vdi.vo.res;

import java.util.List;

import com.vdi.dao.desktop.domain.CenterEntity;
import com.vdi.vo.res.ListCenter.CenterList;

public class ListCenter implements  Response<CenterList> {
	private Header head;
	private CenterList body;
	
	
	public Header getHead() {
		return head;
	}


	public void setHead(Header head) {
		this.head = head;
	}


	public CenterList getBody() {
		return body;
	}


	public void setBody(CenterList body) {
		this.body = body;
	}


	public static class CenterList{
		private List<CenterEntity> list;

		public List<CenterEntity> getList() {
			return list;
		}

		public void setList(List<CenterEntity> list) {
			this.list = list;
		}
		
	}
}
