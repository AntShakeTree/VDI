package com.opzoon.vdi.core.refresher;

import com.opzoon.ohvc.common.Job;
import com.opzoon.ohvc.common.JobStatus;
import com.opzoon.vdi.core.cloud.CloudManager;
import com.opzoon.vdi.core.facade.CommonException;
import com.opzoon.vdi.core.fsm.Refresher;
import com.opzoon.vdi.core.operations.OperationContext;

public class CloudJobRefresher implements Refresher
{

  private final CloudManager cloudManager;
  private final Job<?> job;

  public CloudJobRefresher(CloudManager cloudManager, Job<?> job)
  {
    this.cloudManager = cloudManager;
    this.job = job;
  }

  @Override
  public boolean refresh(final OperationContext operationContext)
  {
    try {
      cloudManager.queryJobStatus(job);
    } catch (Exception e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
      job.setStatus(JobStatus.FAILED);
      job.setError(CommonException.HYPERVISOR_ABNORMAL);
      return true;
    }
    return job.getStatus() != JobStatus.RUNNING;
  }

  @Override
  public int getIntevalInSeconds()
  {
    return 20;
  }

}
