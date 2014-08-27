/**   
* @Title: RailApplicationToUserReq.java 
* @Package com.opzoon.vdi.core.app.request 
* @Description: TODO(用一句话描述该文件做什么) 
* @author Nathan   
* @date 2013-3-11 下午2:23:31 
* @version V1.0   
*/
package com.opzoon.vdi.core.app.request;

import com.opzoon.ohvc.common.anotation.DaoName;
import com.opzoon.ohvc.common.anotation.Required;

/** 
 * @ClassName: RailApplicationToUserReq 
 * @Description: TODO(这里用一句话描述这个类的作用) 
 * @author Nathan 
 * @date 2013-3-11 下午2:23:31 
 *  
 */
@DaoName(name="RailApplicationToUser")
public class RailApplicationToUserReq {
	@Required
	private String applicationid;
	private int[] userids;
	private Integer userid;
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
	 * @return userids 
	 */
	public int[] getUserids() {
		return userids;
	}
	/**
	 * @param userids the userids to set
	 */
	public void setUserids(int[] userids) {
		this.userids = userids;
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
	
}
