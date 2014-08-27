package com.opzoon.vdi.core.refresher;

import org.apache.log4j.Logger;

import com.opzoon.vdi.core.domain.DesktopStatus;
import com.opzoon.vdi.core.domain.state.DesktopState;
import com.opzoon.vdi.core.fsm.Entity;
import com.opzoon.vdi.core.fsm.Refresher;
import com.opzoon.vdi.core.operations.OperationContext;

public class FreeDesktopRefresher implements Refresher
{

  private static Logger log = Logger.getLogger(FreeDesktopRefresher.class);
  
  private final int desktopid;

  public FreeDesktopRefresher(int desktopid)
  {
    this.desktopid = desktopid;
  }

  @Override
  public boolean refresh(final OperationContext operationContext)
  {
    final DesktopState desktopState = (DesktopState) operationContext.getOperationRegistry().getStateMachine().loadState(new Entity(DesktopStatus.class, desktopid));
    log.trace(String.format("Desktop %d: ownerid = %d.", desktopid, desktopState.getOwnerid()));
    return desktopState.getOwnerid() == -1;
  }

  @Override
  public int getIntevalInSeconds()
  {
    return 20;
  }

}
