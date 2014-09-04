package com.vdi.vo.res;

import java.util.List;

import com.vdi.dao.PageRequest;
import com.vdi.dao.desktop.domain.StorageEntity;
import com.vdi.vo.res.ListStorage.StorageList;

/**
 * 
 * @author ant_shake_tree
 *
 */
public class ListStorage implements Response<StorageList>{
	private Header head;
	private StorageList body;
	private PageRequest<StorageEntity> page;
	
	public Header getHead() {
		return head;
	}
	

	public PageRequest<StorageEntity> getPage() {
		return page;
	}


	public void setPage(PageRequest<StorageEntity> page) {
		this.page = page;
	}


	public void setHead(Header head) {
		this.head = head;
	}


	public StorageList getBody() {
		return body;
	}


	public void setBody(StorageList body) {
		this.body = body;
	}


	public static class StorageList{
		private List<StorageEntity> list;

		public List<StorageEntity> getList() {
			return list;
		}

		public void setList(List<StorageEntity> list) {
			this.list = list;
		}
		
	}
}
