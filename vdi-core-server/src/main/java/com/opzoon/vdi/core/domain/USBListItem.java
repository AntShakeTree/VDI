package com.opzoon.vdi.core.domain;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

import org.codehaus.jackson.annotate.JsonProperty;

@Entity
public class USBListItem implements Serializable {

	private static final long serialVersionUID = 1L;

	public static final int LIST_TYPE_CLASS_WHITE = 1;
	public static final int LIST_TYPE_CLASS_BLACK = 2;
	public static final int LIST_TYPE_DEVICE_WHITE = 3;
	public static final int LIST_TYPE_DEVICE_BLACK = 4;

	private Integer idusblistitem;
	private Integer restrictionstrategyid;
	private int listtype;
	private String itemname;
	@JsonProperty("class")
	private Integer clazz;
	private Integer subclass;
	private Integer protocol;
	private Integer venderid;
	private Integer productid;

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	public Integer getIdusblistitem() {
		return idusblistitem;
	}
	public void setIdusblistitem(Integer idusblistitem) {
		this.idusblistitem = idusblistitem;
	}
	public Integer getRestrictionstrategyid() {
		return restrictionstrategyid;
	}
	public void setRestrictionstrategyid(Integer restrictionstrategyid) {
		this.restrictionstrategyid = restrictionstrategyid;
	}
	public int getListtype() {
		return listtype;
	}
	public void setListtype(int listtype) {
		this.listtype = listtype;
	}
	public String getItemname() {
		return itemname;
	}
	public void setItemname(String itemname) {
		this.itemname = itemname;
	}
	@Column(name = "class")
	public Integer getClazz() {
		return clazz;
	}
	public void setClazz(Integer clazz) {
		this.clazz = clazz;
	}
	public Integer getSubclass() {
		return subclass;
	}
	public void setSubclass(Integer subclass) {
		this.subclass = subclass;
	}
	public Integer getProtocol() {
		return protocol;
	}
	public void setProtocol(Integer protocol) {
		this.protocol = protocol;
	}
	public Integer getVenderid() {
		return venderid;
	}
	public void setVenderid(Integer venderid) {
		this.venderid = venderid;
	}
	public Integer getProductid() {
		return productid;
	}
	public void setProductid(Integer productid) {
		this.productid = productid;
	}
	
}
