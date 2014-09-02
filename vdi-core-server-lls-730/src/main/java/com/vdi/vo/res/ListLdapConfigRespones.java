package com.vdi.vo.res;

import java.util.List;

import com.vdi.dao.user.domain.LdapConfig;

public class ListLdapConfigRespones {
	private Header head=new Header();
	private ListLdapConfig body;
	
	public Header getHead() {
		return head;
	}

	public void setHead(Header head) {
		this.head = head;
	}

	public ListLdapConfig getBody() {
		return body;
	}

	public void setBody(ListLdapConfig body) {
		this.body = body;
	}

	public static class ListLdapConfig{
		List<LdapConfig> list;

		public List<LdapConfig> getList() {
			return list;
		}

		public void setList(List<LdapConfig> list) {
			this.list = list;
		}
		
	}
}
