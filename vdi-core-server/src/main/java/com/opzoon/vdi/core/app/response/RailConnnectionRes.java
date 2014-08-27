/**   
* @Title: RailConnnectionRes.java 
* @Package com.opzoon.ohvc.response 
* @Description: TODO 
* @author David   
* @date 2013-1-31 下午3:03:08 
* @version V1.0   
*/
package com.opzoon.vdi.core.app.response;

import java.io.Serializable;

import com.opzoon.ohvc.domain.Head;
import com.opzoon.vdi.core.domain.Connection;

/** 
 * ClassName: RailConnnectionRes 
 * @Description: 虚拟应用返回代理类
 * @author David 
 * @date 2013-1-31 下午3:03:08 
 *  
 */
public class RailConnnectionRes  implements Serializable{
	private static final long serialVersionUID = 1931700070128225828L;
	private RailConnection body;
	private Head head;
	/** 
	 * @return head 
	 */
	public Head getHead() {
		return head;
	}
	
	/** 
	 * @return body 
	 */
	public RailConnection getBody() {
		return body;
	}

	/**
	 * @param body the body to set
	 */
	public void setBody(RailConnection body) {
		this.body = body;
	}

	/**
	 * @param head the head to set
	 */
	public void setHead(Head head) {
		this.head = head;
	}
	
}
