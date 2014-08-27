package com.opzoon.vdi.core.operation;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.opzoon.vdi.core.domain.DesktopStatus;
import com.opzoon.vdi.core.facade.CommonException;
import com.opzoon.vdi.core.fsm.Entity;
import com.opzoon.vdi.core.fsm.StateMatcher;
import com.opzoon.vdi.core.matcher.DeletingDesktopMatcher;
import com.opzoon.vdi.core.operations.Event;
import com.opzoon.vdi.core.operations.Operation;
import com.opzoon.vdi.core.operations.OperationContext;
import com.opzoon.vdi.core.refresher.FreeDesktopRefresher;
import com.opzoon.vdi.core.request.FinalizeDesktopRequest;

public class AsyncDeleteDesktopOperation implements Operation
{

  private final int desktoppoolid;
  private final int desktopid;
  private final String vmid;
  private final boolean force;

  public AsyncDeleteDesktopOperation(int desktoppoolid, int desktopid, String vmid, boolean force)
  {
    this.desktoppoolid = desktoppoolid;
    this.desktopid = desktopid;
    this.vmid = vmid;
    this.force = force;
  }

  @Override
  public Object execute(OperationContext operationContext) throws CommonException
  {
    try
    {
      operationContext.getOperationRegistry().start(new DeallocDesktopOperation(Integer.toHexString(desktoppoolid) + "#" + Integer.toHexString(desktopid), force, null, null));
    } catch (CommonException e)
    {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
    operationContext.getOperationRegistry().refreshFrequently(this, operationContext, new FreeDesktopRefresher(desktopid));
    if (vmid != null)
    {
      try
      {
        operationContext.getOperationRegistry().start(new DestroyInstanceOperation(desktoppoolid, desktopid, vmid));
      } catch (CommonException e)
      {
        // TODO Auto-generated catch block
        e.printStackTrace();
      }
    }
    operationContext.getOperationRegistry().publishStateRequest(new FinalizeDesktopRequest(desktopid));
    operationContext.getOperationRegistry().getStateMachine().getDatabaseFacade().update(
        "delete from DesktopStatus where iddesktop = ?",
        desktopid);
    operationContext.getOperationRegistry().getStateMachine().getDatabaseFacade().update(
        "delete from Desktop where iddesktop = ?",
        desktopid);
    return null;
  }

  @Override
  public boolean rejects(Operation operation)
  {
    return this.equals(operation);
  }

  @Override
  public boolean delays(Operation operation)
  {
    return false;
  }

  @Override
  public boolean onEvent(OperationContext operationContext, Event event)
  {
    return false;
  }

  @Override
  public Map<Entity, StateMatcher> getNeededStates()
  {
    final Map<Entity, StateMatcher> neededStates = new HashMap<Entity, StateMatcher>();
    neededStates.put(new Entity(DesktopStatus.class, desktopid), new DeletingDesktopMatcher());
    return neededStates;
  }

  @Override
  public List<Object> getParams()
  {
    return Arrays.asList(new Object[] { desktoppoolid, desktopid, vmid, force });
  }

  @Override
  public boolean equals(Object object)
  {
    if (object == null)
    {
      return false;
    }
    if (!object.getClass().equals(this.getClass()))
    {
      return false;
    }
    final AsyncDeleteDesktopOperation that = (AsyncDeleteDesktopOperation) object;
    return this.desktopid == that.desktopid;
  }

  @Override
  public int hashCode()
  {
    return this.desktopid;
  }

}
