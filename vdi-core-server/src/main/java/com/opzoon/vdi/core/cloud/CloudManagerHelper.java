package com.opzoon.vdi.core.cloud;

import static com.opzoon.vdi.core.facade.CommonException.NOT_FOUND;
import static com.opzoon.vdi.core.facade.CommonException.NO_ERRORS;
import static com.opzoon.vdi.core.facade.CommonException.DRIVER_PASS;
import static com.opzoon.vdi.core.facade.CommonException.DRIVER_UNTOUCHABLE;
import static com.opzoon.vdi.core.facade.CommonException.UNKNOWN;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.opzoon.appstatus.facade.AppStatusService;
import com.opzoon.appstatus.facade.impl.AppStatusServiceImpl;
import com.opzoon.ohvc.common.Job;
import com.opzoon.ohvc.common.JobStatus;
import com.opzoon.ohvc.domain.Driver;
import com.opzoon.ohvc.response.UsernameOrPasswordException;
import com.opzoon.ohvc.service.DriverManager;
import com.opzoon.ohvc.service.VdiAgentClientImpl;
import com.opzoon.vdi.core.domain.CloudManagerEntity;
import com.opzoon.vdi.core.domain.VMInstance;
import com.opzoon.vdi.core.facade.transience.AsyncJobFacade;
import com.opzoon.vdi.core.facade.transience.CloudManagerStatusFacade;

/**
 * CloudManager的帮助类.
 */
public abstract class CloudManagerHelper {
  
  private static final Logger log = LoggerFactory.getLogger(CloudManagerHelper.class);
  // TODO HashMap is OK ?
  private static final Map<Integer, CloudManager> cloudManagers = new ConcurrentHashMap<Integer, CloudManager>();
  
  private static final List<Driver> drivers = DriverManager.findDriverName(
      CloudManagerHelper.class.getResource("/").getPath());
  
  private CloudManagerHelper () {}
  
  public static List<String> findDrivers() {
    List<String> driverNames = new LinkedList<String>();
    for (Driver driver : drivers) {
      driverNames.add(driver.getDriverName());
    }
    return driverNames;
  }
  
  /**
   * 从数据库中的CloudManagerEntity初始化CloudManager.
   * 
   * @param cloudManagerEntity 数据库中的CloudManagerEntity, 提供ID和URL用于存储.
   */
  public static int initCloudManager(
      final CloudManagerEntity cloudManagerEntity,
      CloudManagerStatusFacade cloudManagerStatusFacade) {
    final CloudManager cloudManager;
    Driver driver = null;
    for (Driver driverCandidate : drivers) {
      if (driverCandidate.getDriverName().equals(cloudManagerEntity.getClouddrivername())) {
        driver = driverCandidate;
        break;
      }
    }
    if (driver == null) {
      log.error("Unsupported Driver '{}'", cloudManagerEntity.getClouddrivername());
      return UNKNOWN;
    }
    try {
      cloudManager = DriverManager.instanceByDriver(driver.getDriverClass());
      cloudManager.setBaseUrl(cloudManagerEntity.getBaseurl());
      
      log.trace("CloudManager {} ({}) init OK", cloudManagerEntity.getIdcloudmanager(), cloudManagerEntity.getBaseurl());
    } catch (Exception e) {
      log.error("CloudManager {} ({}) init failed", cloudManagerEntity.getIdcloudmanager(), cloudManagerEntity.getBaseurl());
      log.error("Init error:", e);
      // TODO Detailed error codes.
      return UNKNOWN;
    }
    cloudManagers.put(cloudManagerEntity.getIdcloudmanager(), cloudManager);
    log.trace("CloudManager {} ({}) prepare to sync work", cloudManagerEntity.getIdcloudmanager(), cloudManagerEntity.getBaseurl());
    return syncWorkWithCloudManager(
        cloudManagerStatusFacade,
        cloudManagerEntity,
        new CloudManagerWorker() {
          @Override
          public int execute(CloudManager cloudManager, Object[] resultContainer, int jobid)
              throws Exception {
            try {
              cloudManager.login(cloudManagerEntity.getUsername(), cloudManagerEntity.getPassword(), cloudManagerEntity.getDomain());
            
            } catch (UsernameOrPasswordException e) {
              return DRIVER_PASS;
            } catch (Exception e) {
              log.warn("Login error:", e);
              return DRIVER_UNTOUCHABLE;
            }
            return NO_ERRORS;
          }
        });
  }
  
  public static void dropCloudManager(final int idcloudmanager) {
    final CloudManager cloudManager = cloudManagers.remove(idcloudmanager);
    if (cloudManager != null) {
      Thread thread = new Thread() {
        @Override
        public void run() {
          try {
            cloudManager.exitLogin();
          } catch (Exception e) {
            log.error("CloudManager {} logout failed", idcloudmanager);
            log.error("Logout error:", e);
          }
        }
        @Override
        public String toString() {
          return super.toString() + "THREAD dropCloudManager " + idcloudmanager;
        }
      };
      com.opzoon.ohvc.session.ExcecutorUtil.execute(thread);
    }
  }
  
  /**
   * 根据idcloudmanager找到CloudManager.
   * 
   * @param idcloudmanager 数据库中的CloudManagerEntity的ID.
   * @return CloudManager实例(非空)
   */
  public static CloudManager findCloudManager(CloudManagerEntity cloudManagerEntity) {
    if (!cloudManagers.containsKey(cloudManagerEntity.getIdcloudmanager())) {
      log.warn("Unmatched CloudManager #{}", cloudManagerEntity.getIdcloudmanager());
      initCloudManager(cloudManagerEntity, null);
    }
    return cloudManagers.get(cloudManagerEntity.getIdcloudmanager());
  }

  /**
   * 同步调用CloudManager的业务方法.
   * 
   * @param cloudmanagerid 所需CloudManagerEntity的id.
   * @param cloudManagerWorker 包含业务方法的回调.
   * @return 错误代码.<br />
   *         {@link com.opzoon.vdi.core.facade.CommonException#NO_ERRORS}: 成功;<br />
   *         {@link com.opzoon.vdi.core.facade.CommonException#NOT_FOUND}: 找不到对应的CloudManagerEntity;<br />
   *         {@link com.opzoon.vdi.core.facade.CommonException#UNKNOWN}: 执行返回错误或出现异常. (异常则记录warn)
   */
  public static int syncWorkWithCloudManager(
      final CloudManagerStatusFacade cloudManagerStatusFacade,
      CloudManagerEntity cloudManagerEntity,
      final CloudManagerWorker cloudManagerWorker) {
    final CloudManager cloudManager = findCloudManager(cloudManagerEntity);
    if (cloudManager == null) {
      return NOT_FOUND;
    }
    try {
      int error = cloudManagerWorker.execute(cloudManager, null, -1);
      if (cloudManagerStatusFacade != null)
      {
        cloudManagerStatusFacade.markAsOK(cloudManagerEntity.getIdcloudmanager());
      }
      return error;
    } catch (Exception e) {
      log.warn(
          String.format(
              "Sync work with CloudManager #%d failed",
              cloudManagerEntity.getIdcloudmanager()),
          e);
      if (cloudManagerStatusFacade != null)
      {
        cloudManagerStatusFacade.markAsAbnormal(cloudManagerEntity.getIdcloudmanager());
      }
      return UNKNOWN;
    }
  }

  /**
   * 异步调用CloudManager的业务方法.
   * 
   * @param asyncJobFacade 异步任务相关的数据库facade, 用于记录任务状态.
   * @param cloudmanagerid 所需CloudManagerEntity的id.
   * @param asyncJobName 异步任务名称, 通常为接口名. null表示不在数据库中维护任务.
   * @param cloudManagerWorker 包含业务方法的回调.
   * @return 任务id. 不在数据库中维护任务则返回-1.
   */
  public static int asyncWorkWithCloudManager(
      final CloudManagerStatusFacade cloudManagerStatusFacade,
      final AsyncJobFacade asyncJobFacade,
      final CloudManagerEntity cloudManagerEntity,
      final String asyncJobName,
      final Object asyncJobResult,
      final CloudManagerWorker cloudManagerWorker) {
    log.info("Starting");
    final CloudManager cloudManager = findCloudManager(cloudManagerEntity);
    if (cloudManager == null) {
      if (cloudManagerStatusFacade != null) {
        cloudManagerStatusFacade.markAsAbnormal(cloudManagerEntity.getIdcloudmanager());
      }
      log.info("Null CloudManager");
      return -1;
    }
    log.info("Listing templates");
    if (asyncJobName != null) {
      try {
        cloudManager.listTemplates();
      } catch (Exception e) {
        log.warn("asyncWorkWithCloudManager failed", e);
        if (cloudManagerStatusFacade != null) {
          cloudManagerStatusFacade.markAsAbnormal(cloudManagerEntity.getIdcloudmanager());
        }
        log.info("Listing templates failed");
        return -1;
      }
    }
    log.info("Creating a thread");
    final String superThread = Thread.currentThread().getName();
    AsyncJobThread asyncJobThread = new AsyncJobThread() {
      @Override
      public void run() {
        log.info("Running the thread");
        try {
          log.trace("Async work with CloudManager #{} start from {}", cloudManagerEntity.getIdcloudmanager(), superThread);
          Object[] resultContainer = new Object[1];
          int error = cloudManagerWorker.execute(cloudManager, resultContainer, this.getJobId() == null ? -1 : this.getJobId());
          if (cloudManagerStatusFacade != null) {
            cloudManagerStatusFacade.markAsOK(cloudManagerEntity.getIdcloudmanager());
          }
          if (asyncJobName != null) {
            asyncJobFacade.finishAsyncJob(this.getJobId(), error, resultContainer[0]);
          }
          log.trace("Async work with CloudManager #{} done", cloudManagerEntity.getIdcloudmanager());
        } catch (Exception e) {
          log.warn(
              String.format(
                  "Async work with CloudManager #%d failed",
                  cloudManagerEntity.getIdcloudmanager()),
              e);
          if (cloudManagerStatusFacade != null) {
            cloudManagerStatusFacade.markAsAbnormal(cloudManagerEntity.getIdcloudmanager());
          }
          if (asyncJobName != null) {
            asyncJobFacade.finishAsyncJob(this.getJobId(), UNKNOWN, null);
          }
        }
      }
      @Override
      public String toString() {
        return super.toString() + "THREAD asyncWorkWithCloudManager " + cloudManagerEntity.getIdcloudmanager() + " " + asyncJobName + " " + asyncJobResult;
      }
    };
    int jobId = -1;
    if (asyncJobName != null) {
      jobId = asyncJobFacade.saveAsyncJob(asyncJobName, asyncJobResult, asyncJobThread.getId());
      asyncJobThread.setJobId(jobId);
    }
    log.info("Submit the thread");
    com.opzoon.ohvc.session.ExcecutorUtil.execute(asyncJobThread);
//    asyncJobThread.start();
    return jobId;
  }
  
  public static void waitForResult(CloudManager cloudManager, Job<?> cloudJob) throws Exception {
    int tried = 0;
    int interval = 500;
    for (;;) {
      try {
        Thread.sleep(interval);
      } catch (InterruptedException e) {}
      log.trace("Waiting for result {} from {}", cloudJob, cloudManager);
      try {
        cloudManager.queryJobStatus(cloudJob);
        tried = 0;
      } catch (Exception e) {
        log.warn("waitForResult Exception", e);
        if (tried >= 5) {
          log.warn("Max retrying times reached");
          throw e;
        } else {
          tried++;
        }
      }
      if (cloudJob.getStatus() != JobStatus.RUNNING) {
        return;
      }
      interval = Math.min(interval * 2, 5000);
    }
  }
  
  public static void waitForAgentResult(Job<?> cloudJob) throws Exception {
    int tried = 0;
    int interval = 500;
    for (;;) {
      try {
        Thread.sleep(interval);
      } catch (InterruptedException e) {}
      log.trace("Waiting for agent result {}", cloudJob);
      try {
        VdiAgentClientImpl.queryAsyncJobResult(cloudJob);
        tried = 0;
      } catch (Exception e) {
        log.warn("waitForResult Exception", e);
        if (tried >= 5) {
          log.warn("Max retrying times reached");
          throw e;
        } else {
          tried++;
        }
      }
      if (cloudJob.getStatus() != JobStatus.RUNNING) {
        return;
      }
      interval = Math.min(interval * 2, 5000);
    }
  }
  
  @SuppressWarnings({ "rawtypes", "unchecked" })
  public static void waitForResults(CloudManager cloudManager, List<Job> cloudJobs) {
    for (;;) {
      int completed = 0;
      for (Job cloudJob : cloudJobs) {
        if (cloudJob.getStatus() != JobStatus.RUNNING) {
          completed++;
          continue;
        }
        try {
          cloudManager.queryJobStatus(cloudJob);
        } catch (Exception e) {
          // TODO Auto-generated catch block
          e.printStackTrace();
        }
        if (cloudJob.getStatus() != JobStatus.RUNNING) {
          completed++;
        }
      }
      if (completed < cloudJobs.size()) {
        try {
          Thread.sleep(1000);
        } catch (InterruptedException e) {}
      } else {
        return;
      }
    }
  }
  
  public static int waitForRDP (CloudManager cloudManager, String vmid) {
    for (;;) {
      try {
        log.trace("Trying to get RDP status.", vmid);
        if (cloudManager.getRdpStatus(vmid)) {
          log.trace("Querying {} for RDP is OK.", vmid);
          return NO_ERRORS;
        }
        log.trace("Get {} RDP status done.", vmid);
      } catch (Exception e) {
        log.warn("Exception", e);
      }
      try {
        Thread.sleep(5000);
      } catch (InterruptedException e) {}
    }
  }
  
  public static int waitForRDP (String ipaddress) {
    for (;;) {
      try {
        log.trace("Trying to get RDP status.", ipaddress);
        if (VdiAgentClientImpl.getRDPStatus(ipaddress)) {
          log.trace("Querying {} for RDP is OK.", ipaddress);
          return NO_ERRORS;
        }
        log.trace("Get {} RDP status done.", ipaddress);
      } catch (Exception e) {
        log.warn("Exception", e);
      }
      try {
        Thread.sleep(5000);
      } catch (InterruptedException e) {}
    }
  }
  
  public static boolean isRunning(CloudManager cloudManager, String vmid) throws Exception {
    VMInstance vm = cloudManager.getVM(vmid);
    return isRunning(vm);
  }
  
  public static boolean isRunning(VMInstance vm) {
    // TODO Ugly "running".
    return vm == null ? false : "running".equalsIgnoreCase(vm.getState());
  }

  /**
   * 业务方法的回调类.
   */
  public static interface CloudManagerWorker {
    
    /**
     * 回调方法.
     * 
     * @param cloudManager 执行业务方法的CloudManager实例.
     * @param resultContainer 结果容器. 仅用于异步调用.
     * @return 错误代码.
     * @throws Exception 业务异常.
     */
    int execute(CloudManager cloudManager, Object[] resultContainer, int jobid) throws Exception;
    
  }
  
  public static abstract class AsyncJobThread extends Thread {
    
    private Integer jobId;

    public Integer getJobId() {
      return jobId;
    }
    public void setJobId(Integer jobId) {
      this.jobId = jobId;
    }
    
  }


  

}
