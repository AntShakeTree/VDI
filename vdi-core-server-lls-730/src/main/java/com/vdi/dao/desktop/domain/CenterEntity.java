package com.vdi.dao.desktop.domain;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import com.vdi.common.cache.CacheDomain;
import com.vdi.dao.PageRequest;

@Entity
@Table(name="center")
public class CenterEntity extends PageRequest<CenterEntity>  implements CacheDomain{
	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	private Integer idcenter;
	private String address;
	private String centername;
	
	public Integer getIdcenter() {
		return idcenter;
	}
	public void setIdcenter(Integer idcenter) {
		this.idcenter = idcenter;
	}
	public String getAddress() {
		return address;
	}
	public void setAddress(String address) {
		this.address = address;
	}
	public String getCentername() {
		return centername;
	}
	public void setCentername(String centername) {
		this.centername = centername;
	}
	@Override
	public Object getId() {
		// 
		return this.idcenter;
	}
	
}
