/**   
 * @Title: PageRequest.java 
 * @Package com.opzoon.ohvc.request 
 * @Description: TODO 
 * @author David   
 * @date 2013-1-30 下午4:49:55 
 * @version V1.0   
 */
package com.opzoon.ohvc.request;

/**
 * ClassName: PageRequest
 * @Description: TODO
 * @author David
 * @date 2013-1-30 下午4:49:55
 */
public interface PageRequest<T> {

	public String getSortkey();
	public void setSortkey(String sortkey);
	public int getAscend();
	public void setAscend(int ascend);
	public int getPagesize();
	public void setPagesize(int pagesize);
	public int getPage();
	public void setPage(int page);
	public int getAmount();
	public void setAmount(int amount);
}
