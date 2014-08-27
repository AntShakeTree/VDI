package com.opzoon.appstatus.domain.res;

import java.util.List;
/**
 * 
 * @author hadoop
 *
 * @param <T>
 */
public class ListNodesBody<T> extends Body<T> {

	private List<T> list;
	
	private String sortkey;
	
	private int ascend;
	
	private int pagesize = -1;
	
	private int page = 1;
	
	private int amount;
	
	private int availableNum;
	
	private int unAvailableNum;

	
	public List<T> getList() {
		return list;
	}

	public void setList(List<T> list) {
		this.list = list;
	}

	public String getSortkey() {
		return sortkey;
	}

	public void setSortkey(String sortkey) {
		this.sortkey = sortkey;
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
	
	public int getAvailableNum()
	{
		return availableNum;
	}

	public void setAvailableNum(int availableNum)
	{
		this.availableNum = availableNum;
	}

	public int getUnAvailableNum()
	{
		return unAvailableNum;
	}

	public void setUnAvailableNum(int unAvailableNum)
	{
		this.unAvailableNum = unAvailableNum;
	}
}