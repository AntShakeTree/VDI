/**   
 * @Title: RailApplicationResourceReq.java 
 * @Package com.opzoon.ohvc.request 
 * @Description: TODO 
 * @author David   
 * @date 2013-2-22 下午1:18:36 
 * @version V1.0   
 */
package com.opzoon.vdi.core.app.request;

import com.opzoon.ohvc.common.anotation.Required;

/**
 * ClassName: RailApplicationResourceReq Description: TODO
 * 
 * @author David
 * @date 2013-2-22 下午1:18:36
 */
public class RailApplicationResourceReq {

	@Required
	private Integer userid;

	/**
	 * @return userid
	 */
	public Integer getUserid() {
		return userid;
	}

	/**
	 * @param userid
	 *            the userid to set
	 */
	public void setUserid(Integer userid) {
		this.userid = userid;
	}

}
