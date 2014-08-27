package com.opzoon.vdi.core.refresher;

import org.apache.log4j.Logger;

import com.opzoon.vdi.core.domain.DesktopStatus;
import com.opzoon.vdi.core.domain.state.DesktopState;
import com.opzoon.vdi.core.fsm.Entity;
import com.opzoon.vdi.core.fsm.Refresher;
import com.opzoon.vdi.core.operations.OperationContext;

public class StandbyDesktopRefresher implements Refresher
{

  private static Logger log = Logger.getLogger(StandbyDesktopRefresher.class);
  
  private final int desktopid;

  public StandbyDesktopRefresher(int desktopid)
  {
    this.desktopid = desktopid;
  }

  @Override
  public boolean refresh(final OperationContext operationContext)
  {
    final DesktopState desktopState = (DesktopState) operationContext.getOperationRegistry().getStateMachine().loadState(new Entity(DesktopStatus.class, desktopid));
    log.trace(String.format("Desktop %d: connectivity = %d.", desktopid, desktopState.getConnectivity()));
    return desktopState.getConnectivity() == DesktopState.DESKTOP_CONNECTIVITY_STANDBY;
  }

  @Override
  public int getIntevalInSeconds()
  {
    return 2;
  }

}
