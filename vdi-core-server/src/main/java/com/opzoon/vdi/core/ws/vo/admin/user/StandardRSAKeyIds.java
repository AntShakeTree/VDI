package com.opzoon.vdi.core.ws.vo.admin.user;

import java.io.Serializable;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "rsakeyid")
public class StandardRSAKeyIds implements Serializable {

  private static final long serialVersionUID = 1L;

  private int[] rsakeyid;
  private int[] userid;

  public int[] getRsakeyid()
  {
    return rsakeyid;
  }

  public void setRsakeyid(int[] rsakeyid)
  {
    this.rsakeyid = rsakeyid;
  }

  public int[] getUserid()
  {
    return userid;
  }

  public void setUserid(int[] userid)
  {
    this.userid = userid;
  }
  
}