package com.opzoon.vdi.core.event;

import com.opzoon.vdi.core.operations.Event;

public class DeletingCloudManagerEvent implements Event
{
  
  private final int cloudmanagerid;

  public DeletingCloudManagerEvent(int cloudmanagerid)
  {
    this.cloudmanagerid = cloudmanagerid;
  }

  public int getCloudmanagerid()
  {
    return cloudmanagerid;
  }

}
