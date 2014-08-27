package com.opzoon.vdi.core.ws.vo.admin.user;

import java.io.Serializable;

public class RSAKeyIdAndError implements Serializable {

  private static final long serialVersionUID = 1L;

  private int rsakeyid;
  private String keyid;
  private int error;

  public RSAKeyIdAndError(int rsakeyid, String keyid, int error)
  {
    this.rsakeyid = rsakeyid;
    this.keyid = keyid;
    this.error = error;
  }

  public int getRsakeyid()
  {
    return rsakeyid;
  }

  public void setRsakeyid(int rsakeyid)
  {
    this.rsakeyid = rsakeyid;
  }

  public String getKeyid()
  {
    return keyid;
  }

  public void setKeyid(String keyid)
  {
    this.keyid = keyid;
  }

  public int getError()
  {
    return error;
  }

  public void setError(int error)
  {
    this.error = error;
  }
  
}