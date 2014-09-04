package com.vdi.vo.res;

import com.vdi.dao.desktop.domain.StorageEntity;

public class StorageResponse implements Response<StorageEntity>{
	private Header head;
	private StorageEntity body;
	public Header getHead() {
		return head;
	}
	public void setHead(Header head) {
		this.head = head;
	}
	public StorageEntity getBody() {
		return body;
	}
	public void setBody(StorageEntity body) {
		this.body = body;
	}
	
}
