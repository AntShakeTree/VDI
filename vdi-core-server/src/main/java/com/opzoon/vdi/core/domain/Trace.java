package com.opzoon.vdi.core.domain;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
@Entity
public class Trace implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 2588498920374371601L;
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long idtrace;
	private String action;
	private Date createtime;
//	private String username;
	private Integer operatorid;
	private String operatorname;
	private Integer targetid;
	private String targetname;
	public Long getIdtrace() {
		return idtrace;
	}
	public void setIdtrace(Long idtrace) {
		this.idtrace = idtrace;
	}
	public String getAction() {
		return action;
	}
	public void setAction(String action) {
		this.action = action;
	}
	public Date getCreatetime() {
		return createtime;
	}
	public void setCreatetime(Date createtime) {
		this.createtime = createtime;
	}
	public Integer getOperatorid() {
		return operatorid;
	}
	public void setOperatorid(Integer operatorid) {
		this.operatorid = operatorid;
	}
	public String getOperatorname() {
		return operatorname;
	}
	public void setOperatorname(String operatorname) {
		this.operatorname = operatorname;
	}
	public Integer getTargetid() {
		return targetid;
	}
	public void setTargetid(Integer targetid) {
		this.targetid = targetid;
	}
	public String getTargetname() {
		return targetname;
	}
	public void setTargetname(String targetname) {
		this.targetname = targetname;
	}
		
	
}
