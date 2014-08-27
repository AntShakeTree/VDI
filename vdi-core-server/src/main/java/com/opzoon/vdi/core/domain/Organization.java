package com.opzoon.vdi.core.domain;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Transient;
import javax.xml.bind.annotation.XmlRootElement;

import org.codehaus.jackson.annotate.JsonIgnore;

/**
 * 用户的组织关系, 通常用来存放目录服务中的用户组织关系. 本地域默认存在一个ID为0的顶层组织.
 */
@XmlRootElement(name = "organization")
@Entity
public class Organization implements Serializable {

	private static final long serialVersionUID = 1L;

	/**
	 * 组织类型: 本地组织.
	 */
	public static final int ORGANIZATION_TYPE_LOCAL = 0x0;
	/**
	 * 组织类型: MSAD同步组织单元.
	 */
	public static final int ORGANIZATION_TYPE_MSAD = 0x1;
	/**
	 * 组织类型: ApacheDS同步组织单元.
	 */
	public static final int ORGANIZATION_TYPE_APACHE = 0x2;

	/**
	 * 默认顶层组织的ID.
	 */
	public static final int DEFAULT_ORGANIZATION_ID = 0;

	private Integer idorganization;
	private int organizationtype;
  @JsonIgnore
  private String guid;
	private int domainid;
	private String organizationname;
	private int level;
	private int parent;
	private String notes;
	private String domainname;
	
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	public Integer getIdorganization() {
		return idorganization;
	}
	public void setIdorganization(Integer idorganization) {
		this.idorganization = idorganization;
	}
	/**
	 * @return 组织类型. 参考{@link Organization#ORGANIZATION_TYPE_LOCAL}, {@link Organization#ORGANIZATION_TYPE_MSAD}, {@link Organization#ORGANIZATION_TYPE_APACHE}.
	 */
	public int getOrganizationtype() {
		return organizationtype;
	}
	public void setOrganizationtype(int organizationtype) {
		this.organizationtype = organizationtype;
	}
  public String getGuid()
  {
    return guid;
  }
  public void setGuid(String guid)
  {
    this.guid = guid;
  }
	/**
	 * @return 组织所属的域.
	 */
	public int getDomainid() {
		return domainid;
	}
	public void setDomainid(int domainid) {
		this.domainid = domainid;
	}
	/**
	 * @return 组织名称.
	 */
	public String getOrganizationname() {
		return organizationname;
	}
	public void setOrganizationname(String organizationname) {
		this.organizationname = organizationname;
	}
	/**
	 * @return 组织级别, 顶层组织的组织级别为0.
	 */
	public int getLevel() {
		return level;
	}
	public void setLevel(int level) {
		this.level = level;
	}
	/**
	 * @return 上一级组织ID, 顶层组织的上一级组织为-1.
	 */
	public int getParent() {
		return parent;
	}
	public void setParent(int parent) {
		this.parent = parent;
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
	@Transient
	public String getDomainname() {
		return domainname;
	}
	public void setDomainname(String domainname) {
		this.domainname = domainname;
	}
	
}
