package com.vdi.vo.res;

import java.util.List;

import com.vdi.dao.desktop.domain.HostEntity;

public class ListHostResponse {
	private Header head=new Header().setError(0);
	private ListHost body;
	
	public Header getHead() {
		return head;
	}

	public void setHead(Header head) {
		this.head = head;
	}

	public ListHost getBody() {
		return body;
	}

	public void setBody(ListHost body) {
		this.body = body;
	}

	public static class ListHost{
		List<HostEntity> list;

		public List<HostEntity> getList() {
			return list;
		}

		public void setList(List<HostEntity> list) {
			this.list = list;
		}
		
	}
}
