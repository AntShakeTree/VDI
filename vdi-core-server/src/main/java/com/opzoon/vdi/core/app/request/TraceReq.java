package com.opzoon.vdi.core.app.request;

import com.opzoon.ohvc.common.anotation.DaoName;
import com.opzoon.ohvc.request.PageRequest;
import com.opzoon.vdi.core.domain.Trace;

@DaoName(name="Trace")
public class TraceReq implements PageRequest<Trace>{
	private Integer  targetid;
	private Integer operatorid;
	private String sortkey;
	private Integer status;
	private int ascend;
	private int pagesize=15;
	private int page=1;
	private int amount;

	public Integer getOperatorid() {
		return operatorid;
	}
	public void setOperatorid(Integer operatorid) {
		this.operatorid = operatorid;
	}
	public Integer getTargetid() {
		return targetid;
	}
	public void setTargetid(Integer targetid) {
		this.targetid = targetid;
	}
	public String getSortkey() {
		return sortkey;
	}
	public void setSortkey(String sortkey) {
		this.sortkey = sortkey;
	}
	public Integer getStatus() {
		return status;
	}
	public void setStatus(Integer status) {
		this.status = status;
	}
	public int getAscend() {
		return ascend;
	}
	public void setAscend(int ascend) {
		this.ascend = ascend;
	}
	public int getPagesize() {
		return pagesize;
	}
	public void setPagesize(int pagesize) {
		this.pagesize = pagesize;
	}
	public int getPage() {
		return page;
	}
	public void setPage(int page) {
		this.page = page;
	}
	public int getAmount() {
		return amount;
	}
	public void setAmount(int amount) {
		this.amount = amount;
	}

}
