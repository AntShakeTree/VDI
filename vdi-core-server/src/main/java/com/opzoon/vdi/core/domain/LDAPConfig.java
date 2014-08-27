package com.opzoon.vdi.core.domain;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Transient;

/**
 * LDAP配置.
 */
@Entity
public class LDAPConfig implements Serializable {

	private static final long serialVersionUID = 1L;
	
	private Integer idldapconfig;
	private int domainid;
	private String domainsearchbase;
	private int syncinterval;
	private Domain domain;
	
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	public Integer getIdldapconfig() {
		return idldapconfig;
	}
	public void setIdldapconfig(Integer idldapconfig) {
		this.idldapconfig = idldapconfig;
	}
	/**
	 * @return LDAP配置所属的域ID.
	 */
	public int getDomainid() {
		return domainid;
	}
	public void setDomainid(int domainid) {
		this.domainid = domainid;
	}
	/**
	 * @return 域服务器查询起点.
	 */
	public String getDomainsearchbase() {
		return domainsearchbase;
	}
	public void setDomainsearchbase(String domainsearchbase) {
		this.domainsearchbase = domainsearchbase;
	}
	/**
	 * @return 同步时间间隔秒数.
	 */
	public int getSyncinterval() {
		return syncinterval;
	}
	public void setSyncinterval(int syncinterval) {
		this.syncinterval = syncinterval;
	}
	@Transient
	public Domain getDomain() {
		return domain;
	}
	public void setDomain(Domain domain) {
		this.domain = domain;
	}
	
}
