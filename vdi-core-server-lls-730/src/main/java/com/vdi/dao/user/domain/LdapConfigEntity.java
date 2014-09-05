package com.vdi.dao.user.domain;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

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
	@ManyToOne
	@JoinColumn(name="domainguid")
	private Domain domain;
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
	public Domain getDomain() {
		return domain;
	}
	public void setDomain(Domain domain) {
		this.domain = domain;
	}
	@Override
	public Object getId() {
		return this.getIdldapcinfig();
	}
	
}
