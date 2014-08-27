package com.opzoon.vdi.core.refresher;

import com.opzoon.vdi.core.fsm.Refresher;
import com.opzoon.vdi.core.operations.OperationContext;

public class NappingRefresher implements Refresher
{

  @Override
  public boolean refresh(final OperationContext operationContext)
  {
    return true;
  }

  @Override
  public int getIntevalInSeconds()
  {
    return 20;
  }

}
