/**
 * Project Name:vdi-core-server-lls
 * File Name:Delivery.java
 * Package Name:com.vdi.dao.user.domain
 * Date:2014年8月14日下午3:30:19
 * Copyright (c) 2014 All Rights Reserved.
 *
*/

package com.vdi.dao.user.domain;

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
import javax.persistence.Table;
import javax.persistence.Transient;

import com.vdi.common.cache.CacheDomain;
import com.vdi.dao.Request;
/**
 * 
 * ClassName: Group <br/>
 * Function: Delivery group, is used to allocate resources (Desktoppool). Delivery group is many-to-many relationship with the user. <br/>
 * date: 2014年8月15日 上午8:59:37 <br/>
 *
 * @author maxiaochao
 * @version 
 * @since JDK 1.7
 */
@Entity
@Table(name="deliverygroup")
public class DeliveryGroup implements Request<DeliveryGroup>,CacheDomain{
	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	private Integer idgroup;
	private String name;
	@ManyToMany(cascade = CascadeType.REFRESH,fetch=FetchType.EAGER)  
	@JoinTable(name = "groups_users", inverseJoinColumns = @JoinColumn(name = "groupid"), joinColumns = @JoinColumn(name = "userid"))  	
	private Set<User> users;
	@ManyToMany(cascade = CascadeType.REFRESH,fetch=FetchType.EAGER)  
	@JoinTable(name = "origanazations_users", inverseJoinColumns = @JoinColumn(name = "groupid"), joinColumns = @JoinColumn(name = "oraganizationid"))  	
	private Set<Organization> origanazations;
	public Integer getIdgroup() {
		return idgroup;
	}
	public void setIdgroup(Integer idgroup) {
		this.idgroup = idgroup;
	}
	
	public Set<Organization> getOriganazations() {
		return origanazations;
	}
	public void setOriganazations(Set<Organization> origanazations) {
		this.origanazations = origanazations;
	}
	public String getName() {
		return name;
	}
	public DeliveryGroup setName(String name) {
		this.name = name;
		return this;
	}
	public Set<User> getUsers() {
		return users;
	}
	public void setUsers(Set<User> users) {
		this.users = users;
	
	}
	public DeliveryGroup addUser(User user){
		if(this.getUsers()==null){
			this.setUsers(new HashSet<User>());
		}
		this.getUsers().add(user);
		return this;
	}
	@Override
	@Transient
	public Object getId() {
		return this.getIdgroup();
	}
	
	
}
