package com.opzoon.vdi.core.request;

import java.util.HashSet;
import java.util.Set;

import com.opzoon.vdi.core.domain.CloudManagerStatus;
import com.opzoon.vdi.core.fsm.Entity;
import com.opzoon.vdi.core.fsm.Request;

public class FinalizeCloudManagerRequest implements Request
{
  
  private final int cloudmanagerid;

  public FinalizeCloudManagerRequest(int cloudmanagerid)
  {
    this.cloudmanagerid = cloudmanagerid;
  }

  @Override
  public Set<Entity> getTargetEntities()
  {
    final Set<Entity> targetEntities = new HashSet<Entity>();
    targetEntities.add(new Entity(CloudManagerStatus.class, cloudmanagerid));
    return targetEntities;
  }

}
