package com.opzoon.vdi.core.facade;

import static com.opzoon.vdi.core.cloud.CloudManagerHelper.syncWorkWithCloudManager;
import static com.opzoon.vdi.core.domain.DesktopPoolEntity.DESKTOP_POOL_SOURCE_AUTO;
import static com.opzoon.vdi.core.domain.ResourceAssignment.RESOURCE_PERMISSION_ALLOWED;
import static com.opzoon.vdi.core.domain.ResourceAssignment.RESOURCE_TYPE_POOL;
import static com.opzoon.vdi.core.domain.ResourceAssignment.RESOURCE_VISITOR_TYPE_GROUP;
import static com.opzoon.vdi.core.domain.ResourceAssignment.RESOURCE_VISITOR_TYPE_ORGANIZATION;
import static com.opzoon.vdi.core.domain.ResourceAssignment.RESOURCE_VISITOR_TYPE_USER;
import static com.opzoon.vdi.core.facade.CommonException.CONFLICT;
import static com.opzoon.vdi.core.facade.CommonException.FORBIDDEN;
import static com.opzoon.vdi.core.facade.CommonException.NOT_FOUND;
import static com.opzoon.vdi.core.facade.CommonException.NO_ERRORS;
import static com.opzoon.vdi.core.facade.FacadeHelper.count;
import static com.opzoon.vdi.core.facade.FacadeHelper.exists;
import static com.opzoon.vdi.core.facade.FacadeHelper.pagingFind;
import static com.opzoon.vdi.core.util.ConditionUtils.numberEquals;
import static com.opzoon.vdi.core.util.ConditionUtils.numberNotEquals;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import javax.persistence.EntityExistsException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.opzoon.vdi.core.RunnableWithException;
import com.opzoon.vdi.core.cloud.CloudManager;
import com.opzoon.vdi.core.cloud.CloudManagerHelper.CloudManagerWorker;
import com.opzoon.vdi.core.domain.CloudManagerEntity;
import com.opzoon.vdi.core.domain.Desktop;
import com.opzoon.vdi.core.domain.DesktopPoolEntity;
import com.opzoon.vdi.core.domain.DesktopPoolStatus;
import com.opzoon.vdi.core.domain.DesktopStatus;
import com.opzoon.vdi.core.domain.Domain;
import com.opzoon.vdi.core.domain.Organization;
import com.opzoon.vdi.core.domain.ResourceAssignment;
import com.opzoon.vdi.core.domain.RestrictionStrategyAssignment;
import com.opzoon.vdi.core.domain.User;
import com.opzoon.vdi.core.domain.state.DesktopPoolState;
import com.opzoon.vdi.core.domain.state.DesktopState;
import com.opzoon.vdi.core.facade.FacadeHelper.PagingInfo;
import com.opzoon.vdi.core.facade.transience.AsyncJobFacade;
import com.opzoon.vdi.core.facade.transience.CloudManagerStatusFacade;
import com.opzoon.vdi.core.facade.transience.DesktopPoolStatusFacade;
import com.opzoon.vdi.core.facade.transience.DesktopStatusFacade;
import com.opzoon.vdi.core.facade.transience.SessionFacade;
import com.opzoon.vdi.core.operation.AdjustDesktopCountOperation;
import com.opzoon.vdi.core.operation.DeleteDesktopOperation;
import com.opzoon.vdi.core.operation.DeleteDesktopPoolOperation;
import com.opzoon.vdi.core.operations.OperationRegistry;

/**
 * 桌面池相关业务接口.
 */
public class DesktopPoolFacade {
	
	private static final Logger log = LoggerFactory.getLogger(DesktopPoolFacade.class);
	
	private final ConcurrentMap<Integer, BlockingQueue<Resizing>> resizingQueues = new ConcurrentHashMap<Integer, BlockingQueue<Resizing>>();

	private DatabaseFacade databaseFacade;
	private UserFacade userFacade;
  private GroupFacade groupFacade;
  private OrganizationFacade organizationFacade;
	private DesktopFacade desktopFacade;
	private AsyncJobFacade asyncJobFacade;
	private DesktopPoolStatusFacade desktopPoolStatusFacade;
	private DesktopStatusFacade desktopStatusFacade;
	private SessionFacade sessionFacade;
	private CloudManagerStatusFacade cloudManagerStatusFacade;
	private ResourceFacade resourceFacade;
  private OperationRegistry operationRegistry;

	public void init() {
    FacadeHelper.waitUntilDatabaseIsReady(databaseFacade);
//	  resourceFacade.destroyAllConnections();
		Thread thread = new Thread() {
			@Override
			public void run() {
		    resourceFacade.destroyUnforwardingConnections();
//				synchronizeAllDesktopPoolStatus();
			}
			@Override
			public String toString() {
				return super.toString() + "THREAD destroyUnforwardingConnections";
			}
		};
		com.opzoon.ohvc.session.ExcecutorUtil.execute(thread);
	}
	
	public void synchronizeAllDesktopPoolStatus() {
		@SuppressWarnings("unchecked")
		List<DesktopPoolEntity> pools = (List<DesktopPoolEntity>) databaseFacade.find(
				"from DesktopPoolEntity");
		for (final DesktopPoolEntity pool : pools) {
			List<Desktop> desktops = desktopFacade.findDesktops(pool.getIddesktoppool());
			for (final Desktop desktop : desktops) {
				// Useless ?
				/*
				if (pool.getAssignment() == DesktopPoolEntity.DESKTOP_POOL_ASSIGNMENT_FLOATING) {
					DesktopStatus status = desktopStatusFacade.findDesktopStatus(desktop.getIddesktop());
					if (status != null && status.getOwnerid() != -1) {
						desktopFacade.unassign(pool, desktop.getIddesktop(), status.getOwnerid());
					}
				}
				*/
				desktopFacade.refreshDesktopStatus(pool.getCloudmanagerid(), desktop);
			}
		}
	}
	
	/** TODO 这是原克隆逻辑(顺序克隆). 需要根据平台性能选择相应的逻辑.
	 * @throws CommonException */
	public int createDesktopPool(final DesktopPoolEntity desktopPool) throws CommonException {
		if(!userFacade.isSuperAdmin(sessionFacade.getCurrentSession().getUserid())) {
			return FORBIDDEN;
		}
		int error = this.fulfillDomain(desktopPool);
		if (numberNotEquals(error, NO_ERRORS)) {
			return error;
		}
		if (numberEquals(desktopPool.getVmsource(), DESKTOP_POOL_SOURCE_AUTO)) {
			error = syncWorkWithCloudManager(
					cloudManagerStatusFacade,
					databaseFacade.load(CloudManagerEntity.class, desktopPool.getCloudmanagerid()),
					new CloudManagerWorker() {
						@Override
						public int execute(CloudManager cloudManager, Object[] resultContainer, int jobid) throws Exception {
							List<com.opzoon.vdi.core.domain.Template> cloudTemplates = cloudManager.listTemplates();
							for (com.opzoon.vdi.core.domain.Template cloudTemplate : cloudTemplates) {
								if (cloudTemplate.getTemplateId().equals(desktopPool.getTemplateid())) {
									desktopPool.setTemplatename(cloudTemplate.getTemplatename());
									return NO_ERRORS;
								}
							}
							return NOT_FOUND;
						}
					});
			if (numberNotEquals(error, NO_ERRORS)) {
				return NOT_FOUND;
			}
		} else {
      desktopPool.setVmnamepatterrn(desktopPool.getPoolname());
      desktopPool.setComputernamepattern(desktopPool.getPoolname());
      desktopPool.setMaxdesktops(0);
      desktopPool.setAvailableprotocols(desktopPool.getAvailableprotocols() & 0x111);
		}
    databaseFacade.persist(desktopPool);
    DesktopPoolStatus desktopPoolStatus = new DesktopPoolStatus();
    desktopPoolStatus.setIddesktoppool(desktopPool.getIddesktoppool());
    desktopPoolStatus.setPhase(DesktopPoolState.DESKTOP_POOL_PHASE_NORMAL);
    desktopPoolStatus.setStatus(DesktopPoolState.DESKTOP_POOL_STATUS_FULL);
    desktopPoolStatus.setSparingdesktops(0);
    desktopPoolStatus.setAbnormaldesktops(0);
    databaseFacade.persist(desktopPoolStatus);
    RestrictionStrategyAssignment restrictionStrategyAssignment = new RestrictionStrategyAssignment();
    restrictionStrategyAssignment.setTargettype(RestrictionStrategyAssignment.RESTRICTION_STRATEGY_ASSIGNMENT_TARGET_TYPE_RESOURCE);
    restrictionStrategyAssignment.setTargetid(desktopPool.getIddesktoppool());
    restrictionStrategyAssignment.setRestrictionstrategyid(desktopPool.getStrategyid());
    restrictionStrategyAssignment.setDomainid(desktopPool.getDomainid());
    databaseFacade.persist(restrictionStrategyAssignment);
		return NO_ERRORS;
	}
	
	public void adjustDesktopCount(DesktopPoolEntity desktopPool) throws CommonException
	{
    operationRegistry.start(new AdjustDesktopCountOperation(desktopPool.getIddesktoppool(), desktopPool.getCloudmanagerid()));
	}

	/**
	 * 创建桌面池.
	 * 
	 * @param desktopPool 桌面池.
	 * @return 错误代码.<br />
	 *         {@link com.opzoon.vdi.core.facade.CommonException#NO_ERRORS}: 成功.
	 *         {@link com.opzoon.vdi.core.facade.CommonException#FORBIDDEN}: 权限不足.
	 */
	/*
	public int createDesktopPool(final DesktopPoolEntity desktopPool) {
		if(!userFacade.isSuperAdmin(sessionFacade.getCurrentSession().getUserid())) {
			return FORBIDDEN;
		}
		// 持久化桌面池.
		databaseFacade.persist(desktopPool);
		// 同步保存桌面池状态.
		try {
			desktopPoolStatusFacade.createNewDesktopPoolStatus(
					desktopPool.getIddesktoppool(),
					0,
					0);
		} catch (CommonException e) {
			log.warn("createNewDesktopPoolStatus failed.", e);
		}
		// 若为自动池, 直接根据模板初始化所有桌面.
		if (numberEquals(desktopPool.getVmsource(), DESKTOP_POOL_SOURCE_AUTO)) {
			asyncWorkWithCloudManager(
					asyncJobFacade,
					desktopPool.getCloudmanagerid(),
					null,
					new CloudManagerWorker() {
						@Override
						public int execute(CloudManager cloudManager, Object[] resultContainer) {
							List<String> readyForUsedOnCloning = new LinkedList<String>();
							List<String> namesToClone = new LinkedList<String>();
							readyForUsedOnCloning.add(desktopPool.getTemplateid());
							List<Desktop> clonedDesktops = new LinkedList<Desktop>();
							for (int i = 0, c = desktopPool.getMaxdesktops(); i < c; ++i) {
								// 拼接桌面名称.
								String vmName = strcat(desktopPool.getVmnamepatterrn(), "-", i + 1);
								namesToClone.add(vmName);
								if (numberEquals(i, c - 1) || numberEquals(namesToClone.size(), readyForUsedOnCloning.size())) {
									clonedDesktops.addAll(desktopFacade.cloneDesktops(
											cloudManager,
											desktopPool.getIddesktoppool(),
											readyForUsedOnCloning,
											namesToClone));
									readyForUsedOnCloning.clear();
									readyForUsedOnCloning.add(desktopPool.getTemplateid());
									for (Desktop desktop : clonedDesktops) {
										readyForUsedOnCloning.add(desktop.getVmid());
									}
									namesToClone.clear();
								}
							}
							for (Desktop desktop : clonedDesktops) {
								int sparing = desktopPoolStatusFacade.countSparing(desktopPool.getIddesktoppool());
								if (sparing >= desktopPool.getSparedesktops()) {
									break;
								}
								desktopFacade.start(cloudManager, desktop.getIddesktop());
							}
							return NO_ERRORS;
						}
					});
		}
		return NO_ERRORS;
	}*/

	public int updateDesktopPool(DesktopPoolEntity desktopPool) {
		if (!exists(databaseFacade.findFirst(
				"select count(iddesktoppool) from DesktopPoolEntity where iddesktoppool = ?",
				desktopPool.getIddesktoppool()))) {
			return NOT_FOUND;
		}
		if(!userFacade.isSuperAdmin(sessionFacade.getCurrentSession().getUserid())) {
			return FORBIDDEN;
		}
		StringBuilder updateClause = new StringBuilder("update DesktopPoolEntity set iddesktoppool = iddesktoppool");
		List<Object> params = new ArrayList<Object>();
		if (desktopPool.getPoolname() != null) {
			updateClause.append(", poolname = ?");
			params.add(desktopPool.getPoolname());
		}
		if (desktopPool.getNotes() != null) {
			updateClause.append(", notes = ?");
			params.add(desktopPool.getNotes());
		}
		if (desktopPool.getAvailableprotocols() != -1) {
			updateClause.append(", availableprotocols = ?");
			params.add(desktopPool.getAvailableprotocols());
		}
    if (desktopPool.getUnassignmentdelay() != -1) {
      updateClause.append(", unassignmentdelay = ?");
      params.add(desktopPool.getUnassignmentdelay());
    }
		updateClause.append(" where iddesktoppool = ?");
		params.add(desktopPool.getIddesktoppool());
		Object[] paramsArray = params.toArray();
		if (!exists(databaseFacade.update(updateClause.toString(), paramsArray))) {
			return NOT_FOUND;
		}
		if (desktopPool.getStrategyid() != null) {
			databaseFacade.update(
					"delete from RestrictionStrategyAssignment where targettype = ? and targetid = ?",
					RestrictionStrategyAssignment.RESTRICTION_STRATEGY_ASSIGNMENT_TARGET_TYPE_RESOURCE, desktopPool.getIddesktoppool());
			RestrictionStrategyAssignment restrictionStrategyAssignment = new RestrictionStrategyAssignment();
			restrictionStrategyAssignment.setTargettype(RestrictionStrategyAssignment.RESTRICTION_STRATEGY_ASSIGNMENT_TARGET_TYPE_RESOURCE);
			restrictionStrategyAssignment.setTargetid(desktopPool.getIddesktoppool());
			restrictionStrategyAssignment.setRestrictionstrategyid(desktopPool.getStrategyid());
			databaseFacade.persist(restrictionStrategyAssignment);
		}
		return NO_ERRORS;
	}

	/**
	 * 删除桌面池.
	 * 
	 * @param iddesktoppool 池ID.
	 * @return 错误代码.<br />
	 *         {@link com.opzoon.vdi.core.facade.CommonException#NO_ERRORS}: 成功;<br />
	 *         {@link com.opzoon.vdi.core.facade.CommonException#NOT_FOUND}: 桌面池不存在.
	 */
	public int deleteDesktopPool(final int iddesktoppool, boolean force) {
		final DesktopPoolEntity desktopPool = databaseFacade.load(DesktopPoolEntity.class, iddesktoppool);
		if (desktopPool == null) {
			return NOT_FOUND;
		}
//		@SuppressWarnings("unchecked")
//		final List<Desktop> desktops = (List<Desktop>) databaseFacade.find(
//				"from Desktop where desktoppoolid = ?",
//				desktopPool.getIddesktoppool());
//		for (Desktop desktop : desktops) {
//			final DesktopStatus desktopStatus = desktopStatusFacade.findDesktopStatus(desktop.getIddesktop());
//			if (desktopStatus.getStatus() == DesktopStatus.DESKTOP_STATUS_PROVISIONING) {
//				return CommonException.PROVISIONING;
//			}
//		}
		this.forceDeleteDesktopPool(desktopPool, force);
		return NO_ERRORS;
	}

	@SuppressWarnings("unchecked")
	public void deleteAllDesktopPools(final int idcloudmanager, final Runnable callback) {
		List<DesktopPoolEntity> pools = (List<DesktopPoolEntity>) databaseFacade.find(
				"from DesktopPoolEntity where cloudmanagerid = ?",
				idcloudmanager);
		for (DesktopPoolEntity pool : pools) {
			this.forceDeleteDesktopPool(pool, true);
		}
		callback.run();
	}

	/**
	 * 分页查询桌面池.
	 * 
	 * @param cloudmanagerid 平台ID. -1为忽略.
	 * @param pagingInfo 分页信息.
	 * @param amountContainer 查询结果的总数量的容器.
	 * @return 查询结果列表.
	 */
	@SuppressWarnings("unchecked")
	public List<DesktopPoolEntity> findDesktopPools(int cloudmanagerid, int desktoppoolid,
			PagingInfo pagingInfo, int[] amountContainer) {
		StringBuilder whereClause = new StringBuilder("from DesktopPoolEntity where 1 = 1");
		List<Object> params = new ArrayList<Object>();
		if (cloudmanagerid > -1) {
			whereClause.append(" and cloudmanagerid = ?");
			params.add(cloudmanagerid);
		}
		if (desktoppoolid > 0) {
			whereClause.append(" and iddesktoppool = ?");
			params.add(desktoppoolid);
		}
		whereClause.append(FacadeHelper.keyword(pagingInfo, params));
		Object[] paramsArray = params.toArray();
		count(databaseFacade, "iddesktoppool", whereClause, paramsArray, amountContainer);
		List<DesktopPoolEntity> pools = pagingFind(databaseFacade, whereClause, paramsArray, pagingInfo);
		for (Iterator<DesktopPoolEntity> iterator = pools.iterator(); iterator.hasNext(); ) {
			DesktopPoolEntity pool = (DesktopPoolEntity) iterator.next();
			pool.setCloudname((String) databaseFacade.findFirst(
					"select cloudname from CloudManagerEntity where idcloudmanager = ?",
					pool.getCloudmanagerid()));
			DesktopPoolStatus desktopPoolStatus = desktopPoolStatusFacade.findDesktopPoolStatus(pool.getIddesktoppool());
			if (desktopPoolStatus == null) {
				iterator.remove();
				continue;
			}
			pool.setStatus(
			    desktopPoolStatus.getPhase() == DesktopPoolState.DESKTOP_POOL_PHASE_DELETING ?
			        254 :// FIXME
                0);// FIXME
			pool.setSparingdesktops(desktopPoolStatus.getSparingdesktops());
			pool.setAbnormaldesktops(desktopPoolStatus.getAbnormaldesktops());
      int connecteddesktops = 0;
      int abnormaldesktops = 0;
      int havedbinds=0;
			List<Desktop> desktops = desktopFacade.findDesktops(pool.getIddesktoppool());
			for (Desktop desktop : desktops) {
				DesktopStatus desktopStatus = desktopStatusFacade.findDesktopStatus(desktop.getIddesktop());
				// TODO desktopStatus can be null after destroy?
				if (desktopStatus != null && desktopStatus.getConnected() == 1) {
					connecteddesktops++;
				}
        if (desktopStatus != null && 
            (desktopStatus.getPhase() == DesktopState.DESKTOP_PHASE_DEFICIENT
            || (desktopStatus.getPhase() == DesktopState.DESKTOP_PHASE_NORMAL
            && (desktopStatus.getStatus() == DesktopState.DESKTOP_STATUS_ERROR
                || desktopStatus.getStatus() == DesktopState.DESKTOP_STATUS_UNKNOWN)))) {
          abnormaldesktops++;
        }
        	if(desktopStatus.getOwnerid()!=-1){
        		havedbinds++;
        	}
			}
	  pool.setAvailabledesktops(pool.getMaxdesktops()-abnormaldesktops-havedbinds);
      pool.setConnecteddesktops(connecteddesktops);
      pool.setAbnormaldesktops(abnormaldesktops);
			if (pool.getDomainname() != null) {
				pool.setDomainid((Integer) databaseFacade.findFirst(
						"select iddomain from Domain where domainname = ?",
						pool.getDomainname()));
			} else {
				pool.setDomainid(Domain.DEFAULT_DOMAIN_ID);
			}
			pool.setStrategyid((Integer) databaseFacade.findFirst(
						"select restrictionstrategyid from RestrictionStrategyAssignment where targettype = ? and targetid = ?",
						RestrictionStrategyAssignment.RESTRICTION_STRATEGY_ASSIGNMENT_TARGET_TYPE_RESOURCE, pool.getIddesktoppool()));
		}
		return pools;
	}

	/**
	 * 把桌面池分配给用户.
	 * 
	 * @param desktoppoolid 池ID.
	 * @param userid 用户ID.
	 * @return 错误代码.<br />
	 *         {@link com.opzoon.vdi.core.facade.CommonException#NO_ERRORS}: 成功;<br />
	 *         {@link com.opzoon.vdi.core.facade.CommonException#CONFLICT}: 此关联已存在.
	 * @throws CommonException {@link com.opzoon.vdi.core.facade.CommonException#CONFLICT}: 此关联已存在.
	 */
	public int assignDesktopPoolToUser(int desktoppoolid, int userid) throws CommonException {
		if((!userFacade.isSuperAdmin(sessionFacade.getCurrentSession().getUserid()))
				&& (!userFacade.canManageUser(userid))) {
			return FORBIDDEN;
		}
		return assignDesktopPoolTo(desktoppoolid, userid, RESOURCE_VISITOR_TYPE_USER);
	}

	/**
	 * 取消分配给用户使用的桌面池.
	 * 
	 * @param desktoppoolid 池ID.
	 * @param userid 用户ID.
	 * @return 错误代码.<br />
	 *         {@link com.opzoon.vdi.core.facade.CommonException#NO_ERRORS}: 成功;<br />
	 *         {@link com.opzoon.vdi.core.facade.CommonException#NOT_FOUND}: 此用户未曾被分配给此桌面池.
	 */
	public int unassignDesktopPoolToUser(int desktoppoolid, int userid, boolean force) {
		if((!userFacade.isSuperAdmin(sessionFacade.getCurrentSession().getUserid()))
				&& (!userFacade.canManageUser(userid))) {
			return FORBIDDEN;
		}
		return unassignDesktopPoolTo(desktoppoolid, userid, RESOURCE_VISITOR_TYPE_USER, force);
	}

	/**
	 * 把桌面池分配给组.
	 * 
	 * @param desktoppoolid 池ID.
	 * @param userid 组ID.
	 * @return 错误代码.<br />
	 *         {@link com.opzoon.vdi.core.facade.CommonException#NO_ERRORS}: 成功;<br />
	 *         {@link com.opzoon.vdi.core.facade.CommonException#CONFLICT}: 此关联已存在.
	 * @throws CommonException {@link com.opzoon.vdi.core.facade.CommonException#CONFLICT}: 此关联已存在.
	 */
	public int assignDesktopPoolToGroup(int desktoppoolid, int groupid) throws CommonException {
		if((!userFacade.isSuperAdmin(sessionFacade.getCurrentSession().getUserid()))
				&& (!userFacade.canManageGroup(groupid))) {
			return FORBIDDEN;
		}
		return assignDesktopPoolTo(desktoppoolid, groupid, RESOURCE_VISITOR_TYPE_GROUP);
	}

	/**
	 * 取消分配给组使用的桌面池.
	 * 
	 * @param desktoppoolid 池ID.
	 * @param groupid 用户ID.
	 * @return 错误代码.<br />
	 *         {@link com.opzoon.vdi.core.facade.CommonException#NO_ERRORS}: 成功;<br />
	 *         {@link com.opzoon.vdi.core.facade.CommonException#NOT_FOUND}: 此组未曾被分配给此桌面池.
	 */
	public int unassignDesktopPoolToGroup(int desktoppoolid, int groupid, boolean force) {
		if((!userFacade.isSuperAdmin(sessionFacade.getCurrentSession().getUserid()))
				&& (!userFacade.canManageGroup(groupid))) {
			return FORBIDDEN;
		}
		return unassignDesktopPoolTo(desktoppoolid, groupid, RESOURCE_VISITOR_TYPE_GROUP, force);
	}

	public int assignDesktopPoolToOrganization(int desktoppoolid, int organizationid) throws CommonException {
		if((!userFacade.isSuperAdmin(sessionFacade.getCurrentSession().getUserid()))
				&& (!userFacade.canManageOrganization(organizationid))) {
			return FORBIDDEN;
		}
		return assignDesktopPoolTo(desktoppoolid, organizationid, RESOURCE_VISITOR_TYPE_ORGANIZATION);
	}

	public int unassignDesktopPoolToOrganization(int desktoppoolid, int organizationid, boolean force) {
		if((!userFacade.isSuperAdmin(sessionFacade.getCurrentSession().getUserid()))
				&& (!userFacade.canManageOrganization(organizationid))) {
			return FORBIDDEN;
		}
		return unassignDesktopPoolTo(desktoppoolid, organizationid, RESOURCE_VISITOR_TYPE_ORGANIZATION, force);
	}

  public void notifyRresizing(int desktoppoolid, int maxdesktops)
  {
    final DesktopPoolEntity desktopPool = databaseFacade.load(DesktopPoolEntity.class, desktoppoolid);
    try
    {
      operationRegistry.start(new AdjustDesktopCountOperation(desktoppoolid, desktopPool.getCloudmanagerid()));
    } catch (CommonException e1)
    {
      // TODO Auto-generated catch block
      e1.printStackTrace();
    }
  }

	public int resizeDesktopPool(final int desktoppoolid, int maxdesktops,
			int[] errorContainer) {
    final DesktopPoolEntity desktopPool = databaseFacade.load(DesktopPoolEntity.class, desktoppoolid);
    desktopPool.setMaxdesktops(maxdesktops);
    databaseFacade.merge(desktopPool);
    /**
		BlockingQueue<Resizing> resizingQueue = resizingQueues.get(desktoppoolid);
		if (resizingQueue == null) {
			BlockingQueue<Resizing> newResizingQueue = new LinkedBlockingQueue<Resizing>();
			resizingQueue = resizingQueues.putIfAbsent(desktoppoolid, newResizingQueue);
			if (resizingQueue == null) {
				resizingQueue = newResizingQueue;
				Thread thread = new Thread() {
					@Override
					public void run() {
						while (true) {
							Resizing resizing;
							try {
								resizing = resizingQueues.get(desktoppoolid).take();
							} catch (InterruptedException e) {
								log.warn("InterruptedException", e);
								continue;
							}
							final DesktopPoolEntity desktopPool = databaseFacade.load(DesktopPoolEntity.class, desktoppoolid);
							int existingDesktopCount = ((Long) databaseFacade.findFirst(
									"select count(iddesktop) from Desktop where desktoppoolid = ?",
									desktoppoolid)).intValue();
							if (resizing.getMaxdesktops() < desktopPool.getMaxdesktops()
									&& desktopPool.getVmsource() == DesktopPoolEntity.DESKTOP_POOL_SOURCE_MANUAL
									&& resizing.getMaxdesktops() < existingDesktopCount) {
								asyncJobFacade.finishAsyncJob(resizing.getJobId(), CONFLICT, desktoppoolid);
								continue;
							}
							if (desktopPool.getVmsource() == DesktopPoolEntity.DESKTOP_POOL_SOURCE_MANUAL) {
								asyncJobFacade.finishAsyncJob(resizing.getJobId(), NO_ERRORS, desktoppoolid);
								continue;
							}
							if (resizing.getMaxdesktops() > desktopPool.getMaxdesktops()) {
								final List<Desktop> desktops = new LinkedList<Desktop>();
								for (int i = 0, c = resizing.getMaxdesktops() - desktopPool.getMaxdesktops(); i < c; ++i) {
									// 拼接桌面名称.
									String vmName = strcat(desktopPool.getVmnamepatterrn(), "-", existingDesktopCount + i + 1);
									desktops.add(desktopFacade.createDesktop(desktopPool.getIddesktoppool(), vmName));
									desktopPool.setMaxdesktops(desktopPool.getMaxdesktops() + 1);
									databaseFacade.merge(desktopPool);
								}
								syncWorkWithCloudManager(
										cloudManagerStatusFacade,
										desktopPool.getCloudmanagerid(),
										new CloudManagerWorker() {
											@Override
											public int execute(CloudManager cloudManager, Object[] resultContainer) {
												for (Desktop desktop : desktops) {
													// 每个桌面的创建均要等待前一个桌面的创建结果.
													int[] errorContainer = new int[1];
													String vmid = desktopFacade.cloneDesktop(
															desktopPool.getCloudmanagerid(),
															cloudManager,
															desktopPool.getIddesktoppool(),
															desktopPool.getTemplateid(),
															null,
															desktop.getVmname(),
															errorContainer);
													desktop.setVmid(vmid);
													if (vmid == null) {
														desktopPoolStatusFacade.markAsError(desktop.getIddesktop(), desktop.getDesktoppoolid());
													}
													if (poolNotExisting(desktopPool.getIddesktoppool())) {
														break;
													}
												}
												if (poolNotExisting(desktopPool.getIddesktoppool())) {
													for (final Desktop desktop : desktops) {
														if (desktop.getVmid() != null) {
															try {
																cloudManager.destroyVM(desktop.getVmid());
															} catch (Exception e) {
																log.warn("destroyVM failed: {}", desktop.getVmid());
																log.warn("Exception:", e);
															}
														}
													}
													return CONFLICT;
												}
												for (final Desktop desktop : desktops) {
													if (desktop.getVmid() != null) {
														asyncWorkWithCloudManager(
																cloudManagerStatusFacade,
																asyncJobFacade,
																desktopPool.getCloudmanagerid(),
																null,
																null,
																new CloudManagerWorker() {
																	@Override
																	public int execute(CloudManager cloudManager, Object[] resultContainer) {
																		return desktopFacade.afterClone(
																				desktopPool.getCloudmanagerid(),
																				cloudManager,
																				desktopPool,
																				desktop);
																	}
																});
													}
												}
												return NO_ERRORS;
											}
										});
							} else {
								int newMax = desktopPool.getMaxdesktops();
								while (newMax > resizing.getMaxdesktops()) {
									DesktopStatus desktopStatus = (DesktopStatus) databaseFacade.findFirst(
											"select ds from DesktopStatus ds left join ds.desktop d where d.desktoppoolid = ? and ds.ownerid = ? and ds.status != ?",
											desktoppoolid, -1, DesktopStatus.DESKTOP_STATUS_DESTROYING);
									if (desktopStatus != null) {
										Desktop desktop = databaseFacade.load(Desktop.class, desktopStatus.getIddesktop());
										desktopFacade.destroy(
												desktopPool.getCloudmanagerid(),
												desktop,
												desktopPool,
												false,
												null,
												null,
												null,
												-1,
												false,
												false);
									}
									try {
										Thread.sleep(5000);
									} catch (InterruptedException e) {}
									newMax = ((Long) databaseFacade.findFirst(
											"select count(ds.iddesktop) from DesktopStatus ds left join ds.desktop d where d.desktoppoolid = ? and ds.status != ?",
											desktoppoolid, DesktopStatus.DESKTOP_STATUS_DESTROYING)).intValue();
									boolean poolExisting = 0 < databaseFacade.update(
											"update DesktopPool set maxdesktops = ? where iddesktoppool = ?",
											newMax, desktoppoolid);
									if ((!poolExisting) || (!resizingQueues.isEmpty())) {
										asyncJobFacade.finishAsyncJob(resizing.getJobId(), CONFLICT, desktoppoolid);
										return;
									}
								}
							}
							asyncJobFacade.finishAsyncJob(resizing.getJobId(), NO_ERRORS, desktoppoolid);
						}
					}
				};
				com.opzoon.ohvc.session.ExcecutorUtil.execute(thread);
			}
		}
		*/
		int jobId = asyncJobFacade.saveAsyncJob("resizeDesktopPool", desktoppoolid, -1);
		Resizing resizing = new Resizing();
		resizing.setJobId(jobId);
		resizing.setMaxdesktops(maxdesktops);
//		resizingQueue.offer(resizing);
		return jobId;
	}

	public int deleteDesktops(int[] desktopids, boolean force) {
	  if (desktopids.length < 1)
    {
      return NO_ERRORS;
    }
	  DesktopPoolEntity desktopPool = null;
	  List<Desktop> desktops = new LinkedList<Desktop>();
		for (int desktopid : desktopids) {
			Desktop desktop = databaseFacade.load(Desktop.class, desktopid);
			desktops.add(desktop);
			if (desktopPool == null)
      {
			  desktopPool = databaseFacade.load(DesktopPoolEntity.class, desktop.getDesktoppoolid());
      } else {
        if (desktop.getDesktoppoolid() != desktopPool.getIddesktoppool().intValue())
        {
          return CONFLICT;
        }
      }
	    try
	    {
	      operationRegistry.start(new DeleteDesktopOperation(
	          Integer.toHexString(desktop.getDesktoppoolid()) + "#" + Integer.toHexString(desktopPool.getCloudmanagerid()) + "#" + Integer.toHexString(desktopid), desktop.getVmid(), force));
	    } catch (CommonException e1)
	    {
	      // TODO Auto-generated catch block
	      e1.printStackTrace();
	      return CONFLICT;
	    }
		}
    databaseFacade.update(
        "update DesktopPoolEntity set maxdesktops = ? where iddesktoppool = ?",
        desktopPool.getMaxdesktops() - desktops.size(), desktopPool.getIddesktoppool());
		return NO_ERRORS;
	}

	/**
	 * 查找桌面池.
	 * 
	 * @param iddesktoppool 桌面池ID.
	 * @return 桌面池.
	 */
	public DesktopPoolEntity loadDesktopPool(int iddesktoppool) {
		return databaseFacade.load(DesktopPoolEntity.class, iddesktoppool);
	}

	public boolean poolNotExisting(int iddesktoppool) {
		return !exists(databaseFacade.findFirst(
				"select count(iddesktoppool) from DesktopPoolEntity where iddesktoppool = ?",
				iddesktoppool));
	}

	public boolean deleteAssignmentIfConflictInDomain(int targetType,
			int target, int desktoppoolid) {
		Integer domainOfPool = resourceFacade.findDomainIdOfPool(desktoppoolid);
		if (domainOfPool == null) {
			this.deleteAssignment(targetType, target, desktoppoolid);
			return true;
		}
		Integer domainOfTarget = this.findDomainIdOfVisitor(desktoppoolid, targetType, target);
		if (domainOfTarget == null) {
			return false;
		}
		if(numberNotEquals(domainOfTarget, domainOfPool)) {
			this.deleteAssignment(targetType, target, desktoppoolid);
			return true;
		}
		return false;
	}

	public void setDatabaseFacade(DatabaseFacade databaseFacade) {
		this.databaseFacade = databaseFacade;
	}

	public void setUserFacade(UserFacade userFacade) {
		this.userFacade = userFacade;
	}

	public void setGroupFacade(GroupFacade groupFacade) {
		this.groupFacade = groupFacade;
	}

	public void setOrganizationFacade(OrganizationFacade organizationFacade)
  {
    this.organizationFacade = organizationFacade;
  }

  public void setDesktopFacade(DesktopFacade desktopFacade) {
		this.desktopFacade = desktopFacade;
	}

	public void setAsyncJobFacade(AsyncJobFacade asyncJobFacade) {
		this.asyncJobFacade = asyncJobFacade;
	}

	public void setDesktopPoolStatusFacade(
			DesktopPoolStatusFacade desktopPoolStatusFacade) {
		this.desktopPoolStatusFacade = desktopPoolStatusFacade;
	}

	public void setDesktopStatusFacade(DesktopStatusFacade desktopStatusFacade) {
		this.desktopStatusFacade = desktopStatusFacade;
	}

	public void setSessionFacade(SessionFacade sessionFacade) {
		this.sessionFacade = sessionFacade;
	}

	public void setCloudManagerStatusFacade(
			CloudManagerStatusFacade cloudManagerStatusFacade) {
		this.cloudManagerStatusFacade = cloudManagerStatusFacade;
	}

	public void setResourceFacade(ResourceFacade resourceFacade) {
		this.resourceFacade = resourceFacade;
	}

	public void setOperationRegistry(OperationRegistry operationRegistry)
  {
    this.operationRegistry = operationRegistry;
  }

  private int assignDesktopPoolTo(int desktoppoolid, int visitorid,
			int visitorType) throws CommonException {
		if (!exists(databaseFacade.findFirst(
				"select count(iddesktoppool) from DesktopPoolEntity where iddesktoppool = ?",
				desktoppoolid))) {
			return NOT_FOUND;
		}
		if (0 < databaseFacade.update(
					"update ResourceAssignment set permission = ? where resourcetype = ? and resourceid = ? and visitortype = ? and visitorid = ?",
					RESOURCE_PERMISSION_ALLOWED, RESOURCE_TYPE_POOL, desktoppoolid, visitorType, visitorid)) {
			return NO_ERRORS;
		}
		ResourceAssignment resourceAssignment = new ResourceAssignment();
		resourceAssignment.setPermission(RESOURCE_PERMISSION_ALLOWED);
		resourceAssignment.setResourceid(desktoppoolid);
		resourceAssignment.setResourcetype(RESOURCE_TYPE_POOL);
		resourceAssignment.setVisitorid(visitorid);
		resourceAssignment.setVisitortype(visitorType);
		try {
			databaseFacade.persist(resourceAssignment);
		} catch (EntityExistsException e) {
			throw new CommonException(CONFLICT);
		}
		return NO_ERRORS;
	}

	private int unassignDesktopPoolTo(final int desktoppoolid, final int targetid,
	    final int visitorType, boolean force) {
		if (!exists(databaseFacade.findFirst(
				"select count(iddesktoppool) from DesktopPoolEntity where iddesktoppool = ?",
				desktoppoolid))) {
			return NOT_FOUND;
		}
		List<Integer> users = new LinkedList<Integer>();
		if (numberEquals(visitorType, RESOURCE_VISITOR_TYPE_GROUP)) {
			users.addAll(groupFacade.findUsers(targetid));
		} else if (numberEquals(visitorType, RESOURCE_VISITOR_TYPE_ORGANIZATION)) {
			users.addAll(organizationFacade.findUsers(targetid));
		} else {
			users.add(targetid);
		}
    try
    {
      resourceFacade.autoClearResources(
          users,
          force,
          new RunnableWithException() {
            @Override
            public void run() {
              deleteAssignment(visitorType, targetid, desktoppoolid);
            }
          });
    } catch (CommonException e)
    {
      // TODO Auto-generated catch block
      e.printStackTrace();
      return e.getError();
    }
//		if (1 > this.deleteAssignment(visitorType, targetid, desktoppoolid)) {
//			return NOT_FOUND;
//		}
//		for (Integer user : users) {
//			if (force) {
//				resourceFacade.disconnectUnassignedDesktops(user, false);
//			} else {
//				outter:
//				while (true) {
//					List<Integer> sessions = sessionFacade.findSessionsByUserId(user);
//					boolean connected = false;
//					inner:
//					for (Integer session : sessions) {
//						@SuppressWarnings("unchecked")
//						List<Connection> connections = (List<Connection>) databaseFacade.find(
//								"from Connection where sessionid = ?",
//								session);
//						for (Connection connection : connections) {
//							if (connection.getResourcetype() == RESOURCE_TYPE_POOL
//									&& connection.getResourceid() == desktoppoolid) {
//								connected = true;
//								break inner;
//							}
//						}
//					}
//					if (!connected) {
//						// FIXME Force deleted other pools now...
//						resourceFacade.disconnectUnassignedDesktops(user, false);
//						break outter;
//					}
//					try {
//						Thread.sleep(5000);
//					} catch (InterruptedException e) {}
//				}
//			}
//		}
		return NO_ERRORS;
	}

	private void forceDeleteDesktopPool(DesktopPoolEntity desktopPool, boolean force) {
    try
    {
      operationRegistry.start(new DeleteDesktopPoolOperation(desktopPool.getIddesktoppool(), force));
    } catch (CommonException e)
    {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
//		if (!exists(databaseFacade.update(
//				"update DesktopPoolStatus set status = ? where iddesktoppool = ? and status != ?",
//				DesktopPoolStatus.DESKTOPPOOL_STATUS_MAINTAIN, desktopPool.getIddesktoppool(), DesktopPoolStatus.DESKTOPPOOL_STATUS_MAINTAIN))) {
//			return;
//		}
//		// 删除数据库中的桌面池.
//		databaseFacade.update(
//				"delete from ResourceAssignment where resourcetype = ? and resourceid = ?",
//				RESOURCE_TYPE_POOL, desktopPool.getIddesktoppool());
//		@SuppressWarnings("unchecked")
//		final List<Desktop> desktops = (List<Desktop>) databaseFacade.find(
//				"from Desktop where desktoppoolid = ?",
//				desktopPool.getIddesktoppool());
//		for (Desktop desktop : desktops) {
//			// 逐个删除桌面.
//			desktopFacade.destroy(
//					desktopPool.getCloudmanagerid(),
//					desktop,
//					desktopPool,
//					false,
//					null,
//					null,
//					null,
//					-1,
//					false,
//					force);
//		}
	}

	private Integer findDomainIdOfVisitor(int desktoppoolid, int visitorType,
			int visitorid) {
		switch (visitorType) {
		case RESOURCE_VISITOR_TYPE_USER:
			User user = databaseFacade.load(User.class, visitorid);
			return user.getDomainid();
		case RESOURCE_VISITOR_TYPE_ORGANIZATION:
			Organization organization = databaseFacade.load(Organization.class, visitorid);
			return organization.getDomainid();
		default:
			return groupFacade.findDomainId(visitorid);
		}
	}

	private int deleteAssignment(int targetType, int target, int desktoppoolid) {
		ResourceAssignment resourceAssignment = (com.opzoon.vdi.core.domain.ResourceAssignment) databaseFacade.findFirst(
						"from ResourceAssignment where resourcetype = ? and resourceid = ? and visitortype = ? and visitorid = ?",
						RESOURCE_TYPE_POOL, desktoppoolid, targetType, target);
		if (resourceAssignment != null) {
			databaseFacade.remove(resourceAssignment);
			return 1;
		}
		return 0;
	}
	
	private int fulfillDomain(DesktopPoolEntity desktopPool) {
		if (desktopPool.getDomainid() == null
				|| desktopPool.getDomainid() < 0
				|| desktopPool.getDomainid() == Domain.DEFAULT_DOMAIN_ID) {
			return NO_ERRORS;
		}
		Domain domain = databaseFacade.load(Domain.class, desktopPool.getDomainid());
		if (domain == null) {
			return NOT_FOUND;
		}
		desktopPool.setDomainname(domain.getDomainname());
		desktopPool.setDomainbinddn(domain.getDomainbinddn());
		desktopPool.setDomainbindpass(domain.getDomainbindpass());
		return NO_ERRORS;
	}
	
	public static class Resizing {

		private int jobId;
		private int maxdesktops;
		
		public int getJobId() {
			return jobId;
		}
		public void setJobId(int jobId) {
			this.jobId = jobId;
		}
		public int getMaxdesktops() {
			return maxdesktops;
		}
		public void setMaxdesktops(int maxdesktops) {
			this.maxdesktops = maxdesktops;
		}
		
	}

}
