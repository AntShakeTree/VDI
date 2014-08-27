package com.opzoon.vdi.core.operation;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.opzoon.vdi.core.domain.DesktopPoolStatus;
import com.opzoon.vdi.core.domain.DesktopStatus;
import com.opzoon.vdi.core.facade.CommonException;
import com.opzoon.vdi.core.fsm.Entity;
import com.opzoon.vdi.core.fsm.StateMatcher;
import com.opzoon.vdi.core.matcher.NormalDesktopMatcher;
import com.opzoon.vdi.core.matcher.NormalDesktopPoolMatcher;
import com.opzoon.vdi.core.operations.Event;
import com.opzoon.vdi.core.operations.Operation;
import com.opzoon.vdi.core.operations.OperationContext;
import com.opzoon.vdi.core.refresher.StandbyDesktopRefresher;

public class RebootDesktopOperation implements Operation
{

  private final int desktoppoolid;
  private final int cloudmanagerid;
  private final int desktopid;
  private final String vmid;

  public RebootDesktopOperation(int desktoppoolid, int cloudmanagerid, int desktopid, String vmid)
  {
    this.desktoppoolid = desktoppoolid;
    this.cloudmanagerid = cloudmanagerid;
    this.desktopid = desktopid;
    this.vmid = vmid;
  }

  @Override
  public Object execute(OperationContext operationContext) throws CommonException
  {
    try
    {
      operationContext.getOperationRegistry().start(new DisconnectDesktopOperation(desktoppoolid, desktopid, null, null));
    } catch (CommonException e)
    {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
    operationContext.getOperationRegistry().refreshFrequently(this, operationContext, new StandbyDesktopRefresher(desktopid));
    operationContext.getOperationRegistry().start(new AsyncRebootDesktopOperation(cloudmanagerid, desktopid, vmid));
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
    neededStates.put(new Entity(DesktopStatus.class, desktopid), new NormalDesktopMatcher());
    return neededStates;
  }

  @Override
  public List<Object> getParams()
  {
    return Arrays.asList(new Object[] { desktoppoolid, cloudmanagerid, desktopid, vmid });
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
    final RebootDesktopOperation that = (RebootDesktopOperation) object;
    return this.desktopid == that.desktopid;
  }

  @Override
  public int hashCode()
  {
    return this.desktopid;
  }

}
