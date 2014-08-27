package com.opzoon.vdi.core.event;

import com.opzoon.vdi.core.operations.Event;
import com.opzoon.vdi.core.operations.Operation;

public class OperationDelayedEvent implements Event
{
  
  private final Operation operation;

  public OperationDelayedEvent(Operation operation)
  {
    this.operation = operation;
  }

  public Operation getOperation()
  {
    return operation;
  }

}
