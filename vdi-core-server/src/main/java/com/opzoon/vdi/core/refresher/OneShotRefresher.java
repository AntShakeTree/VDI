package com.opzoon.vdi.core.refresher;

import com.opzoon.vdi.core.fsm.Refresher;
import com.opzoon.vdi.core.operations.OperationContext;

public class OneShotRefresher implements Refresher
{

  private final int delayInSeconds;
  private int refreshed;

  public OneShotRefresher(int delayInSeconds)
  {
    this.delayInSeconds = delayInSeconds;
    this.refreshed = 0;
  }

  @Override
  public boolean refresh(final OperationContext operationContext)
  {
    return (refreshed++) == 1;
  }

  @Override
  public int getIntevalInSeconds()
  {
    return delayInSeconds;
  }

}
