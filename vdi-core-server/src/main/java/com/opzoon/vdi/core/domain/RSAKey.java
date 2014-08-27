package com.opzoon.vdi.core.domain;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Entity
public class RSAKey implements Serializable {

  /**
   * RSA认证类型：标准认证.
   */
  public static final int RSA_TYPE_STANDARD = 0x00;
  /**
   * RSA认证类型：增强型认证.
   */
  public static final int RSA_TYPE_ADVENCED = 0x11;

	private static final long serialVersionUID = 1L;
  private Integer idrsakey;
  private int rsatype;
  private String keyid;
  private String pdata;
  private int ownerid;
  private int disabled;
  private String ownername;
  
  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  public Integer getIdrsakey()
  {
    return idrsakey;
  }
  public void setIdrsakey(Integer idrsakey)
  {
    this.idrsakey = idrsakey;
  }
  public int getRsatype()
  {
    return rsatype;
  }
  public void setRsatype(int rsatype)
  {
    this.rsatype = rsatype;
  }
  public String getKeyid()
  {
    return keyid;
  }
  public void setKeyid(String keyid)
  {
    this.keyid = keyid;
  }
  public String getPdata()
  {
    return pdata;
  }
  public void setPdata(String pdata)
  {
    this.pdata = pdata;
  }
  public int getOwnerid()
  {
    return ownerid;
  }
  public void setOwnerid(int ownerid)
  {
    this.ownerid = ownerid;
  }
  public String getOwnername()
  {
    return ownername;
  }
  public void setOwnername(String ownername)
  {
    this.ownername = ownername;
  }
  public int getDisabled()
  {
    return disabled;
  }
  public void setDisabled(int disabled)
  {
    this.disabled = disabled;
  }
	
}
