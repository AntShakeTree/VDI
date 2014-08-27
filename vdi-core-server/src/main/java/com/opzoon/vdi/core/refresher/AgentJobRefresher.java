package com.opzoon.vdi.core.refresher;

import com.opzoon.ohvc.common.Job;
import com.opzoon.ohvc.common.JobStatus;
import com.opzoon.ohvc.service.VdiAgentClientImpl;
import com.opzoon.vdi.core.fsm.Refresher;
import com.opzoon.vdi.core.operations.OperationContext;

public class AgentJobRefresher implements Refresher
{

  private final Job<?> job;

  public AgentJobRefresher(Job<?> job)
  {
    this.job = job;
  }

  @Override
  public boolean refresh(final OperationContext operationContext)
  {
    try {
      VdiAgentClientImpl.queryAsyncJobResult(job);
    } catch (Exception e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
      return false;
    }
    return job.getStatus() != JobStatus.RUNNING;
  }

  @Override
  public int getIntevalInSeconds()
  {
    return 3;
  }

}
