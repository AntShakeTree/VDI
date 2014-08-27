package com.opzoon.vdi.core.domain;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Transient;

@Entity
public class RestrictionStrategyAssignment implements Serializable {

	private static final long serialVersionUID = 1L;

	public static final int RESTRICTION_STRATEGY_ASSIGNMENT_TARGET_TYPE_USER = 0x11;
	public static final int RESTRICTION_STRATEGY_ASSIGNMENT_TARGET_TYPE_ORGANIZATION = 0x12;
	public static final int RESTRICTION_STRATEGY_ASSIGNMENT_TARGET_TYPE_GROUP = 0x20;
	public static final int RESTRICTION_STRATEGY_ASSIGNMENT_TARGET_TYPE_RESOURCE = 0x30;
	
	private Integer idrestrictionstrategyassignment;
	private int restrictionstrategyid;
	private int targettype;
	private int targetid;
	private int domainid;
	private String targetname;
	private String domainname;
	private String realname;
	private int rootadmin;
	private String notes;
	private int useramount;
	private int vmsource;
	private int assignment;
	private int maxdesktops;

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	public Integer getIdrestrictionstrategyassignment() {
		return idrestrictionstrategyassignment;
	}
	public void setIdrestrictionstrategyassignment(Integer idrestrictionstrategyassignment) {
		this.idrestrictionstrategyassignment = idrestrictionstrategyassignment;
	}
	public int getRestrictionstrategyid() {
		return restrictionstrategyid;
	}
	public void setRestrictionstrategyid(int restrictionstrategyid) {
		this.restrictionstrategyid = restrictionstrategyid;
	}
	public int getTargettype() {
		return targettype;
	}
	public void setTargettype(int targettype) {
		this.targettype = targettype;
	}
	public int getTargetid() {
		return targetid;
	}
	public void setTargetid(int targetid) {
		this.targetid = targetid;
	}
	public int getDomainid() {
		return domainid;
	}
	public void setDomainid(int domainid) {
		this.domainid = domainid;
	}
	@Transient
	public String getTargetname() {
		return targetname;
	}
	public void setTargetname(String targetname) {
		this.targetname = targetname;
	}
	@Transient
	public String getDomainname() {
		return domainname;
	}
	public void setDomainname(String domainname) {
		this.domainname = domainname;
	}
	@Transient
	public String getRealname() {
		return realname;
	}
	public void setRealname(String realname) {
		this.realname = realname;
	}
	@Transient
	public int getRootadmin() {
		return rootadmin;
	}
	public void setRootadmin(int rootadmin) {
		this.rootadmin = rootadmin;
	}
	@Transient
	public String getNotes() {
		return notes;
	}
	public void setNotes(String notes) {
		this.notes = notes;
	}
	@Transient
	public int getUseramount() {
		return useramount;
	}
	public void setUseramount(int useramount) {
		this.useramount = useramount;
	}
	@Transient
	public int getVmsource() {
		return vmsource;
	}
	public void setVmsource(int vmsource) {
		this.vmsource = vmsource;
	}
	@Transient
	public int getAssignment() {
		return assignment;
	}
	public void setAssignment(int assignment) {
		this.assignment = assignment;
	}
	@Transient
	public int getMaxdesktops() {
		return maxdesktops;
	}
	public void setMaxdesktops(int maxdesktops) {
		this.maxdesktops = maxdesktops;
	}
	
}
