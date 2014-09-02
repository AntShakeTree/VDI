package com.vdi.vo.res;

import java.util.List;

import com.vdi.dao.user.domain.DeliveryGroup;
import com.vdi.vo.res.ListGroupsResponse.ListGroup;

public class ListGroupsResponse implements Response<ListGroup> {
	private Header head=new Header();
	private ListGroup body;
	
	
	public Header getHead() {
		return head;
	}


	public void setHead(Header head) {
		this.head = head;
	}


	public ListGroup getBody() {
		return body;
	}


	public void setBody(ListGroup body) {
		this.body = body;
	}


	public static class ListGroup{
		List<DeliveryGroup> list;

		public List<DeliveryGroup> getList() {
			return list;
		}

		public void setList(List<DeliveryGroup> list) {
			this.list = list;
		}
		
	}
}
