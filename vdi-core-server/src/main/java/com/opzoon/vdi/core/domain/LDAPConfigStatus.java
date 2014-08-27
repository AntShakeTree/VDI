package com.opzoon.vdi.core.domain;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.Id;

/**
 * TODO Confirm HLD.
 * LDAP配置状态.
 */
@Entity
public class LDAPConfigStatus implements Serializable {

	private static final long serialVersionUID = 1L;

	/**
	 * 平台状态: 正常.
	 */
	public static final int LDAP_CONFIG_STATUS_OK = 0;
	/**
	 * 平台状态: 异常.
	 */
	public static final int LDAP_CONFIG_STATUS_ABNORMAL = 1;
	/**
	 * 平台状态: 同步中.
	 */
	public static final int LDAP_CONFIG_STATUS_SYNCHRONIZING = 2;
	
	private Integer idldapconfig;
	private int status;

	@Id
	public Integer getIdldapconfig() {
		return idldapconfig;
	}
	public void setIdldapconfig(Integer idldapconfig) {
		this.idldapconfig = idldapconfig;
	}
	/**
	 * @return 状态. 参考{@link LDAPConfigStatus#LDAP_CONFIG_STATUS_OK}, {@link LDAPConfigStatus#LDAP_CONFIG_STATUS_ABNORMAL}, {@link LDAPConfigStatus#LDAP_CONFIG_STATUS_SYNCHRONIZING}.
	 */
	public int getStatus() {
		return status;
	}
	public void setStatus(int status) {
		this.status = status;
	}
	
}
