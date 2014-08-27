package com.opzoon.vdi.core.refresher;

import com.opzoon.ohvc.service.VdiAgentClientImpl;
import com.opzoon.vdi.core.fsm.Refresher;
import com.opzoon.vdi.core.operations.OperationContext;

public class RDPWithTimeoutRefresher implements Refresher
{

  private static final int TIMEOUT_IN_SECONDS = 60 * 10;
  private static final int INTERVAL_IN_SECONDS = 20;
  
  private final String ip;
  private final boolean[] status;
  
  private long start;

  public RDPWithTimeoutRefresher(String ip, boolean[] status)
  {
    this.ip = ip;
    this.status = status;
    this.start = System.currentTimeMillis();
  }

  @Override
  public boolean refresh(final OperationContext operationContext)
  {
    try {
      status[0] = VdiAgentClientImpl.getRDPStatus(ip);
    } catch (Exception e) {
      status[0] = false;
    }
    if (status[0])
    {
      return true;
    } else {
      return (System.currentTimeMillis() - start) >= 1000 * TIMEOUT_IN_SECONDS;
    }
  }

  @Override
  public int getIntevalInSeconds()
  {
    return INTERVAL_IN_SECONDS;
  }

}
