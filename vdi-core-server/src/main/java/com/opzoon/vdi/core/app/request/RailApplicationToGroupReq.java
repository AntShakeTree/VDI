/**   
* @Title: RailApplicationToGroupReq.java 
* @Package com.opzoon.vdi.core.app.request 
* @Description: TODO(用一句话描述该文件做什么) 
* @author Nathan   
* @date 2013-3-11 下午2:40:56 
* @version V1.0   
*/
package com.opzoon.vdi.core.app.request;

import com.opzoon.ohvc.common.anotation.Required;

/** 
 * @ClassName: RailApplicationToGroupReq 
 * @Description: TODO(这里用一句话描述这个类的作用) 
 * @author Nathan 
 * @date 2013-3-11 下午2:40:56 
 *  
 */
public class RailApplicationToGroupReq {
	@Required
	private String applicationid;
	private Integer groupid;
	private int[] groupids;
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
	 * @return groupids 
	 */
	public int[] getGroupids() {
		return groupids;
	}
	/**
	 * @param groupids the groupids to set
	 */
	public void setGroupids(int[] groupids) {
		this.groupids = groupids;
	}
	
}
