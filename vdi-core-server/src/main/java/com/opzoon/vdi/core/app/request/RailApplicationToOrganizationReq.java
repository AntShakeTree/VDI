/**   
* @Title: RailApplicationToOrganizationReq.java 
* @Package com.opzoon.vdi.core.app.request 
* @Description: TODO(用一句话描述该文件做什么) 
* @author Nathan   
* @date 2013-3-11 下午2:33:05 
* @version V1.0   
*/
package com.opzoon.vdi.core.app.request;

import com.opzoon.ohvc.common.anotation.Required;

/** 
 * @ClassName: RailApplicationToOrganizationReq 
 * @Description: TODO(这里用一句话描述这个类的作用) 
 * @author Nathan 
 * @date 2013-3-11 下午2:33:05 
 *  
 */
public class RailApplicationToOrganizationReq {
	@Required
	private String applicationid;
	private int[] organizationids;
	private Integer organizationid;
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
	 * @return organizationids 
	 */
	public int[] getOrganizationids() {
		return organizationids;
	}
	/**
	 * @param organizationids the organizationids to set
	 */
	public void setOrganizationids(int[] organizationids) {
		this.organizationids = organizationids;
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
	
}
