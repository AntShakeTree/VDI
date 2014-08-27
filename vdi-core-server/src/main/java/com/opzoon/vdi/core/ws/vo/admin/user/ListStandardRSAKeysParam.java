package com.opzoon.vdi.core.ws.vo.admin.user;

import java.io.Serializable;

import javax.xml.bind.annotation.XmlRootElement;

import com.opzoon.vdi.core.facade.FacadeHelper.PagingInfo;

@XmlRootElement(name = "listParam")
public class ListStandardRSAKeysParam extends PagingInfo implements Serializable {

  private static final long serialVersionUID = 1L;
  
  private int unassignedonly;

  public int getUnassignedonly()
  {
    return unassignedonly;
  }

  public void setUnassignedonly(int unassignedonly)
  {
    this.unassignedonly = unassignedonly;
  }
  
}