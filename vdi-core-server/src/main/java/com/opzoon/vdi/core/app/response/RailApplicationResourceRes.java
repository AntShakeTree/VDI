/**   
 * Title: RailApplicationResourceRes.java 
 * Package com.opzoon.ohvc.response 
 * Description: TODO 
 * @author David   
 * @date 2013-2-22 下午1:25:47 
 * @version V1.0   
 */
package com.opzoon.vdi.core.app.response;

import java.util.List;

import com.opzoon.ohvc.domain.Head;
import com.opzoon.vdi.core.app.domain.RailApplicationView;

/**
 * ClassName: RailApplicationResourceRes 
 *  
 *  Description: 根据userid查询虚拟应用列表返回实体类
 * 
 * @author David
 * @date 2013-2-22 下午1:25:47
 */
public class RailApplicationResourceRes {
	private Head head;
	private List<RailApplicationView> body;

	/**
	 * @return head
	 */
	public Head getHead() {
		return head;
	}

	/**
	 * @param head
	 *            the head to set
	 */
	public void setHead(Head head) {
		this.head = head;
	}

	/**
	 * @return body
	 */
	public List<RailApplicationView> getBody() {
		return body;
	}

	/**
	 * @param body
	 *            the body to set
	 */
	public void setBody(List<RailApplicationView> body) {
		this.body = body;
	}

}
