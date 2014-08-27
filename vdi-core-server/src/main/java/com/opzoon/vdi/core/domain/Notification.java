package com.opzoon.vdi.core.domain;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Entity
public class Notification implements Serializable {

	private static final long serialVersionUID = 1L;

	private Integer idnotification;
	private int userid;
	private int sessionid;
	private int major;
	private int minor;
	private int level;
	private String parameter;
	
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	public Integer getIdnotification() {
		return idnotification;
	}
	public void setIdnotification(Integer idnotification) {
		this.idnotification = idnotification;
	}
	public int getUserid() {
		return userid;
	}
	public void setUserid(int userid) {
		this.userid = userid;
	}
	public int getSessionid() {
		return sessionid;
	}
	public void setSessionid(int sessionid) {
		this.sessionid = sessionid;
	}
	public int getMajor() {
		return major;
	}
	public void setMajor(int major) {
		this.major = major;
	}
	public int getMinor() {
		return minor;
	}
	public void setMinor(int minor) {
		this.minor = minor;
	}
	public int getLevel() {
		return level;
	}
	public void setLevel(int level) {
		this.level = level;
	}
	public String getParameter() {
		return parameter;
	}
	public void setParameter(String parameter) {
		this.parameter = parameter;
	}
	
}
