package com.opzoon.vdi.core.operation;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.opzoon.ohvc.common.Job;
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
import com.opzoon.vdi.core.refresher.CloudJobRefresher;

public class AsyncRebootDesktopOperation implements Operation
{

  private final int cloudmanagerid;
  private final int desktopid;
  private final String vmid;

  public AsyncRebootDesktopOperation(int cloudmanagerid, int desktopid, String vmid)
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
        VdiAgentClientImpl.restartSystem(desktop.getIpaddress());
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
        final Job<?> cloudJob = cloudManager.stopVM(vmid);
        operationContext.getOperationRegistry().refreshFrequently(this, operationContext, new CloudJobRefresher(cloudManager, cloudJob));
      } catch (Exception e)
      {
        // TODO Auto-generated catch block
        e.printStackTrace();
      }
      try
      {
        cloudManager.startVM(vmid);
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
    final AsyncRebootDesktopOperation that = (AsyncRebootDesktopOperation) object;
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
