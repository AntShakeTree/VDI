package com.opzoon.vdi.core.request;

import java.util.HashSet;
import java.util.Set;

import com.opzoon.vdi.core.domain.DesktopPoolStatus;
import com.opzoon.vdi.core.fsm.Entity;
import com.opzoon.vdi.core.fsm.Request;

public class DeleteDesktopPoolRequest implements Request
{
  
  private final int desktoppoolid;

  public DeleteDesktopPoolRequest(int desktoppoolid)
  {
    this.desktoppoolid = desktoppoolid;
  }

  @Override
  public Set<Entity> getTargetEntities()
  {
    final Set<Entity> targetEntities = new HashSet<Entity>();
    targetEntities.add(new Entity(DesktopPoolStatus.class, desktoppoolid));
    return targetEntities;
  }

}
