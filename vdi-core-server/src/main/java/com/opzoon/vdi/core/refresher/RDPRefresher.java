package com.opzoon.vdi.core.refresher;

import com.opzoon.ohvc.service.VdiAgentClientImpl;
import com.opzoon.vdi.core.fsm.Refresher;
import com.opzoon.vdi.core.operations.OperationContext;

public class RDPRefresher implements Refresher
{

  private final String ip;

  public RDPRefresher(String ip)
  {
    this.ip = ip;
  }

  @Override
  public boolean refresh(final OperationContext operationContext)
  {
    try {
      return VdiAgentClientImpl.getRDPStatus(ip);
    } catch (Exception e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
      return false;
    }
  }

  @Override
  public int getIntevalInSeconds()
  {
    return 20;
  }

}
