package com.vdi.vo.res;

import java.util.List;

import com.vdi.dao.PageRequest;
import com.vdi.dao.user.domain.UserMapBridge;
import com.vdi.dao.user.domain.LdapConfigEntity;

public class ListLdapConfigRespones {
	private Header head=new Header();
	private ListLdapConfig body;
	private PageRequest<LdapConfigEntity> page;
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
		List<LdapConfigEntity> list;

		public List<LdapConfigEntity> getList() {
			return list;
		}

		public void setList(List<LdapConfigEntity> list) {
			this.list = list;
		}
		
	}
}
