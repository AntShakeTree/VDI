package com.opzoon.vdi.core.ws.vo.admin.domain;

import java.io.Serializable;

import javax.xml.bind.annotation.XmlRootElement;

import com.opzoon.vdi.core.facade.FacadeHelper.PagingInfo;

@XmlRootElement(name = "listParam")
public class ListLDAPConfigsParam extends PagingInfo implements Serializable {

	private static final long serialVersionUID = 1L;

	private int idldapconfig;
	private int domainid;
	private String domainsearchbase;
	private int syncinterval;
	
	public int getIdldapconfig() {
		return idldapconfig;
	}
	public void setIdldapconfig(int idldapconfig) {
		this.idldapconfig = idldapconfig;
	}
	public int getDomainid() {
		return domainid;
	}
	public void setDomainid(int domainid) {
		this.domainid = domainid;
	}
	public String getDomainsearchbase() {
		return domainsearchbase;
	}
	public void setDomainsearchbase(String domainsearchbase) {
		this.domainsearchbase = domainsearchbase;
	}
	public int getSyncinterval() {
		return syncinterval;
	}
	public void setSyncinterval(int syncinterval) {
		this.syncinterval = syncinterval;
	}
	
}