/**   
 * @Title: RailApplicationViewReq.java 
 * @Package com.opzoon.ohvc.request 
 * @Description: TODO 
 * @author David   
 * @date 2013-1-31 上午12:03:08 
 * @version V1.0   
 */
package com.opzoon.vdi.core.app.request;

import java.io.Serializable;

import com.opzoon.ohvc.common.anotation.DaoName;
import com.opzoon.ohvc.request.PageRequest;
import com.opzoon.vdi.core.app.domain.RailApplicationView;

/**
 * ClassName: RailApplicationViewReq
 * @Description: TODO
 * @author David
 * @date 2013-1-31 上午12:03:08
 */
@DaoName(name="RailApplicationView")
public class RailApplicationViewReq implements PageRequest<RailApplicationView>, Serializable {

	private static final long serialVersionUID = -8060746993701983452L;
	private String applicationname, applicationversion, applicationicon, applicationid;
	private Boolean published;
	private int ascend;
	private int pagesize = 15;
	private int page = 1;
	private int amount;
	private String sortkey;

	
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
	 * @return applicationname
	 */
	public String getApplicationname() {
		return applicationname;
	}

	/**
	 * @param applicationname
	 *            the applicationname to set
	 */
	public void setApplicationname(String applicationname) {
		this.applicationname = applicationname;
	}

	/**
	 * @return applicationversion
	 */
	public String getApplicationversion() {
		return applicationversion;
	}

	/**
	 * @param applicationversion
	 *            the applicationversion to set
	 */
	public void setApplicationversion(String applicationversion) {
		this.applicationversion = applicationversion;
	}

	/**
	 * @return applicationicon
	 */
	public String getApplicationicon() {
		return applicationicon;
	}

	/**
	 * @param applicationicon
	 *            the applicationicon to set
	 */
	public void setApplicationicon(String applicationicon) {
		this.applicationicon = applicationicon;
	}

	/**
	 * @return applicationid
	 */
	public String getApplicationid() {
		return applicationid;
	}

	/**
	 * @param applicationid
	 *            the applicationid to set
	 */
	public void setApplicationid(String applicationid) {
		this.applicationid = applicationid;
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
		return pagesize;
	}

	/**
	 * @param pagesize
	 *            the pagesize to set
	 */
	public void setPagesize(int pagesize) {
		this.pagesize = pagesize;
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

}
