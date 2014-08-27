package com.opzoon.vdi.core.ws.vo.admin.domain;

import java.io.Serializable;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "ldapConfig")
public class LDAPConfig implements Serializable {

	private static final long serialVersionUID = 1L;

	private String domainservername;
	private int domainserverport;
	private String searchbase;
	private String binddn;
	private String bindpass;
	private int syncinterval;
	// Output
	private Integer idldapconfig;
	private int domainid;
	private int domaintype;
	private String domainname;
	private String domainnetworkname;
	private String domainnotes;
	private int status;
	
	public String getDomainservername() {
		return domainservername;
	}
	public void setDomainservername(String domainservername) {
		this.domainservername = domainservername;
	}
	public int getDomainserverport() {
		return domainserverport;
	}
	public void setDomainserverport(int domainserverport) {
		this.domainserverport = domainserverport;
	}
	public String getSearchbase() {
		return searchbase;
	}
	public void setSearchbase(String searchbase) {
		this.searchbase = searchbase;
	}
	public String getBinddn() {
		return binddn;
	}
	public void setBinddn(String binddn) {
		this.binddn = binddn;
	}
	public String getBindpass() {
		return bindpass;
	}
	public void setBindpass(String bindpass) {
		this.bindpass = bindpass;
	}
	public int getSyncinterval() {
		return syncinterval;
	}
	public void setSyncinterval(int syncinterval) {
		this.syncinterval = syncinterval;
	}
	// Output
	public Integer getIdldapconfig() {
		return idldapconfig;
	}
	public void setIdldapconfig(Integer idldapconfig) {
		this.idldapconfig = idldapconfig;
	}
	public int getDomainid() {
		return domainid;
	}
	public void setDomainid(int domainid) {
		this.domainid = domainid;
	}
	public int getDomaintype() {
		return domaintype;
	}
	public void setDomaintype(int domaintype) {
		this.domaintype = domaintype;
	}
	public String getDomainname() {
		return domainname;
	}
	public void setDomainname(String domainname) {
		this.domainname = domainname;
	}
	public String getDomainnetworkname() {
		return domainnetworkname;
	}
	public void setDomainnetworkname(String domainnetworkname) {
		this.domainnetworkname = domainnetworkname;
	}
	public String getDomainnotes() {
		return domainnotes;
	}
	public void setDomainnotes(String domainnotes) {
		this.domainnotes = domainnotes;
	}
	public int getStatus() {
		return status;
	}
	public void setStatus(int status) {
		this.status = status;
	}
	
}