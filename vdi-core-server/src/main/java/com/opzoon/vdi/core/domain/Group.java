package com.opzoon.vdi.core.domain;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * 组.
 */
@XmlRootElement(name = "group")
@Entity
@Table(name = "groups")
public class Group implements Serializable {

	private static final long serialVersionUID = 1L;
	
	private Integer idgroup;
	private String groupname;
	private int domainid;
	private String notes;
	private int useramount;
	private String domainname;
	
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	public Integer getIdgroup() {
		return idgroup;
	}
	public void setIdgroup(Integer idgroup) {
		this.idgroup = idgroup;
	}
	/**
	 * @return 组名.
	 */
	public String getGroupname() {
		return groupname;
	}
	public void setGroupname(String groupname) {
		this.groupname = groupname;
	}
	/**
	 * @return 组所属的域ID.
	 */
	public int getDomainid() {
		return domainid;
	}
	public void setDomainid(int domainid) {
		this.domainid = domainid;
	}
	/**
	 * @return 备注.
	 */
	public String getNotes() {
		return notes;
	}
	public void setNotes(String notes) {
		this.notes = notes;
	}
	/**
	 * @return 组中用户数.
	 */
	@Transient
	public int getUseramount() {
		return useramount;
	}
	public void setUseramount(int useramount) {
		this.useramount = useramount;
	}
	/**
	 * @return 域名.
	 */
	@Transient
	public String getDomainname() {
		return domainname;
	}
	public void setDomainname(String domainname) {
		this.domainname = domainname;
	}
	
}
