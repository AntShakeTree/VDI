package com.opzoon.vdi.core.operation;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.opzoon.vdi.core.domain.CloudManagerStatus;
import com.opzoon.vdi.core.facade.CommonException;
import com.opzoon.vdi.core.fsm.Entity;
import com.opzoon.vdi.core.fsm.StateMatcher;
import com.opzoon.vdi.core.matcher.NormalCloudManagerMatcher;
import com.opzoon.vdi.core.operations.Event;
import com.opzoon.vdi.core.operations.Operation;
import com.opzoon.vdi.core.operations.OperationContext;
import com.opzoon.vdi.core.request.DeleteCloudManagerRequest;

public class DeleteCloudManagerOperation implements Operation
{

  private final int cloudmanagerid;

  public DeleteCloudManagerOperation(int cloudmanagerid)
  {
    this.cloudmanagerid = cloudmanagerid;
  }

  @Override
  public Object execute(OperationContext operationContext) throws CommonException
  {
    operationContext.getOperationRegistry().publishStateRequest(new DeleteCloudManagerRequest(cloudmanagerid));
    operationContext.getOperationRegistry().start(new AsyncDeleteCloudManagerOperation(cloudmanagerid));
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
    neededStates.put(new Entity(CloudManagerStatus.class, cloudmanagerid), new NormalCloudManagerMatcher());
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
    final DeleteCloudManagerOperation that = (DeleteCloudManagerOperation) object;
    return this.cloudmanagerid == that.cloudmanagerid;
  }

  @Override
  public int hashCode()
  {
    return this.cloudmanagerid;
  }

  public int getCloudmanagerid()
  {
    return cloudmanagerid;
  }

}
