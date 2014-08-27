package com.opzoon.ohvc.domain;
/**
 * 
 * @author david
 * @version: V04 
 * @since V04
 * 2013-2-1
 */
public class RailInformation{
	
	private int jointype;// 主机所在组织的类型 unknown：未知状 unjoined：没有加入任何组织 group：加入了工作组 domain：加入了域
	private String joinname;// 所在域或工作组的名字

	

	public int getJointype() {
		return jointype;
	}

	public void setJointype(int jointype) {
		this.jointype = jointype;
	}

	/**
	 * @return the joinname
	 */
	public String getJoinname() {
		return joinname;
	}

	/**
	 * @param joinname
	 *            the joinname to set
	 */
	public void setJoinname(String joinname) {
		this.joinname = joinname;
	}

}
