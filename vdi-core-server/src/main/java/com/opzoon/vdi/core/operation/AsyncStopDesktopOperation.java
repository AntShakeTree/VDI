package com.opzoon.vdi.core.operation;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.opzoon.ohvc.service.VdiAgentClientImpl;
import com.opzoon.vdi.core.cloud.CloudManager;
import com.opzoon.vdi.core.cloud.CloudManagerHelper;
import com.opzoon.vdi.core.domain.CloudManagerEntity;
import com.opzoon.vdi.core.domain.Desktop;
import com.opzoon.vdi.core.domain.DesktopStatus;
import com.opzoon.vdi.core.facade.CommonException;
import com.opzoon.vdi.core.fsm.Entity;
import com.opzoon.vdi.core.fsm.StateMatcher;
import com.opzoon.vdi.core.matcher.NormalDesktopMatcher;
import com.opzoon.vdi.core.operations.Event;
import com.opzoon.vdi.core.operations.Operation;
import com.opzoon.vdi.core.operations.OperationContext;

public class AsyncStopDesktopOperation implements Operation
{

  private final int cloudmanagerid;
  private final int desktopid;
  private final String vmid;

  public AsyncStopDesktopOperation(int cloudmanagerid, int desktopid, String vmid)
  {
    this.cloudmanagerid = cloudmanagerid;
    this.desktopid = desktopid;
    this.vmid = vmid;
  }

  @Override
  public Object execute(OperationContext operationContext) throws CommonException
  {
    if (cloudmanagerid == -1)
    {
      final Desktop desktop = operationContext.getOperationRegistry().getStateMachine().getDatabaseFacade().load(Desktop.class, desktopid);
      try {
        VdiAgentClientImpl.shutdownSystem(desktop.getIpaddress());
      } catch (Exception e) {
        // TODO Auto-generated catch block
        e.printStackTrace();
      }
    } else
    {
      final CloudManager cloudManager = CloudManagerHelper.findCloudManager(
          operationContext.getOperationRegistry().getStateMachine().getDatabaseFacade().load(CloudManagerEntity.class, cloudmanagerid));
      try
      {
        cloudManager.stopVM(vmid);
      } catch (Exception e)
      {
        // TODO Auto-generated catch block
        e.printStackTrace();
      }
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
    neededStates.put(new Entity(DesktopStatus.class, desktopid), new NormalDesktopMatcher());
    return neededStates;
  }

  @Override
  public List<Object> getParams()
  {
    return Arrays.asList(new Object[] { cloudmanagerid, desktopid, vmid });
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
    final AsyncStopDesktopOperation that = (AsyncStopDesktopOperation) object;
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

}
