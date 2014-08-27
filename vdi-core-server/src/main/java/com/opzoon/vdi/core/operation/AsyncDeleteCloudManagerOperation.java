package com.opzoon.vdi.core.operation;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.opzoon.ohvc.common.Job;
import com.opzoon.ohvc.common.JobStatus;
import com.opzoon.ohvc.service.VdiAgentClientImpl;
import com.opzoon.vdi.core.cloud.CloudManager;
import com.opzoon.vdi.core.cloud.CloudManagerHelper;
import com.opzoon.vdi.core.domain.CloudManagerEntity;
import com.opzoon.vdi.core.domain.CloudManagerStatus;
import com.opzoon.vdi.core.domain.DesktopPoolEntity;
import com.opzoon.vdi.core.domain.VMInstance;
import com.opzoon.vdi.core.facade.CommonException;
import com.opzoon.vdi.core.fsm.Entity;
import com.opzoon.vdi.core.fsm.StateMatcher;
import com.opzoon.vdi.core.matcher.DeletingCloudManagerMatcher;
import com.opzoon.vdi.core.operations.Event;
import com.opzoon.vdi.core.operations.Operation;
import com.opzoon.vdi.core.operations.OperationContext;
import com.opzoon.vdi.core.refresher.CloudJobRefresher;
import com.opzoon.vdi.core.refresher.EmptyCloudManagerRefresher;
import com.opzoon.vdi.core.refresher.RDPRefresher;
import com.opzoon.vdi.core.request.FinalizeCloudManagerRequest;
import com.opzoon.vdi.core.request.FinishCloningDesktopRequest;
import com.opzoon.vdi.core.request.RepealDesktopRequest;

public class AsyncDeleteCloudManagerOperation implements Operation
{

  private final int cloudmanagerid;

  public AsyncDeleteCloudManagerOperation(int cloudmanagerid)
  {
    this.cloudmanagerid = cloudmanagerid;
  }

  @Override
  public Object execute(OperationContext operationContext) throws CommonException
  {
    @SuppressWarnings("unchecked")
    List<DesktopPoolEntity> pools = (List<DesktopPoolEntity>) operationContext.getOperationRegistry().getStateMachine().getDatabaseFacade().find(
        "from DesktopPoolEntity where cloudmanagerid = ?",
        cloudmanagerid);
    for (DesktopPoolEntity pool : pools) {
      try
      {
        operationContext.getOperationRegistry().start(new DeleteDesktopPoolOperation(pool.getIddesktoppool(), true));
      } catch (CommonException e)
      {
        // TODO Auto-generated catch block
        e.printStackTrace();
        continue;
      }
    }
    operationContext.getOperationRegistry().refreshFrequently(this, operationContext, new EmptyCloudManagerRefresher(cloudmanagerid));
    operationContext.getOperationRegistry().publishStateRequest(new FinalizeCloudManagerRequest(cloudmanagerid));
    operationContext.getOperationRegistry().getStateMachine().getDatabaseFacade().update(
        "delete from CloudManagerStatus where idcloudmanager = ?",
        cloudmanagerid);
    operationContext.getOperationRegistry().getStateMachine().getDatabaseFacade().update(
        "delete from CloudManagerEntity where idcloudmanager = ?",
        cloudmanagerid);
    final CloudManager cloudManager = CloudManagerHelper.findCloudManager(
        operationContext.getOperationRegistry().getStateMachine().getDatabaseFacade().load(CloudManagerEntity.class, cloudmanagerid));
    try {
      cloudManager.exitLogin();
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
    if (operation instanceof AdjustDesktopCountOperation
        && ((AdjustDesktopCountOperation) operation).getCloudmanagerid() == cloudmanagerid)
    {
      return true;
    }
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
    neededStates.put(new Entity(CloudManagerStatus.class, cloudmanagerid), new DeletingCloudManagerMatcher());
    return neededStates;
  }

  @Override
  public List<Object> getParams()
  {
    return Arrays.asList(new Object[] { cloudmanagerid });
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
    final AsyncDeleteCloudManagerOperation that = (AsyncDeleteCloudManagerOperation) object;
    return this.cloudmanagerid == that.cloudmanagerid;
  }

  @Override
  public int hashCode()
  {
    return this.cloudmanagerid;
  }

}
