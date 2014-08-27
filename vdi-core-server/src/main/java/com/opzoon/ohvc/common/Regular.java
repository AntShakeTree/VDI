/**   
 * @Title: Regular.java 
 * @Package com.opzoon.ohvc.common 
 * @Description: TODO 
 * @author David   
 * @date 2013-1-30 上午9:48:22 
 * @version V1.0   
 */
package com.opzoon.ohvc.common;


/**
 * ClassName: Regular
 * @Description: TODO
 * @author David
 * @date 2013-1-30 上午9:48:22
 */
public enum Regular {

	NULL, INETADDRESS, UNREPEATABLE, EXIST;
	private Regular(){};
	private Regular(String daoname){
		this.daoName=daoname;
	}
	private String daoName;

	/** 
	 * @return daoName 
	 */
	public String getDaoName() {
		return daoName;
	}

	/**
	 * @param daoName the daoName to set
	 */
	public void setDaoName(String daoName) {
		this.daoName = daoName;
	}
	
}
