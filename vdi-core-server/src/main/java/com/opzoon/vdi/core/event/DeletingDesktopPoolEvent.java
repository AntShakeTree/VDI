package com.opzoon.vdi.core.event;

import com.opzoon.vdi.core.operations.Event;

public class DeletingDesktopPoolEvent implements Event
{
  
  private final int cloudmanagerid;

  public DeletingDesktopPoolEvent(int cloudmanagerid)
  {
    this.cloudmanagerid = cloudmanagerid;
  }

  public int getCloudmanagerid()
  {
    return cloudmanagerid;
  }

}
