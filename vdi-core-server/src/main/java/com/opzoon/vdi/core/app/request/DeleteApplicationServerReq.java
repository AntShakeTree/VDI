/**   
* @Title: DelApplicationServer.java 
* @Package com.opzoon.ohvc.request 
* @Description: TODO 
* @author David   
* @date 2013-1-30 下午1:06:10 
* @version V1.0   
*/
package com.opzoon.vdi.core.app.request;

import java.io.Serializable;

import com.opzoon.ohvc.common.Regular;
import com.opzoon.ohvc.common.anotation.DaoName;
import com.opzoon.ohvc.common.anotation.Required;

/** 
 * ClassName:DelApplicationServerReq
 * @Description:删除虚拟应用的请求体
 * @author David 
 * @date 2013-1-30 下午1:06:10 
 *  
 */
@DaoName(name="RailApplicationServer")
public class DeleteApplicationServerReq implements Serializable{

	private static final long serialVersionUID = 1L;
	@Required(regular=Regular.EXIST,daoName="RailApplicationServer")
	private Integer idapplicationserver;// id
	/** 
	 * @return idapplicationserver 
	 */
	public Integer getIdapplicationserver() {
		return idapplicationserver;
	}

	/**
	 * @param idapplicationserver the idapplicationserver to set
	 */
	public void setIdapplicationserver(Integer idapplicationserver) {
		this.idapplicationserver = idapplicationserver;
	}
	
}
