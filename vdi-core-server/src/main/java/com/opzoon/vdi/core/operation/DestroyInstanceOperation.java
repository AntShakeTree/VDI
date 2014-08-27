package com.opzoon.vdi.core.operation;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import com.opzoon.ohvc.common.Job;
import com.opzoon.ohvc.common.JobStatus;
import com.opzoon.vdi.core.cloud.CloudManager;
import com.opzoon.vdi.core.cloud.CloudManagerHelper;
import com.opzoon.vdi.core.domain.CloudManagerEntity;
import com.opzoon.vdi.core.domain.DesktopPoolEntity;
import com.opzoon.vdi.core.facade.CommonException;
import com.opzoon.vdi.core.fsm.Entity;
import com.opzoon.vdi.core.fsm.StateMatcher;
import com.opzoon.vdi.core.operations.Event;
import com.opzoon.vdi.core.operations.Operation;
import com.opzoon.vdi.core.operations.OperationContext;
import com.opzoon.vdi.core.refresher.AgentJobRefresher;
import com.opzoon.vdi.core.refresher.CloudJobRefresher;

public class DestroyInstanceOperation implements Operation
{

  private final int desktoppoolid;
  private final int desktopid;
  private final String vmid;

  public DestroyInstanceOperation(int desktoppoolid, int desktopid, String vmid)
  {
    this.desktoppoolid = desktoppoolid;
    this.desktopid = desktopid;
    this.vmid = vmid;
  }

  @Override
  public Object execute(OperationContext operationContext) throws CommonException
  {
    final DesktopPoolEntity desktopPool = operationContext.getOperationRegistry().getStateMachine().getDatabaseFacade().load(DesktopPoolEntity.class, desktoppoolid);
    final CloudManager cloudManager = CloudManagerHelper.findCloudManager(
        operationContext.getOperationRegistry().getStateMachine().getDatabaseFacade().load(CloudManagerEntity.class, desktopPool.getCloudmanagerid()));
    try
    {
      if (desktopPool.getDomainname() != null && desktopPool.getDomainname().length() > 0) {
        final String domainadminname = desktopPool.getDomainbinddn().indexOf("\\") > -1 ?
            desktopPool.getDomainbinddn().substring(desktopPool.getDomainbinddn().indexOf("\\") + 1) :
              desktopPool.getDomainbinddn();
            //bugID 3236 解除绑定时候要joinWorkGrop最后一个参数传true
        final Job<String> cloudJob = cloudManager.joinWorkgroup(vmid, "WORKGROUP", domainadminname, desktopPool.getDomainbindpass(), true);
        operationContext.getOperationRegistry().refreshFrequently(this, operationContext, new AgentJobRefresher(cloudJob));
      }
    } catch (Exception e)
    {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
    try
    {
      Job<?> cloudJob = null;
      while (cloudJob == null || cloudJob.getStatus() == JobStatus.FAILED)
      {
        cloudJob = cloudManager.destroyVM(vmid);
        operationContext.getOperationRegistry().refreshFrequently(this, operationContext, new CloudJobRefresher(cloudManager, cloudJob));
      }
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
    return null;
  }

  @Override
  public List<Object> getParams()
  {
    return Arrays.asList(new Object[] { desktoppoolid, desktopid, vmid });
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
    final DestroyInstanceOperation that = (DestroyInstanceOperation) object;
    return this.desktopid == that.desktopid;
  }

  @Override
  public int hashCode()
  {
    return this.desktopid;
  }

}
