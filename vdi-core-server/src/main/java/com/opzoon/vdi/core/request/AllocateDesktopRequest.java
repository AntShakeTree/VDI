package com.opzoon.vdi.core.request;

import java.util.HashSet;
import java.util.Set;

import com.opzoon.vdi.core.domain.DesktopPoolStatus;
import com.opzoon.vdi.core.domain.DesktopStatus;
import com.opzoon.vdi.core.fsm.Entity;
import com.opzoon.vdi.core.fsm.Request;

public class AllocateDesktopRequest implements Request
{

  private final int desktoppoolid;
  private final int desktopid;
  private final int userid;

  public AllocateDesktopRequest(int desktoppoolid, int desktopid, int userid)
  {
    this.desktoppoolid = desktoppoolid;
    this.desktopid = desktopid;
    this.userid = userid;
  }

  @Override
  public Set<Entity> getTargetEntities()
  {
    final Set<Entity> targetEntities = new HashSet<Entity>();
    targetEntities.add(new Entity(DesktopPoolStatus.class, desktoppoolid));
    targetEntities.add(new Entity(DesktopStatus.class, desktopid));
    return targetEntities;
  }

  public int getDesktoppoolid()
  {
    return desktoppoolid;
  }

  public int getDesktopid()
  {
    return desktopid;
  }

  public int getUserid()
  {
    return userid;
  }

}
