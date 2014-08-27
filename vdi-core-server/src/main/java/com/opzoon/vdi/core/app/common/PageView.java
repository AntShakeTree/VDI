package com.opzoon.vdi.core.app.common;


public class PageView {
	private long totalpage = 1;
	private int maxresult = 15;
	private int currentpage = 1;
	private long totalrecord;
	private int pagecode = 10;

	public int getFirstResult() {
		return (this.currentpage - 1) * this.maxresult;
	}

	public int getPagecode() {
		return pagecode;
	}

	public void setPagecode(int pagecode) {
		this.pagecode = pagecode;
	}

	public PageView(int maxresult, int currentpage) {
		this.maxresult = maxresult;
		this.currentpage = currentpage;
	}

	public long getTotalrecord() {
		return totalrecord;
	}

	public void setTotalrecord(long totalrecord) {
		this.totalrecord = totalrecord;
		setTotalpage(this.totalrecord % this.maxresult == 0 ? this.totalrecord / this.maxresult : this.totalrecord / this.maxresult + 1);
	}


	public long getTotalpage() {
		return totalpage;
	}

	public void setTotalpage(long totalpage) {
		this.totalpage = totalpage;
	}

	public int getMaxresult() {
		return maxresult;
	}

	public int getCurrentpage() {
		return currentpage;
	}
}
