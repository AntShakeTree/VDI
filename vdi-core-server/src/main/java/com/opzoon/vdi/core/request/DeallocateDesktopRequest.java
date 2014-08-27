package com.opzoon.vdi.core.request;

import java.util.HashSet;
import java.util.Set;

import com.opzoon.vdi.core.domain.DesktopStatus;
import com.opzoon.vdi.core.fsm.Entity;
import com.opzoon.vdi.core.fsm.Request;

public class DeallocateDesktopRequest implements Request
{
  
  private final int desktopid;

  public DeallocateDesktopRequest(int desktopid)
  {
    this.desktopid = desktopid;
  }

  @Override
  public Set<Entity> getTargetEntities()
  {
    final Set<Entity> targetEntities = new HashSet<Entity>();
    targetEntities.add(new Entity(DesktopStatus.class, desktopid));
    return targetEntities;
  }

}
