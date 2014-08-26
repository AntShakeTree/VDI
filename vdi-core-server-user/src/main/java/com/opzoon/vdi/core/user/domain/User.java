package com.opzoon.vdi.core.user.domain;

import java.io.Serializable;
import java.util.List;
import java.util.Set;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.Transient;
import javax.xml.bind.annotation.XmlRootElement;

import org.codehaus.jackson.annotate.JsonIgnore;

import com.opzoon.vdi.core.facade.UserFacade.GroupInfo;

/**
 * �?��可以访问系统的用�? 不区分普通用户或管理�? 系统默认存在�?��ID�?的管理员, 是第�?��全局管理�?
 */
@XmlRootElement(name = "user")
//@Entity
public class User implements Serializable {

	private static final long serialVersionUID = 1L;
	
	public static final int DEFAULT_ADMIN_ID = 0;
	
	private Integer iduser;
	private int usertype;
//  @JsonIgnore
  private String guid;
	private int domainid;
	private Integer organizationid;
	private String username;
	private String password;
	private String realname;
	private String idcardtype;
	private String idcard;
	private String email;
	private String address;
	private String telephone;
	private String notes;
	private Set<GroupElement> groupElements;
	private int rootadmin;
	private List<GroupInfo> groups;
	private String organizationname;
  private String domainname;
  private int deleted;
  private int assignedrsa;
  @JsonIgnore
  private Set<RSAKey> rsakeys;
	
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	public Integer getIduser() {
		return iduser;
	}
	public void setIduser(Integer iduser) {
		this.iduser = iduser;
	}
	/**
	 * @return 用户类型. 参�?{@link Domain#DOMAIN_TYPE_LOCAL}, {@link Domain#DOMAIN_TYPE_MSAD}, {@link Domain#DOMAIN_TYPE_APACHE}.
	 */
	public int getUsertype() {
		return usertype;
	}
	public void setUsertype(int usertype) {
		this.usertype = usertype;
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
	 * @return 用户�?��的域ID.
	 */
	public int getDomainid() {
		return domainid;
	}
	public void setDomainid(int domainid) {
		this.domainid = domainid;
	}
	/**
	 * @return 用户�?��的组织ID.
	 */
	public Integer getOrganizationid() {
		return organizationid;
	}
	public void setOrganizationid(Integer organizationid) {
		this.organizationid = organizationid;
	}
	/**
	 * @return 用户�? 用户登录系统使用的名�?
	 */
	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
	}
	/**
	 * @return 口令, 加密存储.
	 */
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	/**
	 * @return 真实姓名.
	 */
	public String getRealname() {
		return realname;
	}
	public void setRealname(String realname) {
		this.realname = realname;
	}
	/**
	 * @return 证件类型.
	 */
	public String getIdcardtype() {
		return idcardtype;
	}
	public void setIdcardtype(String idcardtype) {
		this.idcardtype = idcardtype;
	}
	/**
	 * @return 证件号码.
	 */
	public String getIdcard() {
		return idcard;
	}
	public void setIdcard(String idcard) {
		this.idcard = idcard;
	}
	/**
	 * @return 邮件地址.
	 */
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	/**
	 * @return 地址.
	 */
	public String getAddress() {
		return address;
	}
	public void setAddress(String address) {
		this.address = address;
	}
	/**
	 * @return 电话.
	 */
	public String getTelephone() {
		return telephone;
	}
	public void setTelephone(String telephone) {
		this.telephone = telephone;
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
	 * @return 用于列举用户时关联查询的组列�?
	 */
	@OneToMany(cascade = {}, fetch = FetchType.LAZY, targetEntity = GroupElement.class)
	@JoinColumn(name = "elementid")
	public Set<GroupElement> getGroupElements() {
		return groupElements;
	}
	public void setGroupElements(Set<GroupElement> groupElements) {
		this.groupElements = groupElements;
	}
	/**
	 * @return 是否为全�?��理员.
	 */
	@Transient
	public int getRootadmin() {
		return rootadmin;
	}
	public void setRootadmin(int rootadmin) {
		this.rootadmin = rootadmin;
	}
	/**
	 * @return 用户�?��的组.
	 */
	@Transient
	public List<GroupInfo> getGroups() {
		return groups;
	}
	public void setGroups(List<GroupInfo> groups) {
		this.groups = groups;
	}
	/**
	 * TODO HLD
	 * @return 组织单元名称.
	 */
	public String getOrganizationname() {
		return organizationname;
	}
	public void setOrganizationname(String organizationname) {
		this.organizationname = organizationname;
	}
	/**
	 * TODO HLD
	 * @return 域名�?
	 */
	public String getDomainname() {
		return domainname;
	}
	public void setDomainname(String domainname) {
		this.domainname = domainname;
	}
  public int getDeleted()
  {
    return deleted;
  }
  public void setDeleted(int deleted)
  {
    this.deleted = deleted;
  }
  @OneToMany(cascade = {}, fetch = FetchType.LAZY, targetEntity = RSAKey.class)
  @JoinColumn(name = "ownerid")
  public Set<RSAKey> getRsakeys()
  {
    return rsakeys;
  }
  public void setRsakeys(Set<RSAKey> rsakeys)
  {
    this.rsakeys = rsakeys;
  }
  @Transient
public int getAssignedrsa() {
	return assignedrsa;
}
public void setAssignedrsa(int assignedrsa) {
	this.assignedrsa = assignedrsa;
}
	
}
