/**   
* Title: ListRailAssignmentsReq.java 
* Package com.opzoon.ohvc.request 
* Description: TODO 
* @author David   
* @date 2013-2-27 下午2:18:30 
* @version V1.0   
*/
package com.opzoon.vdi.core.app.request;

/** 
 * ClassName: ListRailAssignmentsReq 
 * Description: TODO
 * @author David 
 * @date 2013-2-27 下午2:18:30 
 *  
 */
public class ListRailAssignmentsReq {
	private int resourcetype;
	private String applicationid;
	private Integer userid;
	private Integer groupid;
	private Integer organizationid;
	private String sortkey;
	private int ascend;
	private int pagesize=10;
	private int page=1;
	/** 
	 * @return resourcetype 
	 */
	public int getResourcetype() {
		return resourcetype;
	}
	/**
	 * @param resourcetype the resourcetype to set
	 */
	public void setResourcetype(int resourcetype) {
		this.resourcetype = resourcetype;
	}
	/** 
	 * @return applicationid 
	 */
	public String getApplicationid() {
		return applicationid;
	}
	/**
	 * @param applicationid the applicationid to set
	 */
	public void setApplicationid(String applicationid) {
		this.applicationid = applicationid;
	}
	/** 
	 * @return userid 
	 */
	public Integer getUserid() {
		return userid;
	}
	/**
	 * @param userid the userid to set
	 */
	public void setUserid(Integer userid) {
		this.userid = userid;
	}
	/** 
	 * @return groupid 
	 */
	public Integer getGroupid() {
		return groupid;
	}
	/**
	 * @param groupid the groupid to set
	 */
	public void setGroupid(Integer groupid) {
		this.groupid = groupid;
	}
	/** 
	 * @return organizationid 
	 */
	public Integer getOrganizationid() {
		return organizationid;
	}
	/**
	 * @param organizationid the organizationid to set
	 */
	public void setOrganizationid(Integer organizationid) {
		this.organizationid = organizationid;
	}
	/** 
	 * @return sortkey 
	 */
	public String getSortkey() {
		return sortkey;
	}
	/**
	 * @param sortkey the sortkey to set
	 */
	public void setSortkey(String sortkey) {
		this.sortkey = sortkey;
	}
	/** 
	 * @return ascend 
	 */
	public int getAscend() {
		return ascend;
	}
	/**
	 * @param ascend the ascend to set
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
	 * @param pagesize the pagesize to set
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
	 * @param page the page to set
	 */
	public void setPage(int page) {
		this.page = page;
	}
		
}
