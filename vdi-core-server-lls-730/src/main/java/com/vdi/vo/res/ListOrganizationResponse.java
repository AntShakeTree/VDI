package com.vdi.vo.res;

import java.util.List;

import com.vdi.dao.PageRequest;
import com.vdi.dao.user.domain.Organization;

public class ListOrganizationResponse {
	private Header head = new Header();
	private ListOrganization body;
	private PageRequest<Organization> page;

	public Header getHead() {
		return head;
	}

	public void setHead(Header head) {
		this.head = head;
	}

	public ListOrganization getBody() {
		return body;
	}

	public void setBody(ListOrganization body) {
		this.body = body;
	}

	public PageRequest<Organization> getPage() {
		return page;
	}

	public void setPage(PageRequest<Organization> page) {
		this.page = page;
	}

	public static class ListOrganization {
		private List<Organization> list;

		public List<Organization> getList() {
			return list;
		}

		public void setList(List<Organization> list) {
			this.list = list;
		}

	}
}
