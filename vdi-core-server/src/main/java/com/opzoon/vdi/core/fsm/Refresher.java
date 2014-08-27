package com.opzoon.vdi.core.fsm;

import com.opzoon.vdi.core.operations.OperationContext;

public interface Refresher
{

  boolean refresh(OperationContext operationContext);
  
  int getIntevalInSeconds();

}
