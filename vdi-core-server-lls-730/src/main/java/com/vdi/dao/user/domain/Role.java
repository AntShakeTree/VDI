/**   
 * Title: Role.java 
 * @Package com.crawler.pojo 
 * : Role.java 
 * @author david   
 * @date 2013-2-3 上午1:07:59 
 * @version 
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
import javax.persistence.ManyToMany;
import javax.persistence.Transient;

import org.springframework.security.core.GrantedAuthority;

import com.vdi.common.cache.CacheDomain;
import com.vdi.dao.Request;

/**
 * ClassName: Role
 * :
 * @author maxiaochao
 * @date 2013-2-3 上午1:07:59
 */
@Entity
public class Role  implements GrantedAuthority,CacheDomain,Request<Role>{
	/**
	 * serialVersionUID:
	 * @since JDK 1.7
	 */
	private static final long serialVersionUID = 15678L;
	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	private Integer idrole;
	private String authority;
	private int parent=0;
	public static final String ROLE_ADMIN = "ROLE_ADMIN";
	public static final String ROLE_USER = "ROLE_USER";
	
	
	@ManyToMany(cascade={CascadeType.ALL},targetEntity=User.class,fetch=FetchType.EAGER)
	private Set<User> users;

	public Integer getIdrole() {
		return idrole;
	}

	public void setIdrole(Integer idrole) {
		this.idrole = idrole;
	}

	/**
	 * @return authority
	 */
	public String getAuthority() {
		return authority;
	}

	/**
	 * @param authority
	 *            the authority to set
	 */
	public Role setAuthority(String authority) {
		this.authority = authority;
		return this;
	}

	

	/* 
	* <p>Title: hashCode</p> 
	* <p>Description: </p> 
	* @return 
	* @see java.lang.Object#hashCode() 
	*/
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((authority == null) ? 0 : authority.hashCode());
		return result;
	}

	/* 
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
		if (!(obj instanceof Role)) {
			return false;
		}
		Role other = (Role) obj;
		if (authority == null) {
			if (other.authority != null) {
				return false;
			}
		} else if (!authority.equals(other.authority)) {
			return false;
		}
		return true;
	}

	/** 
	 * @return parent 
	 */
	public int getParent() {
		return parent;
	}

	/**
	 * @param parent the parent to set
	 */
	public void setParent(int parent) {
		this.parent = parent;
	}

	@Override
	@Transient
	public Object getId() {
		// TODO Auto-generated method stub
		return getIdrole();
	}

	public Set<User> getUsers() {
		return users;
	}

	public void setUsers(Set<User> users) {
		this.users = users;
	}

}
