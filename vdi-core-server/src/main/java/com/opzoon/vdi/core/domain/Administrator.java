package com.opzoon.vdi.core.domain;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

/**
 * 用户所能管理的对象.
 */
@Entity
public class Administrator implements Serializable {

	private static final long serialVersionUID = 1L;

	/**
	 * 管理对象类型: 全局管理员.
	 */
	public static final int ADMIN_TARGET_ALL = 0x00;
	/**
	 * 管理对象类型: 组织.
	 */
	public static final int ADMIN_TARGET_ORGANIZATION = 0x12;
	/**
	 * 管理对象类型: 组.
	 */
	public static final int ADMIN_TARGET_GROUP = 0x20;

	private Integer idadministrator;
	private int userid;
	private int targettype;
	private int targetid;

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	public Integer getIdadministrator() {
		return idadministrator;
	}
	public void setIdadministrator(Integer idadministrator) {
		this.idadministrator = idadministrator;
	}
	/**
	 * @return 用户id.
	 */
	public int getUserid() {
		return userid;
	}
	public void setUserid(int userid) {
		this.userid = userid;
	}
	/**
	 * @return 管理对象类型. 参考{@link Administrator#ADMIN_TARGET_ALL}, {@link Administrator#ADMIN_TARGET_USER}, {@link Administrator#ADMIN_TARGET_ORGANIZATION}, {@link Administrator#ADMIN_TARGET_GROUP}.
	 */
	public int getTargettype() {
		return targettype;
	}
	public void setTargettype(int targettype) {
		this.targettype = targettype;
	}
	/**
	 * @return 管理对象id.
	 */
	public int getTargetid() {
		return targetid;
	}
	public void setTargetid(int targetid) {
		this.targetid = targetid;
	}
	
}
