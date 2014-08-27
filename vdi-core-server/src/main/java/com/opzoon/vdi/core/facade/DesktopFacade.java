package com.opzoon.vdi.core.facade;

import static com.opzoon.vdi.core.cloud.CloudManagerHelper.asyncWorkWithCloudManager;
import static com.opzoon.vdi.core.cloud.CloudManagerHelper.isRunning;
import static com.opzoon.vdi.core.cloud.CloudManagerHelper.syncWorkWithCloudManager;
import static com.opzoon.vdi.core.cloud.CloudManagerHelper.waitForAgentResult;
import static com.opzoon.vdi.core.cloud.CloudManagerHelper.waitForRDP;
import static com.opzoon.vdi.core.cloud.CloudManagerHelper.waitForResult;
import static com.opzoon.vdi.core.domain.DesktopPoolEntity.DESKTOP_POOL_ASSIGNMENT_DEDICATED;
import static com.opzoon.vdi.core.domain.DesktopPoolEntity.DESKTOP_POOL_SOURCE_AUTO;
import static com.opzoon.vdi.core.domain.DesktopStatus.DESKTOP_STATUS_PROVISIONING;
import static com.opzoon.vdi.core.domain.DesktopStatus.DESKTOP_STATUS_SERVING;
import static com.opzoon.vdi.core.domain.DesktopStatus.DESKTOP_STATUS_STOPPED;
import static com.opzoon.vdi.core.domain.GroupElement.ELEMENT_TYPE_USER;
import static com.opzoon.vdi.core.facade.CommonException.CONFLICT;
import static com.opzoon.vdi.core.facade.CommonException.FORBIDDEN;
import static com.opzoon.vdi.core.facade.CommonException.HYPERVISOR_ABNORMAL;
import static com.opzoon.vdi.core.facade.CommonException.HYPERVISOR_NO_ENOUGH_RESOURCES;
import static com.opzoon.vdi.core.facade.CommonException.NOT_FOUND;
import static com.opzoon.vdi.core.facade.CommonException.NO_ERRORS;
import static com.opzoon.vdi.core.facade.FacadeHelper.count;
import static com.opzoon.vdi.core.facade.FacadeHelper.exists;
import static com.opzoon.vdi.core.facade.FacadeHelper.pagingFind;
import static com.opzoon.vdi.core.util.ConditionUtils.numberEquals;
import static com.opzoon.vdi.core.util.ConditionUtils.numberNotEquals;
import static com.opzoon.vdi.core.util.StringUtils.strcat;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import javax.persistence.EntityExistsException;
import javax.persistence.PersistenceException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.opzoon.ohvc.common.Job;
import com.opzoon.ohvc.common.JobStatus;
import com.opzoon.ohvc.response.ResourceNotMeetException;
import com.opzoon.ohvc.service.VdiAgentClientImpl;
import com.opzoon.vdi.core.cloud.CloudManager;
import com.opzoon.vdi.core.cloud.CloudManagerHelper.AsyncJobThread;
import com.opzoon.vdi.core.cloud.CloudManagerHelper.CloudManagerWorker;
import com.opzoon.vdi.core.domain.CloudManagerEntity;
import com.opzoon.vdi.core.domain.Desktop;
import com.opzoon.vdi.core.domain.DesktopPoolEntity;
import com.opzoon.vdi.core.domain.DesktopPoolStatus;
import com.opzoon.vdi.core.domain.DesktopStatus;
import com.opzoon.vdi.core.domain.Domain;
import com.opzoon.vdi.core.domain.Group;
import com.opzoon.vdi.core.domain.Notification;
import com.opzoon.vdi.core.domain.Organization;
import com.opzoon.vdi.core.domain.RestrictionStrategy;
import com.opzoon.vdi.core.domain.RestrictionStrategyAssignment;
import com.opzoon.vdi.core.domain.Session;
import com.opzoon.vdi.core.domain.USBListItem;
import com.opzoon.vdi.core.domain.User;
import com.opzoon.vdi.core.domain.UserVolume;
import com.opzoon.vdi.core.domain.VMInstance;
import com.opzoon.vdi.core.domain.Volume;
import com.opzoon.vdi.core.domain.state.DesktopState;
import com.opzoon.vdi.core.facade.FacadeHelper.PagingInfo;
import com.opzoon.vdi.core.facade.transience.AsyncJobFacade;
import com.opzoon.vdi.core.facade.transience.CloudManagerStatusFacade;
import com.opzoon.vdi.core.facade.transience.DesktopPoolStatusFacade;
import com.opzoon.vdi.core.facade.transience.DesktopStatusFacade;
import com.opzoon.vdi.core.facade.transience.SessionFacade;
import com.opzoon.vdi.core.operation.CloneInstanceOperation;
import com.opzoon.vdi.core.operation.DeallocDesktopOperation;
import com.opzoon.vdi.core.operation.RebootDesktopOperation;
import com.opzoon.vdi.core.operation.ResetDesktopOperation;
import com.opzoon.vdi.core.operation.StartDesktopOperation;
import com.opzoon.vdi.core.operation.StopDesktopOperation;
import com.opzoon.vdi.core.operations.OperationRegistry;
import com.opzoon.vdi.core.ws.Services;

/**
 * 桌面相关业务接口.
 */
public class DesktopFacade {
  
  private static final Logger log = LoggerFactory.getLogger(DesktopFacade.class);
  
  private final Map<Integer, Long> statusRefreshedTimes = new ConcurrentHashMap<Integer, Long>();

  private DatabaseFacade databaseFacade;
  private UserFacade userFacade;
  private OrganizationFacade organizationFacade;
  private ResourceFacade resourceFacade;
  private AsyncJobFacade asyncJobFacade;
  private DesktopPoolFacade desktopPoolFacade;
  private DesktopStatusFacade desktopStatusFacade;
  private DesktopPoolStatusFacade desktopPoolStatusFacade;
  private SessionFacade sessionFacade;
  private CloudManagerStatusFacade cloudManagerStatusFacade;
  private OperationRegistry operationRegistry;

  public DatabaseFacade getDatabaseFacade() {
    return databaseFacade;
  }

  public void refreshDesktopStatus(final int cloudmanagerid, final Desktop desktop) {
    if (desktop.getVmid() != null) {
      asyncWorkWithCloudManager(
          cloudManagerStatusFacade,
          asyncJobFacade,
          databaseFacade.load(CloudManagerEntity.class, cloudmanagerid),
          null,
          null,
          new CloudManagerWorker() {
            @Override
            public int execute(CloudManager cloudManager, Object[] resultContainer, int jobid) throws Exception {
              return doRefreshDesktopStatus(cloudManager, cloudmanagerid, desktop);
            }
          });
    } else {
      if (cloudmanagerid < 1) {
        Thread thread = new Thread() {
          @Override
          public void run() {
            doRefreshDesktopStatus(null, 0, desktop);
          }
        };
        com.opzoon.ohvc.session.ExcecutorUtil.execute(thread);
      }
    }
  }

  public void syncRefreshDesktopStatus(CloudManager cloudManager, int cloudmanagerid, Desktop desktop) {
    if (desktop.getVmid() != null) {
      doRefreshDesktopStatus(cloudManager, cloudmanagerid, desktop);
    }
  }

  @SuppressWarnings("unchecked")
  public List<Desktop> findDesktops(int desktoppoolid) {
    return  (List<Desktop>) databaseFacade.find(
        "from Desktop where desktoppoolid = ?",
        desktoppoolid);
  }
  
  /**
   * 销毁桌面.
   * 
   * @param desktopid 桌面ID.
   * @param errorContainer 错误代码容器.
   * @return 任务ID. 有错误则返回-1.
   */
  public int destroyDesktop(final int desktopid, int[] errorContainer) {
    final CloudManagerIdAndVM cloudManagerIdAndVM = this.findCloudManagerIdAndVM(desktopid);
    if(numberNotEquals(cloudManagerIdAndVM.getError(), NO_ERRORS)) {
      errorContainer[0] = cloudManagerIdAndVM.getError();
      return -1;
    }
    if (cloudManagerIdAndVM.getIdcloudmanager() < 1) {
      errorContainer[0] = CONFLICT;
      return -1;
    }
    Desktop desktop = databaseFacade.load(Desktop.class, desktopid);
    DesktopPoolEntity desktoppool = databaseFacade.load(DesktopPoolEntity.class, desktop.getDesktoppoolid());
    try
    {
      operationRegistry.start(new ResetDesktopOperation(
          desktop.getDesktoppoolid(), desktoppool.getCloudmanagerid(), desktopid, desktop.getVmid()));
    } catch (CommonException e)
    {
      // TODO Auto-generated catch block
      e.printStackTrace();
      return -1;
    }
    return 1;// FIXME
//    int owner = desktop.getOwnerid();
//    int jobId = DesktopFacade.this.destroy(
//        cloudManagerIdAndVM.getIdcloudmanager(),
//        desktop,
//        desktoppool,
//        cloudManagerIdAndVM.getTemplateid() != null,
//        cloudManagerIdAndVM.getTemplateid(),
//        cloudManagerIdAndVM.getVmname(),
//        cloudManagerIdAndVM.getPcname(),
//        owner,
//        false,
//        true);
//    if (jobId == -1) {
//      errorContainer[0] = HYPERVISOR_ABNORMAL;
//    }
//    return jobId;
  }

  /**
   * 重启桌面.
   * 
   * @param desktopid 桌面ID.
   * @param errorContainer 错误代码容器.
   * @return 任务ID. 有错误则返回-1.
   */
  public int rebootDesktop(final int desktopid, int[] errorContainer) {
    final CloudManagerIdAndVM cloudManagerIdAndVM = this.findCloudManagerIdAndVM(desktopid);
    if(numberNotEquals(cloudManagerIdAndVM.getError(), NO_ERRORS)) {
      errorContainer[0] = cloudManagerIdAndVM.getError();
      return -1;
    }
    Desktop desktop = databaseFacade.load(Desktop.class, desktopid);
    DesktopPoolEntity desktopPool = databaseFacade.load(DesktopPoolEntity.class, desktop.getDesktoppoolid());
    try
    {
      operationRegistry.start(new RebootDesktopOperation(desktop.getDesktoppoolid(), desktopPool.getCloudmanagerid(), desktopid, desktop.getVmid()));
    } catch (CommonException e)
    {
      // TODO Auto-generated catch block
      e.printStackTrace();
      return -1;
    }
    return 1;// FIXME
//    int jobId = DesktopFacade.this.disconnectAndShutdown(
//        cloudManagerIdAndVM.getIdcloudmanager(),
//        desktop.getDesktoppoolid(),
//        desktop.getIddesktop(),
//        desktop.getVmid(),
//        pool,
//        false,
//        true,
//        false,
//        true,
//        null,
//        null,
//        null,
//        -1,
//        false,
//        true);
//    if (jobId == -1) {
//      errorContainer[0] = HYPERVISOR_ABNORMAL;
//    }
//    return jobId;
  }
  
  public int rebootDesktopOS(final int desktopid, int[] errorContainer) {
    final CloudManagerIdAndVM cloudManagerIdAndVM = this.findCloudManagerIdAndVM(desktopid);
    if(numberNotEquals(cloudManagerIdAndVM.getError(), NO_ERRORS)) {
      errorContainer[0] = cloudManagerIdAndVM.getError();
      return -1;
    }
    Desktop desktop = databaseFacade.load(Desktop.class, desktopid);
    DesktopPoolEntity pool = databaseFacade.load(DesktopPoolEntity.class, desktop.getDesktoppoolid());
    int jobId = DesktopFacade.this.disconnectAndShutdown(
        cloudManagerIdAndVM.getIdcloudmanager(),
        desktop.getDesktoppoolid(),
        desktop.getIddesktop(),
        desktop.getVmid(),
        pool,
        true,
        true,
        false,
        true,
        null,
        null,
        null,
        -1,
        false,
        true);
    if (jobId == -1) {
      errorContainer[0] = HYPERVISOR_ABNORMAL;
    }
    return jobId;
  }

  /**
   * 启动桌面.
   * 
   * @param desktopid 桌面ID.
   * @param errorContainer 错误代码容器.
   * @return 任务ID. 有错误则返回-1.
   */
  public int startDesktop(final int desktopid, int[] errorContainer) {
    final CloudManagerIdAndVM cloudManagerIdAndVM = this.findCloudManagerIdAndVM(desktopid);
    if(numberNotEquals(cloudManagerIdAndVM.getError(), NO_ERRORS)) {
      errorContainer[0] = cloudManagerIdAndVM.getError();
      return -1;
    }
    if (cloudManagerIdAndVM.getIdcloudmanager() < 1) {
      errorContainer[0] = CONFLICT;
      return -1;
    }
    Desktop desktop = databaseFacade.load(Desktop.class, desktopid);
    DesktopPoolEntity desktopPool = databaseFacade.load(DesktopPoolEntity.class, desktop.getDesktoppoolid());
    try
    {
      operationRegistry.start(new StartDesktopOperation(desktopPool.getIddesktoppool(), desktopPool.getCloudmanagerid(), desktopid, desktop.getVmid()));
    } catch (CommonException e)
    {
      // TODO Auto-generated catch block
      e.printStackTrace();
      return -1;
    }
    return 1;// FIXME
//    int jobId = asyncWorkWithCloudManager(
//        cloudManagerStatusFacade,
//        asyncJobFacade,
//        cloudManagerIdAndVM.getIdcloudmanager(),
//        "startDesktop",
//        desktopid,
//        new CloudManagerWorker() {
//          @Override
//          public int execute(CloudManager cloudManager, Object[] resultContainer) {
//            resultContainer[0] = desktopid;
//            Desktop desktop = databaseFacade.load(Desktop.class, desktopid);
//            return DesktopFacade.this.start(cloudManagerIdAndVM.getIdcloudmanager(), cloudManager, desktop.getDesktoppoolid(), desktopid, desktop.getVmid());
//          }
//        });
//    if (jobId == -1) {
//      errorContainer[0] = HYPERVISOR_ABNORMAL;
//    }
//    return jobId;
  }

  /**
   * 停止桌面.
   * 
   * @param desktopid 桌面ID.
   * @param errorContainer 错误代码容器.
   * @return 任务ID. 有错误则返回-1.
   */
  public int stopDesktop(final int desktopid, int[] errorContainer) {
    final CloudManagerIdAndVM cloudManagerIdAndVM = this.findCloudManagerIdAndVM(desktopid);
    if(numberNotEquals(cloudManagerIdAndVM.getError(), NO_ERRORS)) {
      errorContainer[0] = cloudManagerIdAndVM.getError();
      return -1;
    }
    Desktop desktop = databaseFacade.load(Desktop.class, desktopid);
    DesktopPoolEntity desktopPool = databaseFacade.load(DesktopPoolEntity.class, desktop.getDesktoppoolid());
    try
    {
      operationRegistry.start(new StopDesktopOperation(desktop.getDesktoppoolid(), desktopPool.getCloudmanagerid(), desktopid, desktop.getVmid()));
    } catch (CommonException e)
    {
      // TODO Auto-generated catch block
      e.printStackTrace();
      return -1;
    }
    return 1;// FIXME
//    int jobId = DesktopFacade.this.disconnectAndShutdown(
//        cloudManagerIdAndVM.getIdcloudmanager(),
//        desktop.getDesktoppoolid(),
//        desktop.getIddesktop(),
//        desktop.getVmid(),
//        pool,
//        false,
//        false,
//        false,
//        true,
//        null,
//        null,
//        null,
//        -1,
//        false,
//        true);
//    if (jobId == -1) {
//      errorContainer[0] = HYPERVISOR_ABNORMAL;
//    }
//    return jobId;
  }
  
  public int stopDesktopOS(final int desktopid, int[] errorContainer) {
    final CloudManagerIdAndVM cloudManagerIdAndVM = this.findCloudManagerIdAndVM(desktopid);
    if(numberNotEquals(cloudManagerIdAndVM.getError(), NO_ERRORS)) {
      errorContainer[0] = cloudManagerIdAndVM.getError();
      return -1;
    }
    Desktop desktop = databaseFacade.load(Desktop.class, desktopid);
    DesktopPoolEntity pool = databaseFacade.load(DesktopPoolEntity.class, desktop.getDesktoppoolid());
    int jobId = DesktopFacade.this.disconnectAndShutdown(
        cloudManagerIdAndVM.getIdcloudmanager(),
        desktop.getDesktoppoolid(),
        desktop.getIddesktop(),
        desktop.getVmid(),
        pool,
        true,
        false,
        false,
        true,
        null,
        null,
        null,
        -1,
        false,
        true);
    if (jobId == -1) {
      errorContainer[0] = HYPERVISOR_ABNORMAL;
    }
    return jobId;
  }

  /**
   * 分页查询桌面.
   * 
   * @param desktoppoolid 桌面池ID. -1为忽略.
   * @param pagingInfo 分页信息.
   * @param amountContainer 查询结果的总数量的容器.
   * @return 查询结果列表.
   */
  @SuppressWarnings("unchecked")
  public List<Desktop> findDesktops(int desktoppoolid, int desktopid,
      PagingInfo pagingInfo, int[] amountContainer) {
    StringBuilder whereClause = new StringBuilder("from Desktop where 1 = 1");
    List<Object> params = new ArrayList<Object>();
    if (desktoppoolid > -1) {
      whereClause.append(" and desktoppoolid = ?");
      params.add(desktoppoolid);
    }
    if (desktopid > 0) {
      whereClause.append(" and iddesktop = ?");
      params.add(desktopid);
    }
    whereClause.append(FacadeHelper.keyword(pagingInfo, params));
    Object[] paramsArray = params.toArray();
    count(databaseFacade, "iddesktop", whereClause, paramsArray, amountContainer);
    List<Desktop> desktops = pagingFind(databaseFacade, whereClause, paramsArray, pagingInfo);
    long now = System.currentTimeMillis();
    Map<Integer, Set<Desktop>> toRefresh = new HashMap<Integer, Set<Desktop>>();
    for (Iterator<Desktop> iterator = desktops.iterator(); iterator.hasNext(); ) {
      Desktop desktop = (Desktop) iterator.next();
      DesktopPoolEntity pool = (DesktopPoolEntity) databaseFacade.findFirst(
          "from DesktopPoolEntity where iddesktoppool = ?",
          desktop.getDesktoppoolid());
      // TODO null on delete pool.
      if (pool == null) {
        iterator.remove();
        continue;
      }
      if (desktop.getVmid() != null
          || pool.getVmsource() == DesktopPoolEntity.DESKTOP_POOL_SOURCE_MANUAL) {
        Long statusRefreshedTime = statusRefreshedTimes.get(desktop.getIddesktop());
//        if (statusRefreshedTime == null || (now - statusRefreshedTime) > 30000) {
          statusRefreshedTimes.put(desktop.getIddesktop(), now);
          Set<Desktop> set = toRefresh.get(pool.getCloudmanagerid());
          if (set == null) {
            set = new HashSet<Desktop>();
            toRefresh.put(pool.getCloudmanagerid(), set);
          }
          set.add(desktop);
//        }
      }
      DesktopStatus desktopStatus = desktopStatusFacade.findDesktopStatus(desktop.getIddesktop());
      // TODO why null?
      if (desktopStatus == null) {
        iterator.remove();
        continue;
      }
      desktop.setOwnerid(desktopStatus.getOwnerid());
      if (desktopStatus.getOwnerid() == -1) {
        desktop.setOwnername(null);
        desktop.setRealname(null);
      }
      desktop.setStatus(
          desktopStatus.getPhase() == DesktopState.DESKTOP_PHASE_CREATING ?
              DesktopStatus.DESKTOP_STATUS_PROVISIONING : (
                  desktopStatus.getPhase() == DesktopState.DESKTOP_PHASE_DELETING ?
                      DesktopStatus.DESKTOP_STATUS_DESTROYING : (
                          desktopStatus.getPhase() == DesktopState.DESKTOP_PHASE_DEFICIENT ?
                              DesktopStatus.DESKTOP_STATUS_ERROR : (
                                  desktopStatus.getConnected() == 1 ?
                                      DesktopStatus.DESKTOP_STATUS_CONNECTED : (
                                          desktopStatus.getStatus() == DesktopState.DESKTOP_STATUS_STARTING ?
                                              DesktopStatus.DESKTOP_STATUS_STARTING : (
                                                  desktopStatus.getStatus() == DesktopState.DESKTOP_STATUS_ERROR ?
                                                      DesktopStatus.DESKTOP_STATUS_ERROR : (
                                                          desktopStatus.getStatus() == DesktopState.DESKTOP_STATUS_SERVING ?
                                                              DesktopStatus.DESKTOP_STATUS_SERVING : (
                                                                  desktopStatus.getStatus() == DesktopState.DESKTOP_STATUS_RUNNING ?
                                                                      DesktopStatus.DESKTOP_STATUS_RUNNING : (
                                                                          desktopStatus.getStatus() == DesktopState.DESKTOP_STATUS_STOPPED ?
                                                                              DesktopStatus.DESKTOP_STATUS_STOPPED : (
                                                                                  desktopStatus.getStatus() == DesktopState.DESKTOP_STATUS_STOPPING ?
                                                                                      DesktopStatus.DESKTOP_STATUS_STOPPING :
                                                                                        DesktopStatus.DESKTOP_STATUS_ERROR))))))))));
//      desktop.setDesktoppoolname(pool.getPoolname());
      desktop.setDesktoppooltype(pool.getAssignment());
      if (pool.getDomainname() != null) {
        desktop.setDomainid((Integer) databaseFacade.findFirst(
            "select iddomain from Domain where domainname = ?",
            pool.getDomainname()));
        desktop.setDomainname(pool.getDomainname());
      } else {
        desktop.setDomainid(Domain.DEFAULT_DOMAIN_ID);
      }
    }
    for (final Integer cloudmanagerid : toRefresh.keySet()) {
      final Set<Desktop> set = toRefresh.get(cloudmanagerid);
      if (!set.isEmpty()) {
        final int id = (int) (Math.random() * 100000000);
        log.trace("Start refreshing " + id);
        if (cloudmanagerid > 0) {
          asyncWorkWithCloudManager(
              cloudManagerStatusFacade,
              asyncJobFacade,
              databaseFacade.load(CloudManagerEntity.class, cloudmanagerid),
              null,
              null,
              new CloudManagerWorker() {
                @Override
                public int execute(CloudManager cloudManager, Object[] resultContainer, int jobid) throws Exception {
                  log.trace("Do start refreshing " + id);
                  for (Desktop desktop : set) {
                    doRefreshDesktopStatus(cloudManager, cloudmanagerid, desktop);
                  }
                  return NO_ERRORS;
                }
              });
        } else {
          Thread thread = new Thread() {
            @Override
            public void run() {
              for (Desktop desktop : set) {
                doRefreshDesktopStatus(null, 0, desktop);
              }
            }
          };
          com.opzoon.ohvc.session.ExcecutorUtil.execute(thread);
        }
      }
    }
    return desktops;
  }

  /**
   * 根据桌面ID找到其所属的平台ID和关联的虚拟机.
   * 
   * @param desktopid 桌面ID.
   * @return 其所属的平台ID和关联的虚拟机.
   */
  public CloudManagerIdAndVM findCloudManagerIdAndVM(int desktopid) {
    CloudManagerIdAndVM cloudManagerIdAndVM = new CloudManagerIdAndVM();
    Desktop desktop = databaseFacade.load(Desktop.class, desktopid);
    if (desktop == null) {
      cloudManagerIdAndVM.setError(NOT_FOUND);
      return cloudManagerIdAndVM;
    }
    DesktopStatus desktopStatus = desktopStatusFacade.findDesktopStatus(desktopid);
    // TODO Everyone can operate a desktop owned by none ?
    // sessionFacade.getCurrentSession() == null means background run.
    if (desktopStatus.getOwnerid() > -1 && sessionFacade.getCurrentSession() != null) {
      if(numberNotEquals(sessionFacade.getCurrentSession().getUserid(), desktopStatus.getOwnerid())
          && (!userFacade.isSuperAdmin(sessionFacade.getCurrentSession().getUserid()))
          && (!userFacade.canManageUser(desktopStatus.getOwnerid()))) {
        cloudManagerIdAndVM.setError(FORBIDDEN);
        return cloudManagerIdAndVM;
      }
    }
    DesktopPoolEntity pool = databaseFacade.load(DesktopPoolEntity.class, desktop.getDesktoppoolid());
    cloudManagerIdAndVM.setIdcloudmanager(pool.getCloudmanagerid());
    cloudManagerIdAndVM.setVmid(desktop.getVmid());
    cloudManagerIdAndVM.setVmname(desktop.getVmname());
    cloudManagerIdAndVM.setPcname(guessPcname(pool.getComputernamepattern(), desktop.getVmname()));
    if (numberEquals(pool.getVmsource(), DESKTOP_POOL_SOURCE_AUTO)) {
      cloudManagerIdAndVM.setTemplateid(pool.getTemplateid());
    }
    return cloudManagerIdAndVM;
  }

  public static String guessPcname(String computernamepattern, String vmname) {
    String suffix = vmname.substring(vmname.lastIndexOf('-'));
    if (computernamepattern.length() + suffix.length() > 15) {
      computernamepattern = computernamepattern.substring(0, 15 - suffix.length());
    }
    return strcat(computernamepattern, suffix);
  }

  /**
   * 克隆桌面.
   * 
   * @param cloudManager 平台.
   * @param iddesktoppool 桌面池ID.
   * @param templateid 模板ID.
   * @param vmName 桌面名称.
   * @param errorContainer 错误代码容器.
   * @return 桌面.
   */
  public String cloneDesktop(
      int cloudManagerid,
      CloudManager cloudManager,
      int iddesktoppool,
      String templateid,
      String oldvmname,
      String vmName,
      boolean link,
      int[] errorContainer) {
    errorContainer[0] = NO_ERRORS;
    
    if (oldvmname == null) {
      vmName = vmName + "-" + iddesktoppool + "-" + 1;
    } else {
      String[] oldvmnameArray = oldvmname.split("\\-");
      vmName = vmName + "-" + iddesktoppool + "-" + (Integer.parseInt(oldvmnameArray[oldvmnameArray.length - 1]) + 1);
    }
    
    // 执行克隆操作.
    Job<VMInstance> cloudJob = null;
    try {
      cloudJob = cloudManager.cloneVM(templateid, vmName,link);
      // 等待结果.
      waitForResult(cloudManager, cloudJob);
    } catch (ResourceNotMeetException e) {
      log.warn("Clone failed", e);
      cloudJob = new Job<VMInstance>() {{ this.setStatus(JobStatus.FAILED); this.setError(HYPERVISOR_NO_ENOUGH_RESOURCES); }};
      cloudManagerStatusFacade.markAsAbnormal(cloudManagerid);
    } catch (Exception e) {
      log.warn("Clone failed", e);
      cloudJob = new Job<VMInstance>() {{ this.setStatus(JobStatus.FAILED); this.setError(HYPERVISOR_ABNORMAL); }};
      cloudManagerStatusFacade.markAsAbnormal(cloudManagerid);
    }
    if (cloudJob.getStatus() == JobStatus.FAILED) {
      log.warn("Clone failed: {} to {}", templateid, vmName);
      // TODO Adapter getError.
      errorContainer[0] = cloudJob.getError() == NO_ERRORS ? HYPERVISOR_ABNORMAL : cloudJob.getError();
      return null;
    }
    VMInstance vm = cloudJob.getResult();
    log.trace("Clone OK: {}", vm.getId());
    return vm.getId();
  }

  /**
   * 创建桌面.
   * 
   * @param iddesktoppool 桌面池ID.
   * @param vmid 虚拟机ID. null表示创建失败.
   * @param vmname 虚拟机名称.
   */
  public void createDesktop(CloudManager cloudManager, int iddesktoppool, String vmid, String vmname, boolean running, int owner) {
    if (!exists(databaseFacade.findFirst(
        "select count(iddesktoppool) from DesktopPoolEntity where iddesktoppool = ?",
        iddesktoppool))) {
      return;
    }
    Desktop desktop = new Desktop();
    desktop.setDesktoppoolid(iddesktoppool);
    desktop.setOwnerid(owner);
    desktop.setVmid(vmid);
    desktop.setVmname(vmname);
    databaseFacade.persist(desktop);
    try {
      desktopStatusFacade.createNewDesktopStatus(
          desktop.getIddesktop(),
          // This is a temp status if vmid == null, because desktopPoolStatusFacade.markAsAbnormal() will overwrite it soon.
          running ? DESKTOP_STATUS_SERVING : DESKTOP_STATUS_STOPPED,
          owner);
    } catch (CommonException e) {
      log.warn("createNewDesktopStatus failed.", e);
    }
    if (vmid == null) {
      desktopPoolStatusFacade.markAsError(desktop.getIddesktop(), iddesktoppool);
    }
  }

  public Desktop createDesktop(int iddesktoppool, String vmname) {
    Desktop desktop = new Desktop();
    desktop.setDesktoppoolid(iddesktoppool);
    desktop.setOwnerid(-1);
    desktop.setVmname(vmname);
    desktop.setIpaddress(iddesktoppool + " " + vmname);
    databaseFacade.persist(desktop);
    try {
      desktopStatusFacade.createNewDesktopStatus(
          desktop.getIddesktop(),
          DESKTOP_STATUS_PROVISIONING,
          -1);
    } catch (CommonException e) {
      log.warn("createNewDesktopStatus failed.", e);
    }
    return desktop;
  }

//  public List<Desktop> cloneDesktops(
//      CloudManager cloudManager, Integer iddesktoppool,
//      List<String> readyForUsedOnCloning, List<String> namesToClone) {
//    @SuppressWarnings("rawtypes")
//    List<Job> cloudJobs = new LinkedList<Job>();
//    Iterator<String> iter = readyForUsedOnCloning.iterator();
//    List<String> failedNames = new LinkedList<String>();
//    List<Desktop> desktops = new LinkedList<Desktop>();
//    for (String name : namesToClone) {
//      String templateid = iter.next();
//      try {
//        cloudJobs.add(cloudManager.cloneVM(templateid, name));
//      } catch (Exception e) {
//        log.warn("Clone failed", e);
//        failedNames.add(name);
//      }
//    }
//    waitForResults(cloudManager, cloudJobs);
//    for (String failedName : failedNames) {
//      this.createDesktop(iddesktoppool, null, failedName, false, -1);
//    }
//    for (Job<VMInstance> cloneJob : cloudJobs) {
//      VMInstance vm = cloneJob.getResult();
//      if (vm == null) {
//        continue;
//      }
//      // TODO vm.getName() may be null.
//      log.trace("Clone OK: {} {}", vm.getId(), vm.getName());
//      log.trace("Cloned state: {}", vm.getState());
//      desktops.add(this.createDesktop(iddesktoppool, vm.getId(), vm.getName(), "running".equalsIgnoreCase(vm.getState()), -1));
//    }
//    return desktops;
//  }

  public void asyncStart(final int cloudmanagerid, final int desktopid) {
    asyncWorkWithCloudManager(
        cloudManagerStatusFacade,
        null,
        databaseFacade.load(CloudManagerEntity.class, cloudmanagerid),
        null,
        desktopid,
        new CloudManagerWorker() {
          @Override
          public int execute(CloudManager cloudManager, Object[] resultContainer, int jobid) {
            resultContainer[0] = desktopid;
            Desktop desktop = databaseFacade.load(Desktop.class, desktopid);
            return DesktopFacade.this.start(cloudmanagerid, cloudManager, desktop.getDesktoppoolid(), desktopid, desktop.getVmid());
          }
        });
  }

  /**
   * 启动桌面.
   * 
   * @param cloudManager 平台.
   * @param desktopid 桌面ID.
   * @return 错误代码.
   */
  public int start(int cloudmanagerid, CloudManager cloudManager, int desktoppoolid, int desktopid, String vmid) {
    if (vmid == null) {
      return CONFLICT;
    }
    // FIXME
    // For V03.
    if (cloudManager.getClass().getSimpleName().toLowerCase().indexOf("opzoon") < 0) {
      boolean running = false;
      try {
        running = isRunning(cloudManager, vmid);
      } catch (Exception e1) {
        // TODO Auto-generated catch block
        log.warn("isRunning", e1);
      }
      if (running) {
        desktopPoolStatusFacade.markAsRunning(desktopid);
        log.trace("Querying {} for RDP start.", vmid);
        boolean ready = false;
        try {
          ready = cloudManager.getRdpStatus(vmid);
        } catch (Exception e) {
          // TODO Auto-generated catch block
          log.warn("getRdpStatus", e);
        }
        log.trace("Querying {} for RDP is OK.", vmid);
        if (ready) {
          desktopPoolStatusFacade.markAsServing(desktopid, desktoppoolid);
          return NO_ERRORS;
        }
      }
    }
    desktopPoolStatusFacade.markAsStarting(desktopid, desktoppoolid);
    Job<?> cloudJob;
    try {
      log.trace("Trying to start {}.", vmid);
      cloudJob = cloudManager.startVM(vmid);
      waitForResult(cloudManager, cloudJob);
    } catch (Exception e) {
      log.warn("Exception", e);
      cloudJob = new Job<String>() {{ this.setStatus(JobStatus.FAILED); this.setError(HYPERVISOR_ABNORMAL); }};
      cloudManagerStatusFacade.markAsAbnormal(cloudmanagerid);
    }
    if (cloudJob.getStatus() == JobStatus.FAILED) {
      log.warn("startVM failed: {}", vmid);
      // FIXME
      if (cloudJob.getError() != 0x80000107) {
        desktopPoolStatusFacade.markAsError(desktopid, desktoppoolid);
        // TODO Adapter getError.
        return numberEquals(cloudJob.getError(), NO_ERRORS) ? HYPERVISOR_ABNORMAL : cloudJob.getError();
      } else {
        log.trace("start Conflicted: {}", vmid);
      }
    }
    desktopPoolStatusFacade.markAsRunning(desktopid);
    log.trace("start OK: {}", vmid);
    int error = waitForRDP(cloudManager, vmid);
    if (numberNotEquals(error, NO_ERRORS)) {
      cloudManagerStatusFacade.markAsAbnormal(cloudmanagerid);
      desktopPoolStatusFacade.markAsError(desktopid, desktoppoolid);
      return HYPERVISOR_ABNORMAL;
    }
    desktopPoolStatusFacade.markAsServing(desktopid, desktoppoolid);
    return NO_ERRORS;
  }

  /**
   * 销毁桌面.
   * 
   * @param cloudManager 平台.
   * @param desktopPool 桌面池.
   * @param desktop 桌面.
   * @return 任务ID.
   */
  public int destroy(
      int cloudmanagerid,
      Desktop desktop,
      DesktopPoolEntity desktoppool,
      boolean toCloneAfterDestroy,
      String templateidForClone,
      String vmnameForClone,
      String pcnameForClone,
      int ownerForClone,
      boolean toShutdown,
      boolean force) {
    return this.disconnectAndShutdown(
        cloudmanagerid,
        desktop.getDesktoppoolid(),
        desktop.getIddesktop(),
        desktop.getVmid(),
        desktoppool,
        false,
        false,
        true,
        toCloneAfterDestroy,
        templateidForClone,
        vmnameForClone,
        pcnameForClone,
        ownerForClone,
        toShutdown,
        force);
  }

  /**
   * 在数据库中绑定用户到某桌面.
   * 
   * @param userid 用户ID.
   * @param availableDesktop 桌面ID.
   * @param pool 桌面池.
   */
  public int assignDesktopToConnect(
      int cloudmanagerid,
      String ipaddress,
      int userid,
      String password,
      int availableDesktop,
      DesktopPoolEntity pool) {
    Desktop desktop = databaseFacade.load(Desktop.class, availableDesktop);
    boolean alreadyOwned = desktop.getOwnerid() == userid;
    int oldOwner = desktop.getOwnerid();
    int error = desktopStatusFacade.assignDesktopToConnect(userid, availableDesktop, pool);
    if (numberNotEquals(error, NO_ERRORS)) {
      return error;
    }
    if (numberEquals(pool.getAssignment(), DESKTOP_POOL_ASSIGNMENT_DEDICATED)) {
      desktop.setOwnerid(sessionFacade.getCurrentSession().getUserid());
      databaseFacade.merge(desktop);
    }
    
    User user = databaseFacade.load(User.class, userid);
    if (user.getDomainid() == Domain.DEFAULT_DOMAIN_ID) {
      if (!alreadyOwned) {
        error = this.deleteUser(ipaddress);
        log.trace("Delete user result: {}", error);
      }
      error = this.createUser(ipaddress, user.getUsername(), password);
      log.trace("Create user result: {}", error);
      if (numberNotEquals(error, NO_ERRORS)) {
        if (numberEquals(Services.err.unwrap(error), AgentErrors.AERR_USER_EXISTS)) {
          log.trace("User exists. Trying to update user's password.");
          error = this.updateUserPassword(ipaddress, user.getUsername(), password);
          log.trace("updateUserPassword result: {}", error);
//          if (numberNotEquals(error, NO_ERRORS)) {
//            return HYPERVISOR_ABNORMAL;
//          }
        }
//        else {
//          return HYPERVISOR_ABNORMAL;
//        }
      }
    } else {
      if ((!alreadyOwned) && oldOwner > -1) {
        User oldUser = databaseFacade.load(User.class, oldOwner);
        if (oldUser != null) {
          error = this.deleteUserProfile(ipaddress, pool.getDomainname(), oldUser.getUsername());
          log.trace("Delete user profile result: {}", error);
        }
      }
    }
    return NO_ERRORS;
  }

  public Integer unassign(DesktopPoolEntity pool, int desktopid, int userid, boolean toCloneAfterDestroy, boolean toShutdown) {
    if (exists(databaseFacade.update(
        "update Desktop set ownerid = ? where iddesktop = ? and ownerid = ?",
        -1, desktopid, userid))) {
      desktopStatusFacade.unassignDesktop(pool, desktopid, userid);
    }
    desktopPoolStatusFacade.markDesktopPoolAsNotFullIfNeeded(pool);
    Desktop desktop = databaseFacade.load(Desktop.class, desktopid);
    if (pool.getVmsource() == DesktopPoolEntity.DESKTOP_POOL_SOURCE_MANUAL) {
      User user = databaseFacade.load(User.class, userid);
      // TODO Handle closed desktop.
      this.deleteUserProfile(desktop.getIpaddress(), pool.getDomainname(), user.getUsername());
      return null;
    } else {
      DesktopPoolStatus assignedPoolStatus = databaseFacade.load(DesktopPoolStatus.class, pool.getIddesktoppool());
      return DesktopFacade.this.destroy(
          pool.getCloudmanagerid(),
          desktop,
          pool,
          pool.getTemplateid() != null && toCloneAfterDestroy
              && assignedPoolStatus.getStatus() != DesktopPoolStatus.DESKTOPPOOL_STATUS_MAINTAIN,
          pool.getTemplateid(),
          desktop.getVmname(),
          guessPcname(pool.getComputernamepattern(), desktop.getVmname()),
          -1,
          toShutdown,
          true);
    }
//  public void unassign(DesktopPoolEntity pool, int desktopid) {
    
//    this.deleteUser(pool.getCloudmanagerid(), desktopid, userid);
    
//    if (pool.getAssignment() == DesktopPoolEntity.DESKTOP_POOL_ASSIGNMENT_DEDICATED) {
//      Desktop desktop = databaseFacade.load(Desktop.class, desktopid);
//      DesktopFacade.this.destroy(
//          pool.getCloudmanagerid(),
//          desktop,
//          pool.getTemplateid() != null,
//          pool.getTemplateid(),
//          desktop.getVmname(),
//          guessPcname(pool.getComputernamepattern(), desktop.getVmname()),
//          -1);
//    } else {
//      this.rebootDesktop(desktopid, new int[1]);
//    }
  }

  public int afterClone(
      int cloudmanagerid,
      CloudManager cloudManager,
      DesktopPoolEntity desktopPool,
      Desktop desktop) {
    Job<?> cloudJob;
    try {
      log.trace("Trying to start {}.", desktop.getVmid());
      cloudJob = cloudManager.startVM(desktop.getVmid());
      waitForResult(cloudManager, cloudJob);
    } catch (Exception e) {
      log.warn("Exception", e);
      cloudJob = new Job<String>() {{ this.setStatus(JobStatus.FAILED); this.setError(HYPERVISOR_ABNORMAL); }};
      cloudManagerStatusFacade.markAsAbnormal(cloudmanagerid);
    }
    if (cloudJob.getStatus() == JobStatus.FAILED) {
      log.warn("startVM failed: {} ({})", desktop.getVmid(), cloudJob.getError());
      // FIXME
      if (cloudJob.getError() != 0x80000107) {
        desktopPoolStatusFacade.markAsError(desktop.getIddesktop(), desktop.getDesktoppoolid());
        return HYPERVISOR_ABNORMAL;
      } else {
        log.trace("start Conflicted: {}", desktop.getVmid());
      }
    }
    int setHostNameError = this.setHostName(cloudManager, desktopPool, desktop.getVmid(), desktop.getVmname());
    if (setHostNameError != NO_ERRORS) {
      desktopPoolStatusFacade.markAsError(desktop.getIddesktop(), desktop.getDesktoppoolid());
      return setHostNameError;
    }
    if (desktopPool.getDomainname() != null) {
      int joinDomainError = this.joinDomain(cloudManager, desktopPool, desktop.getVmid(), desktop.getVmname());
      if (joinDomainError != NO_ERRORS) {
        desktopPoolStatusFacade.markAsError(desktop.getIddesktop(), desktop.getDesktoppoolid());
        return joinDomainError;
      }
    }
    int error = waitForRDP(cloudManager, desktop.getVmid());
    if (numberNotEquals(error, NO_ERRORS)) {
      cloudManagerStatusFacade.markAsAbnormal(desktopPool.getCloudmanagerid());
      desktopPoolStatusFacade.markAsError(desktop.getIddesktop(), desktop.getDesktoppoolid());
      return HYPERVISOR_ABNORMAL;
    }
    databaseFacade.update(
        "update Desktop set vmid = ? where iddesktop = ?",
        desktop.getVmid(), desktop.getIddesktop());
    desktopPoolStatusFacade.markAsServing(desktop.getIddesktop(), desktop.getDesktoppoolid());
    return NO_ERRORS;
  }

  public int createUserVolume(final int cloudmanagerid, final int userid, final long size, String volumename) {
    if((!userFacade.isSuperAdmin(sessionFacade.getCurrentSession().getUserid()))
        && (!userFacade.canManageUser(userid))) {
      return FORBIDDEN;
    }
    return this.doCreateUserVolume(cloudmanagerid, userid, size, volumename);
  }

  public int deleteUserVolumes(int userid, Integer uservolumeid) {
    if((!userFacade.isSuperAdmin(sessionFacade.getCurrentSession().getUserid()))
        && (!userFacade.canManageUser(userid))) {
      return FORBIDDEN;
    }
    User user = databaseFacade.load(User.class, userid);
    return this.doDeleteUserVolumes(user, uservolumeid);
  }

  @SuppressWarnings("unchecked")
  public int doDeleteUserVolumes(User user, Integer uservolumeid) {
    List<UserVolume> userVolumes = null;
    if (uservolumeid == null) {
      userVolumes = (List<UserVolume>) databaseFacade.find(
          "from UserVolume where userid = ?",
          user.getIduser());
      userFacade.deleteUserVolumes(user.getIduser());
    } else {
      userVolumes = new LinkedList<UserVolume>();
      userVolumes.add(databaseFacade.load(UserVolume.class, uservolumeid));
      userFacade.deleteUserVolume(uservolumeid);
    }
    for (UserVolume userVolume : userVolumes) {
      final int cloudmanagerid = userVolume.getCloudmanagerid();
      final String storageid = userVolume.getStorageid();
      asyncWorkWithCloudManager(
          cloudManagerStatusFacade,
          asyncJobFacade,
          databaseFacade.load(CloudManagerEntity.class, cloudmanagerid),
          "deleteVolume",
          null,
          new CloudManagerWorker() {
            @Override
            public int execute(CloudManager cloudManager, Object[] resultContainer, int jobid) throws Exception {
              Job<?> cloudJob;
              try {
                log.trace("Trying to detach volume #{}.", storageid);
                cloudJob = cloudManager.detachVolume(storageid);
                waitForResult(cloudManager, cloudJob);
              } catch (Exception e) {
                log.warn("Exception", e);
              }
              try {
                log.trace("Trying to delete volume #{}.", storageid);
//                int error = 
                    cloudManager.deleteVolume(storageid);
//                log.trace("Delete volume result: {}", error);
//                if (numberNotEquals(error, NO_ERRORS)) {
//                  return HYPERVISOR_ABNORMAL;
//                }
              } catch (Exception e) {
                log.warn("Exception", e);
                cloudManagerStatusFacade.markAsAbnormal(cloudmanagerid);
                return HYPERVISOR_ABNORMAL;
              }
              log.trace("deleteVolume OK: {}", storageid);
              return NO_ERRORS;
            }
          });
    }
    return NO_ERRORS;
  }

  @SuppressWarnings("unchecked")
  public int eraseUserVolumes(final int userid, Integer uservolumeid) {
    if((!userFacade.isSuperAdmin(sessionFacade.getCurrentSession().getUserid()))
        && (!userFacade.canManageUser(userid))) {
      return FORBIDDEN;
    }
    User user = databaseFacade.load(User.class, userid);
    List<UserVolume> userVolumes = null;
    if (uservolumeid == null) {
      userVolumes = (List<UserVolume>) databaseFacade.find(
          "from UserVolume where userid = ?",
          user.getIduser());
    } else {
      userVolumes = new LinkedList<UserVolume>();
      userVolumes.add(databaseFacade.load(UserVolume.class, uservolumeid));
    }
    for (final UserVolume userVolume : userVolumes) {
      final int cloudmanagerid = userVolume.getCloudmanagerid();
      final String storageid = userVolume.getStorageid();
      asyncWorkWithCloudManager(
          cloudManagerStatusFacade,
          asyncJobFacade,
          databaseFacade.load(CloudManagerEntity.class, cloudmanagerid),
          "deleteVolume",
          null,
          new CloudManagerWorker() {
            @Override
            public int execute(CloudManager cloudManager, Object[] resultContainer, int jobid) throws Exception {
              Job<?> cloudJob;
              try {
                log.trace("Trying to detach volume #{}.", storageid);
                cloudJob = cloudManager.detachVolume(storageid);
                waitForResult(cloudManager, cloudJob);
              } catch (Exception e) {
                log.warn("Exception", e);
              }
              try {
                log.trace("Trying to erase volume #{}.", storageid);
                cloudJob = cloudManager.eraseVolume(storageid);
                waitForResult(cloudManager, cloudJob);
              } catch (Exception e) {
                log.warn("Exception", e);
                cloudJob = new Job<String>() {{ this.setStatus(JobStatus.FAILED); this.setError(HYPERVISOR_ABNORMAL); }};
                cloudManagerStatusFacade.markAsAbnormal(cloudmanagerid);
              }
              if (cloudJob.getStatus() == JobStatus.FAILED) {
                log.warn("eraseVolume failed: {}", cloudmanagerid);
                // TODO Adapter getError.
                return numberEquals(cloudJob.getError(), NO_ERRORS) ? HYPERVISOR_ABNORMAL : cloudJob.getError();
              }
              log.trace("eraseVolume OK: {}", cloudmanagerid);
              Volume volume = (Volume) cloudJob.getResult();
              userFacade.updateUserVolume(userVolume.getIduservolume(), volume.getId());
              return NO_ERRORS;
            }
          });
    }
    return NO_ERRORS;
  }

  public List<UserVolume> findUserVolumes(int userid, int[] errorContainer) {
    StringBuilder whereClause = new StringBuilder("from UserVolume where 1 = 1");
    List<Object> params = new ArrayList<Object>();
    if (userid > -1) {
      whereClause.append(" and userid = ?");
      params.add(userid);
    }
    Object[] paramsArray = params.toArray();
    @SuppressWarnings("unchecked")
    List<UserVolume> uservolumes = (List<UserVolume>) databaseFacade.find(
        whereClause.toString(),
        paramsArray);
    return uservolumes;
  }

  public List<UserVolume> findUserVolumes(int userid, PagingInfo pagingInfo, int[] amountContainer) {
    StringBuilder whereClause = new StringBuilder("from UserVolume where 1 = 1");
    List<Object> params = new ArrayList<Object>();
    if (userid > -1) {
      whereClause.append(" and userid = ?");
      params.add(userid);
    }
    Object[] paramsArray = params.toArray();
    count(databaseFacade, "iduservolume", whereClause, paramsArray, amountContainer);
    @SuppressWarnings("unchecked")
    List<UserVolume> uservolumes = pagingFind(databaseFacade, whereClause, paramsArray, pagingInfo);
    for (UserVolume userVolume : uservolumes)
    {
      userVolume.setCloudname(databaseFacade.load(CloudManagerEntity.class, userVolume.getCloudmanagerid()).getCloudname());
      if (userVolume.getDesktoppoolid() != null)
      {
        userVolume.setPoolname(databaseFacade.load(DesktopPoolEntity.class, userVolume.getDesktoppoolid()).getPoolname());
        userVolume.setVmname(databaseFacade.load(Desktop.class, userVolume.getDesktopid()).getVmname());
      }
    }
    return uservolumes;
  }

  @SuppressWarnings("unchecked")
  public int attachUserVolume(int userid, final int cloudmanagerid,
      final Integer uservolumeid, final Desktop desktop) {
    final List<UserVolume> userVolumes;
    if (uservolumeid == null) {
      userVolumes = (List<UserVolume>) databaseFacade.find(
          "from UserVolume where userid = ?",
          userid);
    } else {
      userVolumes = new LinkedList<UserVolume>();
      userVolumes.add(databaseFacade.load(UserVolume.class, uservolumeid));
    }
    return asyncWorkWithCloudManager(
        cloudManagerStatusFacade,
        asyncJobFacade,
        databaseFacade.load(CloudManagerEntity.class, cloudmanagerid),
        null,
        null,
        new CloudManagerWorker() {
          @Override
          public int execute(CloudManager cloudManager, Object[] resultContainer, int jobid)
              throws Exception
          {
            for (final UserVolume userVolume : userVolumes) {
              final int volumecloudmanagerid = userVolume.getCloudmanagerid();
              final String storageid = userVolume.getStorageid();
              if (volumecloudmanagerid != cloudmanagerid) {
                continue;
              }
              Job<?> cloudJob;
              try {
                log.trace("Trying to detach volume #{}.", storageid);
                cloudJob = cloudManager.detachVolume(storageid);
                waitForResult(cloudManager, cloudJob);
              } catch (Exception e) {
                log.warn("Exception", e);
              }
              try {
                log.trace("Trying to attach volume #{} to #{}.", storageid, desktop.getVmid());
                cloudJob = cloudManager.attachVolume(storageid, desktop.getVmid());
                waitForResult(cloudManager, cloudJob);
              } catch (Exception e) {
                log.warn("Exception", e);
                cloudJob = new Job<String>() {{ this.setStatus(JobStatus.FAILED); this.setError(HYPERVISOR_ABNORMAL); }};
                cloudManagerStatusFacade.markAsAbnormal(cloudmanagerid);
              }
              if (cloudJob.getStatus() == JobStatus.FAILED) {
                log.warn("attachVolume failed: {}", cloudmanagerid);
                // TODO Adapter getError.
//                return numberEquals(cloudJob.getError(), NO_ERRORS) ? HYPERVISOR_ABNORMAL : cloudJob.getError();
                continue;
              }
              databaseFacade.update(
                  "update UserVolume set desktoppoolid = ?, desktopid = ? where iduservolume = ?",
                  desktop.getDesktoppoolid(), desktop.getIddesktop(), userVolume.getIduservolume());
              log.trace("attachVolume OK: {}", cloudmanagerid);
            }
            return NO_ERRORS;
          }
        });
  }

  public int logOff(final int userid, final int desktopid, final int desktoppoolid) {
    final User user = databaseFacade.load(User.class, userid);
    final String username = user.getUsername();
    final String domainname = user.getDomainid() == Domain.DEFAULT_DOMAIN_ID ? null : databaseFacade.load(Domain.class, user.getDomainid()).getDomainname();
    DesktopPoolEntity pool = databaseFacade.load(DesktopPoolEntity.class, desktoppoolid);
    Desktop desktop = databaseFacade.load(Desktop.class, desktopid);
    if (pool.getVmsource() == DesktopPoolEntity.DESKTOP_POOL_SOURCE_MANUAL) {
      final String ipaddress = desktop.getIpaddress();
      Thread thread = new Thread() {
        @Override
        public void run() {
          Job<?> cloudJob;
          try {
            log.trace("Trying to log off #{}.", userid);
            cloudJob = VdiAgentClientImpl.logOff(ipaddress, domainname, username);
            waitForAgentResult(cloudJob);
          } catch (Exception e) {
            log.warn("Exception", e);
            cloudJob = new Job<String>() {{ this.setStatus(JobStatus.FAILED); this.setError(HYPERVISOR_ABNORMAL); }};
          }
          if (cloudJob.getStatus() == JobStatus.FAILED) {
            log.warn("logOff failed: {}", ipaddress);
            // TODO Adapter getError.
            return;
          }
          log.trace("logOff OK: {}", ipaddress);
        }
      };
      com.opzoon.ohvc.session.ExcecutorUtil.execute(thread);
      return -1;
    } else {
      final String vmid = desktop.getVmid();
      final int cloudmanagerid = pool.getCloudmanagerid();
      return syncWorkWithCloudManager(
          cloudManagerStatusFacade,
          databaseFacade.load(CloudManagerEntity.class, cloudmanagerid),
          new CloudManagerWorker() {
            @Override
            public int execute(CloudManager cloudManager, Object[] resultContainer, int jobid) throws Exception {
              Job<?> cloudJob;
              try {
                log.trace("Trying to log off #{}.", userid);
                cloudJob = cloudManager.logOff(vmid, domainname, username);
                waitForResult(cloudManager, cloudJob);
              } catch (Exception e) {
                log.warn("Exception", e);
                cloudJob = new Job<String>() {{ this.setStatus(JobStatus.FAILED); this.setError(HYPERVISOR_ABNORMAL); }};
                cloudManagerStatusFacade.markAsAbnormal(cloudmanagerid);
              }
              if (cloudJob.getStatus() == JobStatus.FAILED) {
                log.warn("logOff failed: {}", cloudmanagerid);
                // TODO Adapter getError.
                return numberEquals(cloudJob.getError(), NO_ERRORS) ? HYPERVISOR_ABNORMAL : cloudJob.getError();
              }
              log.trace("logOff OK: {}", cloudmanagerid);
              return NO_ERRORS;
            }
          });
    }
  }

  @SuppressWarnings("unchecked")
  public int detachUserVolumes(final int userid, final Integer uservolumeid) {
    final List<UserVolume> userVolumes;
    if (uservolumeid == null) {
      userVolumes = (List<UserVolume>) databaseFacade.find(
          "from UserVolume where userid = ?",
          userid);
    } else {
      userVolumes = new LinkedList<UserVolume>();
      userVolumes.add(databaseFacade.load(UserVolume.class, uservolumeid));
    }
    final int cloudmanagerid = userVolumes.iterator().next().getCloudmanagerid();
    return asyncWorkWithCloudManager(
        cloudManagerStatusFacade,
        asyncJobFacade,
        databaseFacade.load(CloudManagerEntity.class, cloudmanagerid),
        null,
        null,
        new CloudManagerWorker() {
          @Override
          public int execute(CloudManager cloudManager, Object[] resultContainer, int jobid)
              throws Exception
          {
            for (final UserVolume userVolume : userVolumes) {
              final int volumecloudmanagerid = userVolume.getCloudmanagerid();
              final String storageid = userVolume.getStorageid();
              databaseFacade.update(
                  "update UserVolume set desktoppoolid = null, desktopid = null, lastdetachtime = ? where iduservolume = ?",
                  (int) (System.currentTimeMillis() / 1000), userVolume.getIduservolume());
              Job<?> cloudJob;
              try {
                log.trace("Trying to detach volume #{}.", storageid);
                cloudJob = cloudManager.detachVolume(storageid);
                waitForResult(cloudManager, cloudJob);
              } catch (Exception e) {
                log.warn("Exception", e);
                cloudJob = new Job<String>() {{ this.setStatus(JobStatus.FAILED); this.setError(HYPERVISOR_ABNORMAL); }};
                cloudManagerStatusFacade.markAsAbnormal(volumecloudmanagerid);
              }
              if (cloudJob.getStatus() == JobStatus.FAILED) {
                log.warn("detachVolume failed: {}", volumecloudmanagerid);
                // TODO Adapter getError.
//                return numberEquals(cloudJob.getError(), NO_ERRORS) ? HYPERVISOR_ABNORMAL : cloudJob.getError();
                continue;
              }
              log.trace("detachVolume OK: {}", volumecloudmanagerid);
            }
            return NO_ERRORS;
          }
        });
  }

  // FIXME Delete it.
//  public void tempCreateUserVolume(final int userid) {
//    final Integer firstCloudManagerId = (Integer) databaseFacade.findFirst(
//        "select idcloudmanager from CloudManagerEntity");
//    if (firstCloudManagerId == null) {
//      return;
//    }
//    Thread thread = new Thread() {
//      @Override
//      public void run() {
//        doCreateUserVolume(firstCloudManagerId, userid, 20);
//      }
//    };
//    com.opzoon.ohvc.session.ExcecutorUtil.execute(thread);
//  }

  public List<RestrictionStrategy> findRestrictionStrategies(
      PagingInfo pagingInfo,
      int[] amountContainer) {
    StringBuilder whereClause = new StringBuilder("from RestrictionStrategy where 1 = 1");
    List<Object> params = new ArrayList<Object>();
    whereClause.append(FacadeHelper.keyword(pagingInfo, params));
    Object[] paramsArray = params.toArray();
    count(databaseFacade, "idrestrictionstrategy", whereClause, paramsArray, amountContainer);
    @SuppressWarnings("unchecked")
    List<RestrictionStrategy> list = (List<RestrictionStrategy>) pagingFind(databaseFacade,
        whereClause.toString(), paramsArray, pagingInfo);
    for (RestrictionStrategy restrictionStrategy : list) {
      @SuppressWarnings("unchecked")
      List<USBListItem> items = (List<USBListItem>) databaseFacade.find(
          "from USBListItem where restrictionstrategyid = ?",
          restrictionStrategy.getIdrestrictionstrategy());
      restrictionStrategy.setUsbclasswhitelist(new LinkedList<USBListItem>());
      restrictionStrategy.setUsbclassblacklist(new LinkedList<USBListItem>());
      restrictionStrategy.setUsbdevicewhitelist(new LinkedList<USBListItem>());
      restrictionStrategy.setUsbdeviceblacklist(new LinkedList<USBListItem>());
      for (USBListItem usbListItem : items) {
        switch (usbListItem.getListtype()) {
        case USBListItem.LIST_TYPE_CLASS_WHITE:
          restrictionStrategy.getUsbclasswhitelist().add(usbListItem);
          break;
        case USBListItem.LIST_TYPE_CLASS_BLACK:
          restrictionStrategy.getUsbclassblacklist().add(usbListItem);
          break;
        case USBListItem.LIST_TYPE_DEVICE_WHITE:
          restrictionStrategy.getUsbdevicewhitelist().add(usbListItem);
          break;
        default:
          restrictionStrategy.getUsbdeviceblacklist().add(usbListItem);
          break;
        }
      }
      restrictionStrategy.setDefault(restrictionStrategy.getIdrestrictionstrategy().intValue() == 0 ? 1 : 0);
    }
    return list;
  }

  public int createRestrictionStrategy(RestrictionStrategy restrictionStrategy) throws CommonException {
    if(!userFacade.isSuperAdmin(sessionFacade.getCurrentSession().getUserid())) {
      return FORBIDDEN;
    }
//    restrictionStrategy.setHasusbclasswhitelist(restrictionStrategy.getUsbclasswhitelist() != null ? 1 : 0);
//    restrictionStrategy.setHasusbclassblacklist(restrictionStrategy.getUsbclassblacklist() != null ? 1 : 0);
//    restrictionStrategy.setHasusbdevicewhitelist(restrictionStrategy.getUsbdevicewhitelist() != null ? 1 : 0);
//    restrictionStrategy.setHasusbdeviceblacklist(restrictionStrategy.getUsbdeviceblacklist() != null ? 1 : 0);
    try {
      databaseFacade.persist(restrictionStrategy);
      if (restrictionStrategy.getUsbclasswhitelist() != null) {
        for (USBListItem usbListItem : restrictionStrategy.getUsbclasswhitelist()) {
          usbListItem.setRestrictionstrategyid(restrictionStrategy.getIdrestrictionstrategy());
          usbListItem.setListtype(USBListItem.LIST_TYPE_CLASS_WHITE);
          databaseFacade.persist(usbListItem);
        }
      }
      if (restrictionStrategy.getUsbclassblacklist() != null) {
        for (USBListItem usbListItem : restrictionStrategy.getUsbclassblacklist()) {
          usbListItem.setRestrictionstrategyid(restrictionStrategy.getIdrestrictionstrategy());
          usbListItem.setListtype(USBListItem.LIST_TYPE_CLASS_BLACK);
          databaseFacade.persist(usbListItem);
        }
      }
      if (restrictionStrategy.getUsbdevicewhitelist() != null) {
        for (USBListItem usbListItem : restrictionStrategy.getUsbdevicewhitelist()) {
          usbListItem.setRestrictionstrategyid(restrictionStrategy.getIdrestrictionstrategy());
          usbListItem.setListtype(USBListItem.LIST_TYPE_DEVICE_WHITE);
          databaseFacade.persist(usbListItem);
        }
      }
      if (restrictionStrategy.getUsbdeviceblacklist() != null) {
        for (USBListItem usbListItem : restrictionStrategy.getUsbdeviceblacklist()) {
          usbListItem.setRestrictionstrategyid(restrictionStrategy.getIdrestrictionstrategy());
          usbListItem.setListtype(USBListItem.LIST_TYPE_DEVICE_BLACK);
          databaseFacade.persist(usbListItem);
        }
      }
    } catch (PersistenceException e) {
      throw new CommonException(CONFLICT);
    }
    return NO_ERRORS;
  }

  public int updateRestrictionStrategy(RestrictionStrategy restrictionStrategy) {
    if (!exists(databaseFacade.findFirst(
        "select count(idrestrictionstrategy) from RestrictionStrategy where idrestrictionstrategy = ?",
        restrictionStrategy.getIdrestrictionstrategy()))) {
      return NOT_FOUND;
    }
    if(!userFacade.isSuperAdmin(sessionFacade.getCurrentSession().getUserid())) {
      return FORBIDDEN;
    }
    StringBuilder updateClause = new StringBuilder("update RestrictionStrategy set idrestrictionstrategy = idrestrictionstrategy");
    List<Object> params = new ArrayList<Object>();
    if (restrictionStrategy.getStrategyname() != null) {
      updateClause.append(", strategyname = ?");
      params.add(restrictionStrategy.getStrategyname());
    }
    if (restrictionStrategy.getUsbenabled() != null) {
      updateClause.append(", usbenabled = ?");
      params.add(restrictionStrategy.getUsbenabled());
    }
    if (restrictionStrategy.getDisk() != null) {
      updateClause.append(", disk = ?");
      params.add(restrictionStrategy.getDisk());
    }
    if (restrictionStrategy.getClipboard() != null) {
      updateClause.append(", clipboard = ?");
      params.add(restrictionStrategy.getClipboard());
    }
    if (restrictionStrategy.getAudio() != null) {
      updateClause.append(", audio = ?");
      params.add(restrictionStrategy.getAudio());
    }
    if (restrictionStrategy.getUservolume() != null) {
      updateClause.append(", uservolume = ?");
      params.add(restrictionStrategy.getUservolume());
    }
    if (restrictionStrategy.getNotes() != null) {
      updateClause.append(", notes = ?");
      params.add(restrictionStrategy.getNotes());
    }
    updateClause.append(" where idrestrictionstrategy = ?");
    params.add(restrictionStrategy.getIdrestrictionstrategy());
    Object[] paramsArray = params.toArray();
    if (!exists(databaseFacade.update(updateClause.toString(), paramsArray))) {
      return NOT_FOUND;
    }
    if (restrictionStrategy.getUsbclasswhitelist() != null) {
      databaseFacade.update(
          "delete from USBListItem where restrictionstrategyid = ? and listtype = ?",
          restrictionStrategy.getIdrestrictionstrategy(), USBListItem.LIST_TYPE_CLASS_WHITE);
      for (USBListItem usbListItem : restrictionStrategy.getUsbclasswhitelist()) {
        usbListItem.setRestrictionstrategyid(restrictionStrategy.getIdrestrictionstrategy());
        usbListItem.setListtype(USBListItem.LIST_TYPE_CLASS_WHITE);
        databaseFacade.persist(usbListItem);
      }
    }
    if (restrictionStrategy.getUsbclassblacklist() != null) {
      databaseFacade.update(
          "delete from USBListItem where restrictionstrategyid = ? and listtype = ?",
          restrictionStrategy.getIdrestrictionstrategy(), USBListItem.LIST_TYPE_CLASS_BLACK);
      for (USBListItem usbListItem : restrictionStrategy.getUsbclassblacklist()) {
        usbListItem.setRestrictionstrategyid(restrictionStrategy.getIdrestrictionstrategy());
        usbListItem.setListtype(USBListItem.LIST_TYPE_CLASS_BLACK);
        databaseFacade.persist(usbListItem);
      }
    }
    if (restrictionStrategy.getUsbdevicewhitelist() != null) {
      databaseFacade.update(
          "delete from USBListItem where restrictionstrategyid = ? and listtype = ?",
          restrictionStrategy.getIdrestrictionstrategy(), USBListItem.LIST_TYPE_DEVICE_WHITE);
      for (USBListItem usbListItem : restrictionStrategy.getUsbdevicewhitelist()) {
        usbListItem.setRestrictionstrategyid(restrictionStrategy.getIdrestrictionstrategy());
        usbListItem.setListtype(USBListItem.LIST_TYPE_DEVICE_WHITE);
        databaseFacade.persist(usbListItem);
      }
    }
    if (restrictionStrategy.getUsbdeviceblacklist() != null) {
      databaseFacade.update(
          "delete from USBListItem where restrictionstrategyid = ? and listtype = ?",
          restrictionStrategy.getIdrestrictionstrategy(), USBListItem.LIST_TYPE_DEVICE_BLACK);
      for (USBListItem usbListItem : restrictionStrategy.getUsbdeviceblacklist()) {
        usbListItem.setRestrictionstrategyid(restrictionStrategy.getIdrestrictionstrategy());
        usbListItem.setListtype(USBListItem.LIST_TYPE_DEVICE_BLACK);
        databaseFacade.persist(usbListItem);
      }
    }
    return NO_ERRORS;
  }

  public List<RestrictionStrategyAssignment> findRestrictionStrategyAssignments(
      int resourcetype,
      int resourceid,
      int restrictionstrategyid,
      int domainid,
      int targettype,
      PagingInfo pagingInfo,
      int[] amountContainer) {
    StringBuilder whereClause = new StringBuilder("select v from RestrictionStrategyAssignment v where 1 = 1");
    List<Object> params = new ArrayList<Object>();
    if (targettype > -1)
    {
      whereClause.append(" and v.targettype = ?");
      params.add(targettype);
    }
    if (resourceid > 0) {
      whereClause.append(" and v.targettype = ? and v.targetid = ?");
      params.add(RestrictionStrategyAssignment.RESTRICTION_STRATEGY_ASSIGNMENT_TARGET_TYPE_RESOURCE);
      params.add(resourceid);
    }
    if (restrictionstrategyid > -1) {
      whereClause.append(" and v.restrictionstrategyid = ?");
      params.add(restrictionstrategyid);
    }
    if (domainid > -1) {
      whereClause.append(" and v.domainid = ?");
      params.add(domainid);
    }
    Object[] paramsArray = params.toArray();
    count(databaseFacade, "idrestrictionstrategyassignment", whereClause, paramsArray, amountContainer);
    @SuppressWarnings("unchecked")
    List<RestrictionStrategyAssignment> resourceRecords = pagingFind(databaseFacade,
        whereClause.toString(), paramsArray, pagingInfo);
    for (RestrictionStrategyAssignment restrictionStrategyAssignment : resourceRecords) {
      Domain domain = databaseFacade.load(Domain.class, restrictionStrategyAssignment.getDomainid());
      restrictionStrategyAssignment.setDomainname(domain.getDomainname());
      if (restrictionStrategyAssignment.getTargettype() == RestrictionStrategyAssignment.RESTRICTION_STRATEGY_ASSIGNMENT_TARGET_TYPE_GROUP) {
        Group group = databaseFacade.load(Group.class, restrictionStrategyAssignment.getTargetid());
        restrictionStrategyAssignment.setTargetname(group.getGroupname());
        restrictionStrategyAssignment.setUseramount(((Long) databaseFacade.findFirst(
            "select count(idgroupelement) from GroupElement where groupid = ? and elementtype = ?",
            group.getIdgroup(), ELEMENT_TYPE_USER)).intValue());
      } else if (restrictionStrategyAssignment.getTargettype() == RestrictionStrategyAssignment.RESTRICTION_STRATEGY_ASSIGNMENT_TARGET_TYPE_ORGANIZATION) {
        Organization org = databaseFacade.load(Organization.class, restrictionStrategyAssignment.getTargetid());
        restrictionStrategyAssignment.setTargetname(org.getOrganizationname());
        restrictionStrategyAssignment.setUseramount(organizationFacade.findUsers(restrictionStrategyAssignment.getTargetid()).size());
      } else if (restrictionStrategyAssignment.getTargettype() == RestrictionStrategyAssignment.RESTRICTION_STRATEGY_ASSIGNMENT_TARGET_TYPE_USER) {
        User user = databaseFacade.load(User.class, restrictionStrategyAssignment.getTargetid());
        restrictionStrategyAssignment.setTargetname(user.getUsername());
        restrictionStrategyAssignment.setRealname(user.getRealname());
        restrictionStrategyAssignment.setNotes(user.getNotes());
        restrictionStrategyAssignment.setRootadmin(userFacade.isSuperAdmin(restrictionStrategyAssignment.getTargetid()) ? 1 : 0);
      } else {
        DesktopPoolEntity pool = desktopPoolFacade.loadDesktopPool(restrictionStrategyAssignment.getTargetid());
        restrictionStrategyAssignment.setTargetname(pool.getPoolname());
        restrictionStrategyAssignment.setVmsource(pool.getVmsource());
        restrictionStrategyAssignment.setAssignment(pool.getAssignment());
        restrictionStrategyAssignment.setMaxdesktops(pool.getMaxdesktops());
        restrictionStrategyAssignment.setNotes(pool.getNotes());
      }
    }
    return resourceRecords;
  }

  public void verifyDesktopByIPAddress(String ipaddress) throws CommonException {
    if(exists(databaseFacade.findFirst(
        "select count(iddesktop) from Desktop where ipaddress = ?",
        ipaddress))) {
      throw new CommonException(CommonException.DUPE_IP);
    }
    try {
      if (!VdiAgentClientImpl.getRDPStatus(ipaddress)) {
        throw new CommonException(CommonException.CONFLICT);
      }
    } catch (Exception e) {
      log.warn("Exception", e);
      throw new CommonException(CommonException.CONFLICT);
    }
  }

  public int addDesktopByIPAddress(int desktoppoolid, String ipaddress) throws CommonException {
    if (!userFacade.isSuperAdmin(sessionFacade.getCurrentSession().getUserid())) {
      throw new CommonException(FORBIDDEN);
    }
    DesktopPoolEntity desktopPool = databaseFacade.load(DesktopPoolEntity.class, desktoppoolid);
    if (desktopPool == null) {
      throw new CommonException(NOT_FOUND);
    }
    if (desktopPool.getVmsource() != DesktopPoolEntity.DESKTOP_POOL_SOURCE_MANUAL) {
      throw new CommonException(CONFLICT);
    }
    DesktopPoolStatus desktopPoolStatus = databaseFacade.load(DesktopPoolStatus.class, desktoppoolid);
    if (desktopPoolStatus.getStatus() == DesktopPoolStatus.DESKTOPPOOL_STATUS_MAINTAIN) {
      throw new CommonException(CONFLICT);
    }
//    if (desktopPool.getMaxdesktops() <= (Long) databaseFacade.findFirst(
//        "select count(iddesktop) from Desktop where desktoppoolid = ?",
//        desktoppoolid)) {
//      throw new CommonException(CONFLICT);
//    }
    desktopPool.setMaxdesktops(desktopPool.getMaxdesktops() + 1);
    databaseFacade.merge(desktopPool);
    final String vmName = strcat(desktopPool.getVmnamepatterrn(), "-", desktopPool.getMaxdesktops() + 1);
    final boolean isAutoPool = numberEquals(desktopPool.getVmsource(), DESKTOP_POOL_SOURCE_AUTO);
    Desktop desktop = new Desktop();
    desktop.setDesktoppoolid(desktopPool.getIddesktoppool());
    desktop.setOwnerid(-1);
    desktop.setVmname(vmName);
    desktop.setIpaddress(isAutoPool ? (desktopPool.getIddesktoppool() + " " + vmName) : ipaddress);
    desktop.setDesktoppoolname(desktopPool.getPoolname());
    desktop.setVmsource(desktopPool.getVmsource());
    desktop.setAssignment(desktopPool.getAssignment());
    databaseFacade.persist(desktop);
    DesktopStatus desktopStatus = new DesktopStatus();
    desktopStatus.setIddesktop(desktop.getIddesktop());
    desktopStatus.setPhase(isAutoPool ? DesktopState.DESKTOP_PHASE_START : DesktopState.DESKTOP_PHASE_NORMAL);
    desktopStatus.setStatus(DesktopState.DESKTOP_STATUS_UNKNOWN);
    desktopStatus.setOwnerid(-1);
    try
    {
      databaseFacade.persist(desktopStatus);
    } catch (Exception e)
    {
      throw new CommonException(CommonException.DUPE_IP);
    }
    if (isAutoPool)
    {
      operationRegistry.start(new CloneInstanceOperation(desktoppoolid, desktopPool.getCloudmanagerid(), desktop.getIddesktop(), null));
    } else {
      if (desktopPool.getDomainname() != null) {
        try
        {
          VdiAgentClientImpl.joinDomain(
              ipaddress,
              desktopPool.getDomainname(),
              desktopPool.getDomainbinddn().replaceFirst("^.*\\\\", ""),
              desktopPool.getDomainbindpass(),
              true);
        } catch (Exception e)
        {
          // TODO Auto-generated catch block
          e.printStackTrace();
        }
      }
    }
    return desktop.getIddesktop();
  }

  public void preassignDesktop(int desktopid, int userid) throws CommonException {
    if(!exists(databaseFacade.update(
        "update DesktopStatus set ownerid = ? where iddesktop = ? and ownerid = ?",
        userid, desktopid, -1))) {
      throw new CommonException(CONFLICT);
    }
      User user = databaseFacade.load(User.class, userid);
      databaseFacade.update(
          "update Desktop set ownername = ? where iddesktop = ?",
          user.getUsername(),
          desktopid);
  }

  public void unassignDesktop(int desktopid, int userid, boolean force) throws CommonException {
    Desktop desktop = databaseFacade.load(Desktop.class, desktopid);
    DesktopStatus desktopStatus = databaseFacade.load(DesktopStatus.class, desktopid);
    if (desktopStatus.getOwnerid() != userid)
    {
      throw new CommonException(CONFLICT);
    }
    operationRegistry.start(
        new DeallocDesktopOperation(
            Integer.toHexString(desktop.getDesktoppoolid()) + "#" + Integer.toHexString(desktopid),
            force,
            null,
            null));
  }

  public void setDatabaseFacade(DatabaseFacade databaseFacade) {
    this.databaseFacade = databaseFacade;
  }

  public void setUserFacade(UserFacade userFacade) {
    this.userFacade = userFacade;
  }

  public void setResourceFacade(ResourceFacade resourceFacade) {
    this.resourceFacade = resourceFacade;
  }

  public void setAsyncJobFacade(AsyncJobFacade asyncJobFacade) {
    this.asyncJobFacade = asyncJobFacade;
  }

  public void setDesktopPoolFacade(DesktopPoolFacade desktopPoolFacade) {
    this.desktopPoolFacade = desktopPoolFacade;
  }

  public void setDesktopStatusFacade(DesktopStatusFacade desktopStatusFacade) {
    this.desktopStatusFacade = desktopStatusFacade;
  }

  public void setDesktopPoolStatusFacade(
      DesktopPoolStatusFacade desktopPoolStatusFacade) {
    this.desktopPoolStatusFacade = desktopPoolStatusFacade;
  }

  public void setSessionFacade(SessionFacade sessionFacade) {
    this.sessionFacade = sessionFacade;
  }

  public void setCloudManagerStatusFacade(
      CloudManagerStatusFacade cloudManagerStatusFacade) {
    this.cloudManagerStatusFacade = cloudManagerStatusFacade;
  }

  public void setOrganizationFacade(OrganizationFacade organizationFacade) {
    this.organizationFacade = organizationFacade;
  }

  public void setOperationRegistry(OperationRegistry operationRegistry)
  {
    this.operationRegistry = operationRegistry;
  }

  private int disconnectAndShutdown(
      final int cloudmanagerid,
      final int desktopPoolId,
      final int desktopid,
      final String vmid,
      final DesktopPoolEntity pool,
      final boolean byAgent,
      final boolean toReboot,
      final boolean toDestroy,
      final boolean toCloneAfterDestroy,
      final String templateidForClone,
      final String vmnameForClone,
      final String pcnameForClone,
      final int ownerForClone,
      final boolean toShutdown,
      final boolean force) {
    // TODO Try-catch all Exceptions ?
    if (log.isTraceEnabled()) {
      log.trace(String.format(
          "Trying to shut down: cloudmanagerid = %d, desktopPoolId = %d, desktop = %d, vmid = %s, toReboot = %b, toDestroy = %b, toCloneAfterDestroy = %b, templateidForClone = %s, vmnameForClone = %s, ownerForClone = %d",
          cloudmanagerid, desktopPoolId, desktopid, vmid, toReboot, toDestroy, toCloneAfterDestroy, templateidForClone, vmnameForClone, ownerForClone));
    }
    final DesktopStatus desktopStatus = desktopStatusFacade.findDesktopStatus(desktopid);
    if (numberEquals(desktopStatus.getStatus(), DesktopStatus.DESKTOP_STATUS_CONNECTED)
        && toDestroy && (!toCloneAfterDestroy) && (!force)) {
      return -1;
    }
    // TODO desktopStatus = null on deleteDesktopPool.
    if (desktopStatus != null) {
//      if (numberEquals(desktopStatus.getStatus(), DESKTOP_STATUS_CONNECTED)) {
        Integer destroyingJobId = resourceFacade.destroyConnectionsOfDesktop(desktopPoolId, desktopid, toCloneAfterDestroy, (!toReboot) && (!toDestroy));
        if (destroyingJobId != null) {
          return destroyingJobId;
        }
//      }
    }
    if (toDestroy) {
      if (toCloneAfterDestroy) {
        databaseFacade.update(
            "update Desktop set vmid = null where iddesktop = ?",
            desktopid);
        if (!exists(desktopPoolStatusFacade.markAsDestroying(desktopid, desktopPoolId))) {
          return -1;
        }
        desktopPoolStatusFacade.refreshDesktopPoolSparingAndAbnormalCount(desktopPoolId);
        final String[] oldvmname = new String[1];
        final boolean link =pool.getLink()==1?true:false;
        if (vmid != null) {
          syncWorkWithCloudManager(
              cloudManagerStatusFacade,
              databaseFacade.load(CloudManagerEntity.class, cloudmanagerid),
              new CloudManagerWorker() {
                @Override
                public int execute(CloudManager cloudManager, Object[] resultContainer, int jobid) throws Exception {
                  try {
                    VMInstance vm = cloudManager.getVM(vmid);
                    oldvmname[0] = vm.getName();
                  } catch (Exception e) {
                    log.warn("Exception", e);
                    cloudManagerStatusFacade.markAsAbnormal(cloudmanagerid);
                  }
                  return NO_ERRORS;
                }
              });
          asyncWorkWithCloudManager(
              cloudManagerStatusFacade,
              asyncJobFacade,
              databaseFacade.load(CloudManagerEntity.class, cloudmanagerid),
              null,
              desktopid,
              new CloudManagerWorker() {
                @Override
                public int execute(CloudManager cloudManager, Object[] resultContainer, int jobid) {
                  if (log.isTraceEnabled()) {
                    log.trace(String.format(
                        "Async job started to shut down: cloudmanagerid = %d, desktopPoolId = %d, desktop = %d, vmid = %s, toReboot = %b, toDestroy = %b, toCloneAfterDestroy = %b, templateidForClone = %s, vmnameForClone = %s, ownerForClone = %d",
                        cloudmanagerid, desktopPoolId, desktopid, vmid, toReboot, toDestroy, toCloneAfterDestroy, templateidForClone, vmnameForClone, ownerForClone));
                  }
                  resultContainer[0] = desktopid;
                  Job<?> cloudJob;
                  if (vmid != null) {
                    if (pool.getDomainname() != null && pool.getDomainname().length() > 0) {
                      try {
                        cloudJob = cloudManager.joinWorkgroup(vmid, "WORKGROUP", pool.getDomainbinddn(), pool.getDomainbindpass(), false);
                        waitForResult(cloudManager, cloudJob);
                      } catch (Exception e) {
                        log.warn("Exception", e);
                        cloudJob = new Job<String>() {{ this.setStatus(JobStatus.FAILED); this.setError(HYPERVISOR_ABNORMAL); }};
                        cloudManagerStatusFacade.markAsAbnormal(cloudmanagerid);
                      }
                      if (cloudJob.getStatus() == JobStatus.FAILED) {
                        log.warn("joinWorkgroup failed: {} (toReboot = {})", desktopid, toReboot);
                      }
                    }
//                    desktopPoolStatusFacade.markAsStopping(desktopid, desktopPoolId);
                    // TODO Status by Agent.
//                    try {
//                      cloudJob = cloudManager.stopVM(vmid);
//                      waitForResult(cloudManager, cloudJob);
//                    } catch (Exception e) {
//                      log.warn("Exception", e);
//                      cloudJob = new Job<String>() {{ this.setStatus(JobStatus.FAILED); this.setError(HYPERVISOR_ABNORMAL); }};
//                      cloudManagerStatusFacade.markAsAbnormal(cloudmanagerid);
//                    }
//                    if (cloudJob.getStatus() == JobStatus.FAILED) {
//                      log.warn("disconnectAndShutdown failed: {} (toReboot = {})", desktopid, toReboot);
//                      // FIXME
//                      if (cloudJob.getError() == 0x80000107) {
//                        if (log.isTraceEnabled()) {
//                          log.trace(String.format(
//                              "Async job failed with CONFLICT to shut down: cloudmanagerid = %d, desktopPoolId = %d, desktop = %d, vmid = %s, toReboot = %b, toDestroy = %b, toCloneAfterDestroy = %b, templateidForClone = %s, vmnameForClone = %s, ownerForClone = %d",
//                              cloudmanagerid, desktopPoolId, desktopid, vmid, toReboot, toDestroy, toCloneAfterDestroy, templateidForClone, vmnameForClone, ownerForClone));
//                        }
//                        if (!toDestroy) {
//                          return CONFLICT;
//                        }
//                      }
//                    } else {
////                      desktopPoolStatusFacade.markAsStopped(desktopid, desktopPoolId);
//                    }
                    waitUntilStable(cloudManager, vmid);
                  }
                  log.trace("stop OK: {} (toReboot = {})", desktopid, toReboot);
                  if (vmid != null) {
                    try {
                      cloudJob = cloudManager.destroyVM(vmid);
                      waitForResult(cloudManager, cloudJob);
                    } catch (Exception e) {
                      log.warn("Exception", e);
                      cloudJob = new Job<String>() {{ this.setStatus(JobStatus.FAILED); this.setError(HYPERVISOR_ABNORMAL); }};
                      cloudManagerStatusFacade.markAsAbnormal(cloudmanagerid);
                    }
                    if (cloudJob.getStatus() == JobStatus.FAILED) {
                      log.warn("destroyVM failed: {}, {}", desktopid, vmid);
//                      desktopPoolStatusFacade.markAsError(desktopid, desktopPoolId);
                    } else {
                      log.trace("Destroy OK: {}, {}", desktopid, vmid);
                    }
                  }
                  if (log.isTraceEnabled()) {
                    log.trace(String.format(
                        "Async job to shut down done half-successfully: cloudmanagerid = %d, desktopPoolId = %d, desktop = %d, vmid = %s, toReboot = %b, toDestroy = %b, toCloneAfterDestroy = %b, templateidForClone = %s, vmnameForClone = %s, ownerForClone = %d",
                        cloudmanagerid, desktopPoolId, desktopid, vmid, toReboot, toDestroy, toCloneAfterDestroy, templateidForClone, vmnameForClone, ownerForClone));
                  }
                  return NO_ERRORS;
                }
              });
        }
        return asyncWorkWithCloudManager(
            cloudManagerStatusFacade,
            asyncJobFacade,
            databaseFacade.load(CloudManagerEntity.class, cloudmanagerid),
            "destroyDesktop",
            desktopid,
            new CloudManagerWorker() {
              @Override
              public int execute(CloudManager cloudManager, Object[] resultContainer, int jobid) {
                if (log.isTraceEnabled()) {
                  log.trace(String.format(
                      "Async job started to shut down (reclone): cloudmanagerid = %d, desktopPoolId = %d, desktop = %d, vmid = %s, toReboot = %b, toDestroy = %b, toCloneAfterDestroy = %b, templateidForClone = %s, vmnameForClone = %s, ownerForClone = %d",
                      cloudmanagerid, desktopPoolId, desktopid, vmid, toReboot, toDestroy, toCloneAfterDestroy, templateidForClone, vmnameForClone, ownerForClone));
                }
                resultContainer[0] = desktopid;
                desktopPoolStatusFacade.markAsProvisioning(desktopid, desktopPoolId);
                int[] errorContainer = new int[1];
                String vmid = DesktopFacade.this.cloneDesktop(
                    cloudmanagerid,
                    cloudManager,
                    desktopPoolId,
                    templateidForClone,
                    oldvmname[0],
                    vmnameForClone,
                    link,
                    errorContainer);
                if (vmid == null) {
                  if (log.isTraceEnabled()) {
                    log.trace(String.format(
                        "Async job failed with cloning to shut down: cloudmanagerid = %d, desktopPoolId = %d, desktop = %d, vmid = %s, toReboot = %b, toDestroy = %b, toCloneAfterDestroy = %b, templateidForClone = %s, vmnameForClone = %s, ownerForClone = %d",
                        cloudmanagerid, desktopPoolId, desktopid, vmid, toReboot, toDestroy, toCloneAfterDestroy, templateidForClone, vmnameForClone, ownerForClone));
                  }
                  desktopPoolStatusFacade.markAsError(desktopid, desktopPoolId);
                  return errorContainer[0];
                }
                if (desktopPoolFacade.poolNotExisting(desktopPoolId)) {
                  try {
                    cloudManager.destroyVM(vmid);
                  } catch (Exception e) {
                    log.warn("destroyVM failed: {}", vmid);
                    log.warn("Exception:", e);
                  }
                  return CONFLICT;
                }
                DesktopPoolEntity desktopPool = databaseFacade.load(DesktopPoolEntity.class, desktopPoolId);
                Desktop desktop = databaseFacade.load(Desktop.class, desktopid);
                desktop.setVmid(vmid == null ? "" : vmid);
                int error = afterClone(
                    cloudmanagerid,
                    cloudManager,
                    desktopPool,
                    desktop);
                if (numberNotEquals(error, NO_ERRORS)) {
                  if (log.isTraceEnabled()) {
                    log.trace(String.format(
                        "Async job failed with after cloning to shut down: cloudmanagerid = %d, desktopPoolId = %d, desktop = %d, vmid = %s, toReboot = %b, toDestroy = %b, toCloneAfterDestroy = %b, templateidForClone = %s, vmnameForClone = %s, ownerForClone = %d",
                        cloudmanagerid, desktopPoolId, desktopid, vmid, toReboot, toDestroy, toCloneAfterDestroy, templateidForClone, vmnameForClone, ownerForClone));
                  }
                  return error;
                }
                if (log.isTraceEnabled()) {
                  log.trace(String.format(
                      "Async job to shut down done successfully: cloudmanagerid = %d, desktopPoolId = %d, desktop = %d, vmid = %s, toReboot = %b, toDestroy = %b, toCloneAfterDestroy = %b, templateidForClone = %s, vmnameForClone = %s, ownerForClone = %d",
                      cloudmanagerid, desktopPoolId, desktopid, vmid, toReboot, toDestroy, toCloneAfterDestroy, templateidForClone, vmnameForClone, ownerForClone));
                }
                if (toShutdown) {
                  desktopPoolStatusFacade.markAsStopping(desktopid, desktopPoolId);
                  Job<?> cloudJob;
                  try {
                    cloudJob = cloudManager.stopVM(vmid);
                    waitForResult(cloudManager, cloudJob);
                  } catch (Exception e) {
                    log.warn("Exception", e);
                    cloudJob = new Job<String>() {{ this.setStatus(JobStatus.FAILED); this.setError(HYPERVISOR_ABNORMAL); }};
                    cloudManagerStatusFacade.markAsAbnormal(cloudmanagerid);
                  }
                  if (cloudJob.getStatus() == JobStatus.FAILED) {
                    log.warn("stopVM failed: {}, {}", desktopid, vmid);
                    desktopPoolStatusFacade.markAsError(desktopid, desktopPoolId);
                  } else {
                    log.trace("stopVM OK: {}, {}", desktopid, vmid);
                    desktopPoolStatusFacade.markAsStopped(desktopid, desktopPoolId);
                  }
                }
                return NO_ERRORS;
              }
            });
      } else {
        desktopStatusFacade.removeDesktopStatus(desktopid);
        databaseFacade.update(
            "delete from Desktop where iddesktop = ?",
            desktopid);
        if (!exists(databaseFacade.findFirst(
            "select count(iddesktop) from Desktop where desktoppoolid = ?",
            pool.getIddesktoppool()))) {
          databaseFacade.update(
              "delete from RestrictionStrategyAssignment where targettype = ? and targetid = ?",
              RestrictionStrategyAssignment.RESTRICTION_STRATEGY_ASSIGNMENT_TARGET_TYPE_RESOURCE, pool.getIddesktoppool());
          desktopPoolStatusFacade.removeDesktopPoolStatus(pool.getIddesktoppool());
          databaseFacade.update(
              "delete from DesktopPoolEntity where iddesktoppool = ?",
              pool.getIddesktoppool());
        }
        if (pool.getVmsource() == DesktopPoolEntity.DESKTOP_POOL_SOURCE_MANUAL) {
          return -1;
        } else {
          return asyncWorkWithCloudManager(
              cloudManagerStatusFacade,
              asyncJobFacade,
              databaseFacade.load(CloudManagerEntity.class, cloudmanagerid),
              null,
              desktopid,
              new CloudManagerWorker() {
                @Override
                public int execute(CloudManager cloudManager, Object[] resultContainer, int jobid) {
                  if (log.isTraceEnabled()) {
                    log.trace(String.format(
                        "Async job started to shut down: cloudmanagerid = %d, desktopPoolId = %d, desktop = %d, vmid = %s, toReboot = %b, toDestroy = %b, toCloneAfterDestroy = %b, templateidForClone = %s, vmnameForClone = %s, ownerForClone = %d",
                        cloudmanagerid, desktopPoolId, desktopid, vmid, toReboot, toDestroy, toCloneAfterDestroy, templateidForClone, vmnameForClone, ownerForClone));
                  }
                  resultContainer[0] = desktopid;
                  Job<?> cloudJob;
                  if (vmid != null) {
                    if (pool.getDomainname() != null && pool.getDomainname().length() > 0) {
                      try {
                        cloudJob = cloudManager.joinWorkgroup(vmid, "WORKGROUP", pool.getDomainbinddn(), pool.getDomainbindpass(), false);
                        waitForResult(cloudManager, cloudJob);
                      } catch (Exception e) {
                        log.warn("Exception", e);
                        cloudJob = new Job<String>() {{ this.setStatus(JobStatus.FAILED); this.setError(HYPERVISOR_ABNORMAL); }};
                        cloudManagerStatusFacade.markAsAbnormal(cloudmanagerid);
                      }
                      if (cloudJob.getStatus() == JobStatus.FAILED) {
                        log.warn("joinWorkgroup failed: {} (toReboot = {})", desktopid, toReboot);
                      }
                    }
//                    desktopPoolStatusFacade.markAsStopping(desktopid, desktopPoolId);
                    // TODO Status by Agent.
//                    try {
//                      cloudJob = cloudManager.stopVM(vmid);
//                      waitForResult(cloudManager, cloudJob);
//                    } catch (Exception e) {
//                      log.warn("Exception", e);
//                      cloudJob = new Job<String>() {{ this.setStatus(JobStatus.FAILED); this.setError(HYPERVISOR_ABNORMAL); }};
//                      cloudManagerStatusFacade.markAsAbnormal(cloudmanagerid);
//                    }
//                    if (cloudJob.getStatus() == JobStatus.FAILED) {
//                      log.warn("disconnectAndShutdown failed: {} (toReboot = {})", desktopid, toReboot);
//                      // FIXME
//                      if (cloudJob.getError() == 0x80000107) {
//                        if (log.isTraceEnabled()) {
//                          log.trace(String.format(
//                              "Async job failed with CONFLICT to shut down: cloudmanagerid = %d, desktopPoolId = %d, desktop = %d, vmid = %s, toReboot = %b, toDestroy = %b, toCloneAfterDestroy = %b, templateidForClone = %s, vmnameForClone = %s, ownerForClone = %d",
//                              cloudmanagerid, desktopPoolId, desktopid, vmid, toReboot, toDestroy, toCloneAfterDestroy, templateidForClone, vmnameForClone, ownerForClone));
//                        }
//                      }
//                    } else {
////                      desktopPoolStatusFacade.markAsStopped(desktopid, desktopPoolId);
//                    }
                    waitUntilStable(cloudManager, vmid);
                  }
                  log.trace("stop OK: {} (toReboot = {})", desktopid, toReboot);
                  if (vmid != null) {
                    try {
                      cloudJob = cloudManager.destroyVM(vmid);
                      waitForResult(cloudManager, cloudJob);
                    } catch (Exception e) {
                      log.warn("Exception", e);
                      cloudJob = new Job<String>() {{ this.setStatus(JobStatus.FAILED); this.setError(HYPERVISOR_ABNORMAL); }};
                      cloudManagerStatusFacade.markAsAbnormal(cloudmanagerid);
                    }
                    if (cloudJob.getStatus() == JobStatus.FAILED) {
                      log.warn("destroyVM failed: {}, {}", desktopid, vmid);
//                      desktopPoolStatusFacade.markAsError(desktopid, desktopPoolId);
                    } else {
                      log.trace("Destroy OK: {}, {}", desktopid, vmid);
                    }
                  }
                  if (log.isTraceEnabled()) {
                    log.trace(String.format(
                        "Async job to shut down done successfully: cloudmanagerid = %d, desktopPoolId = %d, desktop = %d, vmid = %s, toReboot = %b, toDestroy = %b, toCloneAfterDestroy = %b, templateidForClone = %s, vmnameForClone = %s, ownerForClone = %d",
                        cloudmanagerid, desktopPoolId, desktopid, vmid, toReboot, toDestroy, toCloneAfterDestroy, templateidForClone, vmnameForClone, ownerForClone));
                  }
                  return NO_ERRORS;
                }
              });
        }
      }
    } else {
      if (pool.getVmsource() == DesktopPoolEntity.DESKTOP_POOL_SOURCE_MANUAL) {
        AsyncJobThread thread = new AsyncJobThread() {
          @Override
          public void run() {
            Desktop desktop = databaseFacade.load(Desktop.class, desktopid);
            if (log.isTraceEnabled()) {
              log.trace(String.format(
                  "Async job started to shut down: cloudmanagerid = %d, desktopPoolId = %d, desktop = %d, vmid = %s, toReboot = %b, toDestroy = %b, toCloneAfterDestroy = %b, templateidForClone = %s, vmnameForClone = %s, ownerForClone = %d",
                  cloudmanagerid, desktopPoolId, desktopid, desktop.getIpaddress(), toReboot, toDestroy, toCloneAfterDestroy, templateidForClone, vmnameForClone, ownerForClone));
            }
            Job<?> cloudJob;
            desktopPoolStatusFacade.markAsStopping(desktopid, desktopPoolId);
            // TODO Status by Agent.
            try {
              if (toReboot) {
                cloudJob = VdiAgentClientImpl.restartSystem(desktop.getIpaddress());
                waitForAgentResult(cloudJob);
              } else {
                VdiAgentClientImpl.shutdownSystem(desktop.getIpaddress());
                cloudJob = new Job<String>() {{ this.setStatus(JobStatus.SUCCESSFUL); this.setError(NO_ERRORS); }};
              }
            } catch (Exception e) {
              log.warn("Exception", e);
              cloudJob = new Job<String>() {{ this.setStatus(JobStatus.FAILED); this.setError(HYPERVISOR_ABNORMAL); }};
            }
            if (cloudJob.getStatus() == JobStatus.FAILED) {
              log.warn("disconnectAndShutdown failed: {} (toReboot = {})", desktopid, toReboot);
              desktopPoolStatusFacade.markAsError(
                  desktopid,
                  desktopPoolId);
              if (log.isTraceEnabled()) {
                log.trace(String.format(
                    "Async job failed with UNKNOWN to shut down: cloudmanagerid = %d, desktopPoolId = %d, desktop = %d, vmid = %s, toReboot = %b, toDestroy = %b, toCloneAfterDestroy = %b, templateidForClone = %s, vmnameForClone = %s, ownerForClone = %d",
                    cloudmanagerid, desktopPoolId, desktopid, desktop.getIpaddress(), toReboot, toDestroy, toCloneAfterDestroy, templateidForClone, vmnameForClone, ownerForClone));
              }
              createNotification(toReboot ? 3: 2, desktopid, true);
              asyncJobFacade.finishAsyncJob(this.getJobId(), HYPERVISOR_ABNORMAL, desktopid);
              return;
            } else {
              if (toReboot) {
                desktopPoolStatusFacade.markAsRunning(desktopid);
                log.trace("Querying {} for RDP start.", desktop.getIpaddress());
                int error = waitForRDP(desktop.getIpaddress());
                if (numberNotEquals(error, NO_ERRORS)) {
                  desktopPoolStatusFacade.markAsError(desktopid, desktopPoolId);
                  createNotification(3, desktopid, true);
                  asyncJobFacade.finishAsyncJob(this.getJobId(), HYPERVISOR_ABNORMAL, desktopid);
                  return;
                }
                log.trace("Querying {} for RDP is OK.", desktop.getIpaddress());
                desktopPoolStatusFacade.markAsServing(desktopid, desktopPoolId);
                createNotification(3, desktopid, false);
                asyncJobFacade.finishAsyncJob(this.getJobId(), NO_ERRORS, desktopid);
              } else {
                desktopPoolStatusFacade.markAsStopped(desktopid, desktopPoolId);
                createNotification(2, desktopid, false);
                asyncJobFacade.finishAsyncJob(this.getJobId(), NO_ERRORS, desktopid);
              }
            }
            log.trace("stop OK: {} (toReboot = {})", desktopid, toReboot);
            if (log.isTraceEnabled()) {
              log.trace(String.format(
                  "Async job to shut down done successfully: cloudmanagerid = %d, desktopPoolId = %d, desktop = %d, vmid = %s, toReboot = %b, toDestroy = %b, toCloneAfterDestroy = %b, templateidForClone = %s, vmnameForClone = %s, ownerForClone = %d",
                  cloudmanagerid, desktopPoolId, desktopid, desktop.getIpaddress(), toReboot, toDestroy, toCloneAfterDestroy, templateidForClone, vmnameForClone, ownerForClone));
            }
          }
        };
        int jobId = asyncJobFacade.saveAsyncJob(toReboot ? "rebootDesktop" : "stopDesktop", desktopid, thread.getId());
        thread.setJobId(jobId);
        com.opzoon.ohvc.session.ExcecutorUtil.execute(thread);
        return jobId;
      } else {
        return asyncWorkWithCloudManager(
            cloudManagerStatusFacade,
            asyncJobFacade,
            databaseFacade.load(CloudManagerEntity.class, cloudmanagerid),
            toReboot ? "rebootDesktop" : "stopDesktop",
            desktopid,
            new CloudManagerWorker() {
              @Override
              public int execute(CloudManager cloudManager, Object[] resultContainer, int jobid) {
                if (log.isTraceEnabled()) {
                  log.trace(String.format(
                      "Async job started to shut down: cloudmanagerid = %d, desktopPoolId = %d, desktop = %d, vmid = %s, toReboot = %b, toDestroy = %b, toCloneAfterDestroy = %b, templateidForClone = %s, vmnameForClone = %s, ownerForClone = %d",
                      cloudmanagerid, desktopPoolId, desktopid, vmid, toReboot, toDestroy, toCloneAfterDestroy, templateidForClone, vmnameForClone, ownerForClone));
                }
                resultContainer[0] = desktopid;
                Job<?> cloudJob;
                if (vmid != null) {
                  desktopPoolStatusFacade.markAsStopping(desktopid, desktopPoolId);
                  // TODO Status by Agent.
                  try {
                    if ((!toReboot) && byAgent) {
                      cloudManager.shutdownSystem(vmid);
                      cloudJob = new Job<String>() {{ this.setStatus(JobStatus.SUCCESSFUL); this.setError(NO_ERRORS); }};
                    } else {
                      cloudJob = 
                          toReboot ?
                              (byAgent ? cloudManager.restartSystem(vmid) : cloudManager.stopVM(vmid)) :
                                cloudManager.stopVM(vmid);
                      waitForResult(cloudManager, cloudJob);
                    }
                  } catch (Exception e) {
                    log.warn("Exception", e);
                    cloudJob = new Job<String>() {{ this.setStatus(JobStatus.FAILED); this.setError(HYPERVISOR_ABNORMAL); }};
                    cloudManagerStatusFacade.markAsAbnormal(cloudmanagerid);
                  }
                  if (cloudJob.getStatus() == JobStatus.FAILED) {
                    log.warn("disconnectAndShutdown failed: {} (toReboot = {})", desktopid, toReboot);
                    // FIXME
                    if (cloudJob.getError() == 0x80000107) {
                      if (log.isTraceEnabled()) {
                        log.trace(String.format(
                            "Async job failed with CONFLICT to shut down: cloudmanagerid = %d, desktopPoolId = %d, desktop = %d, vmid = %s, toReboot = %b, toDestroy = %b, toCloneAfterDestroy = %b, templateidForClone = %s, vmnameForClone = %s, ownerForClone = %d",
                            cloudmanagerid, desktopPoolId, desktopid, vmid, toReboot, toDestroy, toCloneAfterDestroy, templateidForClone, vmnameForClone, ownerForClone));
                      }
                      if (!toDestroy) {
                        desktopPoolStatusFacade.markAsError(
                            desktopid,
                            desktopPoolId);
                        createNotification(toReboot ? 3: 2, desktopid, true);
                        return CONFLICT;
                      }
                    } else {
                      desktopPoolStatusFacade.markAsError(
                          desktopid,
                          desktopPoolId);
                      if (log.isTraceEnabled()) {
                        log.trace(String.format(
                            "Async job failed with UNKNOWN to shut down: cloudmanagerid = %d, desktopPoolId = %d, desktop = %d, vmid = %s, toReboot = %b, toDestroy = %b, toCloneAfterDestroy = %b, templateidForClone = %s, vmnameForClone = %s, ownerForClone = %d",
                            cloudmanagerid, desktopPoolId, desktopid, vmid, toReboot, toDestroy, toCloneAfterDestroy, templateidForClone, vmnameForClone, ownerForClone));
                      }
                      createNotification(toReboot ? 3: 2, desktopid, true);
                      return HYPERVISOR_ABNORMAL;
                    }
                  } else {
                    if (toReboot) {
                      if (!byAgent) {
                        desktopPoolStatusFacade.markAsStarting(desktopid, desktopPoolId);
                        try {
                          cloudJob = cloudManager.startVM(vmid);
                          waitForResult(cloudManager, cloudJob);
                        } catch (Exception e) {
                          log.warn("Exception", e);
                          cloudJob = new Job<String>() {{ this.setStatus(JobStatus.FAILED); this.setError(HYPERVISOR_ABNORMAL); }};
                          cloudManagerStatusFacade.markAsAbnormal(cloudmanagerid);
                        }
                        if (cloudJob.getStatus() == JobStatus.FAILED) {
                          log.warn("disconnectAndShutdown failed: {} (toReboot = {}, start)", desktopid, toReboot);
                          // FIXME
                          if (cloudJob.getError() == 0x80000107) {
                            if (log.isTraceEnabled()) {
                              log.trace(String.format(
                                  "Async job failed with CONFLICT to shut down: cloudmanagerid = %d, desktopPoolId = %d, desktop = %d, vmid = %s, toReboot = %b, toDestroy = %b, toCloneAfterDestroy = %b, templateidForClone = %s, vmnameForClone = %s, ownerForClone = %d",
                                  cloudmanagerid, desktopPoolId, desktopid, vmid, toReboot, toDestroy, toCloneAfterDestroy, templateidForClone, vmnameForClone, ownerForClone));
                            }
                            createNotification(3, desktopid, true);
                            return CONFLICT;
                          } else {
                            desktopPoolStatusFacade.markAsError(
                                desktopid,
                                desktopPoolId);
                            createNotification(3, desktopid, true);
                            return HYPERVISOR_ABNORMAL;
                          }
                        }
                      }
                      desktopPoolStatusFacade.markAsRunning(desktopid);
                      log.trace("Querying {} for RDP start.", vmid);
                      int error = waitForRDP(cloudManager, vmid);
                      if (numberNotEquals(error, NO_ERRORS)) {
                        cloudManagerStatusFacade.markAsAbnormal(cloudmanagerid);
                        desktopPoolStatusFacade.markAsError(desktopid, desktopPoolId);
                        createNotification(3, desktopid, true);
                        return HYPERVISOR_ABNORMAL;
                      }
                      log.trace("Querying {} for RDP is OK.", vmid);
                      desktopPoolStatusFacade.markAsServing(desktopid, desktopPoolId);
                      createNotification(3, desktopid, false);
                    } else {
                      desktopPoolStatusFacade.markAsStopped(desktopid, desktopPoolId);
                      createNotification(2, desktopid, false);
                    }
                  }
                }
                log.trace("stop OK: {} (toReboot = {})", desktopid, toReboot);
                if (log.isTraceEnabled()) {
                  log.trace(String.format(
                      "Async job to shut down done successfully: cloudmanagerid = %d, desktopPoolId = %d, desktop = %d, vmid = %s, toReboot = %b, toDestroy = %b, toCloneAfterDestroy = %b, templateidForClone = %s, vmnameForClone = %s, ownerForClone = %d",
                      cloudmanagerid, desktopPoolId, desktopid, vmid, toReboot, toDestroy, toCloneAfterDestroy, templateidForClone, vmnameForClone, ownerForClone));
                }
                return NO_ERRORS;
              }
            });
      }
    }
  }

  private int setHostName(CloudManager cloudManager, DesktopPoolEntity desktopPool, String vmid, String vmName) {
    int error = waitForRDP(cloudManager, vmid);
    if (numberNotEquals(error, NO_ERRORS)) {
      cloudManagerStatusFacade.markAsAbnormal(desktopPool.getCloudmanagerid());
      return HYPERVISOR_ABNORMAL;
    }
    Job<?> setHostNameJob = null;
    try {
      log.trace("Trying to setHostname {}.", vmid);
      setHostNameJob = cloudManager.setHostname(
          vmid,
          guessPcname(desktopPool.getComputernamepattern(), vmName),
          2,// TODO Domain.
          "",
          "",
          true);
      waitForResult(cloudManager, setHostNameJob);
    } catch (Exception e) {
      log.warn("setHostName failed", e);
      setHostNameJob = new Job<String>() {{ this.setStatus(JobStatus.FAILED); this.setError(HYPERVISOR_ABNORMAL); }};
      cloudManagerStatusFacade.markAsAbnormal(desktopPool.getCloudmanagerid());
    }
    if (setHostNameJob.getStatus() == JobStatus.FAILED) {
      log.warn("setHostName failed: {} ({})", vmid, setHostNameJob.getError());
      return HYPERVISOR_ABNORMAL;
    }
    log.trace("setHostname OK {}.", vmid);
    return NO_ERRORS;
  }

  private int joinDomain(CloudManager cloudManager, DesktopPoolEntity desktopPool, String vmid, String vmName) {
    int error = waitForRDP(cloudManager, vmid);
    if (numberNotEquals(error, NO_ERRORS)) {
      cloudManagerStatusFacade.markAsAbnormal(desktopPool.getCloudmanagerid());
      return HYPERVISOR_ABNORMAL;
    }
    Job<?> joinDomainJob = null;
    try {
      log.trace("Trying to joinDomain {}.", vmid);
      joinDomainJob = cloudManager.joinDomain(
          vmid,
          desktopPool.getDomainname(),
          desktopPool.getDomainbinddn().replaceFirst("^.*\\\\", ""),
          desktopPool.getDomainbindpass(),
          true);
      waitForResult(cloudManager, joinDomainJob);
    } catch (Exception e) {
      log.warn("joinDomain failed", e);
      joinDomainJob = new Job<String>() {{ this.setStatus(JobStatus.FAILED); this.setError(HYPERVISOR_ABNORMAL); }};
      cloudManagerStatusFacade.markAsAbnormal(desktopPool.getCloudmanagerid());
    }
    if (joinDomainJob.getStatus() == JobStatus.FAILED) {
      log.warn("joinDomain failed: {} ({})", vmid, joinDomainJob.getError());
      return HYPERVISOR_ABNORMAL;
    }
    log.trace("joinDomain OK {}.", vmid);
    return NO_ERRORS;
  }

  private int createUser(String ipaddress, String username, String password) {
    Job<?> job = null;
    try {
      log.trace("Trying to createUser {}.", ipaddress);
      job = VdiAgentClientImpl.createUser(ipaddress, username, password);
      waitForAgentResult(job);
    } catch (Exception e) {
      log.warn("createUser failed", e);
      job = new Job<String>() {{ this.setStatus(JobStatus.FAILED); this.setError(HYPERVISOR_ABNORMAL); }};
    }
    if (job.getStatus() == JobStatus.FAILED) {
      log.warn("createUser failed: {} ({})", ipaddress, job.getError());
      return numberEquals(job.getError(), NO_ERRORS) ? HYPERVISOR_ABNORMAL : job.getError();
    }
    log.trace("createUser OK {}.", ipaddress);
    return NO_ERRORS;
  }

  private int updateUserPassword(String ipaddress, String username, String password) {
    Job<?> job = null;
    try {
      log.trace("Trying to updateUserPassword {}.", ipaddress);
      job = VdiAgentClientImpl.updateUserPassword(ipaddress, username, password);
      waitForAgentResult(job);
    } catch (Exception e) {
      log.warn("updateUserPassword failed", e);
      job = new Job<String>() {{ this.setStatus(JobStatus.FAILED); this.setError(HYPERVISOR_ABNORMAL); }};
    }
    if (job.getStatus() == JobStatus.FAILED) {
      log.warn("updateUserPassword failed: {} ({})", ipaddress, job.getError());
      return numberEquals(job.getError(), NO_ERRORS) ? HYPERVISOR_ABNORMAL : job.getError();
    }
    log.trace("updateUserPassword OK {}.", ipaddress);
    return NO_ERRORS;
  }

  private int deleteUser(final String ipaddress) {
    Job<?> job = null;
    try {
      log.trace("Trying to deleteUser {}.", ipaddress);
      job = VdiAgentClientImpl.deleteUser(ipaddress, null, null);
      waitForAgentResult(job);
    } catch (Exception e) {
      log.warn("deleteUser failed", e);
      job = new Job<String>() {{ this.setStatus(JobStatus.FAILED); this.setError(HYPERVISOR_ABNORMAL); }};
    }
    if (job.getStatus() == JobStatus.FAILED) {
      log.warn("deleteUser failed: {} ({})", ipaddress, job.getError());
      return numberEquals(job.getError(), NO_ERRORS) ? HYPERVISOR_ABNORMAL : job.getError();
    }
    log.trace("deleteUser OK {}.", ipaddress);
    return NO_ERRORS;
  }

  private int deleteUserProfile(final String ipaddress, final String domain, final String username) {
    Job<?> job = null;
    try {
      log.trace("Trying to deleteUserProfile {}.", ipaddress);
      job = VdiAgentClientImpl.deleteUserProfile(ipaddress, domain, username);
      waitForAgentResult(job);
    } catch (Exception e) {
      log.warn("deleteUserProfile failed", e);
      job = new Job<String>() {{ this.setStatus(JobStatus.FAILED); this.setError(HYPERVISOR_ABNORMAL); }};
    }
    if (job.getStatus() == JobStatus.FAILED) {
      log.warn("deleteUserProfile failed: {} ({})", ipaddress, job.getError());
      return numberEquals(job.getError(), NO_ERRORS) ? HYPERVISOR_ABNORMAL : job.getError();
    }
    log.trace("deleteUserProfile OK {}.", ipaddress);
    return NO_ERRORS;
  }

  private int doCreateUserVolume(final int cloudmanagerid, final int userid, final long size, final String volumename) {
    if (userFacade.checkIfHavingVolume(userid)) {
      return CONFLICT;
    }
    return syncWorkWithCloudManager(
        cloudManagerStatusFacade,
        databaseFacade.load(CloudManagerEntity.class, cloudmanagerid),
        new CloudManagerWorker() {
          @Override
          public int execute(CloudManager cloudManager, Object[] resultContainer, int jobid) throws Exception {
            Job<?> cloudJob;
            try {
              log.trace("Trying to create volume of {}.", size);
              cloudJob = cloudManager.createVolume(volumename, (int) (size / (1024 * 1024 * 1024)));
              waitForResult(cloudManager, cloudJob);
            } catch (Exception e) {
              log.warn("Exception", e);
              cloudJob = new Job<String>() {{ this.setStatus(JobStatus.FAILED); this.setError(HYPERVISOR_ABNORMAL); }};
              cloudManagerStatusFacade.markAsAbnormal(cloudmanagerid);
            }
            if (cloudJob.getStatus() == JobStatus.FAILED) {
              log.warn("createVolume failed: {}", cloudmanagerid);
              // TODO Adapter getError.
              return numberEquals(cloudJob.getError(), NO_ERRORS) ? HYPERVISOR_ABNORMAL : cloudJob.getError();
            }
            log.trace("createVolume OK: {}", cloudmanagerid);
            Volume volume = (Volume) cloudJob.getResult();
            userFacade.createUserVolume(userid, cloudmanagerid, volumename, volume.getId(), size);
            return NO_ERRORS;
          }
        });
  }

  public int doRefreshDesktopStatus(CloudManager cloudManager, int cloudmanagerid, Desktop desktop) {
    int status = DesktopState.DESKTOP_STATUS_ERROR;
    if (cloudmanagerid < 1) {
      try {
        boolean rdpready = VdiAgentClientImpl.getRDPStatus(desktop.getIpaddress());
        status = rdpready ? DesktopState.DESKTOP_STATUS_SERVING : DesktopState.DESKTOP_STATUS_RUNNING;
      } catch (Exception e) {
        log.warn("Exception", e);
      }
    } else {
      try {
        VMInstance vm = cloudManager.getVM(desktop.getVmid());
        status = "running".equalsIgnoreCase(vm.getState()) ? DesktopState.DESKTOP_STATUS_RUNNING :
          ("starting".equalsIgnoreCase(vm.getState()) ? DesktopState.DESKTOP_STATUS_STARTING :
            "stopping".equalsIgnoreCase(vm.getState()) ? DesktopState.DESKTOP_STATUS_STOPPING :
              "stopped".equalsIgnoreCase(vm.getState()) ? DesktopState.DESKTOP_STATUS_STOPPED :
                DesktopState.DESKTOP_STATUS_ERROR);
        if (status == DesktopState.DESKTOP_STATUS_RUNNING) {
          log.trace("Querying {} for RDP start.", desktop.getVmid());
          boolean ready = false;
          try
          {
            ready = cloudManager.getRdpStatus(desktop.getVmid());
          } catch (Exception e)
          {
            // TODO Auto-generated catch block
            e.printStackTrace();
          }
          log.trace("Querying {} for RDP is OK.", desktop.getVmid());
          status = ready ? DesktopState.DESKTOP_STATUS_SERVING : DesktopState.DESKTOP_STATUS_RUNNING;
        }
      } catch (Exception e) {
        log.warn("getVM failed: {}", cloudManager);
        log.warn("Exception", e);
      }
    }
    desktopStatusFacade.refreshDesktopStatus(desktop, status);
    return NO_ERRORS;
  }

  private void createNotification(int minor, int desktopid, boolean failed) {
    Desktop desktop = databaseFacade.load(Desktop.class, desktopid);
    if (desktop.getOwnerid() == -1) {
      return;
    }
    List<Integer> sessions = sessionFacade.findSessionsByUserId(desktop.getOwnerid());
    for (Integer sessionid : sessions) {
      Session session = databaseFacade.load(Session.class, sessionid);
      if (session.getLogintype() == Session.LOGIN_TYPE_USER) {
        DesktopPoolEntity desktopPool = databaseFacade.load(DesktopPoolEntity.class, desktop.getDesktoppoolid());
        Notification notification = new Notification();
        notification.setMajor(5);
        notification.setMinor(minor);
        notification.setLevel(failed ? 2 : 3);
        notification.setUserid(session.getUserid());
        notification.setSessionid(sessionid);
        notification.setParameter(desktopPool.getPoolname());
        databaseFacade.persist(notification);
        return;
      }
    }
  }
  
  /**
   * 平台ID和虚拟机.
   */
  public static class CloudManagerIdAndVM {

    private int error;
    private int idcloudmanager;
    private String vmid;
    private String vmname;
    private String pcname;
    private String templateid;
    
    public int getError() {
      return error;
    }
    public void setError(int error) {
      this.error = error;
    }
    public int getIdcloudmanager() {
      return idcloudmanager;
    }
    public void setIdcloudmanager(int idcloudmanager) {
      this.idcloudmanager = idcloudmanager;
    }
    public String getVmid() {
      return vmid;
    }
    public void setVmid(String vmid) {
      this.vmid = vmid;
    }
    public String getVmname() {
      return vmname;
    }
    public void setVmname(String vmname) {
      this.vmname = vmname;
    }
    public String getPcname() {
      return pcname;
    }
    public void setPcname(String pcname) {
      this.pcname = pcname;
    }
    public String getTemplateid() {
      return templateid;
    }
    public void setTemplateid(String templateid) {
      this.templateid = templateid;
    }
    
  }

  public int assignRestrictionStrategy(int strategyid, int targettype,
      int targetid) throws CommonException {
    if (numberEquals(targettype, RestrictionStrategyAssignment.RESTRICTION_STRATEGY_ASSIGNMENT_TARGET_TYPE_ORGANIZATION)) {
      if (!userFacade.isSuperAdmin(sessionFacade.getCurrentSession().getUserid())) {
        if (!userFacade.canManageOrganization(targetid)) {
          return FORBIDDEN;
        }
      }
    } else if (numberEquals(targettype, RestrictionStrategyAssignment.RESTRICTION_STRATEGY_ASSIGNMENT_TARGET_TYPE_GROUP)) {
      if (!userFacade.isSuperAdmin(sessionFacade.getCurrentSession().getUserid())) {
        if (!userFacade.canManageGroup(targetid)) {
          return FORBIDDEN;
        }
      }
    } else if (numberEquals(targettype, RestrictionStrategyAssignment.RESTRICTION_STRATEGY_ASSIGNMENT_TARGET_TYPE_USER)) {
      if (!userFacade.isSuperAdmin(sessionFacade.getCurrentSession().getUserid())) {
        if (!userFacade.canManageUser(targetid)) {
          return FORBIDDEN;
        }
      }
    } else {
      if (!userFacade.isSuperAdmin(sessionFacade.getCurrentSession().getUserid())) {
        return FORBIDDEN;
      }
    }
    if (exists(databaseFacade.update(
        "update RestrictionStrategyAssignment set restrictionstrategyid = ? where targettype = ? and targetid = ?",
        strategyid, targettype, targetid)))
    {
      return NO_ERRORS;
    }
    RestrictionStrategyAssignment restrictionStrategyAssignment = new RestrictionStrategyAssignment();
    restrictionStrategyAssignment.setTargetid(targetid);
    restrictionStrategyAssignment.setTargettype(targettype);
    restrictionStrategyAssignment.setRestrictionstrategyid(strategyid);
    restrictionStrategyAssignment.setDomainid(-1);
    switch (targettype) {
    case RestrictionStrategyAssignment.RESTRICTION_STRATEGY_ASSIGNMENT_TARGET_TYPE_GROUP:
      restrictionStrategyAssignment.setDomainid(databaseFacade.load(Group.class, targetid).getDomainid());
      break;

    case RestrictionStrategyAssignment.RESTRICTION_STRATEGY_ASSIGNMENT_TARGET_TYPE_ORGANIZATION:
      restrictionStrategyAssignment.setDomainid(databaseFacade.load(Organization.class, targetid).getDomainid());
      break;

    case RestrictionStrategyAssignment.RESTRICTION_STRATEGY_ASSIGNMENT_TARGET_TYPE_USER:
      restrictionStrategyAssignment.setDomainid(databaseFacade.load(User.class, targetid).getDomainid());
      break;

    default:
      DesktopPoolEntity pool = databaseFacade.load(DesktopPoolEntity.class, targetid);
      if (pool.getDomainname() == null) {
        restrictionStrategyAssignment.setDomainid(0);
      } else {
        Integer domainid = (Integer) databaseFacade.findFirst(
            "select iddomain from Domain where domainname = ?",
            pool.getDomainname());
        restrictionStrategyAssignment.setDomainid(domainid);
      }
      break;
    }
    try {
      databaseFacade.persist(restrictionStrategyAssignment);
    } catch (EntityExistsException e) {
      throw new CommonException(CONFLICT);
    }
    return NO_ERRORS;
  }

  public int unassignRestrictionStrategy(int strategyid, int targettype,
      int targetid) throws CommonException {
    if (numberEquals(targettype, RestrictionStrategyAssignment.RESTRICTION_STRATEGY_ASSIGNMENT_TARGET_TYPE_ORGANIZATION)) {
      if (!userFacade.isSuperAdmin(sessionFacade.getCurrentSession().getUserid())) {
        if (!userFacade.canManageOrganization(targetid)) {
          return FORBIDDEN;
        }
      }
    } else if (numberEquals(targettype, RestrictionStrategyAssignment.RESTRICTION_STRATEGY_ASSIGNMENT_TARGET_TYPE_GROUP)) {
      if (!userFacade.isSuperAdmin(sessionFacade.getCurrentSession().getUserid())) {
        if (!userFacade.canManageGroup(targetid)) {
          return FORBIDDEN;
        }
      }
    } else if (numberEquals(targettype, RestrictionStrategyAssignment.RESTRICTION_STRATEGY_ASSIGNMENT_TARGET_TYPE_USER)) {
      if (!userFacade.isSuperAdmin(sessionFacade.getCurrentSession().getUserid())) {
        if (!userFacade.canManageUser(targetid)) {
          return FORBIDDEN;
        }
      }
    } else {
      if (!userFacade.isSuperAdmin(sessionFacade.getCurrentSession().getUserid())) {
        return FORBIDDEN;
      }
    }
    if (numberEquals(targettype, RestrictionStrategyAssignment.RESTRICTION_STRATEGY_ASSIGNMENT_TARGET_TYPE_RESOURCE)) {

      databaseFacade.update(
          "update RestrictionStrategyAssignment set restrictionstrategyid = ? where targettype = ? and targetid = ? and restrictionstrategyid = ?",
          0, targettype, targetid, strategyid);
    } else
    {
      databaseFacade.update(
          "delete from RestrictionStrategyAssignment where targettype = ? and targetid = ? and restrictionstrategyid = ?",
          targettype, targetid, strategyid);
    }
    return NO_ERRORS;
  }

  public int deleteRestrictionStrategy(int strategyid) throws CommonException {
    databaseFacade.update(
        "update RestrictionStrategyAssignment set restrictionstrategyid = ? where targettype = ? and restrictionstrategyid = ?",
        0, RestrictionStrategyAssignment.RESTRICTION_STRATEGY_ASSIGNMENT_TARGET_TYPE_RESOURCE, strategyid);
    databaseFacade.update(
        "delete from RestrictionStrategyAssignment where restrictionstrategyid = ?",
        strategyid);
    databaseFacade.update(
        "delete from RestrictionStrategy where idrestrictionstrategy = ?",
        strategyid);
    return NO_ERRORS;
  }

  private void waitUntilStable(CloudManager cloudManager, String vmid) {
    while (true) {
      try {
        // TODO Handle unknown vm.
        VMInstance vm = cloudManager.getVM(vmid);
        if (vm == null) {
          log.trace("vm not found: {}", vmid);
          break;
        }
        log.trace("vm state = {}", vm.getState());
        if (vm.getState().equals("running")
            || vm.getState().equals("stopped")
            || vm.getState().equals("error")) {
          break;
        }
      } catch (Exception e) {
        log.trace("Exception in getVM", e);
      }
      try {
        Thread.sleep(5000);
      } catch (Exception e) {}
    }
  }

}
