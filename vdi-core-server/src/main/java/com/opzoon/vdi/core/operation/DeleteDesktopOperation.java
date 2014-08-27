package com.opzoon.vdi.core.operation;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.opzoon.vdi.core.domain.DesktopPoolStatus;
import com.opzoon.vdi.core.domain.DesktopStatus;
import com.opzoon.vdi.core.event.OperationDelayedEvent;
import com.opzoon.vdi.core.facade.CommonException;
import com.opzoon.vdi.core.fsm.Entity;
import com.opzoon.vdi.core.fsm.StateMatcher;
import com.opzoon.vdi.core.matcher.DeletableDesktopMatcher;
import com.opzoon.vdi.core.matcher.NormalOrDeletingDesktopPoolMatcher;
import com.opzoon.vdi.core.operations.Event;
import com.opzoon.vdi.core.operations.Operation;
import com.opzoon.vdi.core.operations.OperationContext;
import com.opzoon.vdi.core.request.DeleteDesktopRequest;

public class DeleteDesktopOperation implements Operation
{

  private final int desktoppoolid;
  private final int cloudmanagerid;
  private final int desktopid;
  private final String vmid;
  private final boolean force;

  public DeleteDesktopOperation(String desktoppoolidNcloudmanageridNdesktopid, String vmid, boolean force)
  {
    String[] ps = desktoppoolidNcloudmanageridNdesktopid.split("#");
    this.desktoppoolid = Integer.parseInt(ps[0], 16);
    this.cloudmanagerid = Integer.parseInt(ps[1].equals("ffffffff") ? "-1" : ps[1], 16);
    this.desktopid = Integer.parseInt(ps[2], 16);
    this.vmid = vmid;
    this.force = force;
  }

  @Override
  public Object execute(OperationContext operationContext) throws CommonException
  {
    operationContext.getOperationRegistry().publishStateRequest(new DeleteDesktopRequest(desktopid));
    operationContext.getOperationRegistry().start(new AsyncDeleteDesktopOperation(desktoppoolid, desktopid, vmid, force));
    return null;
  }

  @Override
  public boolean rejects(Operation operation)
  {
    if (operation instanceof AdjustDesktopCountOperation
        && ((AdjustDesktopCountOperation) operation).getDesktoppoolid() == desktoppoolid)
    {
      return true;
    }
    return false;
  }

  @Override
  public boolean delays(Operation operation)
  {
    if (operation instanceof DeleteCloudManagerOperation
        && ((DeleteCloudManagerOperation) operation).getCloudmanagerid() == cloudmanagerid)
    {
      return true;
    } else if (operation instanceof DeleteDesktopPoolOperation
        && ((DeleteDesktopPoolOperation) operation).getDesktoppoolid() == desktoppoolid)
    {
      return true;
    }
    return false;
  }

  @Override
  public boolean onEvent(OperationContext operationContext, Event event)
  {
    if (event instanceof OperationDelayedEvent)
    {
      final OperationDelayedEvent operationDelayedEvent = (OperationDelayedEvent) event;
      final Operation delayedOperation = operationDelayedEvent.getOperation();
      if (delayedOperation instanceof DeleteCloudManagerOperation)
      {
        // TODO
        return true;
      } else if (delayedOperation instanceof DeleteDesktopPoolOperation)
      {
        // TODO
        return true;
      }
    }
    return false;
  }

  @Override
  public Map<Entity, StateMatcher> getNeededStates()
  {
    final Map<Entity, StateMatcher> neededStates = new HashMap<Entity, StateMatcher>();
    neededStates.put(new Entity(DesktopPoolStatus.class, desktoppoolid), new NormalOrDeletingDesktopPoolMatcher());
    neededStates.put(new Entity(DesktopStatus.class, desktopid), new DeletableDesktopMatcher());
    return neededStates;
  }

  @Override
  public List<Object> getParams()
  {
    return Arrays.asList(new Object[] { Integer.toHexString(desktoppoolid) + "#" + Integer.toHexString(cloudmanagerid) + "#" + Integer.toHexString(desktopid), vmid, force });
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
    final DeleteDesktopOperation that = (DeleteDesktopOperation) object;
    return this.desktopid == that.desktopid;
  }

  @Override
  public int hashCode()
  {
    return this.desktopid;
  }

  public int getDesktopid()
  {
    return desktopid;
  }
  
  public static void main(String[] args)
  {
    System.out.println(Integer.toHexString(-1));
  }

}
