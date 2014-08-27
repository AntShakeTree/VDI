package com.opzoon.vdi.core.operation;

import static com.opzoon.vdi.core.domain.ResourceAssignment.RESOURCE_TYPE_POOL;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.opzoon.vdi.core.domain.Desktop;
import com.opzoon.vdi.core.domain.DesktopPoolEntity;
import com.opzoon.vdi.core.domain.DesktopPoolStatus;
import com.opzoon.vdi.core.domain.RestrictionStrategyAssignment;
import com.opzoon.vdi.core.facade.CommonException;
import com.opzoon.vdi.core.fsm.Entity;
import com.opzoon.vdi.core.fsm.StateMatcher;
import com.opzoon.vdi.core.matcher.DeletingDesktopPoolMatcher;
import com.opzoon.vdi.core.operations.Event;
import com.opzoon.vdi.core.operations.Operation;
import com.opzoon.vdi.core.operations.OperationContext;
import com.opzoon.vdi.core.refresher.EmptyDesktopPoolRefresher;
import com.opzoon.vdi.core.request.FinalizeDesktopPoolRequest;

public class AsyncDeleteDesktopPoolOperation implements Operation
{

  private final int desktoppoolid;
  private final boolean force;

  public AsyncDeleteDesktopPoolOperation(int desktoppoolid, boolean force)
  {
    this.desktoppoolid = desktoppoolid;
    this.force = force;
  }

  @Override
  public Object execute(OperationContext operationContext) throws CommonException
  {
    final DesktopPoolEntity desktopPool = operationContext.getOperationRegistry().getStateMachine().getDatabaseFacade().load(DesktopPoolEntity.class, desktoppoolid);
    @SuppressWarnings("unchecked")
    final List<Desktop> desktops = (List<Desktop>) operationContext.getOperationRegistry().getStateMachine().getDatabaseFacade().find(
        "from Desktop where desktoppoolid = ?",
        desktoppoolid);
    for (final Desktop desktop : desktops) {
      try
      {
        operationContext.getOperationRegistry().start(new DeleteDesktopOperation(Integer.toHexString(desktoppoolid) + "#" + Integer.toHexString(desktopPool.getCloudmanagerid()) + "#" + Integer.toHexString(desktop.getIddesktop()), desktop.getVmid(), force));
      } catch (CommonException e)
      {
        // TODO Auto-generated catch block
        e.printStackTrace();
        continue;
      }
    }
    operationContext.getOperationRegistry().refreshFrequently(this, operationContext, new EmptyDesktopPoolRefresher(desktoppoolid));
    operationContext.getOperationRegistry().getStateMachine().getDatabaseFacade().update(
        "delete from ResourceAssignment where resourcetype = ? and resourceid = ?",
        RESOURCE_TYPE_POOL, desktoppoolid);
    operationContext.getOperationRegistry().getStateMachine().getDatabaseFacade().update(
        "delete from RestrictionStrategyAssignment where targettype = ? and targetid = ?",
        RestrictionStrategyAssignment.RESTRICTION_STRATEGY_ASSIGNMENT_TARGET_TYPE_RESOURCE, desktoppoolid);
    operationContext.getOperationRegistry().publishStateRequest(new FinalizeDesktopPoolRequest(desktoppoolid));
    operationContext.getOperationRegistry().getStateMachine().getDatabaseFacade().update(
        "delete from DesktopPoolStatus where iddesktoppool = ?",
        desktoppoolid);
    operationContext.getOperationRegistry().getStateMachine().getDatabaseFacade().update(
        "delete from DesktopPoolEntity where iddesktoppool = ?",
        desktoppoolid);
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
    neededStates.put(new Entity(DesktopPoolStatus.class, desktoppoolid), new DeletingDesktopPoolMatcher());
    return neededStates;
  }

  @Override
  public List<Object> getParams()
  {
    return Arrays.asList(new Object[] { desktoppoolid, force });
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
    final AsyncDeleteDesktopPoolOperation that = (AsyncDeleteDesktopPoolOperation) object;
    return this.desktoppoolid == that.desktoppoolid;
  }

  @Override
  public int hashCode()
  {
    return this.desktoppoolid;
  }

}
