package com.opzoon.vdi.core.domain.state;

import com.opzoon.vdi.core.facade.DatabaseFacade;
import com.opzoon.vdi.core.fsm.Request;
import com.opzoon.vdi.core.fsm.State;
import com.opzoon.vdi.core.request.DeleteCloudManagerRequest;
import com.opzoon.vdi.core.request.FinalizeCloudManagerRequest;
import com.opzoon.vdi.core.request.NormalizeCloudManagerRequest;

public class CloudManagerState implements State
{

  public static final int CLOUD_MANAGER_PHASE_START = 1;
  public static final int CLOUD_MANAGER_PHASE_NORMAL = 2;
  public static final int CLOUD_MANAGER_PHASE_DELETING = 3;
  public static final int CLOUD_MANAGER_PHASE_END = 0;
  public static final int CLOUD_MANAGER_STATUS_UNMANAGED = 1;
  public static final int CLOUD_MANAGER_STATUS_MAINTAINING = 2;
  public static final int CLOUD_MANAGER_STATUS_READY = 3;
  public static final int CLOUD_MANAGER_STATUS_UNKNOWN = 4;

  private final int phase;
  private final int status;

  public CloudManagerState(int phase, int status)
  {
    this.phase = phase;
    this.status = status;
  }

  @Override
  public boolean accepts(DatabaseFacade databaseFacade, Request request)
  {
    if (request instanceof DeleteCloudManagerRequest)
    {
      return phase == CLOUD_MANAGER_PHASE_NORMAL;
    } else if (request instanceof FinalizeCloudManagerRequest)
    {
      return phase == CLOUD_MANAGER_PHASE_DELETING;
    } else if (request instanceof NormalizeCloudManagerRequest)
    {
      return phase == CLOUD_MANAGER_PHASE_START;
    }
    return false;
  }

  @Override
  public State accept(DatabaseFacade databaseFacade, Request request)
  {
    if (request instanceof DeleteCloudManagerRequest)
    {
      return new CloudManagerState(CLOUD_MANAGER_PHASE_DELETING, status);
    } else if (request instanceof FinalizeCloudManagerRequest)
    {
      return new CloudManagerState(CLOUD_MANAGER_PHASE_END, status);
    } else if (request instanceof NormalizeCloudManagerRequest)
    {
//      return new CloudManagerState(CLOUD_MANAGER_PHASE_NORMAL, CLOUD_MANAGER_STATUS_UNKNOWN);
      return new CloudManagerState(CLOUD_MANAGER_PHASE_NORMAL, status);
    }
    throw new RuntimeException("Impossible");
  }

  public int getPhase()
  {
    return phase;
  }

  public int getStatus()
  {
    return status;
  }

}
