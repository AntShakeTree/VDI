package com.opzoon.vdi.core.app.domain;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;

/**
 * @ClassName: UseraAndResourcestatus
 * @Description: TODO(这里用一句话描述这个类的作用)
 * @author Nathan
 * @date 2013-3-6 下午6:30:44
 */
@Entity
@Table(name = "userandresourcestatus")
public class UserAndResourcestatus {
	public final static int CONNECTION_STATUS = 3;
	public final static int DESTROYCONN_STATUS = 4;
	private Integer userid;
	private String applicationid;
	private String id;
	private int status = 3;// 3连接， 4断开
	private Integer applicationserverid;
	/** 
	 * @return userid 
	 */
	public Integer getUserid() {
		return userid;
	}
	/**
	 * @param userid the userid to set
	 */
	public void setUserid(Integer userid) {
		this.userid = userid;
	}
	/** 
	 * @return applicationid 
	 */
	public String getApplicationid() {
		return applicationid;
	}
	/**
	 * @param applicationid the applicationid to set
	 */
	public void setApplicationid(String applicationid) {
		this.applicationid = applicationid;
	}
	/** 
	 * @return id 
	 */
	@Id
	public String getId() {
		return id;
	}
	/**
	 * @param id the id to set
	 */
	public void setId(String id) {
		this.id = id;
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
	 * @return applicationserverid 
	 */
	@Transient
	public Integer getApplicationserverid() {
		return applicationserverid;
	}
	/**
	 * @param applicationserverid the applicationserverid to set
	 */
	public void setApplicationserverid(Integer applicationserverid) {
		this.applicationserverid = applicationserverid;
	}

}