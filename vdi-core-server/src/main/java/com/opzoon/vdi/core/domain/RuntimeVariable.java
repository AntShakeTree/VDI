package com.opzoon.vdi.core.domain;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

/**
 * TODO Confirm HLD.
 * 运行时变量.
 */
@Entity
public class RuntimeVariable implements Serializable {

	private static final long serialVersionUID = 1L;
	
	private Integer idruntimevariable;
	private String name;
	private String value;
	
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	public Integer getIdruntimevariable() {
		return idruntimevariable;
	}
	public void setIdruntimevariable(Integer idruntimevariable) {
		this.idruntimevariable = idruntimevariable;
	}
	/**
	 * @return 变量名称.
	 */
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	/**
	 * @return 变量值.
	 */
	public String getValue() {
		return value;
	}
	public void setValue(String value) {
		this.value = value;
	}
	
}
