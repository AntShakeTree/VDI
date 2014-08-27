package com.opzoon.vdi.core.operation;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import com.opzoon.vdi.core.facade.CommonException;
import com.opzoon.vdi.core.fsm.Entity;
import com.opzoon.vdi.core.fsm.StateMatcher;
import com.opzoon.vdi.core.operations.Event;
import com.opzoon.vdi.core.operations.Operation;
import com.opzoon.vdi.core.operations.OperationContext;
import com.opzoon.vdi.core.request.NormalizeCloudManagerRequest;

public class CreateCloudManagerOperation implements Operation
{

  private final int cloudmanagerid;

  public CreateCloudManagerOperation(int cloudmanagerid)
  {
    this.cloudmanagerid = cloudmanagerid;
  }

  @Override
  public Object execute(OperationContext operationContext) throws CommonException
  {
    operationContext.getOperationRegistry().publishStateRequest(new NormalizeCloudManagerRequest(cloudmanagerid));
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
    return Arrays.asList(new Object[] { cloudmanagerid });
  }

  @Override
  public boolean equals(Object object)
  {
    return false;
  }

  @Override
  public int hashCode()
  {
    return super.hashCode();
  }

}
