package com.opzoon.vdi.core.facade;

import static com.opzoon.vdi.core.cloud.CloudManagerHelper.asyncWorkWithCloudManager;
import static com.opzoon.vdi.core.cloud.CloudManagerHelper.findCloudManager;
import static com.opzoon.vdi.core.domain.ResourceAssignment.RESOURCE_TYPE_APPLICATION;
import static com.opzoon.vdi.core.domain.ResourceAssignment.RESOURCE_TYPE_POOL;
import static com.opzoon.vdi.core.domain.ResourceAssignment.RESOURCE_VISITOR_TYPE_GROUP;
import static com.opzoon.vdi.core.domain.ResourceAssignment.RESOURCE_VISITOR_TYPE_ORGANIZATION;
import static com.opzoon.vdi.core.domain.ResourceAssignment.RESOURCE_VISITOR_TYPE_USER;
import static com.opzoon.vdi.core.facade.CommonException.CONFLICT;
import static com.opzoon.vdi.core.facade.CommonException.FORBIDDEN;
import static com.opzoon.vdi.core.facade.CommonException.NOT_FOUND;
import static com.opzoon.vdi.core.facade.CommonException.NO_ERRORS;
import static com.opzoon.vdi.core.facade.CommonException.UNKNOWN;
import static com.opzoon.vdi.core.facade.FacadeHelper.count;
import static com.opzoon.vdi.core.facade.FacadeHelper.pagingFind;
import static com.opzoon.vdi.core.util.ConditionUtils.numberEquals;
import static com.opzoon.vdi.core.util.ConditionUtils.numberNotEquals;
import static com.opzoon.vdi.core.util.QueryHelper.buildOrIdWhereClause;
import static com.opzoon.vdi.core.util.StringUtils.strcat;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.opzoon.vdi.core.RunnableWithException;
import com.opzoon.vdi.core.cloud.CloudManager;
import com.opzoon.vdi.core.cloud.CloudManagerHelper.CloudManagerWorker;
import com.opzoon.vdi.core.cloud.ConnectionManager;
import com.opzoon.vdi.core.cloud.ConnectionManager.ConnectionInfo;
import com.opzoon.vdi.core.domain.CloudManagerEntity;
import com.opzoon.vdi.core.domain.Connection;
import com.opzoon.vdi.core.domain.Desktop;
import com.opzoon.vdi.core.domain.DesktopPoolEntity;
import com.opzoon.vdi.core.domain.DesktopPoolStatus;
import com.opzoon.vdi.core.domain.DesktopStatus;
import com.opzoon.vdi.core.domain.Domain;
import com.opzoon.vdi.core.domain.Group;
import com.opzoon.vdi.core.domain.GroupElement;
import com.opzoon.vdi.core.domain.Notification;
import com.opzoon.vdi.core.domain.Organization;
import com.opzoon.vdi.core.domain.ResourceAssignment;
import com.opzoon.vdi.core.domain.ResourceAssignmentWithResourceView;
import com.opzoon.vdi.core.domain.RestrictionStrategy;
import com.opzoon.vdi.core.domain.RestrictionStrategyAssignment;
import com.opzoon.vdi.core.domain.Session;
import com.opzoon.vdi.core.domain.USBListItem;
import com.opzoon.vdi.core.domain.User;
import com.opzoon.vdi.core.domain.UserVolume;
import com.opzoon.vdi.core.domain.state.DesktopState;
import com.opzoon.vdi.core.facade.FacadeHelper.PagingInfo;
import com.opzoon.vdi.core.facade.UserFacade.GroupInfo;
import com.opzoon.vdi.core.facade.transience.AsyncJobFacade;
import com.opzoon.vdi.core.facade.transience.ConnectionFacade;
import com.opzoon.vdi.core.facade.transience.DesktopPoolStatusFacade;
import com.opzoon.vdi.core.facade.transience.DesktopStatusFacade;
import com.opzoon.vdi.core.facade.transience.RuntimeVariableFacade;
import com.opzoon.vdi.core.facade.transience.SessionFacade;
import com.opzoon.vdi.core.operation.AllocAndConnectDesktopOperation;
import com.opzoon.vdi.core.operation.DeallocDesktopOperation;
import com.opzoon.vdi.core.operation.DisconnectDesktopOperation;
import com.opzoon.vdi.core.operations.OperationContext;
import com.opzoon.vdi.core.operations.OperationRegistry;
import com.opzoon.vdi.core.support.Configuration;
import com.opzoon.vdi.core.util.EntityId;
import com.opzoon.vdi.core.util.Pair;
import com.opzoon.vdi.core.ws.admin.LicenseMangement;

/**
 * 资源和连接相关业务接口.
 */
public class ResourceFacade {

	private static final Logger log = LoggerFactory
			.getLogger(ResourceFacade.class);

	private DatabaseFacade databaseFacade;
	private OrganizationFacade organizationFacade;
	private GroupFacade groupFacade;
	private UserFacade userFacade;
	private DesktopFacade desktopFacade;
	private DesktopPoolFacade desktopPoolFacade;
	private ConnectionManager connectionManager;
	private ConnectionFacade connectionFacade;
	private DesktopPoolStatusFacade desktopPoolStatusFacade;
	private DesktopStatusFacade desktopStatusFacade;
	private SessionFacade sessionFacade;
	private RuntimeVariableFacade runtimeVariableFacade;
	private Configuration configuration;
	private RailUserDataManager railUserDataManager;
	private AsyncJobFacade asyncJobFacade;
	private OperationRegistry operationRegistry;

	public void autoClearResources(Collection<Integer> users, boolean force,
			RunnableWithException runnableWithException) throws CommonException {
		// TODO Check stack to avoid repeat invokes ?
		log.trace("Candidates to clear: " + users);
		Set<Pair<Integer, Integer>> ownershipsBefore = this
				.buildPoolOwnerships(users);
		Map<Integer, Integer> sessions = new HashMap<Integer, Integer>();
		Map<Integer, String> usernames = new HashMap<Integer, String>();
		for (final Integer userid : users) {
			sessions.put(
					userid,
					(Integer) databaseFacade
							.findFirst(
									"select idsession from Session where userid = ? and logintype = ? and deleted = ?",
									userid, Session.LOGIN_TYPE_USER, 0));
			usernames.put(userid, (String) databaseFacade.findFirst(
					"select username from User where iduser = ? ", userid));
		}
		runnableWithException.run();
		Set<Pair<Integer, Integer>> ownershipsAfter = this
				.buildPoolOwnerships(users);
		for (Pair<Integer, Integer> ownershipBefore : ownershipsBefore) {
			if (!ownershipsAfter.contains(ownershipBefore)) {
				log.trace("User to clear: " + ownershipBefore.getLeft()
						+ " for " + ownershipBefore.getRight() + " (force = "
						+ force + ")");
				this.clearDesktopOfUser(ownershipBefore.getLeft(),
						ownershipBefore.getRight(), force,
						sessions.get(ownershipBefore.getLeft()),
						usernames.get(ownershipBefore.getLeft()));
			}
		}
	}

	/**
	 * 释放指定用户(的指定会话)所拥有的连接.
	 * 
	 * @param iduser
	 *            用户ID.
	 * @param idsession
	 *            会话ID. 仅当此用户的连接关联在此session时才释放连接. null表示无条件释放此用户的连接.
	 */
	public void releaseConnections(int iduser, Integer idsession) {
		List<Integer> sessions = idsession != null ? Arrays
				.asList(new Integer[] { idsession }) : sessionFacade
				.findSessionsByUserId(iduser);
		for (Integer session : sessions) {
			List<Connection> connections = connectionFacade
					.findConnectionsBySession(session);
			for (Connection connection : connections) {
				connection.setUserid(iduser);
				this.destroyConnection(iduser, session,
						connection.getConnectionticket());
			}
		}
	}

	/**
	 * 根据连接的令牌销毁当前用户的连接.
	 * 
	 * @param connectionticket
	 *            连接的令牌.
	 * @return 错误代码.
	 */
	// public int destroyConnectionOfCurrentUserByTicket(String
	// connectionticket) {
	// if (connectionticket == null) {
	// List<Integer> sessions =
	// sessionFacade.findSessionsByUserId(sessionFacade.getCurrentSession().getUserid());
	// for (Integer session : sessions) {
	// List<Connection> connections =
	// connectionFacade.findConnectionsBySession(session);
	// for (Connection connection : connections) {
	// connection.setUserid(sessionFacade.getCurrentSession().getUserid());
	// this.destroyConnection(connection);
	// }
	// }
	// asyncJobFacade.saveAsyncJob("destroyConnectionOfCurrentUserByTicket",
	// sessionFacade.getCurrentSession().getUserid(), -1);
	// return NO_ERRORS;
	// } else {
	// List<Integer> sessions =
	// sessionFacade.findSessionsByUserId(sessionFacade.getCurrentSession().getUserid());
	// for (Integer session : sessions) {
	// Connection connection = connectionFacade.findConnection(connectionticket,
	// session);
	// if (connection != null) {
	// connection.setUserid(sessionFacade.getCurrentSession().getUserid());
	// this.destroyConnection(connection);
	// return NO_ERRORS;
	// }
	// }
	// return NOT_FOUND;
	// }
	// }

	/**
	 * 列举当前用户拥有的资源.
	 * 
	 * @param resourcetype
	 *            资源类型.
	 * @return 资源列表.
	 */
	@SuppressWarnings("unchecked")
	public List<Resource> findResources(int resourcetype, int userid,
			boolean onlyCheckMapping) {
		if (userid == -1) {
			userid = sessionFacade.getCurrentSession().getUserid();
		}
		List<Resource> resources = new LinkedList<Resource>();
		if (numberEquals(resourcetype, -1)
				|| numberEquals(resourcetype, RESOURCE_TYPE_POOL)) {
			Set<Integer> assignedPools = new HashSet<Integer>();
			// 查询当前用户直接拥有的资源.
			assignedPools
					.addAll((List<Integer>) databaseFacade
							.find("select resourceid from ResourceAssignment where resourcetype = ? and visitortype = ? and visitorid = ?",
									RESOURCE_TYPE_POOL,
									RESOURCE_VISITOR_TYPE_USER, userid));
			// 查询当前用户所在的组.
			List<Integer> groupIdsOfUser = userFacade.findGroupsOfUser(userid);
			if (!groupIdsOfUser.isEmpty()) {
				List<Object> params = new ArrayList<Object>();
				params.add(RESOURCE_TYPE_POOL);
				params.add(RESOURCE_VISITOR_TYPE_GROUP);
				params.addAll(groupIdsOfUser);
				Object[] paramsArray = params.toArray();
				// 查询当前用户所在组拥有的资源.
				assignedPools
						.addAll((List<Integer>) databaseFacade
								.find(strcat(
										"select resourceid from ResourceAssignment where resourcetype = ? and visitortype = ? and ",
										buildOrIdWhereClause(
												groupIdsOfUser.size(),
												"visitorid")), paramsArray));
			}
			Integer organizationIdOfUser = userFacade
					.findOrganizationOfUser(userid);
			if (organizationIdOfUser != null) {
				List<Integer> organizationAndParents = organizationFacade
						.findSelfAndParents(organizationIdOfUser);
				List<Object> params = new ArrayList<Object>();
				params.add(RESOURCE_TYPE_POOL);
				params.add(RESOURCE_VISITOR_TYPE_ORGANIZATION);
				params.addAll(organizationAndParents);
				Object[] paramsArray = params.toArray();
				// 查询当前用户所在组织拥有的资源.
				assignedPools
						.addAll((List<Integer>) databaseFacade
								.find(strcat(
										"select resourceid from ResourceAssignment where resourcetype = ? and visitortype = ? and ",
										buildOrIdWhereClause(
												organizationAndParents.size(),
												"visitorid")), paramsArray));
			}
			for (Integer assignedPoolId : assignedPools) {
				DesktopPoolEntity assignedPool = desktopPoolFacade
						.loadDesktopPool(assignedPoolId);
				Resource resource = new Resource();
				resource.setResourceid(assignedPool.getIddesktoppool());
				resource.setResourcename(assignedPool.getPoolname());
				resource.setAttachablevolumes((List<Integer>) databaseFacade
						.find("select iduservolume from UserVolume where userid = ? and cloudmanagerid = ? and desktoppoolid = ?",
								userid, assignedPool.getCloudmanagerid(), null));
				DesktopPoolStatus assignedPoolStatus = databaseFacade.load(
						DesktopPoolStatus.class, assignedPoolId);
				log.debug("DesktopPoolStatus ::==>" + assignedPoolStatus);
				if (assignedPoolStatus.getStatus() == DesktopPoolStatus.DESKTOPPOOL_STATUS_MAINTAIN) {
					resource.setResourcestatus(DesktopPoolStatus.DESKTOPPOOL_STATUS_MAINTAIN);
				} else {
					if (!onlyCheckMapping) {
						DesktopStatus availableDesktop = desktopPoolStatusFacade
								.findAvailableDesktop(userid,
										assignedPool.getIddesktoppool());
						log.debug("DesktopStatus :: ===>" + availableDesktop);
						resource.setAvailableDesktop(availableDesktop);
						resource.setResourcestatus(availableDesktop == null ? DesktopPoolStatus.DESKTOPPOOL_STATUS_FULL
								: availableDesktop.getOwnerid() < 0 ? DesktopPoolStatus.DESKTOPPOOL_STATUS_VALID
										// : assignedPool.getAssignment() ==
										// DesktopPoolEntity.DESKTOP_POOL_ASSIGNMENT_FLOATING
										// ?
										// DesktopStatus.DESKTOP_STATUS_CONNECTED
										: (availableDesktop.getPhase() == DesktopState.DESKTOP_PHASE_CREATING ? DesktopStatus.DESKTOP_STATUS_PROVISIONING
												: (availableDesktop.getPhase() == DesktopState.DESKTOP_PHASE_DELETING ? DesktopStatus.DESKTOP_STATUS_DESTROYING
														: (availableDesktop
																.getPhase() == DesktopState.DESKTOP_PHASE_DEFICIENT ? DesktopStatus.DESKTOP_STATUS_ERROR
																: (availableDesktop
																		.getConnected() == 1 ? DesktopStatus.DESKTOP_STATUS_CONNECTED
																		: (availableDesktop
																				.getStatus() == DesktopState.DESKTOP_STATUS_STARTING ? DesktopStatus.DESKTOP_STATUS_STARTING
																				: (availableDesktop
																						.getStatus() == DesktopState.DESKTOP_STATUS_ERROR ? DesktopStatus.DESKTOP_STATUS_ERROR
																						: (availableDesktop
																								.getStatus() == DesktopState.DESKTOP_STATUS_SERVING ? DesktopStatus.DESKTOP_STATUS_SERVING
																								: (availableDesktop
																										.getStatus() == DesktopState.DESKTOP_STATUS_RUNNING ? DesktopStatus.DESKTOP_STATUS_RUNNING
																										: (availableDesktop
																												.getStatus() == DesktopState.DESKTOP_STATUS_STOPPED ? DesktopStatus.DESKTOP_STATUS_STOPPED
																												: (availableDesktop
																														.getStatus() == DesktopState.DESKTOP_STATUS_STOPPING ? DesktopStatus.DESKTOP_STATUS_STOPPING
																														: DesktopStatus.DESKTOP_STATUS_ERROR)))))))))));
					} else {
						resource.setResourcestatus(DesktopPoolStatus.DESKTOPPOOL_STATUS_VALID);
					}
				}
				resource.setResourcetype(RESOURCE_TYPE_POOL);
				resource.setAvailableprotocols(assignedPool
						.getAvailableprotocols());
				resources.add(resource);
			}
		}
		if (numberEquals(resourcetype, -1)
				|| numberEquals(resourcetype, RESOURCE_TYPE_APPLICATION)) {
			// TODO Postponed.
		}
		return resources;
	}

	public int establishConnectionA(final int resourcetype,
			final int resourceid, final int brokerprotocol)
			throws CommonException {
		final int sessionid = sessionFacade.getCurrentSession().getIdsession();
		final int userid = sessionFacade.getCurrentSession().getUserid();
		final String password = sessionFacade.getCurrentSession().getPassword();
		// final Date startDate = new Date();
		//add by zhanglu license hours is full start
		if (0 == LicenseMangement.LICENSE_HOURS){
			throw new CommonException(CommonException.ESTABLISH_CONNECTION_ERR);
		}
		//add by zhanglu license hours is full end
		// add by tanyunhua , for control desktop connection sum; start
		// -----------
		if (connectionFacade.findDesktopConnectionCount() >= LicenseMangement.connect_count) {
			throw new CommonException(CommonException.CONNECTION_FULL);
		}
		// add by tanyunhua , for control desktop connection sum; end
		// -------------
		int error = checkProtocol(resourcetype, resourceid, brokerprotocol);
		if (numberNotEquals(error, NO_ERRORS)) {
			throw new CommonException(error);
		}
		if (runtimeVariableFacade.findIfServiceIsStopped()) {
			throw new CommonException(CommonException.CONFLICT);
		}
		DesktopPoolEntity pool = desktopPoolFacade.loadDesktopPool(resourceid);
		int jobId;
		if (pool.getVmsource() == DesktopPoolEntity.DESKTOP_POOL_SOURCE_MANUAL) {
			jobId = asyncJobFacade.saveAsyncJob("establishConnection",
					resourceid, -1);
			try {
				operationRegistry
						.start(new AllocAndConnectDesktopOperation(
								Integer.toHexString(userid)
										+ "#"
										+ Integer.toHexString(resourceid)
										+ "#"
										+ Integer
												.toHexString(brokerprotocol < 1 ? DesktopPoolEntity.PROTOCOL_RDP
														: brokerprotocol) + "#"
										+ Integer.toHexString(sessionid),
								password, jobId));
			} catch (CommonException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				throw e;
			}
		} else {
			log.info("Starting asyncWorkWithCloudManager for a connection");
			jobId = asyncWorkWithCloudManager(
					null,
					asyncJobFacade,
					databaseFacade.load(CloudManagerEntity.class,
							pool.getCloudmanagerid()), "establishConnection",
					resourceid, new CloudManagerWorker() {
						@Override
						public int execute(CloudManager cloudManager,
								Object[] resultContainer, int jobid) {
							log.info("Connecting");
							Session session = new Session();
							session.setIdsession(sessionid);
							session.setUserid(userid);
							session.setPassword(password);
							sessionFacade.setCurrentSession(session);
							log.info("Still connecting");
							try {
								operationRegistry
										.start(new AllocAndConnectDesktopOperation(
												Integer.toHexString(userid)
														+ "#"
														+ Integer
																.toHexString(resourceid)
														+ "#"
														+ Integer
																.toHexString(brokerprotocol < 1 ? DesktopPoolEntity.PROTOCOL_RDP
																		: brokerprotocol)
														+ "#"
														+ Integer
																.toHexString(sessionid),
												password, jobid));
							} catch (CommonException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
								return e.getError();
							}
							// if
							// (asyncJobFacade.justCanceledConnection(sessionFacade.getCurrentSession().getUserid(),
							// startDate)) {
							// destroyConnectionOfCurrentUserByTicket(connection.getConnectionticket());
							// return CommonException.CONFLICT;
							// }
							return NO_ERRORS;
						}
					});
		}
		return jobId;
	}

	public int checkProtocol(int resourcetype, int resourceid,
			int brokerprotocol) {
		DesktopPoolEntity pool = desktopPoolFacade.loadDesktopPool(resourceid);
		if (brokerprotocol > 0
				&& (brokerprotocol & pool.getAvailableprotocols()) < 1) {
			return CONFLICT;
		}
		return NO_ERRORS;
	}

	// public int tryToAssign(int resourcetype, int resourceid) {
	// List<Integer> sessions =
	// sessionFacade.findSessionsByUserId(sessionFacade.getCurrentSession().getUserid());
	// for (Integer session : sessions) {
	// List<Connection> connections =
	// connectionFacade.findConnectionsBySession(session);
	// for (Connection oldConnection : connections) {
	// if (numberEquals(oldConnection.getResourcetype(), resourcetype) &&
	// numberEquals(oldConnection.getResourceid(), resourceid)) {
	// log.trace("Connection existing.");
	// return CONFLICT;
	// }
	// }
	// }
	// switch (resourcetype) {
	// case RESOURCE_TYPE_POOL:
	// DesktopPoolEntity pool = desktopPoolFacade.loadDesktopPool(resourceid);
	// if (pool == null) {
	// return NOT_FOUND;
	// }
	// List<Resource> resources = this.findResources(RESOURCE_TYPE_POOL, -1,
	// false);
	// Resource availableResource = null;
	// for (Resource resource : resources) {
	// if (resource.getResourcetype() == RESOURCE_TYPE_POOL &&
	// resource.getResourceid() == resourceid) {
	// availableResource = resource;
	// break;
	// }
	// }
	// if (availableResource == null) {
	// return NOT_FOUND;
	// }
	// DesktopStatus availableDesktop = availableResource.getAvailableDesktop();
	// if (availableDesktop != null) {
	// Desktop desktop = databaseFacade.load(Desktop.class,
	// availableDesktop.getIddesktop());
	// String ipaddress = null;
	// if (pool.getVmsource() == DesktopPoolEntity.DESKTOP_POOL_SOURCE_MANUAL) {
	// ipaddress = desktop.getIpaddress();
	// desktopFacade.doRefreshDesktopStatus(null, 0, desktop);
	// } else {
	// CloudManager cloudManager = findCloudManager(pool.getCloudmanagerid());
	// VMInstance vm = null;
	// try {
	// vm = cloudManager.getVM(desktop.getVmid());
	// } catch (Exception e) {
	// log.warn("getVM {} failed.", desktop.getVmid());
	// log.warn("Exception:", e);
	// return CommonException.HYPERVISOR_ABNORMAL;
	// }
	// ipaddress = vm.getIpaddress();
	// desktopFacade.syncRefreshDesktopStatus(cloudManager,
	// pool.getCloudmanagerid(), desktop);
	// }
	// availableDesktop = databaseFacade.load(DesktopStatus.class,
	// availableDesktop.getIddesktop());
	// if (availableDesktop.getStatus() == DesktopState.DESKTOP_STATUS_SERVING)
	// {
	// int error =
	// desktopFacade.assignDesktopToConnect(pool.getCloudmanagerid(), ipaddress,
	// sessionFacade.getCurrentSession().getUserid(),
	// sessionFacade.getCurrentSession().getPassword(),
	// availableDesktop.getIddesktop(), pool);
	// if(!sessionFacade.checkIfSessionIsOK()) {
	// error = CONFLICT;
	// }
	// if (numberNotEquals(error, NO_ERRORS)) {
	// log.warn("AssignDesktop {} failed.", availableDesktop);
	// boolean userCanNoLongerOwnThePool = numberNotEquals(pool.getAssignment(),
	// DESKTOP_POOL_ASSIGNMENT_DEDICATED)
	// // Useless ?
	// ||
	// this.checkIfUserCanNoLongerOwnThePool(sessionFacade.getCurrentSession().getUserid(),
	// pool.getIddesktoppool());
	// if (userCanNoLongerOwnThePool) {
	// desktopFacade.unassign(pool, availableDesktop.getIddesktop(),
	// sessionFacade.getCurrentSession().getUserid(), true, false);
	// }
	// return error;
	// }
	// return NO_ERRORS;
	// } else {
	// if (availableDesktop.getStatus() == DesktopState.DESKTOP_STATUS_STOPPED)
	// {
	// desktopFacade.asyncStart(pool.getCloudmanagerid(),
	// availableDesktop.getIddesktop());
	// }
	// }
	// }
	// // TODO Detail errors.
	// return NOT_FOUND;
	// default:
	// // TODO Postponed.
	// break;
	// }
	// return NO_ERRORS;
	// }

	public boolean succeededToAssign(int resourcetype, int resourceid) {
		switch (resourcetype) {
		case RESOURCE_TYPE_POOL:
			return null != desktopPoolStatusFacade.findConnectedDesktop(
					sessionFacade.getCurrentSession().getUserid(), resourceid);
		default:
			// TODO Postponed.
			break;
		}
		return false;
	}

	/**
	 * 建立连接.
	 * 
	 * @param resourcetype
	 *            资源类型.
	 * @param resourceid
	 *            资源ID.
	 * @param errorContainer
	 *            错误代码容器.
	 * @return 连接实例.
	 */
	public Connection establishConnection(int resourcetype, int resourceid,
			int brokerprotocol, int[] errorContainer) {
		errorContainer[0] = NO_ERRORS;
		User user = databaseFacade.load(User.class, sessionFacade
				.getCurrentSession().getUserid());
		switch (resourcetype) {
		case RESOURCE_TYPE_POOL:
			DesktopPoolEntity pool = desktopPoolFacade
					.loadDesktopPool(resourceid);
			DesktopPoolStatus assignedPoolStatus = databaseFacade.load(
					DesktopPoolStatus.class, resourceid);
			if (assignedPoolStatus.getStatus() == DesktopPoolStatus.DESKTOPPOOL_STATUS_MAINTAIN) {
				errorContainer[0] = CommonException.POOL_MAINTAINING;
				return null;
			}
			int desktopid = desktopPoolStatusFacade.findConnectedDesktop(
					sessionFacade.getCurrentSession().getUserid(), resourceid);
			int error = desktopStatusFacade.markAsConnected(desktopid);
			if (numberNotEquals(error, NO_ERRORS)) {
				errorContainer[0] = error;
				return null;
			}
			Desktop desktop = databaseFacade.load(Desktop.class, desktopid);
			CloudManager cloudManager = findCloudManager(databaseFacade.load(
					CloudManagerEntity.class, pool.getCloudmanagerid()));
			String brokerName = configuration.getBrokerName();
			String brokerIP = configuration.getBrokerIP();
			if (brokerprotocol < 1) {
				brokerprotocol = DesktopPoolEntity.PROTOCOL_RDP;
			}
			ConnectionInfo connectionInfo = null;
			// ConnectionInfo connectionInfo =
			// connectionManager.establishConnection(
			// cloudManager, desktop.getVmid(), desktop.getIpaddress(),
			// brokerName, brokerIP, configuration.getBrokerPortMin(),
			// configuration.getBrokerPortMax(),
			// brokerprotocol == DesktopPoolEntity.PROTOCOL_SPICE_OVER_HTTPS
			// || brokerprotocol == DesktopPoolEntity.PROTOCOL_SPICE_OVER_HTTP
			// || brokerprotocol == DesktopPoolEntity.PROTOCOL_SPICE,
			// brokerprotocol == DesktopPoolEntity.PROTOCOL_RDP_OVER_HTTPS
			// || brokerprotocol == DesktopPoolEntity.PROTOCOL_RDP_OVER_HTTP
			// || brokerprotocol == DesktopPoolEntity.PROTOCOL_SPICE_OVER_HTTPS
			// || brokerprotocol == DesktopPoolEntity.PROTOCOL_SPICE_OVER_HTTP);
			if (connectionInfo == null) {
				log.warn("Establish connection {} {} failed.", resourcetype,
						resourceid);
				errorContainer[0] = UNKNOWN;
				return null;
			}
			connectionInfo.setBrokerprotocol(brokerprotocol);
			String tunnelName = (brokerprotocol == DesktopPoolEntity.PROTOCOL_RDP_OVER_HTTPS || brokerprotocol == DesktopPoolEntity.PROTOCOL_SPICE_OVER_HTTPS) ? configuration
					.getHttpsTunnelName()
					: ((brokerprotocol == DesktopPoolEntity.PROTOCOL_RDP_OVER_HTTP || brokerprotocol == DesktopPoolEntity.PROTOCOL_SPICE_OVER_HTTP) ? configuration
							.getHttpTunnelName() : null);
			int tunnelPort = (brokerprotocol == DesktopPoolEntity.PROTOCOL_RDP_OVER_HTTPS || brokerprotocol == DesktopPoolEntity.PROTOCOL_SPICE_OVER_HTTPS) ? configuration
					.getHttpsTunnelPort()
					: ((brokerprotocol == DesktopPoolEntity.PROTOCOL_RDP_OVER_HTTP || brokerprotocol == DesktopPoolEntity.PROTOCOL_SPICE_OVER_HTTP) ? configuration
							.getHttpTunnelPort() : -1);
			if (brokerprotocol != DesktopPoolEntity.PROTOCOL_RDP
					&& brokerprotocol != DesktopPoolEntity.PROTOCOL_SPICE) {
				if (tunnelName == null || tunnelName.length() < 1) {
					tunnelName = connectionInfo.getBrokername();
				}
			}
			Connection connection = connectionFacade.createNewConnection(
					connectionInfo, resourcetype, resourceid, sessionFacade
							.getCurrentSession().getIdsession(), tunnelName,
					tunnelPort, brokerprotocol);
			connection.setRestrictionstrategy(this.findRestrictionStrategy(
					resourceid, errorContainer));
			this.decorateConnectionWithUser(connection, user, pool);
			return connection;
		default:
			// TODO Postponed.
			break;
		}
		errorContainer[0] = CommonException.UNKNOWN;
		return null;
	}

	public void decorateConnection(Connection connection) {
		if (connection.getSessionid() == sessionFacade.getCurrentSession()
				.getIdsession().intValue()) {
			connection.setRestrictionstrategy(this.findRestrictionStrategy(
					connection.getResourceid(), sessionFacade
							.getCurrentSession().getUserid()));
			// connection.setRestrictionstrategy(this.findRestrictionStrategy(connection.getResourceid(),
			// null));
			Session session = databaseFacade.load(Session.class,
					connection.getSessionid());
			User user = databaseFacade.load(User.class, session.getUserid());
			DesktopPoolEntity pool = desktopPoolFacade
					.loadDesktopPool(connection.getResourceid());
			this.decorateConnectionWithUser(connection, user, pool);
		}
	}

	/**
	 * 根据连接的ID断开连接.
	 * 
	 * @param idconnection
	 *            连接ID.
	 * @return 错误代码.
	 */
	public int disconnectConnection(int idconnection) {
		Connection connection = connectionFacade.findConnection(idconnection);
		if (connection == null) {
			return NOT_FOUND;
		}

		Integer ownerid = sessionFacade.findUserBySession(connection
				.getSessionid());
		if (ownerid != null) {
			if ((!userFacade.isSuperAdmin(sessionFacade.getCurrentSession()
					.getUserid())) && (!userFacade.canManageUser(ownerid))) {
				return FORBIDDEN;
			}
			connection.setUserid(ownerid);
		}
		if (connection.getResourcetype() == 257) {
			this.railUserDataManager.destroyRailConnection(connection
					.getConnectionticket());
		}
		this.destroyConnection(ownerid, connection.getSessionid(),
				connection.getConnectionticket());
		return NO_ERRORS;
	}

	/**
	 * 销毁指定桌面上的所有连接.
	 * 
	 * @param pool
	 * @param iddesktop
	 * @return
	 */
	public Integer destroyConnectionsOfDesktop(int pool, Integer iddesktop,
			boolean toCloneAfterDestroy, boolean toShutdown) {
		List<Connection> connections = connectionFacade.findConnections(
				RESOURCE_TYPE_POOL, pool);
		for (Connection connection : connections) {
			if (numberNotEquals(connection.getResourcetype(),
					RESOURCE_TYPE_POOL)) {
				continue;
			}
			this.findDesktopOfConnection(connection);
			// Also destroy dirty connections.
			if (connection.getDesktopid() == null
					|| numberEquals(connection.getDesktopid(), iddesktop)) {
				connection.setToShutdownAfterDestroy(toShutdown);
				connection.setToCloneAfterDestroy(toCloneAfterDestroy);
				return this.destroyConnection(connection);
			}
		}
		return null;
	}

	public List<Resource> findAssignments(int resourcetype, int resourceid,
			int userid, int organizationid, int groupid, PagingInfo pagingInfo,
			int[] amountContainer) {
		StringBuilder whereClause = new StringBuilder(
				"select distinct v from ResourceAssignmentWithResourceView v where 1 = 1");
		List<Object> params = new ArrayList<Object>();
		if (resourcetype > -1) {
			whereClause.append(" and v.resourcetype = ?");
			params.add(resourcetype);
		}
		if (resourceid > -1) {
			whereClause.append(" and v.resourceid = ?");
			params.add(resourceid);
		}
		if (userid > -1 || organizationid > -1 || groupid > -1) {
			whereClause.append(" and ( 1 = 0");
			if (userid > -1) {
				whereClause
						.append(" or ( v.visitortype = ? and v.visitorid = ? )");
				params.add(RESOURCE_VISITOR_TYPE_USER);
				params.add(userid);
			}
			if (organizationid > -1) {
				whereClause
						.append(" or ( v.visitortype = ? and v.visitorid = ? )");
				params.add(RESOURCE_VISITOR_TYPE_ORGANIZATION);
				params.add(organizationid);
			}
			if (groupid > -1) {
				whereClause
						.append(" or ( v.visitortype = ? and v.visitorid = ? )");
				params.add(RESOURCE_VISITOR_TYPE_GROUP);
				params.add(groupid);
			}
			whereClause.append(" )");
		}
		whereClause.append(FacadeHelper.keyword("v", pagingInfo, params));
		Object[] paramsArray = params.toArray();
		count(databaseFacade, "resource", whereClause, paramsArray,
				amountContainer);
		@SuppressWarnings("unchecked")
		List<Object[]> resourceRecords = pagingFind(
				databaseFacade,
				whereClause.toString().replace("distinct v",
						"distinct v.resource, v.resourcetype, v.resourceid"),
				paramsArray, pagingInfo);
		List<Resource> resources = new LinkedList<Resource>();
		for (Object[] resourceRecord : resourceRecords) {
			Resource resource = new Resource();
			resource.setResourcetype((Integer) resourceRecord[1]);
			resource.setResourceid((Integer) resourceRecord[2]);
			if (numberEquals(resource.getResourcetype(), RESOURCE_TYPE_POOL)) {
				DesktopPoolEntity pool = desktopPoolFacade
						.loadDesktopPool(resource.getResourceid());
				DesktopPoolStatus desktopPoolStatus = desktopPoolStatusFacade
						.findDesktopPoolStatus(resource.getResourceid());
				resource.setResourcename(pool.getPoolname());
				resource.setVmsource(pool.getVmsource());
				resource.setAssignment(pool.getAssignment());
				resource.setCloudmanagerid(pool.getCloudmanagerid());
				if (pool.getCloudmanagerid() > 0) {
					resource.setCloudmanagername(databaseFacade.load(
							CloudManagerEntity.class, pool.getCloudmanagerid())
							.getCloudname());
				}
				if (userid < 0) {
					int allocateddesktops = 0;
					int abnormaldesktops = 0;
					List<Desktop> desktops = desktopFacade.findDesktops(pool
							.getIddesktoppool());
					for (Desktop desktop : desktops) {
						DesktopStatus desktopStatus = desktopStatusFacade
								.findDesktopStatus(desktop.getIddesktop());
						// TODO desktopStatus can be null after destroy?
						if (desktopStatus != null
								&& desktopStatus.getOwnerid() != -1) {
							allocateddesktops++;
							continue;
						}
						if (desktopStatus != null
								&& (desktopStatus.getPhase() != DesktopState.DESKTOP_PHASE_NORMAL || (desktopStatus
										.getStatus() == DesktopState.DESKTOP_STATUS_ERROR || desktopStatus
										.getStatus() == DesktopState.DESKTOP_STATUS_UNKNOWN))) {
							abnormaldesktops++;
						}
					}

					resource.setResourcestatus(pool.getMaxdesktops() <= (allocateddesktops + abnormaldesktops) ? DesktopPoolStatus.DESKTOPPOOL_STATUS_FULL
							: DesktopPoolStatus.DESKTOPPOOL_STATUS_VALID);
				} else {
					DesktopStatus availableDesktop = desktopPoolStatusFacade
							.findAvailableDesktop(userid,
									pool.getIddesktoppool());
					resource.setResourcestatus(availableDesktop == null ? DesktopPoolStatus.DESKTOPPOOL_STATUS_FULL
							: availableDesktop.getOwnerid() < 0 ? DesktopPoolStatus.DESKTOPPOOL_STATUS_VALID
									: pool.getAssignment() == DesktopPoolEntity.DESKTOP_POOL_ASSIGNMENT_FLOATING ? DesktopStatus.DESKTOP_STATUS_CONNECTED
											: (availableDesktop.getPhase() == DesktopState.DESKTOP_PHASE_CREATING ? DesktopStatus.DESKTOP_STATUS_PROVISIONING
													: (availableDesktop
															.getPhase() == DesktopState.DESKTOP_PHASE_DELETING ? DesktopStatus.DESKTOP_STATUS_DESTROYING
															: (availableDesktop
																	.getPhase() == DesktopState.DESKTOP_PHASE_DEFICIENT ? DesktopStatus.DESKTOP_STATUS_ERROR
																	: (availableDesktop
																			.getConnected() == 1 ? DesktopStatus.DESKTOP_STATUS_CONNECTED
																			: (availableDesktop
																					.getStatus() == DesktopState.DESKTOP_STATUS_STARTING ? DesktopStatus.DESKTOP_STATUS_STARTING
																					: (availableDesktop
																							.getStatus() == DesktopState.DESKTOP_STATUS_ERROR ? DesktopStatus.DESKTOP_STATUS_ERROR
																							: (availableDesktop
																									.getStatus() == DesktopState.DESKTOP_STATUS_SERVING ? DesktopStatus.DESKTOP_STATUS_SERVING
																									: (availableDesktop
																											.getStatus() == DesktopState.DESKTOP_STATUS_RUNNING ? DesktopStatus.DESKTOP_STATUS_RUNNING
																											: (availableDesktop
																													.getStatus() == DesktopState.DESKTOP_STATUS_STOPPED ? DesktopStatus.DESKTOP_STATUS_STOPPED
																													: (availableDesktop
																															.getStatus() == DesktopState.DESKTOP_STATUS_STOPPING ? DesktopStatus.DESKTOP_STATUS_STOPPING
																															: DesktopStatus.DESKTOP_STATUS_ERROR)))))))))));
				}
				log.debug("===================================mmmmm===final===============================================>"
						+ resource.getResourcestatus());

				resource.setUsers(this.findUsers(resource.getResourcetype(),
						resource.getResourceid()));
				resource.setOrganizations(this.findOrganizations(
						resource.getResourcetype(), resource.getResourceid()));
				resource.setGroups(this.findGroups(resource.getResourcetype(),
						resource.getResourceid()));
			} else {
				// TODO Postponed.
			}
			resources.add(resource);
		}
		return resources;
	}

	public List<Resource> findUserAssignments(int resourcetype, int resourceid,
			PagingInfo pagingInfo, int[] amountContainer) {
		List<Resource> resources = new LinkedList<Resource>();
		Resource resource = new Resource();
		resource.setResourcetype(resourcetype);
		resource.setResourceid(resourceid);
		DesktopPoolEntity pool = desktopPoolFacade.loadDesktopPool(resource
				.getResourceid());
		DesktopPoolStatus desktopPoolStatus = desktopPoolStatusFacade
				.findDesktopPoolStatus(resource.getResourceid());
		resource.setResourcename(pool.getPoolname());
		resource.setVmsource(pool.getVmsource());
		resource.setAssignment(pool.getAssignment());
		resource.setResourcestatus(desktopPoolStatus.getStatus());
		PagingInfo pagingInfoWithFirstPage = new PagingInfo() {
		};
		pagingInfoWithFirstPage.copyFrom(pagingInfo);
		pagingInfoWithFirstPage.setPage(1);
		resource.setUsers(this.findUsers(resource.getResourcetype(),
				resource.getResourceid(), pagingInfo, amountContainer));
		resource.setOrganizations(this.findOrganizations(
				resource.getResourcetype(), resource.getResourceid(),
				pagingInfoWithFirstPage, new int[1]));
		resource.setGroups(this.findGroups(resource.getResourcetype(),
				resource.getResourceid(), pagingInfoWithFirstPage, new int[1]));
		resources.add(resource);
		return resources;
	}

	public List<Resource> findOrganizationAssignments(int resourcetype,
			int resourceid, PagingInfo pagingInfo, int[] amountContainer) {
		List<Resource> resources = new LinkedList<Resource>();
		Resource resource = new Resource();
		resource.setResourcetype(resourcetype);
		resource.setResourceid(resourceid);
		DesktopPoolEntity pool = desktopPoolFacade.loadDesktopPool(resource
				.getResourceid());
		DesktopPoolStatus desktopPoolStatus = desktopPoolStatusFacade
				.findDesktopPoolStatus(resource.getResourceid());
		resource.setResourcename(pool.getPoolname());
		resource.setVmsource(pool.getVmsource());
		resource.setAssignment(pool.getAssignment());
		resource.setResourcestatus(desktopPoolStatus.getStatus());
		PagingInfo pagingInfoWithFirstPage = new PagingInfo() {
		};
		pagingInfoWithFirstPage.copyFrom(pagingInfo);
		pagingInfoWithFirstPage.setPage(1);
		resource.setUsers(this.findUsers(resource.getResourcetype(),
				resource.getResourceid(), pagingInfoWithFirstPage, new int[1]));
		resource.setOrganizations(this.findOrganizations(
				resource.getResourcetype(), resource.getResourceid(),
				pagingInfo, amountContainer));
		resource.setGroups(this.findGroups(resource.getResourcetype(),
				resource.getResourceid(), pagingInfoWithFirstPage, new int[1]));
		resources.add(resource);
		return resources;
	}

	public List<Resource> findGroupAssignments(int resourcetype,
			int resourceid, PagingInfo pagingInfo, int[] amountContainer) {
		List<Resource> resources = new LinkedList<Resource>();
		Resource resource = new Resource();
		resource.setResourcetype(resourcetype);
		resource.setResourceid(resourceid);
		DesktopPoolEntity pool = desktopPoolFacade.loadDesktopPool(resource
				.getResourceid());
		DesktopPoolStatus desktopPoolStatus = desktopPoolStatusFacade
				.findDesktopPoolStatus(resource.getResourceid());
		resource.setResourcename(pool.getPoolname());
		resource.setVmsource(pool.getVmsource());
		resource.setAssignment(pool.getAssignment());
		resource.setResourcestatus(desktopPoolStatus.getStatus());
		PagingInfo pagingInfoWithFirstPage = new PagingInfo() {
		};
		pagingInfoWithFirstPage.copyFrom(pagingInfo);
		pagingInfoWithFirstPage.setPage(1);
		resource.setUsers(this.findUsers(resource.getResourcetype(),
				resource.getResourceid(), pagingInfoWithFirstPage, new int[1]));
		resource.setOrganizations(this.findOrganizations(
				resource.getResourcetype(), resource.getResourceid(),
				pagingInfoWithFirstPage, new int[1]));
		resource.setGroups(this.findGroups(resource.getResourcetype(),
				resource.getResourceid(), pagingInfo, amountContainer));
		resources.add(resource);
		return resources;
	}

	public void destroyUnforwardingConnections() {
		@SuppressWarnings("unchecked")
		List<Connection> connections = (List<Connection>) databaseFacade
				.find("from Connection");
		for (Connection connection : connections) {
			if (!connectionManager.isForwarding(connection.getBrokername(),
					connection.getBrokerport())) {
				try {
					this.destroyConnection(connection);
				} catch (Exception e) {
					log.warn("Exception", e);
				}
			}
		}
	}

	public void destroyAllConnections() {
		@SuppressWarnings("unchecked")
		List<Connection> connections = (List<Connection>) databaseFacade
				.find("from Connection");
		for (Connection connection : connections) {
			this.destroyConnection(connection);
		}
	}

	public void disconnectUnassignedDesktops(int user, boolean all) {
		List<Integer> sessions = sessionFacade.findSessionsByUserId(user);
		for (Integer session : sessions) {
			List<Connection> connections = connectionFacade
					.findConnectionsBySession(session);
			for (Connection connection : connections) {
				if (connection.getResourcetype() == RESOURCE_TYPE_POOL) {
					if (all
							|| checkIfUserCanNoLongerOwnThePool(user,
									connection.getResourceid())) {
						connection.setUserid(user);
						connection.setDesktopid(desktopPoolStatusFacade
								.findConnectedDesktop(connection.getUserid(),
										connection.getResourceid()));
						connection.setUserCanNoLongerOwnThePool(true);
						// Ignoring the result.
						this.destroyConnection(connection);
					}
				} else {
					// TODO
				}
			}
		}
		// FIXME Move and refactor it.
		@SuppressWarnings("unchecked")
		List<Desktop> ownedDesktops = (List<Desktop>) databaseFacade.find(
				"from Desktop where ownerid = ?", user);
		for (Desktop desktop : ownedDesktops) {
			if (all
					|| checkIfUserCanNoLongerOwnThePool(user,
							desktop.getDesktoppoolid())) {
				DesktopPoolEntity pool = desktopPoolFacade
						.loadDesktopPool(desktop.getDesktoppoolid());
				desktopFacade.unassign(pool, desktop.getIddesktop(), user,
						true, false);
			}
		}
	}

	public int attachUserVolumes(int poolid, Integer uservolumeid) {
		if (runtimeVariableFacade.findIfServiceIsStopped()) {
			return FORBIDDEN;
		}
		User user = databaseFacade.load(User.class, sessionFacade
				.getCurrentSession().getUserid());
		Connection connection = null;
		int session = sessionFacade.getCurrentSession().getIdsession();
		List<Connection> connections = connectionFacade
				.findConnectionsBySession(session);
		for (Connection existingConnection : connections) {
			if (numberEquals(existingConnection.getResourcetype(),
					ResourceAssignment.RESOURCE_TYPE_POOL)
					&& numberEquals(existingConnection.getResourceid(), poolid)) {
				log.trace("Connection found.");
				connection = existingConnection;
				break;
			}
		}
		if (connection == null) {
			return NOT_FOUND;
		}
		int desktopid = desktopPoolStatusFacade.findConnectedDesktop(
				sessionFacade.getCurrentSession().getUserid(),
				connection.getResourceid());
		DesktopPoolEntity pool = desktopPoolFacade.loadDesktopPool(poolid);
		if (pool.getVmsource() == DesktopPoolEntity.DESKTOP_POOL_SOURCE_MANUAL) {
			return CONFLICT;
		}
		Desktop desktop = databaseFacade.load(Desktop.class, desktopid);
		return desktopFacade.attachUserVolume(user.getIduser(),
				pool.getCloudmanagerid(), uservolumeid, desktop);
	}

	public int detachUserVolumes(Integer uservolumeid, boolean force) {
		if (runtimeVariableFacade.findIfServiceIsStopped()) {
			return FORBIDDEN;
		}
		User user = databaseFacade.load(User.class, sessionFacade
				.getCurrentSession().getUserid());
		return desktopFacade.detachUserVolumes(user.getIduser(), uservolumeid);
	}

	public List<UserVolume> findOwnedVolumes(int[] errorContainer) {
		List<UserVolume> list = desktopFacade.findUserVolumes(sessionFacade
				.getCurrentSession().getUserid(), errorContainer);
		for (UserVolume userVolume : list) {
			userVolume.setVolumeid(userVolume.getIduservolume());
			userVolume.setResourceid(userVolume.getDesktoppoolid());
		}
		return list;
	}

	public int eraseOwnedVolumes(Integer uservolumeid) {
		return desktopFacade.eraseUserVolumes(sessionFacade.getCurrentSession()
				.getUserid(), uservolumeid);
	}

	public int stopAssignedDesktop(int resourceid, int[] errorContainer) {
		DesktopStatus desktopStatus = desktopPoolStatusFacade
				.findAssignedDesktopStatus(resourceid, sessionFacade
						.getCurrentSession().getUserid());
		if (desktopStatus == null) {
			errorContainer[0] = NOT_FOUND;
			return -1;
		}
		return desktopFacade.stopDesktop(desktopStatus.getIddesktop(),
				errorContainer);
	}

	public int startAssignedDesktop(int resourceid, int[] errorContainer) {
		DesktopStatus desktopStatus = desktopPoolStatusFacade
				.findAssignedDesktopStatus(resourceid, sessionFacade
						.getCurrentSession().getUserid());
		if (desktopStatus == null) {
			errorContainer[0] = NOT_FOUND;
			return -1;
		}
		return desktopFacade.startDesktop(desktopStatus.getIddesktop(),
				errorContainer);
	}

	public int restartAssignedDesktop(int resourceid, int[] errorContainer) {
		DesktopStatus desktopStatus = desktopPoolStatusFacade
				.findAssignedDesktopStatus(resourceid, sessionFacade
						.getCurrentSession().getUserid());
		if (desktopStatus == null) {
			errorContainer[0] = NOT_FOUND;
			return -1;
		}
		return desktopFacade.rebootDesktop(desktopStatus.getIddesktop(),
				errorContainer);
	}

	private RestrictionStrategy findRestrictionStrategy(int resourceid,
			int userid) {
		Integer userRestrictionStrategyId = (Integer) databaseFacade
				.findFirst(
						"select restrictionstrategyid from RestrictionStrategyAssignment where targettype = ? and targetid = ?",
						RestrictionStrategyAssignment.RESTRICTION_STRATEGY_ASSIGNMENT_TARGET_TYPE_USER,
						userid);
		if (userRestrictionStrategyId != null) {
			return this.findRestrictionStrategy(userRestrictionStrategyId);
		}
		return this.findRestrictionStrategy(resourceid, null);
	}

	public RestrictionStrategy findRestrictionStrategy(int resourceid,
			int[] errorContainer) {
		return this
				.findRestrictionStrategy((Integer) databaseFacade
						.findFirst(
								"select restrictionstrategyid from RestrictionStrategyAssignment where targettype = ? and targetid = ?",
								RestrictionStrategyAssignment.RESTRICTION_STRATEGY_ASSIGNMENT_TARGET_TYPE_RESOURCE,
								resourceid));
	}

	private RestrictionStrategy findRestrictionStrategy(
			int restrictionStrategyId) {
		log.debug("findRestrictionStrategy :::=====> " + restrictionStrategyId);
		RestrictionStrategy restrictionStrategy = databaseFacade.load(
				RestrictionStrategy.class, restrictionStrategyId);

		@SuppressWarnings("unchecked")
		List<USBListItem> items = (List<USBListItem>) databaseFacade.find(
				"from USBListItem where restrictionstrategyid = ?",
				restrictionStrategy.getIdrestrictionstrategy());
		restrictionStrategy.setUsbclasswhitelist(new LinkedList<USBListItem>());
		restrictionStrategy.setUsbclassblacklist(new LinkedList<USBListItem>());
		restrictionStrategy
				.setUsbdevicewhitelist(new LinkedList<USBListItem>());
		restrictionStrategy
				.setUsbdeviceblacklist(new LinkedList<USBListItem>());
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
		return restrictionStrategy;
	}

	public Integer findFirstAssignedPoolOfGroup(int groupid) {
		return (Integer) databaseFacade
				.findFirst(
						"select resourceid from ResourceAssignment where resourcetype = ? and visitortype = ? and visitorid = ?",
						ResourceAssignment.RESOURCE_TYPE_POOL,
						ResourceAssignment.RESOURCE_VISITOR_TYPE_GROUP, groupid);
	}

	public Integer findDomainIdOfPool(int poolid) {
		DesktopPoolEntity pool = databaseFacade.load(DesktopPoolEntity.class,
				poolid);
		return (Integer) databaseFacade.findFirst(
				"select iddomain from Domain where domainname = ?",
				pool.getDomainname() == null ? "" : pool.getDomainname());
	}

	public Integer destroyConnectionNA(final int userid, final int sessionid,
			final String connectionticket) {
		final Connection connection = (Connection) databaseFacade.findFirst(
				"from Connection where sessionid = ? and connectionticket = ?",
				sessionid, connectionticket);
		final DesktopPoolEntity desktopPool = databaseFacade.load(
				DesktopPoolEntity.class, connection.getResourceid());
		final Integer desktopid = desktopPoolStatusFacade.findConnectedDesktop(
				userid, connection.getResourceid());
		final Desktop desktop = databaseFacade.load(Desktop.class, desktopid);
		try {
			final OperationContext operationContext = new OperationContext();
			operationContext.setOperationRegistry(operationRegistry);
			new DisconnectDesktopOperation(connection.getResourceid(),
					desktopid, sessionid, null).execute(operationContext);
			return NO_ERRORS;
		} catch (CommonException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return e.getError();
		}
	}

	public Integer destroyConnection(final int userid, final int sessionid,
			final String connectionticket) {
		final Connection connection = (Connection) databaseFacade.findFirst(
				"from Connection where sessionid = ? and connectionticket = ?",
				sessionid, connectionticket);
		final DesktopPoolEntity desktopPool = databaseFacade.load(
				DesktopPoolEntity.class, connection.getResourceid());
		final Integer desktopid = desktopPoolStatusFacade.findConnectedDesktop(
				userid, connection.getResourceid());
		final Desktop desktop = databaseFacade.load(Desktop.class, desktopid);
		try {
			operationRegistry.start(new DisconnectDesktopOperation(connection
					.getResourceid(), desktopid, sessionid, null));
			return NO_ERRORS;
		} catch (CommonException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return e.getError();
		}
	}

	public void setDatabaseFacade(DatabaseFacade databaseFacade) {
		this.databaseFacade = databaseFacade;
	}

	public void setOrganizationFacade(OrganizationFacade organizationFacade) {
		this.organizationFacade = organizationFacade;
	}

	public void setGroupFacade(GroupFacade groupFacade) {
		this.groupFacade = groupFacade;
	}

	public void setUserFacade(UserFacade userFacade) {
		this.userFacade = userFacade;
	}

	public void setDesktopFacade(DesktopFacade desktopFacade) {
		this.desktopFacade = desktopFacade;
	}

	public void setDesktopPoolFacade(DesktopPoolFacade desktopPoolFacade) {
		this.desktopPoolFacade = desktopPoolFacade;
	}

	public void setConnectionManager(ConnectionManager connectionManager) {
		this.connectionManager = connectionManager;
	}

	public void setConnectionFacade(ConnectionFacade connectionFacade) {
		this.connectionFacade = connectionFacade;
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

	public void setRuntimeVariableFacade(
			RuntimeVariableFacade runtimeVariableFacade) {
		this.runtimeVariableFacade = runtimeVariableFacade;
	}

	public void setConfiguration(Configuration configuration) {
		this.configuration = configuration;
	}

	public void setOperationRegistry(OperationRegistry operationRegistry) {
		this.operationRegistry = operationRegistry;
	}

	private Integer destroyConnection(final Connection connection) {
		final DesktopPoolEntity desktopPool = databaseFacade.load(
				DesktopPoolEntity.class, connection.getResourceid());
		final Session session = databaseFacade.load(Session.class,
				connection.getSessionid());
		final Integer desktopid = desktopPoolStatusFacade.findConnectedDesktop(
				session.getUserid(), connection.getResourceid());
		final Desktop desktop = databaseFacade.load(Desktop.class, desktopid);
		try {
			operationRegistry.start(new DisconnectDesktopOperation(connection
					.getResourceid(), desktopid, session.getIdsession(), null));
		} catch (CommonException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
		// Integer destroyingJobId = null;
		// String brokerIP = configuration.getBrokerIP();
		// int error =
		// connectionManager.destroyConnection(connection.getBrokerport(),
		// connection.getHostname(), connection.getHostport(), brokerIP,
		// connection.getTunnelname() != null);
		// if (numberNotEquals(error, NO_ERRORS)) {
		// // Logging and ignoring the error.
		// log.warn("Destroy connection {} failed with error {}.",
		// connection.getIdconnection(), error);
		// }
		// connectionFacade.removeConnection(connection);
		// switch (connection.getResourcetype()) {
		// case RESOURCE_TYPE_POOL:
		// final DesktopPoolEntity pool =
		// desktopPoolFacade.loadDesktopPool(connection.getResourceid());
		// if (connection.getDesktopid() == null) {
		// if (connection.getUserid() == null) {
		// connection.setUserid(sessionFacade.findUserBySession(connection.getSessionid()));
		// }
		// if (connection.getUserid() != null) {
		// connection.setDesktopid(desktopPoolStatusFacade.findConnectedDesktop(connection.getUserid(),
		// connection.getResourceid()));
		// }
		// }
		// if (connection.getDesktopid() != null) {
		// // TODO Cache.
		// boolean userCanNoLongerOwnThePool =
		// connection.isUserCanNoLongerOwnThePool()
		// || numberNotEquals(pool.getAssignment(),
		// DESKTOP_POOL_ASSIGNMENT_DEDICATED)
		// || this.checkIfUserCanNoLongerOwnThePool(connection.getUserid(),
		// connection.getResourceid());
		// desktopStatusFacade.disconnectDesktop(connection.getDesktopid(),
		// pool.getAssignment());
		// if (userCanNoLongerOwnThePool) {
		// if (numberNotEquals(pool.getAssignment(),
		// DESKTOP_POOL_ASSIGNMENT_DEDICATED)
		// && pool.getUnassignmentdelay() > 0) {
		// Thread thread = new Thread() {
		// @Override
		// public void run() {
		// try {
		// Thread.sleep(pool.getUnassignmentdelay());
		// } catch (InterruptedException e) {}
		// DesktopStatus desktopStatus =
		// databaseFacade.load(DesktopStatus.class, connection.getDesktopid());
		// DesktopPoolStatus desktopPoolStatus =
		// databaseFacade.load(DesktopPoolStatus.class,
		// pool.getIddesktoppool());
		// if (desktopStatus.getStatus() !=
		// DesktopStatus.DESKTOP_STATUS_CONNECTED
		// && desktopPoolStatus.getStatus() !=
		// DesktopPoolStatus.DESKTOPPOOL_STATUS_MAINTAIN) {
		// desktopFacade.unassign(pool, connection.getDesktopid(),
		// connection.getUserid(), connection.isToCloneAfterDestroy(),
		// connection.isToShutdownAfterDestroy());
		// }
		// }
		// @Override
		// public String toString() {
		// return super.toString() + "THREAD delayed unassign " +
		// connection.getUserid() + " " + connection.getDesktopid() + " " +
		// connection.getResourceid();
		// }
		// };
		// com.opzoon.ohvc.session.ExcecutorUtil.execute(thread);
		// } else {
		// destroyingJobId = desktopFacade.unassign(pool,
		// connection.getDesktopid(), connection.getUserid(),
		// connection.isToCloneAfterDestroy(),
		// connection.isToShutdownAfterDestroy());
		// }
		// }
		// Thread thread = new Thread() {
		// @Override
		// public void run() {
		// desktopFacade.logOff(connection.getUserid(),
		// connection.getDesktopid(), connection.getResourceid());
		// }
		// @Override
		// public String toString() {
		// return super.toString() + "THREAD logOff " + connection.getUserid() +
		// " " + connection.getDesktopid() + " " + connection.getResourceid();
		// }
		// };
		// com.opzoon.ohvc.session.ExcecutorUtil.execute(thread);
		// }
		// final User user = databaseFacade.load(User.class,
		// connection.getUserid());
		// Thread thread = new Thread() {
		// @Override
		// public void run() {
		// desktopFacade.detachUserVolumes(user.getIduser(), null);
		// }
		// @Override
		// public String toString() {
		// return super.toString() + "THREAD detachUserVolume " +
		// user.getIduser();
		// }
		// };
		// com.opzoon.ohvc.session.ExcecutorUtil.execute(thread);
		// break;
		// case RESOURCE_TYPE_APPLICATION:
		// /*************************************************
		// * maxiaochao /
		// ************************************************/
		// this.databaseFacade.update("delete from UserAndResourcestatus where id=?",
		// connection.getConnectionticket());
		// log.warn(RESOURCE_TYPE_APPLICATION +
		// "{ delete from UserAndResourcestatus where id= ?} [" +
		// connection.getConnectionticket() + "]");
		// /*************************************************
		// * maxiaochao /
		// ************************************************/
		// break;
		// default:
		// // TODO Postponed.
		// break;
		// }
		// return destroyingJobId;
	}

	private boolean checkIfUserCanNoLongerOwnThePool(Integer userid,
			Integer desktoppoolid) {
		if (userid == null) {
			return false;
		}
		if (userFacade.userNotExists(userid)) {
			return true;
		}
		// TODO Cache.
		List<Resource> resources = this.findResources(RESOURCE_TYPE_POOL,
				userid, true);
		for (Resource resource : resources) {
			if (resource.getResourcestatus() == DesktopPoolStatus.DESKTOPPOOL_STATUS_VALID
					&& resource.getResourcetype() == RESOURCE_TYPE_POOL
					&& resource.getResourceid() == desktoppoolid) {
				return false;
			}
		}
		return true;
	}

	private void findDesktopOfConnection(Connection connection) {
		Integer userid = connection.getUserid() == null ? sessionFacade
				.findUserBySession(connection.getSessionid()) : connection
				.getUserid();
		if (userid != null) {
			Integer desktopid = desktopPoolStatusFacade.findConnectedDesktop(
					userid, connection.getResourceid());
			if (desktopid != null) {
				// Delayed userid setting for avoid repeat
				// findConnectedDesktop().
				connection.setUserid(userid);
				connection.setDesktopid(desktopid);
			}
		}
	}

	private List<UserInfo> findUsers(int resourcetype, int resourceid) {
		@SuppressWarnings("unchecked")
		List<ResourceAssignmentWithResourceView> resourceAssignments = (List<ResourceAssignmentWithResourceView>) databaseFacade
				.find("from ResourceAssignmentWithResourceView where visitortype = ? and resourcetype = ? and resourceid = ? order by visitorname",
						RESOURCE_VISITOR_TYPE_USER, resourcetype, resourceid);
		List<UserInfo> users = new LinkedList<UserInfo>();
		for (ResourceAssignmentWithResourceView resourceAssignment : resourceAssignments) {
			UserInfo userInfo = new UserInfo();
			User user = databaseFacade.load(User.class,
					resourceAssignment.getVisitorid());
			users.add(userInfo);
			userInfo.setUserid(user.getIduser());
			userInfo.setUsername(user.getUsername());
			userInfo.setRealname(user.getRealname());
			userInfo.setUsernotes(user.getNotes());
			userInfo.setRootadmin(userFacade.isSuperAdmin(userInfo.getUserid()) ? 1
					: 0);
			if (user.getOrganizationid() != null) {
				userInfo.setOrganizationid(user.getOrganizationid());
				Organization organization = databaseFacade.load(
						Organization.class, user.getOrganizationid());
				userInfo.setOrganizationname(organization.getOrganizationname());
			}
			userInfo.setDomainname(databaseFacade.load(Domain.class,
					user.getDomainid()).getDomainname());
		}
		return users;
	}

	private List<UserInfo> findUsers(int resourcetype, int resourceid,
			PagingInfo pagingInfo, int[] amountContainer) {
		String whereClause = "from ResourceAssignment where visitortype = ? and resourcetype = ? and resourceid = ?";
		Object[] paramsArray = new Object[] { RESOURCE_VISITOR_TYPE_USER,
				resourcetype, resourceid };
		count(databaseFacade, "idresourceassignment", new StringBuilder(
				whereClause), paramsArray, amountContainer);
		@SuppressWarnings("unchecked")
		List<ResourceAssignment> resourceAssignments = pagingFind(
				databaseFacade, whereClause, paramsArray, pagingInfo);
		List<UserInfo> users = new LinkedList<UserInfo>();
		for (ResourceAssignment resourceAssignment : resourceAssignments) {
			UserInfo userInfo = new UserInfo();
			User user = databaseFacade.load(User.class,
					resourceAssignment.getVisitorid());
			users.add(userInfo);
			userInfo.setUserid(user.getIduser());
			userInfo.setUsername(user.getUsername());
			userInfo.setRealname(user.getRealname());
			userInfo.setUsernotes(user.getNotes());
			userInfo.setRootadmin(userFacade.isSuperAdmin(userInfo.getUserid()) ? 1
					: 0);
			if (user.getOrganizationid() != null) {
				userInfo.setOrganizationid(user.getOrganizationid());
				Organization organization = databaseFacade.load(
						Organization.class, user.getOrganizationid());
				userInfo.setOrganizationname(organization.getOrganizationname());
			}
			userInfo.setDomainname(databaseFacade.load(Domain.class,
					user.getDomainid()).getDomainname());
		}
		return users;
	}

	private List<OrganizationInfo> findOrganizations(int resourcetype,
			int resourceid) {
		@SuppressWarnings("unchecked")
		List<ResourceAssignmentWithResourceView> resourceAssignments = (List<ResourceAssignmentWithResourceView>) databaseFacade
				.find("from ResourceAssignmentWithResourceView where visitortype = ? and resourcetype = ? and resourceid = ? order by visitorname",
						RESOURCE_VISITOR_TYPE_ORGANIZATION, resourcetype,
						resourceid);
		List<OrganizationInfo> organizations = new LinkedList<OrganizationInfo>();
		for (ResourceAssignmentWithResourceView resourceAssignment : resourceAssignments) {
			OrganizationInfo organizationInfo = new OrganizationInfo();
			Organization organization = databaseFacade.load(Organization.class,
					resourceAssignment.getVisitorid());
			organizations.add(organizationInfo);
			organizationInfo
					.setOrganizationid(organization.getIdorganization());
			organizationInfo.setOrganizationtype(organization
					.getOrganizationtype());
			organizationInfo.setDomainid(organization.getDomainid());
			organizationInfo.setOrganizationname(organization
					.getOrganizationname());
			organizationInfo.setOrganizationnotes(organization.getNotes());
			organizationInfo.setDomainname(databaseFacade.load(Domain.class,
					organization.getDomainid()).getDomainname());
		}
		return organizations;
	}

	private List<OrganizationInfo> findOrganizations(int resourcetype,
			int resourceid, PagingInfo pagingInfo, int[] amountContainer) {
		String whereClause = "from ResourceAssignment where visitortype = ? and resourcetype = ? and resourceid = ?";
		Object[] paramsArray = new Object[] {
				RESOURCE_VISITOR_TYPE_ORGANIZATION, resourcetype, resourceid };
		count(databaseFacade, "idresourceassignment", new StringBuilder(
				whereClause), paramsArray, amountContainer);
		@SuppressWarnings("unchecked")
		List<ResourceAssignment> resourceAssignments = pagingFind(
				databaseFacade, whereClause, paramsArray, pagingInfo);
		List<OrganizationInfo> organizations = new LinkedList<OrganizationInfo>();
		for (ResourceAssignment resourceAssignment : resourceAssignments) {
			OrganizationInfo organizationInfo = new OrganizationInfo();
			Organization organization = databaseFacade.load(Organization.class,
					resourceAssignment.getVisitorid());
			organizations.add(organizationInfo);
			organizationInfo
					.setOrganizationid(organization.getIdorganization());
			organizationInfo.setOrganizationtype(organization
					.getOrganizationtype());
			organizationInfo.setDomainid(organization.getDomainid());
			organizationInfo.setOrganizationname(organization
					.getOrganizationname());
			organizationInfo.setOrganizationnotes(organization.getNotes());
			organizationInfo.setDomainname(databaseFacade.load(Domain.class,
					organization.getDomainid()).getDomainname());
		}
		return organizations;
	}

	private List<GroupInfo> findGroups(int resourcetype, int resourceid) {
		List<GroupInfo> groupIdAndNames = new LinkedList<GroupInfo>();
		@SuppressWarnings("unchecked")
		List<Object[]> groups = (List<Object[]>) databaseFacade
				.findByNativeSQL(
						"select g.idgroup, g.groupname, g.notes from groups g inner join resourceassignment a on g.idgroup = a.visitorid where a.visitortype = ? and a.resourcetype = ? and a.resourceid = ? order by g.groupname",
						RESOURCE_VISITOR_TYPE_GROUP, resourcetype, resourceid);
		for (Object[] group : groups) {
			GroupInfo groupInfo = new GroupInfo();
			groupInfo.setGroupid((Integer) group[0]);
			groupInfo.setGroupname((String) group[1]);
			groupInfo.setGroupnotes((String) group[2]);
			groupInfo.setDomaintype(Domain.DOMAIN_TYPE_LOCAL);
			groupInfo
					.setUseramount(((Number) databaseFacade
							.findFirst(
									"select count(elementid) from GroupElement where groupid = ? and elementtype = ?",
									groupInfo.getGroupid(),
									GroupElement.ELEMENT_TYPE_USER)).intValue());
			groupIdAndNames.add(groupInfo);
		}
		return groupIdAndNames;
	}

	private List<GroupInfo> findGroups(int resourcetype, int resourceid,
			PagingInfo pagingInfo, int[] amountContainer) {
		List<GroupInfo> groupIdAndNames = new LinkedList<GroupInfo>();
		String whereClause = "from ResourceAssignment where visitortype = ? and resourcetype = ? and resourceid = ?";
		Object[] paramsArray = new Object[] { RESOURCE_VISITOR_TYPE_GROUP,
				resourcetype, resourceid };
		count(databaseFacade, "idresourceassignment", new StringBuilder(
				whereClause), paramsArray, amountContainer);
		@SuppressWarnings("unchecked")
		List<ResourceAssignment> resourceAssignments = pagingFind(
				databaseFacade, whereClause, paramsArray, pagingInfo);
		for (ResourceAssignment resourceAssignment : resourceAssignments) {
			GroupInfo groupInfo = new GroupInfo();
			Group group = databaseFacade.load(Group.class,
					resourceAssignment.getVisitorid());
			groupInfo.setGroupid(group.getIdgroup());
			groupInfo.setGroupname(group.getGroupname());
			groupInfo.setGroupnotes(group.getNotes());
			groupInfo.setDomaintype(Domain.DOMAIN_TYPE_LOCAL);
			groupInfo
					.setUseramount(((Number) databaseFacade
							.findFirst(
									"select count(elementid) from GroupElement where groupid = ? and elementtype = ?",
									groupInfo.getGroupid(),
									GroupElement.ELEMENT_TYPE_USER)).intValue());
			groupIdAndNames.add(groupInfo);
		}
		return groupIdAndNames;
	}

	private void decorateConnectionWithUser(Connection connection, User user,
			DesktopPoolEntity pool) {
		// connection.setUsername("");
		// connection.setPassword("");
		connection.setUsername(user.getUsername());
		connection.setPassword(sessionFacade.getCurrentSession().getPassword());
		if (pool.getDomainname() != null) {
			connection.setDomain(pool.getDomainname());
		} else {
			connection.setDomain("");
		}
	}

	/**
	 * 资源.
	 */
	public static class Resource {

		/**
		 * 资源状态: 可用.
		 */
		// /public static final int RESOURCE_STATUS_AVAILABLE = 0x000;
		/**
		 * 资源状态: 不可用.
		 */
		// /public static final int RESOURCE_STATUS_UNAVAILABLE = 0x100;

		private int resourcetype;
		private int resourceid;
		private String resourcename;
		private int resourcestatus;
		private String resourceicon;
		private int vmsource;
		private int assignment;
		private List<UserInfo> users;
		private List<OrganizationInfo> organizations;
		private List<GroupInfo> groups;
		private DesktopStatus availableDesktop;
		private int availableprotocols;
		private int cloudmanagerid;
		private String cloudmanagername;
		private List<Integer> attachablevolumes;

		// FIXME
		private String applicationname, applicationversion, applicationicon,
				applicationid;
		private Boolean published;
		private Integer status;
		private Integer replication;

		public int getResourcetype() {
			return resourcetype;
		}

		public void setResourcetype(int resourcetype) {
			this.resourcetype = resourcetype;
		}

		public int getResourceid() {
			return resourceid;
		}

		public void setResourceid(int resourceid) {
			this.resourceid = resourceid;
		}

		public String getResourcename() {
			return resourcename;
		}

		public void setResourcename(String resourcename) {
			this.resourcename = resourcename;
		}

		public int getResourcestatus() {
			return resourcestatus;
		}

		public void setResourcestatus(int resourcestatus) {
			this.resourcestatus = resourcestatus;
		}

		public String getResourceicon() {
			return resourceicon;
		}

		public void setResourceicon(String resourceicon) {
			this.resourceicon = resourceicon;
		}

		public int getVmsource() {
			return vmsource;
		}

		public void setVmsource(int vmsource) {
			this.vmsource = vmsource;
		}

		public int getAssignment() {
			return assignment;
		}

		public void setAssignment(int assignment) {
			this.assignment = assignment;
		}

		public List<UserInfo> getUsers() {
			return users;
		}

		public void setUsers(List<UserInfo> users) {
			this.users = users;
		}

		public List<OrganizationInfo> getOrganizations() {
			return organizations;
		}

		public void setOrganizations(List<OrganizationInfo> organizations) {
			this.organizations = organizations;
		}

		public List<GroupInfo> getGroups() {
			return groups;
		}

		public void setGroups(List<GroupInfo> groups) {
			this.groups = groups;
		}

		public DesktopStatus getAvailableDesktop() {
			return availableDesktop;
		}

		public void setAvailableDesktop(DesktopStatus availableDesktop) {
			this.availableDesktop = availableDesktop;
		}

		public int getAvailableprotocols() {
			return availableprotocols;
		}

		public void setAvailableprotocols(int availableprotocols) {
			this.availableprotocols = availableprotocols;
		}

		public int getCloudmanagerid() {
			return cloudmanagerid;
		}

		public void setCloudmanagerid(int cloudmanagerid) {
			this.cloudmanagerid = cloudmanagerid;
		}

		public String getCloudmanagername() {
			return cloudmanagername;
		}

		public void setCloudmanagername(String cloudmanagername) {
			this.cloudmanagername = cloudmanagername;
		}

		public List<Integer> getAttachablevolumes() {
			return attachablevolumes;
		}

		public void setAttachablevolumes(List<Integer> attachablevolumes) {
			this.attachablevolumes = attachablevolumes;
		}

		// FIXME
		public String getApplicationname() {
			return applicationname;
		}

		public void setApplicationname(String applicationname) {
			this.applicationname = applicationname;
		}

		public String getApplicationversion() {
			return applicationversion;
		}

		public void setApplicationversion(String applicationversion) {
			this.applicationversion = applicationversion;
		}

		public String getApplicationicon() {
			return applicationicon;
		}

		public void setApplicationicon(String applicationicon) {
			this.applicationicon = applicationicon;
		}

		public String getApplicationid() {
			return applicationid;
		}

		public void setApplicationid(String applicationid) {
			this.applicationid = applicationid;
		}

		public Boolean getPublished() {
			return published;
		}

		public void setPublished(Boolean published) {
			this.published = published;
		}

		public Integer getStatus() {
			return status;
		}

		public void setStatus(Integer status) {
			this.status = status;
		}

		public Integer getReplication() {
			return replication;
		}

		public void setReplication(Integer replication) {
			this.replication = replication;
		}

	}

	public static class UserInfo {

		private int userid;
		private String username;
		private String realname;
		private String usernotes;
		private int rootadmin;
		private Integer organizationid;
		private String organizationname;
		private String domainname;

		public int getUserid() {
			return userid;
		}

		public void setUserid(int userid) {
			this.userid = userid;
		}

		public String getUsername() {
			return username;
		}

		public void setUsername(String username) {
			this.username = username;
		}

		public String getRealname() {
			return realname;
		}

		public void setRealname(String realname) {
			this.realname = realname;
		}

		public String getUsernotes() {
			return usernotes;
		}

		public void setUsernotes(String usernotes) {
			this.usernotes = usernotes;
		}

		public int getRootadmin() {
			return rootadmin;
		}

		public void setRootadmin(int rootadmin) {
			this.rootadmin = rootadmin;
		}

		public Integer getOrganizationid() {
			return organizationid;
		}

		public void setOrganizationid(Integer organizationid) {
			this.organizationid = organizationid;
		}

		public String getOrganizationname() {
			return organizationname;
		}

		public void setOrganizationname(String organizationname) {
			this.organizationname = organizationname;
		}

		public String getDomainname() {
			return domainname;
		}

		public void setDomainname(String domainname) {
			this.domainname = domainname;
		}

	}

	public static class OrganizationInfo {

		private int organizationid;
		private int organizationtype;
		private int domainid;
		private String organizationname;
		private String organizationnotes;
		private String domainname;

		public int getOrganizationid() {
			return organizationid;
		}

		public void setOrganizationid(int organizationid) {
			this.organizationid = organizationid;
		}

		public int getOrganizationtype() {
			return organizationtype;
		}

		public void setOrganizationtype(int organizationtype) {
			this.organizationtype = organizationtype;
		}

		public int getDomainid() {
			return domainid;
		}

		public void setDomainid(int domainid) {
			this.domainid = domainid;
		}

		public String getOrganizationname() {
			return organizationname;
		}

		public void setOrganizationname(String organizationname) {
			this.organizationname = organizationname;
		}

		public String getOrganizationnotes() {
			return organizationnotes;
		}

		public void setOrganizationnotes(String organizationnotes) {
			this.organizationnotes = organizationnotes;
		}

		public String getDomainname() {
			return domainname;
		}

		public void setDomainname(String domainname) {
			this.domainname = domainname;
		}

	}

	/**
	 * @return railUserDataManager
	 */
	public RailUserDataManager getRailUserDataManager() {
		return railUserDataManager;
	}

	/**
	 * @param railUserDataManager
	 *            the railUserDataManager to set
	 */
	public void setRailUserDataManager(RailUserDataManager railUserDataManager) {
		this.railUserDataManager = railUserDataManager;
	}

	public void setAsyncJobFacade(AsyncJobFacade asyncJobFacade) {
		this.asyncJobFacade = asyncJobFacade;
	}

	@SuppressWarnings("unchecked")
	public List<Notification> findNotifications() {
		List<Notification> list = (List<Notification>) databaseFacade.find(
				"from Notification where userid = ? and sessionid = ?",
				sessionFacade.getCurrentSession().getUserid(), sessionFacade
						.getCurrentSession().getIdsession());
		databaseFacade.update("delete from Notification where userid = ?",
				sessionFacade.getCurrentSession().getUserid());
		return list;
	}

	private Set<Pair<Integer, Integer>> buildPoolOwnerships(
			Collection<Integer> users) {
		Set<Pair<Integer, Integer>> ownerships = new HashSet<Pair<Integer, Integer>>();
		for (Integer user : users) {
			List<List<EntityId>> assignedPools = this.findAssignedPools(user);
			for (List<EntityId> path : assignedPools) {
				ownerships.add(new Pair<Integer, Integer>(user, path.get(
						path.size() - 1).getId()));
			}
		}
		return ownerships;
	}

	private List<List<EntityId>> findAssignedPools(int userid) {
		List<List<EntityId>> assignedPools = new LinkedList<List<EntityId>>();
		// 查询当前用户直接拥有的资源.
		List<Integer> directAssignedPools = this.findPoolAssignments(
				RESOURCE_VISITOR_TYPE_USER, userid);
		for (Integer directAssignedPool : directAssignedPools) {
			List<EntityId> path = new ArrayList<EntityId>();
			path.add(new EntityId(DesktopPoolEntity.class, directAssignedPool));
			assignedPools.add(path);
		}
		// 查询当前用户所在的组.
		List<Integer> directGroupsOfUser = groupFacade
				.findDirectGroupsOfUser(userid);
		if (!directGroupsOfUser.isEmpty()) {
			for (Integer group : directGroupsOfUser) {
				// 查询当前用户所在组拥有的资源.
				List<Integer> groupAssignedPools = this.findPoolAssignments(
						RESOURCE_VISITOR_TYPE_GROUP, new Integer[] { group });
				for (Integer groupAssignedPool : groupAssignedPools) {
					List<EntityId> path = new ArrayList<EntityId>();
					path.add(new EntityId(Group.class, group));
					path.add(new EntityId(DesktopPoolEntity.class,
							groupAssignedPool));
					assignedPools.add(path);
				}
			}
		}
		Integer organizationIdOfUser = userFacade
				.findOrganizationOfUser(userid);
		List<Integer> organizationAndParents = new LinkedList<Integer>();
		if (organizationIdOfUser != null) {
			organizationAndParents.add(organizationIdOfUser);
			organizationAndParents.addAll(organizationFacade
					.findParents(organizationIdOfUser));
		}
		for (Integer organization : organizationAndParents) {
			// 查询当前用户所在组织拥有的资源.
			List<Integer> organizationAssignedPools = this.findPoolAssignments(
					RESOURCE_VISITOR_TYPE_ORGANIZATION,
					new Integer[] { organization });
			for (Integer organizationAssignedPool : organizationAssignedPools) {
				List<EntityId> path = new ArrayList<EntityId>();
				for (Integer organizationInPath : organizationAndParents) {
					path.add(new EntityId(Organization.class,
							organizationInPath));
					if (organization.equals(organizationInPath)) {
						break;
					}
				}
				path.add(new EntityId(DesktopPoolEntity.class,
						organizationAssignedPool));
				assignedPools.add(path);
			}
			List<Integer> directGroupsOfOrganization = groupFacade
					.findDirectGroupsOfOrganization(organization);
			if (!directGroupsOfOrganization.isEmpty()) {
				for (Integer group : directGroupsOfOrganization) {
					// 查询组织所在组拥有的资源.
					List<Integer> groupAssignedPools = this
							.findPoolAssignments(RESOURCE_VISITOR_TYPE_GROUP,
									new Integer[] { group });
					for (Integer groupAssignedPool : groupAssignedPools) {
						List<EntityId> path = new ArrayList<EntityId>();
						for (Integer organizationInPath : organizationAndParents) {
							path.add(new EntityId(Organization.class,
									organizationInPath));
							if (organization.equals(organizationInPath)) {
								break;
							}
						}
						path.add(new EntityId(Group.class, group));
						path.add(new EntityId(DesktopPoolEntity.class,
								groupAssignedPool));
						assignedPools.add(path);
					}
				}
			}
		}
		return assignedPools;
	}

	@SuppressWarnings("unchecked")
	private List<Integer> findPoolAssignments(int visitortype,
			Integer... visitors) {
		List<Object> params = new ArrayList<Object>();
		params.add(RESOURCE_TYPE_POOL);
		params.add(visitortype);
		for (int visitor : visitors) {
			params.add(visitor);
		}
		Object[] paramsArray = params.toArray();
		return (List<Integer>) databaseFacade
				.find(strcat(
						"select resourceid from ResourceAssignment where resourcetype = ? and visitortype = ? and ",
						buildOrIdWhereClause(visitors.length, "visitorid")),
						paramsArray);
	}

	private void clearDesktopOfUser(int userid, int poolId, boolean force,
			Integer sessionid, String username) throws CommonException {
		Integer desktopid = desktopPoolStatusFacade.findConnectedDesktop(
				userid, poolId);
		if (desktopid != null) {
			DesktopPoolEntity desktopPool = desktopPoolFacade
					.loadDesktopPool(poolId);
			Desktop desktop = databaseFacade.load(Desktop.class, desktopid);
			operationRegistry.start(new DeallocDesktopOperation(
					Integer.toHexString(poolId) + "#"
							+ Integer.toHexString(desktopid), force, sessionid,
					username));
		}
	}

}
