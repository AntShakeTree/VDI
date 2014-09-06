///**   
// * Title: Resource.java 
// * @Package com.crawler.pojo 
// * : Resource.java 
// * @author david   
// * @date 2013-2-3 下午11:50:49 
// * @version 
// */
//package com.vdi.dao.user.domain;
//
//import java.util.HashSet;
//import java.util.Set;
//
//import javax.persistence.CascadeType;
//import javax.persistence.Entity;
//import javax.persistence.FetchType;
//import javax.persistence.GeneratedValue;
//import javax.persistence.GenerationType;
//import javax.persistence.Id;
//import javax.persistence.ManyToMany;
//
//import com.vdi.common.cache.CacheDomain;
//import com.vdi.dao.Request;
//
///**
// * ClassName: Resource
// * :
// * @author david
// * @date 2013-2-3 下午11:50:49
// */
//@Entity
//public class Resource implements CacheDomain,Request<Resource>{
//	@Id
//	@GeneratedValue(strategy=GenerationType.AUTO)
//	private Integer idresource;
//	private String url;
//	private String resourcename;
//	@ManyToMany(cascade={CascadeType.ALL},targetEntity=Role.class,fetch=FetchType.EAGER)
//	private Set<Role> roles = new HashSet<Role>();
//
//	public Integer getIdresource() {
//		return idresource;
//	}
//
//	public void setIdresource(Integer idresource) {
//		this.idresource = idresource;
//	}
//
//	/**
//	 * @return url
//	 */
//	public String getUrl() {
//		return url;
//	}
//
//	/**
//	 * @param url
//	 *            the url to set
//	 */
//	public void setUrl(String url) {
//		this.url = url;
//	}
//
//	/**
//	 * @return resourcename
//	 */
//	public String getResourcename() {
//		return resourcename;
//	}
//
//	/**
//	 * @param resourcename
//	 *            the resourcename to set
//	 */
//	public void setResourcename(String resourcename) {
//		this.resourcename = resourcename;
//	}
//
//	/**
//	 * @return roles
//	 */
//	public Set<Role> getRoles() {
//		return roles;
//	}
//
//	/**
//	 * @param roles
//	 *            the roles to set
//	 */
//	public void setRoles(Set<Role> roles) {
//		this.roles = roles;
//	}
//
//	/*
//	 * <p>Title: hashCode</p> <p>Description: </p>
//	 * @return
//	 * @see java.lang.Object#hashCode()
//	 */
//
//	@Override
//	public int hashCode() {
//		final int prime = 31;
//		int result = 1;
//		result = prime * result + ((url == null) ? 0 : url.hashCode());
//		return result;
//	}
//
//	/*
//	 * <p>Title: equals</p> <p>Description: </p>
//	 * @param obj
//	 * @return
//	 * @see java.lang.Object#equals(java.lang.Object)
//	 */
//
//	@Override
//	public boolean equals(Object obj) {
//		if (this == obj) {
//			return true;
//		}
//		if (obj == null) {
//			return false;
//		}
//		if (!(obj instanceof Resource)) {
//			return false;
//		}
//		Resource other = (Resource) obj;
//		if (url == null) {
//			if (other.url != null) {
//				return false;
//			}
//		} else if (!url.equals(other.url)) {
//			return false;
//		}
//		return true;
//	}
//
//	@Override
//	public Object getId() {
//		return this.getIdresource();
//	}
//
//}
