package com.opzoon.vdi.core.operation;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.opzoon.vdi.core.domain.DesktopPoolStatus;
import com.opzoon.vdi.core.facade.CommonException;
import com.opzoon.vdi.core.fsm.Entity;
import com.opzoon.vdi.core.fsm.StateMatcher;
import com.opzoon.vdi.core.matcher.NormalDesktopPoolMatcher;
import com.opzoon.vdi.core.operations.Event;
import com.opzoon.vdi.core.operations.Operation;
import com.opzoon.vdi.core.operations.OperationContext;
import com.opzoon.vdi.core.request.DeleteDesktopPoolRequest;

public class DeleteDesktopPoolOperation implements Operation
{

  private final int desktoppoolid;
  private final boolean force;

  public DeleteDesktopPoolOperation(int desktoppoolid, boolean force)
  {
    this.desktoppoolid = desktoppoolid;
    this.force = force;
  }

  @Override
  public Object execute(OperationContext operationContext) throws CommonException
  {
    operationContext.getOperationRegistry().publishStateRequest(new DeleteDesktopPoolRequest(desktoppoolid));
    operationContext.getOperationRegistry().start(new AsyncDeleteDesktopPoolOperation(desktoppoolid, force));
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
    neededStates.put(new Entity(DesktopPoolStatus.class, desktoppoolid), new NormalDesktopPoolMatcher());
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
    final DeleteDesktopPoolOperation that = (DeleteDesktopPoolOperation) object;
    return this.desktoppoolid == that.getDesktoppoolid();
  }

  @Override
  public int hashCode()
  {
    return this.desktoppoolid;
  }

  public int getDesktoppoolid()
  {
    return desktoppoolid;
  }

}
