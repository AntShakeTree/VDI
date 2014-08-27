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
import java.util.List;

import com.opzoon.ohvc.common.anotation.DaoName;
import com.opzoon.ohvc.common.anotation.Required;

/**
 * ClassName: PublishOrUnPubilshRailApplicationReq
 * @Description: 发布或者取消发布请求代理类
 * @author David
 * @date 2013-1-31 上午12:20:58
 */
@DaoName(name = "RailApplication")
public class PublishOrUnPubilshRailApplicationReq implements Serializable{
	
	private static final long serialVersionUID = -8041161707375887080L;
	@Required
	private List<Integer> idrailapplications;
	/** 
	 * @return idrailapplications 
	 */
	public List<Integer> getIdrailapplications() {
		return idrailapplications;
	}
	/**
	 * @param idrailapplications the idrailapplications to set
	 */
	public void setIdrailapplications(List<Integer> idrailapplications) {
		this.idrailapplications = idrailapplications;
	}
	
}
