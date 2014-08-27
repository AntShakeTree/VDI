package com.opzoon.vdi.core.operation;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.opzoon.vdi.core.cloud.CloudManager;
import com.opzoon.vdi.core.cloud.CloudManagerHelper;
import com.opzoon.vdi.core.domain.CloudManagerEntity;
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

public class StartDesktopOperation implements Operation
{

  private final int desktoppoolid;
  private final int cloudmanagerid;
  private final int desktopid;
  private final String vmid;

  public StartDesktopOperation(int desktoppoolid, int cloudmanagerid, int desktopid, String vmid)
  {
    this.desktoppoolid = desktoppoolid;
    this.cloudmanagerid = cloudmanagerid;
    this.desktopid = desktopid;
    this.vmid = vmid;
  }

  @Override
  public Object execute(OperationContext operationContext) throws CommonException
  {
    final CloudManager cloudManager = CloudManagerHelper.findCloudManager(
        operationContext.getOperationRegistry().getStateMachine().getDatabaseFacade().load(CloudManagerEntity.class, cloudmanagerid));
    try
    {
      cloudManager.startVM(vmid);
    } catch (Exception e)
    {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
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
    final StartDesktopOperation that = (StartDesktopOperation) object;
    return this.desktopid == that.desktopid;
  }

  @Override
  public int hashCode()
  {
    return this.desktopid;
  }

}
