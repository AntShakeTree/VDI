package com.opzoon.vdi.core.refresher;

import static com.opzoon.vdi.core.facade.FacadeHelper.exists;

import org.apache.log4j.Logger;

import com.opzoon.vdi.core.fsm.Refresher;
import com.opzoon.vdi.core.operations.OperationContext;

public class EmptyDesktopPoolRefresher implements Refresher
{

  private static Logger log = Logger.getLogger(EmptyDesktopPoolRefresher.class);
  
  private final int desktoppoolid;

  public EmptyDesktopPoolRefresher(int desktoppoolid)
  {
    this.desktoppoolid = desktoppoolid;
  }

  @Override
  public boolean refresh(final OperationContext operationContext)
  {
    long desktops = (Long) operationContext.getOperationRegistry().getStateMachine().getDatabaseFacade().findFirst(
        "select count(iddesktop) from Desktop where desktoppoolid = ?",
        desktoppoolid);
    log.trace(String.format("Desktop pool %d still has %d desktops.", desktoppoolid, desktops));
    return !exists(desktops);
  }

  @Override
  public int getIntevalInSeconds()
  {
    return 20;
  }

}
