/**   
 * Title: TicketWrapper.java 
 * @Package com.crawler.pojo 
 * : TicketWrapper.java 
 * @author david   
 * @date 2013-2-2 上午12:33:22 
 * @version 
 */
package com.vdi.dao.user.domain;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Transient;

import org.codehaus.jackson.annotate.JsonIgnore;
import org.codehaus.jackson.map.annotate.JsonSerialize;
import org.codehaus.jackson.map.annotate.JsonSerialize.Inclusion;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import com.vdi.common.cache.CacheDomain;
import com.vdi.dao.PageRequest;
import com.vdi.dao.Request;
import com.vdi.dao.annotation.VDIDaoHelper;
import com.vdi.dao.annotation.VDIDaoHelper.IgnoreValue;

/**
 * ClassName: TicketWrapper :
 * 
 * @author david
 * @date 2013-2-2 上午12:33:22
 */
@Entity
@JsonSerialize(include=Inclusion.NON_DEFAULT)
public class User extends PageRequest<User> implements UserDetails,CacheDomain{
	/**
	 * @Fields serialVersionUID
	 */
	private static final long serialVersionUID = -3055321652817602345L;
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Integer iduser;
	private String username;
	private String password;
	private boolean enabled = true;
	private String realname;
	@ManyToMany(cascade=CascadeType.REFRESH,targetEntity=DeliveryGroup.class,mappedBy="users")
	private Set<DeliveryGroup> groups;
	private String mobile;
	private String email;
	private int status=0;
	private String domainguid;
	@ManyToMany(cascade = { CascadeType.MERGE,CascadeType.REFRESH }, targetEntity = Role.class, fetch = FetchType.EAGER)
	@JoinTable(name = "userroles", inverseJoinColumns = @JoinColumn(name = "userid"), joinColumns = @JoinColumn(name = "roleid"))
	private Set<Role> roles;
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "organizationid")////这里设置JoinColum设置了外键的名字，并且organization是关系维护端 
	private Organization organization;
	private String telephone;
	private String address;
	private String notes;



	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	public String getTelephone() {
		return telephone;
	}

	public void setTelephone(String telephone) {
		this.telephone = telephone;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public String getNotes() {
		return notes;
	}

	public void setNotes(String notes) {
		this.notes = notes;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}
	@VDIDaoHelper(ignore=IgnoreValue.DEFAULT)
	public boolean isEnabled() {
		return enabled;
	}

	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}

	public Integer getIduser() {
		return iduser;
	}

	public void setIduser(Integer iduser) {
		this.iduser = iduser;
	}

	public String getRealname() {
		return realname;
	}

	public void setRealname(String realname) {
		this.realname = realname;
	}



	public Set<DeliveryGroup> getGroups() {
		return groups;
	}

	public void setGroups(Set<DeliveryGroup> groups) {
		this.groups = groups;
	}

	public String getMobile() {
		return mobile;
	}

	public void setMobile(String mobile) {
		this.mobile = mobile;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	@Override
	public boolean isAccountNonExpired() {
		return false;
	}

	@Override
	public boolean isAccountNonLocked() {
		return false;
	}

	@Override
	public boolean isCredentialsNonExpired() {
		return false;
	}

	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		return this.getRoles();
	}

	public Set<Role> getRoles() {
		return roles;
	}

	public void setRoles(Set<Role> roles) {
		this.roles = roles;
	}

	public Organization getOrganization() {
		return organization;
	}

	public void setOrganization(Organization organization) {
		this.organization = organization;
	}

	@Override
	@Transient
	@JsonIgnore
	public Object getId() {
		return this.getIduser();
	}

	public String getDomainguid() {
		return domainguid;
	}

	public void setDomainguid(String domainguid) {
		this.domainguid = domainguid;
	}
	
}
