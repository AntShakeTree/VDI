package com.opzoon.vdi.core.refresher;

import static com.opzoon.vdi.core.facade.FacadeHelper.exists;

import com.opzoon.vdi.core.fsm.Refresher;
import com.opzoon.vdi.core.operations.OperationContext;

public class EmptyCloudManagerRefresher implements Refresher
{
  
  private final int cloudmanagerid;

  public EmptyCloudManagerRefresher(int cloudmanagerid)
  {
    this.cloudmanagerid = cloudmanagerid;
  }

  @Override
  public boolean refresh(final OperationContext operationContext)
  {
    return !exists(operationContext.getOperationRegistry().getStateMachine().getDatabaseFacade().findFirst(
        "select count(iddesktoppool) from DesktopPoolEntity where cloudmanagerid = ?",
        cloudmanagerid));
  }

  @Override
  public int getIntevalInSeconds()
  {
    return 20;
  }

}
