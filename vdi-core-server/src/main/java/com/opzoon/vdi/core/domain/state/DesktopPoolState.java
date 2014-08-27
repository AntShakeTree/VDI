package com.opzoon.vdi.core.domain.state;

import com.opzoon.vdi.core.facade.DatabaseFacade;
import com.opzoon.vdi.core.fsm.Request;
import com.opzoon.vdi.core.fsm.State;
import com.opzoon.vdi.core.request.AllocateDesktopRequest;
import com.opzoon.vdi.core.request.ConnectDesktopRequest;
import com.opzoon.vdi.core.request.DeleteDesktopPoolRequest;
import com.opzoon.vdi.core.request.FinalizeDesktopPoolRequest;
import com.opzoon.vdi.core.request.OpenDesktopPoolRequest;

public class DesktopPoolState implements State
{

  public static final int DESKTOP_POOL_PHASE_START = 1;
  public static final int DESKTOP_POOL_PHASE_NORMAL = 2;
  public static final int DESKTOP_POOL_PHASE_SHRINKING = 3;
  public static final int DESKTOP_POOL_PHASE_DELETING = 4;
  public static final int DESKTOP_POOL_PHASE_END = 0;
  public static final int DESKTOP_POOL_STATUS_OPEN = 128;// FIXME
  public static final int DESKTOP_POOL_STATUS_FULL = 129;// FIXME

  private final int phase;
  private final int status;

  public DesktopPoolState(int phase, int status)
  {
    this.phase = phase;
    this.status = status;
  }

  @Override
  public boolean accepts(DatabaseFacade databaseFacade, Request request)
  {
    if (request instanceof DeleteDesktopPoolRequest)
    {
      return phase == DESKTOP_POOL_PHASE_NORMAL;
    } else if (request instanceof FinalizeDesktopPoolRequest)
    {
      return phase == DESKTOP_POOL_PHASE_DELETING;
    } else if (request instanceof OpenDesktopPoolRequest)
    {
      return true;
    } else if (request instanceof AllocateDesktopRequest)
    {
      return phase == DESKTOP_POOL_PHASE_NORMAL;
    } else if (request instanceof ConnectDesktopRequest)
    {
      return phase == DESKTOP_POOL_PHASE_NORMAL;
    }
    return false;
  }

  @Override
  public State accept(DatabaseFacade databaseFacade, Request request)
  {
    if (request instanceof DeleteDesktopPoolRequest)
    {
      return new DesktopPoolState(DESKTOP_POOL_PHASE_DELETING, status);
    } else if (request instanceof FinalizeDesktopPoolRequest)
    {
      return new DesktopPoolState(DESKTOP_POOL_PHASE_END, status);
    } else if (request instanceof OpenDesktopPoolRequest)
    {
      return new DesktopPoolState(phase, DESKTOP_POOL_STATUS_OPEN);
    }
    return null;
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
