/**   
* @Title: RailResponse.java 
* @Package com.opzoon.vdi.core.domain 
* @Description: TODO(用一句话描述该文件做什么) 
* @author david   
* @date 2013-1-25 下午2:33:03 
* @version V1.0   
*/
package com.opzoon.ohvc.common;

import com.opzoon.ohvc.domain.Head;

/** 
 * ClassName: RailResponse 
 * @Description: 虚拟应用返回值
 * @author david 
 * @date 2013-1-25 下午2:33:03 
 *  
 */
public interface  RailResponse<T> {
	
	/** 
	 * @return head 
	 */
	public Head getHead();
	/**
	 * @param head the head to set
	 */
	public void setHead(Head head);
	/** 
	 * @return body 
	 */
	public T getBody(); 
	/**
	 * @param body the body to set
	 */
	public void setBody(T body);
	

	public RailResponse<T> instanceByJson(String reString);
}
