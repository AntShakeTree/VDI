package com.vdi.vo.res;

import java.util.List;

import com.vdi.dao.user.domain.Domain;
import com.vdi.vo.res.ListDomainResponse.ListDomain;

public class ListDomainResponse implements Response<ListDomain> {
	private Header head =new Header();
	private ListDomain body;
	
	public Header getHead() {
		return head;
	}

	public void setHead(Header head) {
		this.head = head;
	}

	public ListDomain getBody() {
		return body;
	}

	public void setBody(ListDomain body) {
		this.body = body;
	}

	public static class ListDomain{
		private List<Domain> list;

		public List<Domain> getList() {
			return list;
		}

		public void setList(List<Domain> list) {
			this.list = list;
		}
	}
}
