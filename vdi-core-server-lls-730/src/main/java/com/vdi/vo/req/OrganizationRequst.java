package com.vdi.vo.req;


public class OrganizationRequst {
	private String organizationname;	
	private Integer parent;
	private Integer groupid;
	private String guid;
	private String binddn;
	private String fullname;
	private String domainguid;
	private Integer ldapconfigid;
	public String getOrganizationname() {
		return organizationname;
	}
	public void setOrganizationname(String organizationname) {
		this.organizationname = organizationname;
	}
	public Integer getParent() {
		return parent;
	}
	public void setParent(Integer parent) {
		this.parent = parent;
	}
	public Integer getGroupid() {
		return groupid;
	}
	public void setGroupid(Integer groupid) {
		this.groupid = groupid;
	}
	public String getGuid() {
		return guid;
	}
	public void setGuid(String guid) {
		this.guid = guid;
	}
	public String getBinddn() {
		return binddn;
	}
	public void setBinddn(String binddn) {
		this.binddn = binddn;
	}
 
	public String getFullname() {
		return fullname;
	}
	public void setFullname(String fullname) {
		this.fullname = fullname;
	}
	public String getDomainguid() {
		return domainguid;
	}
	public void setDomainguid(String domainguid) {
		this.domainguid = domainguid;
	}
	public Integer getLdapconfigid() {
		return ldapconfigid;
	}
	public void setLdapconfigid(Integer ldapconfigid) {
		this.ldapconfigid = ldapconfigid;
	}
	
}
