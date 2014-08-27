package com.opzoon.vdi.core.app.response;

import com.opzoon.ohvc.common.RailResponse;
import com.opzoon.ohvc.domain.Head;
import com.opzoon.ohvc.request.PageRequest;
import com.opzoon.vdi.core.domain.Trace;

public class ListTraceRes implements RailResponse<ListTrace> {
	private Head head;
	private ListTrace body;
	private PageRequest<Trace> page;
	public Head getHead() {
		return head;
	}
	public void setHead(Head head) {
		this.head = head;
	}
	
	public ListTrace getBody() {
		return body;
	}
	public void setBody(ListTrace body) {
		this.body = body;
	}
	@Override
	public RailResponse<ListTrace> instanceByJson(String reString) {
		return null;
	}
	public PageRequest<Trace> getPage() {
		return page;
	}
	public void setPage(PageRequest<Trace> page) {
		this.page = page;
	}
}
