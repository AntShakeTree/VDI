package com.opzoon.vdi.core.app.domain;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * ClassName: RailApllication
 * @Description: 虚拟应用
 * @author david
 * @date 2013-1-26 上午11:12:24
 */
@Entity
@Table(name = "railapplication")
public class RailApplication implements Serializable {

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

	private boolean published ;

	// 外键--对应的是虚拟应用服务器的ID
	private Integer applicationserverid;
	private String applicationarguments;

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
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
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

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((applicationpath == null) ? 0 : applicationpath.hashCode());
		result = prime * result + ((applicationserverid == null) ? 0 : applicationserverid.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (!(obj instanceof RailApplication)) {
			return false;
		}
		RailApplication other = (RailApplication) obj;
		if (applicationpath == null) {
			if (other.applicationpath != null) {
				return false;
			}
		} else if (!applicationpath.equals(other.applicationpath)) {
			return false;
		}
		return true;
	}

}
