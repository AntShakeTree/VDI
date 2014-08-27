/**   
 * @Title: ApplicationToUser.java 
 * @Package com.opzoon.ohvc.domain 
 * @Description: TODO 
 * @author David   
 * @date 2013-1-31 上午1:04:37 
 * @version V1.0   
 */
package com.opzoon.vdi.core.app.domain;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

import com.opzoon.ohvc.common.OpzoonUtils;
import com.opzoon.ohvc.common.anotation.Required;

/**
 * ClassName: ApplicationToUser
 * 
 * @Description: 分给用户实体
 * @author David
 * @date 2013-1-31 上午1:04:37
 */
@Entity
@Table(name = "applicationtogroup")
public class RailApplicationToGroup implements Serializable {

	private static final long serialVersionUID = 4708009476962385204L;
	@Required
	private String applicationid;
	@Required(min = 0)
	private Integer groupid;
	private String id;

	/**
	 * <p>
	 * Title:
	 * </p>
	 * <p>
	 * Description:
	 * </p>
	 * 
	 * @param applicationid2
	 * @param id2
	 */

	public RailApplicationToGroup(String applicationid, Integer groupid) {
		this.applicationid = applicationid;
		this.groupid = groupid;
		getId();
	}
	public RailApplicationToGroup(){};
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
	 * @return groupid
	 */
	public Integer getGroupid() {
		return groupid;
	}

	/**
	 * @param groupid
	 *            the groupid to set
	 */
	public void setGroupid(Integer groupid) {
		this.groupid = groupid;
	}

	/**
	 * @return id
	 */
	@Id
	public String getId() {
		this.id = OpzoonUtils.MD5(applicationid + "") + this.groupid;
		return id;
	}

	/**
	 * @param id
	 *            the id to set
	 */
	public void setId(String id) {
		this.id = OpzoonUtils.MD5(applicationid + "") + this.groupid;
	}

}