/**   
* Title: ListRailApplicationIcon.java 
* Package com.opzoon.ohvc.response 
* Description: TODO 
* @author David   
* @date 2013-2-26 下午5:52:28 
* @version V1.0   
*/
package com.opzoon.ohvc.domain;

import com.opzoon.ohvc.common.anotation.Required;

/** 
 * ClassName: ListRailApplicationIcon 
 * Description: TODO
 * @author David 
 * @date 2013-2-26 下午5:52:28 
 *  
 */
public class RailApplicationIcon {
	
	private String applicationicon;
	@Required
	private String applicationid;
	@Required
	private String servername;
	/** 
	 * @return applicationicon 
	 */
	public String getApplicationicon() {
		return applicationicon;
	}
	/**
	 * @param applicationicon the applicationicon to set
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
	 * @param applicationid the applicationid to set
	 */
	public void setApplicationid(String applicationid) {
		this.applicationid = applicationid;
	}
	/** 
	 * @return servername 
	 */
	public String getServername() {
		return servername;
	}
	/**
	 * @param servername the servername to set
	 */
	public void setServername(String servername) {
		this.servername = servername;
	}
	
	
}
