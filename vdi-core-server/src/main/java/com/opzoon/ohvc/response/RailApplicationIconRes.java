/**   
 * Title: RailApplicationIconRes.java 
 * Package com.opzoon.ohvc.response 
 * Description: TODO 
 * @author David   
 * @date 2013-2-26 下午5:58:52 
 * @version V1.0   
 */
package com.opzoon.ohvc.response;

import com.opzoon.ohvc.domain.Head;
import com.opzoon.ohvc.domain.RailApplicationIcon;

public class RailApplicationIconRes {
	private Head head;
	private RailApplicationIcon body;

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
	public RailApplicationIcon getBody() {
		return body;
	}

	/**
	 * @param body
	 *            the body to set
	 */
	public void setBody(RailApplicationIcon body) {
		this.body = body;
	}

}
