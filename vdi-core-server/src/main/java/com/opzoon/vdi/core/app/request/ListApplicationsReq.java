/**   
 * @Title: ListApplicationServersReq.java 
 * @Package com.opzoon.ohvc.domain 
 * @Description: TODO 
 * @author David   
 * @date 2013-1-30 下午4:24:14 
 * @version V1.0   
 */
package com.opzoon.vdi.core.app.request;

import java.io.Serializable;

import com.opzoon.ohvc.common.anotation.DaoName;
import com.opzoon.ohvc.request.PageRequest;
import com.opzoon.vdi.core.app.domain.RailApplication;

/**
 * ClassName: ListApplicationServersReq
 * 
 * @Description: 查询应用服务器列表请求处理类
 * @author David
 * @date 2013-1-30 下午4:24:14
 */
@DaoName(name = "RailApplication")
public class ListApplicationsReq implements PageRequest<RailApplication>, Serializable {

	private static final long serialVersionUID = 2264468126394754986L;
	private Boolean published;
	private Integer applicationserverid;
	private Integer servertype;
	private String sortkey;
	private Integer status;
	private int ascend;
	private int pagesize = 15;
	private int page = 1;
	private int amount;

	/**
	 * @return amount
	 */
	public int getAmount() {
		return amount;
	}

	/**
	 * @param amount
	 *            the amount to set
	 */
	public void setAmount(int amount) {
		this.amount = amount;
	}

	
	/** 
	 * @return published 
	 */
	public Boolean getPublished() {
		return published;
	}

	/**
	 * @param published the published to set
	 */
	public void setPublished(Boolean published) {
		this.published = published;
	}

	/**
	 * @return applicationserverid
	 */
	public Integer getApplicationserverid() {
		return applicationserverid;
	}

	/**
	 * @param applicationserverid
	 *            the applicationserverid to set
	 */
	public void setApplicationserverid(Integer applicationserverid) {
		this.applicationserverid = applicationserverid;
	}

	/**
	 * @return servertype
	 */
	public Integer getServertype() {
		return servertype;
	}

	/**
	 * @param servertype
	 *            the servertype to set
	 */
	public void setServertype(Integer servertype) {
		this.servertype = servertype;
	}

	/**
	 * @return sortkey
	 */
	public String getSortkey() {
		return sortkey;
	}

	/**
	 * @param sortkey
	 *            the sortkey to set
	 */
	public void setSortkey(String sortkey) {
		this.sortkey = sortkey;
	}

	/**
	 * @return status
	 */
	public Integer getStatus() {
		return status;
	}

	/**
	 * @param status
	 *            the status to set
	 */
	public void setStatus(Integer status) {
		this.status = status;
	}

	/**
	 * @return ascend
	 */
	public int getAscend() {
		return ascend;
	}

	/**
	 * @param ascend
	 *            the ascend to set
	 */
	public void setAscend(int ascend) {
		this.ascend = ascend;
	}

	/**
	 * @return pagesize
	 */
	public int getPagesize() {
		if (pagesize == -1) {
			this.pagesize = Integer.MAX_VALUE;
		}
		return pagesize;
	}

	/**
	 * @param pagesize
	 *            the pagesize to set
	 */
	public void setPagesize(int pagesize) {
		if (pagesize == -1) {
			this.pagesize = Integer.MAX_VALUE;
		} else {
			this.pagesize = pagesize;
		}
	}

	/**
	 * @return page
	 */
	public int getPage() {
		return page;
	}

	/**
	 * @param page
	 *            the page to set
	 */
	public void setPage(int page) {
		this.page = page;
	}

}
