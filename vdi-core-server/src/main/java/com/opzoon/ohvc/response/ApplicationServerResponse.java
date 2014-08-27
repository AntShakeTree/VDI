/**   
* @Title: ApplicationServerResponse.java 
* @Package com.opzoon.ohvc.service 
* @Description: TODO 
* @author David   
* @date 2013-1-29 下午2:51:08 
* @version V1.0   
*/
package com.opzoon.ohvc.response;

import com.google.gson.Gson;
import com.opzoon.ohvc.common.RailResponse;
import com.opzoon.ohvc.domain.Head;
import com.opzoon.ohvc.domain.AgentRailApplicationServer;

/** 
 * ClassName: ApplicationServerResponse 
 * @Description: 虚拟应用服务器返回值
 * @author David 
 * @date 2013-1-29 下午2:51:08 
 *  
 */
public class ApplicationServerResponse  implements RailResponse<AgentRailApplicationServer>{
	private Head head;
	private AgentRailApplicationServer body;
	
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
	public AgentRailApplicationServer getBody() {
		return body;
	}
	/**
	 * @param body the body to set
	 */
	public void setBody(AgentRailApplicationServer body) {
		this.body = body;
	}
	/* (非 Javadoc) 
	* <p>Title: instanceByJson</p> 
	* <p>Description: </p> 
	* @param reString
	* @return 
	* @see com.opzoon.ohvc.common.RailResponse#instanceByJson(java.lang.String) 
	*/
	
	@Override
	public ApplicationServerResponse instanceByJson(String reString) {
		Gson gson =new Gson();
		return gson.fromJson(reString,this.getClass());
	}
	

}
