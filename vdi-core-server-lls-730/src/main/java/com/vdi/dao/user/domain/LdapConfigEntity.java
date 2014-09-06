package com.vdi.dao.user.domain;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;

import com.vdi.common.cache.CacheDomain;
import com.vdi.dao.PageRequest;

@Entity
@Table(name="ldapconfig")
public class LdapConfigEntity extends PageRequest<LdapConfigEntity> implements CacheDomain{
	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	private Integer idldapcinfig;
	private String baseurl;
	private int status;
	private String domainguid;
	public Integer getIdldapcinfig() {
		return idldapcinfig;
	}
	
	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	public void setIdldapcinfig(Integer idldapcinfig) {
		this.idldapcinfig = idldapcinfig;
	}
	public String getBaseurl() {
		return baseurl;
	}
	public void setBaseurl(String baseurl) {
		this.baseurl = baseurl;
	}

	public String getDomainguid() {
		return domainguid;
	}

	public void setDomainguid(String domainguid) {
		this.domainguid = domainguid;
	}

	@Override
	@Transient
	public Object getId() {
		return this.getIdldapcinfig();
	}
	
}
