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
 * @Description: 分给用户实体
 * @author David
 * @date 2013-1-31 上午1:04:37
 */
@Entity
@Table(name = "applicationtoorganization")
public class RailApplicationToOrganization implements Serializable{
	
	private static final long serialVersionUID = -916751791954236641L;
	@Required
	private String applicationid;
	@Required(min=0)
	private Integer organizationid;
	private String id;
	/** 
	* <p>Title: </p> 
	* <p>Description: </p> 
	* @param applicationid2
	* @param organizationid2 
	*/
	
	public RailApplicationToOrganization(String applicationid, Integer organizationid) {
		this.applicationid=applicationid;
		this.organizationid=organizationid;
		getId();
	}
	public RailApplicationToOrganization(){}
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
	 * @return organizationid 
	 */
	public Integer getOrganizationid() {
		return organizationid;
	}
	/**
	 * @param organizationid the organizationid to set
	 */
	public void setOrganizationid(Integer organizationid) {
		this.organizationid = organizationid;
	}
	/** 
	 * @return id 
	 */

	@Id
	public String getId() {
		this.id=OpzoonUtils.MD5(applicationid+"")+organizationid;
		return id;
	}
	/**
	 * @param id the id to set
	 */
	public void setId(String id) {
		this.id=OpzoonUtils.MD5(applicationid+"")+organizationid;
	}


}