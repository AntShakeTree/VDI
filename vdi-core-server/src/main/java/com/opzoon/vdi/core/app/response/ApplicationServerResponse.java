/**   
* @Title: ApplicationServerResponse.java 
* @Package com.opzoon.ohvc.service 
* @Description: TODO 
* @author David   
* @date 2013-1-29 下午2:51:08 
* @version V1.0   
*/
package com.opzoon.vdi.core.app.response;

import com.google.gson.Gson;
import com.opzoon.ohvc.common.RailResponse;
import com.opzoon.ohvc.domain.AgentRailApplicationServer;
import com.opzoon.ohvc.domain.Head;
import com.opzoon.vdi.core.app.domain.RailApplicationServer;

/** 
 * ClassName: ApplicationServerResponse 
 * @Description: 虚拟应用服务器返回值
 * @author David 
 * @date 2013-1-29 下午2:51:08 
 *  
 */
public class ApplicationServerResponse  implements RailResponse<RailApplicationServer>{
	private Head head;
	private RailApplicationServer body;
	/** 
	 * @return head 
	 */
	public Head getHead() {
		return head;
	}
	/**
	 * @param head the head to set
	 */
	public void setHead(Head head) {
		this.head = head;
	}
	/** 
	 * @return body 
	 */
	public RailApplicationServer getBody() {
		return body;
	}
	/**
	 * @param body the body to set
	 */
	public void setBody(RailApplicationServer body) {
		this.body = body;
	}
	/* (非 Javadoc) 
	* <p>Title: instanceByJson</p> 
	* <p>Description: </p> 
	* @param arg0
	* @return 
	* @see com.opzoon.ohvc.common.RailResponse#instanceByJson(java.lang.String) 
	*/
	
	@Override
	public RailResponse<RailApplicationServer> instanceByJson(String arg0) {
		return null;
	}
	
}
