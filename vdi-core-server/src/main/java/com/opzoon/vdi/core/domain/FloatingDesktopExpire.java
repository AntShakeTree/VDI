package com.opzoon.vdi.core.domain;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Entity
public class FloatingDesktopExpire implements Serializable {

	private static final long serialVersionUID = 1L;
	
	private Integer idexpire;
	private int desktoppoolid;
	private int desktopid;
	private Date expire;
  
  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  public Integer getIdexpire()
  {
    return idexpire;
  }
  public void setIdexpire(Integer idexpire)
  {
    this.idexpire = idexpire;
  }
  public int getDesktoppoolid()
  {
    return desktoppoolid;
  }
  public void setDesktoppoolid(int desktoppoolid)
  {
    this.desktoppoolid = desktoppoolid;
  }
  public int getDesktopid()
  {
    return desktopid;
  }
  public void setDesktopid(int desktopid)
  {
    this.desktopid = desktopid;
  }
  public Date getExpire()
  {
    return expire;
  }
  public void setExpire(Date expire)
  {
    this.expire = expire;
  }
	
}
