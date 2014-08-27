/**   
* Title: RailConnection.java 
* Package com.opzoon.ohvc.response 
* Description: TODO 
* @author David   
* @date 2013-3-4 下午2:36:52 
* @version V1.0   
*/
package com.opzoon.vdi.core.app.response;

import com.opzoon.vdi.core.domain.Connection;

/** 
 * ClassName: RailConnection 
 * Description: TODO
 * @author David 
 * @date 2013-3-4 下午2:36:52 
 *  
 */
public class RailConnection extends Connection{
	
	/** 
	* @Fields serialVersionUID : TODO(用一句话描述这个变量表示什么) 
	*/ 
	private static final long serialVersionUID = -3391243004497943858L;
	private String applicationPath;
	//TODO :2013-3-7 
	/**
	 * 虚拟应用的参数
	 */
	private String applicationarguments;
	private int stauts=1;
	
	/** 
	 * @return stauts 
	 */
	public int getStauts() {
		return stauts;
	}

	/**
	 * @param stauts the stauts to set
	 */
	public void setStauts(int stauts) {
		this.stauts = stauts;
	}

	/** 
	 * @return applicationPath 
	 */
	public String getApplicationPath() {
		return applicationPath;
	}

	/**
	 * @param applicationPath the applicationPath to set
	 */
	public void setApplicationPath(String applicationPath) {
		this.applicationPath = applicationPath;
	}

	/** 
	 * @return applicationarguments 
	 */
	public String getApplicationarguments() {
		return applicationarguments;
	}

	/**
	 * @param applicationarguments the applicationarguments to set
	 */
	public void setApplicationarguments(String applicationarguments) {
		this.applicationarguments = applicationarguments;
	}
	
}
