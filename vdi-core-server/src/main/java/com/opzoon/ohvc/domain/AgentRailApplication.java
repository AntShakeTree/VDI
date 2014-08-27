package com.opzoon.ohvc.domain;

import java.io.Serializable;

/**
 * ClassName: RailApllication
 * @Description: 虚拟应用
 * @author david
 * @date 2013-1-26 上午11:12:24
 */
public class AgentRailApplication implements Serializable {

	private static final long serialVersionUID = 123123431L;
	public static int OFFLINE = 0; // Offline 不在线
	public static int ONLINE = 1;// Online 可用 2 Full 预留 256 Error 错误
	public static int Full = 2;// 2 Full 预留 256 Error 错误
	public static int Error = 256;// 256 Error 错误
	private Integer idrailapplication;
	private String applicationname;
	private String applicationversion;
	private String applicationpath;
	private String applicationicon;
	private String applicationid;
	private String servername;// 虚拟应用服务器名称
	private Integer status = 1;
	private String applicationarguments;
	private boolean published ;

	// 外键--对应的是虚拟应用服务器的ID
	private Integer applicationserverid;

	/**
	 * @return published
	 */
	public boolean getPublished() {
		return published;
	}

	/**
	 * @param published
	 *            the published to set
	 */
	public void setPublished(boolean published) {
		this.published = published;
	}

	/**
	 * @return applicationname
	 */
	public String getApplicationname() {
		return applicationname;
	}

	/**
	 * @param status
	 *            the status to set
	 */
	public void setStatus(Integer status) {
		this.status = status;
	}

	/**
	 * @param applicationname
	 *            the applicationname to set
	 */
	public void setApplicationname(String applicationname) {
		this.applicationname = applicationname;
	}

	/**
	 * @return applicationversion
	 */
	public String getApplicationversion() {
		return applicationversion;
	}

	/**
	 * @param applicationversion
	 *            the applicationversion to set
	 */
	public void setApplicationversion(String applicationversion) {
		this.applicationversion = applicationversion;
	}

	/**
	 * @return applicationpath
	 */

	public String getApplicationpath() {
		return applicationpath;
	}

	/**
	 * @param applicationpath
	 *            the applicationpath to set
	 */
	public void setApplicationpath(String applicationpath) {
		this.applicationpath = applicationpath;
	}

	/**
	 * @return applicationicon
	 */
	public String getApplicationicon() {
		return applicationicon;
	}

	/**
	 * @param applicationicon
	 *            the applicationicon to set
	 */
	public void setApplicationicon(String applicationicon) {
		this.applicationicon = applicationicon;
	}

	/**
	 * @return applicationid
	 */
	public String getApplicationid() {
		return applicationid;
	}

	/**
	 * @param applicationid
	 *            the applicationid to set
	 */
	public void setApplicationid(String applicationid) {
		this.applicationid = applicationid;
	}

	/**
	 * @return applicationserverid
	 */
	public Integer getApplicationserverid() {
		return applicationserverid;
	}

	/**
	 * @param applicationserverid
	 *            the applicationserverid to set
	 */
	public void setApplicationserverid(Integer applicationserverid) {
		this.applicationserverid = applicationserverid;
	}

		/**
	 * @return status
	 */
	public int getStatus() {
		return status;
	}

	/** 
	 * @return idrailapplication 
	 */
	public Integer getIdrailapplication() {
		return idrailapplication;
	}

	/**
	 * @param idrailapplication the idrailapplication to set
	 */
	public void setIdrailapplication(Integer idrailapplication) {
		this.idrailapplication = idrailapplication;
	}

	/**
	 * @return servername
	 */
	public String getServername() {
		return servername;
	}

	/**
	 * @param servername
	 *            the servername to set
	 */
	public void setServername(String servername) {
		this.servername = servername;
	}

	/**
	 * @param status
	 *            the status to set
	 */
	public void setStatus(int status) {
		this.status = status;
	}

	/* (非 Javadoc) 
	* <p>Title: hashCode</p> 
	* <p>Description: </p> 
	* @return 
	* @see java.lang.Object#hashCode() 
	*/
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((applicationid == null) ? 0 : applicationid.hashCode());
		return result;
	}

	/* (非 Javadoc) 
	* <p>Title: equals</p> 
	* <p>Description: </p> 
	* @param obj
	* @return 
	* @see java.lang.Object#equals(java.lang.Object) 
	*/
	
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		AgentRailApplication other = (AgentRailApplication) obj;
		if (applicationid == null) {
			if (other.applicationid != null)
				return false;
		} else if (!applicationid.equals(other.applicationid))
			return false;
		return true;
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
