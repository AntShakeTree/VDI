package com.opzoon.vdi.core.domain;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

/**
 * 组包含的元素, 组既可以包含用户, 也可以包含组织, 甚至可以包含组.
 */
@Entity
public class GroupElement implements Serializable {

	private static final long serialVersionUID = 1L;

	/**
	 * 元素类型: 用户.
	 */
	public static final int ELEMENT_TYPE_USER = 0x11;
	/**
	 * 元素类型: 组织.
	 */
	public static final int ELEMENT_TYPE_ORGANIZATION = 0x12;
	/**
	 * 元素类型: 组 (预留).
	 */
	public static final int ELEMENT_TYPE_GROUP = 0x20;

	private Integer idgroupelement;
	private int groupid;
	private int elementid;
	private int elementtype;
	
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	public Integer getIdgroupelement() {
		return idgroupelement;
	}
	public void setIdgroupelement(Integer idgroupelement) {
		this.idgroupelement = idgroupelement;
	}
	/**
	 * @return 组ID.
	 */
	public int getGroupid() {
		return groupid;
	}
	public void setGroupid(int groupid) {
		this.groupid = groupid;
	}
	/**
	 * @return 元素ID
	 */
	public int getElementid() {
		return elementid;
	}
	public void setElementid(int elementid) {
		this.elementid = elementid;
	}
	/**
	 * @return 元素类型. 参考{@link GroupElement#ELEMENT_TYPE_USER}, {@link GroupElement#ELEMENT_TYPE_ORGANIZATION}, {@link GroupElement#ELEMENT_TYPE_GROUP}.
	 */
	public int getElementtype() {
		return elementtype;
	}
	public void setElementtype(int elementtype) {
		this.elementtype = elementtype;
	}
	
}
