/**   
 * @Title: PublishOrUnPubilshRailApplicationReq.java 
 * @Package com.opzoon.ohvc.request 
 * @Description: TODO 
 * @author David   
 * @date 2013-1-31 上午12:20:58 
 * @version V1.0   
 */
package com.opzoon.vdi.core.app.request;

import java.io.Serializable;

import com.opzoon.ohvc.common.anotation.DaoName;
import com.opzoon.ohvc.common.anotation.Required;

/**
 * ClassName: PublishOrUnPubilshRailApplicationReq
 * @Description: 发布或者取消发布请求代理类
 * @author David
 * @date 2013-1-31 上午12:20:58
 */
@DaoName(name = "RailApllication")
public class RailConnectionReq implements Serializable {

	private static final long serialVersionUID = -8041161707375787080L;
	@Required
	private String applicationid;
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



}
