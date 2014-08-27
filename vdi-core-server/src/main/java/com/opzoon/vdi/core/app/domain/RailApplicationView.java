/**   
 * @Title: RailApplicationView.java 
 * @Package com.opzoon.ohvc.domain 
 * @Description: TODO 
 * @author David   
 * @date 2013-1-30 下午11:42:17 
 * @version V1.0   
 */
package com.opzoon.vdi.core.app.domain;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;

/**
 * ClassName: RailApplicationView
 * @Description: 虚拟应用视图类
 * @author David
 * @date 2013-1-30 下午11:42:17
 */
@Entity
@Table(name = "railapplicationview")
public class RailApplicationView  implements Serializable{

	private static final long serialVersionUID = -7109945295251801087L;
	private String applicationname, applicationversion, applicationicon, applicationid;
	private boolean published;
	private int status;
	private Integer replication;
	private Integer connectionstatus;
	
	/**
	 * @return applicationname
	 */
	public String getApplicationname() {
		return applicationname;
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
	 * @return status 
	 */
	public int getStatus() {
		return status;
	}

	/**
	 * @param status the status to set
	 */
	public void setStatus(int status) {
		this.status = status;
	}

	/**
	 * @param applicationversion
	 *            the applicationversion to set
	 */
	public void setApplicationversion(String applicationversion) {
		this.applicationversion = applicationversion;
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
	@Id
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
	 * @return published
	 */
	public boolean isPublished() {
		return published;
	}
	/** 
	 * @return replication 
	 */
	public Integer getReplication() {
		return replication;
	}

	/**
	 * @param replication the replication to set
	 */
	public void setReplication(Integer replication) {
		this.replication = replication;
	}

	/**
	 * @param published
	 *            the published to set
	 */
	public void setPublished(boolean published) {
		this.published = published;
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
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (!(obj instanceof RailApplicationView)) {
			return false;
		}
		RailApplicationView other = (RailApplicationView) obj;
		if (applicationid == null) {
			if (other.applicationid != null) {
				return false;
			}
		} else if (!applicationid.equals(other.applicationid)) {
			return false;
		}
		return true;
	}

	/** 
	 * @return connectionstatus 
	 */
	@Transient
	public Integer getConnectionstatus() {
		return connectionstatus;
	}

	/**
	 * @param connectionstatus the connectionstatus to set
	 */
	public void setConnectionstatus(Integer connectionstatus) {
		this.connectionstatus = connectionstatus;
	}
	
}
