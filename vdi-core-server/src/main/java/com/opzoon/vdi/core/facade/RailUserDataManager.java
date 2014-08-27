/**   
 * Title: RailDataManagement.java 
 * @Package com.opzoon.vdi.core.facade 
 * : TODO(用一句话描述该文件做什么) 
 * @author Nathan   
 * @date 2013-1-29 下午2:16:40 
 * @version V1.0   
 */
package com.opzoon.vdi.core.facade;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import com.opzoon.ohvc.common.Job;
import com.opzoon.ohvc.common.JobStatus;
import com.opzoon.ohvc.common.RailAppError;
import com.opzoon.ohvc.domain.RailApplicationIcon;
import com.opzoon.ohvc.service.VdiAgentClientImpl;
import com.opzoon.vdi.core.app.domain.RailApplication;
import com.opzoon.vdi.core.app.domain.RailApplicationServer;
import com.opzoon.vdi.core.app.domain.RailApplicationToGroup;
import com.opzoon.vdi.core.app.domain.RailApplicationToOrganization;
import com.opzoon.vdi.core.app.domain.RailApplicationToUser;
import com.opzoon.vdi.core.app.domain.RailApplicationView;
import com.opzoon.vdi.core.app.domain.UserAndResourcestatus;
import com.opzoon.vdi.core.app.response.RailConnection;
import com.opzoon.vdi.core.cloud.ConnectionManager;
import com.opzoon.vdi.core.cloud.ConnectionManager.ConnectionInfo;
import com.opzoon.vdi.core.domain.Connection;
import com.opzoon.vdi.core.domain.Domain;
import com.opzoon.vdi.core.domain.GroupElement;
import com.opzoon.vdi.core.domain.Session;
import com.opzoon.vdi.core.domain.User;
import com.opzoon.vdi.core.facade.transience.ConnectionFacade;
import com.opzoon.vdi.core.facade.transience.SessionFacade;
import com.opzoon.vdi.core.support.Configuration;
import com.opzoon.vdi.core.util.BeanUtils;

//import com.opzoon.ohvc.service.VdiAgentClientImpl;

/**
 * @ClassName: RailDataManagement : 虚拟应用数据管理
 * @author david
 * @param <T>
 * @date 2013-1-29 下午2:16:40
 */
public class RailUserDataManager {
	private static Logger log = Logger.getLogger(RailUserDataManager.class);
	private DatabaseFacade databaseFacade;
	private SessionFacade sessionFacade;
	private Configuration configuration;
	private ConnectionFacade connectionFacade;

	/**
	 * @return sessionFacade
	 */
	public SessionFacade getSessionFacade() {
		return sessionFacade;
	}

	/**
	 * @param sessionFacade
	 *            the sessionFacade to set
	 */
	public void setSessionFacade(SessionFacade sessionFacade) {
		this.sessionFacade = sessionFacade;
	}

	/**
	 * @return connectionFacade
	 */
	public ConnectionFacade getConnectionFacade() {
		return connectionFacade;
	}

	/**
	 * @param connectionFacade
	 *            the connectionFacade to set
	 */
	public void setConnectionFacade(ConnectionFacade connectionFacade) {
		this.connectionFacade = connectionFacade;
	}

	/**
	 * @return configuration
	 */
	public Configuration getConfiguration() {
		return configuration;
	}

	/**
	 * @param configuration
	 *            the configuration to set
	 */
	public void setConfiguration(Configuration configuration) {
		this.configuration = configuration;
	}

	// private UserFacade userFacade;
	private ConnectionManager connectionManager;

	/**
	 * @return connectionManager
	 */
	public ConnectionManager getConnectionManager() {
		return connectionManager;
	}

	/**
	 * @param connectionManager
	 *            the connectionManager to set
	 */
	public void setConnectionManager(ConnectionManager connectionManager) {
		this.connectionManager = connectionManager;
	}

	/**
	 * @return databaseFacade
	 */
	public DatabaseFacade getDatabaseFacade() {
		return databaseFacade;
	}

	/**
	 * @param databaseFacade
	 *            the databaseFacade to set
	 */
	public void setDatabaseFacade(DatabaseFacade databaseFacade) {
		this.databaseFacade = databaseFacade;
	}

	/**
	 * Title: persistApplicationServer : 添加虚拟应用服务器
	 * 
	 * @param
	 * @return
	 * @throws
	 */
	public void persistApplicationServer(RailApplicationServer applicationServer) {
		databaseFacade.persist(applicationServer);
	}

	/**
	 * Title: deleteApplicationServer : 删除虚拟应用服务器
	 * 
	 * @param 设定文件
	 * @return void 返回类型
	 * @throws
	 */
	public void deleteApplicationServer(Integer id) {
		this.databaseFacade.remove(getApplicationServer(id));
		this.databaseFacade.update("delete from RailApplication where applicationserverid=?", id);
	}

	/**
	 * Title: getApplicationServer : 获得单个虚拟应用服务器
	 * 
	 * @param @param id
	 * @param @return 设定文件
	 * @return RailApplicationServer 返回类型
	 * @throws
	 */
	public RailApplicationServer getApplicationServer(Integer id) {
		// Evan
		return this.databaseFacade.load(RailApplicationServer.class, id);
	}

	/**
	 * @return Title: updateApplicationServer : update applicationServer
	 * @param idapplicationserver
	 * @return void
	 * @throws
	 */
	public void updateApplicationServer(RailApplicationServer applicationserver) {
		// TODO Auto-generated method stub
		this.databaseFacade.merge(applicationserver);
	}

	/**
	 * @throws CommonException
	 *             Title: findApplicationServerByApplicationIdAndConnectionStrategy : 虚拟应用Id最小链接数策略查询出可以匹配的虚拟应用服务器
	 * @param applicationid
	 * @param @return 设定文件
	 * @return List<RailApplicationServer> 返回类型
	 * @throws
	 */
	public RailConnection createConnectionByApplicationIdAndConnectionStrategy(String applicationid) throws CommonException {
		List<com.opzoon.vdi.core.app.domain.RailApplicationServer> railapplications = new ArrayList<com.opzoon.vdi.core.app.domain.RailApplicationServer>();
		Session session = this.sessionFacade.getCurrentSession();
		Integer userid = session.getUserid();
		User user = this.databaseFacade.load(User.class, userid);
		Integer domainid = user.getDomainid();
		Domain domain = this.databaseFacade.load(Domain.class, domainid);
		String domainname = domain.getDomainname() + "";
		// int idx = -1;
		// if ((idx = domainname.indexOf(".")) != -1) {
		// domainname = domainname.substring(0, idx);
		// }
		@SuppressWarnings("unchecked")
		List<com.opzoon.vdi.core.app.domain.RailApplication> las = (List<com.opzoon.vdi.core.app.domain.RailApplication>) this.databaseFacade.find(
				"from RailApplication where applicationid=? ", applicationid);
		if (las != null && las.size() <= 0) {
			throw new CommonException(0x80000109);
		}
		for (com.opzoon.vdi.core.app.domain.RailApplication ra : las) {
			com.opzoon.vdi.core.app.domain.RailApplicationServer rs = this.databaseFacade.load(com.opzoon.vdi.core.app.domain.RailApplicationServer.class,
					ra.getApplicationserverid());

			if (rs != null && rs.getJoinname() == null || !rs.getJointype().equalsIgnoreCase("domain")) {
				rs.setJoinname("");
			}

			if (domainname.equalsIgnoreCase(rs.getJoinname() + "")) {
				rs.setApplicationname(ra.getApplicationname());
				rs.setIdapplication(ra.getIdrailapplication());
				railapplications.add(rs);
			}
		}
		Collections.sort(railapplications);
		if (railapplications.size() == 0) {
			throw new CommonException(0x80000109);
		}
		com.opzoon.vdi.core.app.domain.RailApplicationServer railApplicationServer = railapplications.get(0);
		int connections = railApplicationServer.getConnections();
		Connection con = this.createConnection(railApplicationServer, user);
		RailConnection conn = new RailConnection();
		BeanUtils.copyProperties(conn, con, false);
		com.opzoon.vdi.core.app.domain.RailApplication rail = (com.opzoon.vdi.core.app.domain.RailApplication) this.databaseFacade.findFirst(
				"from RailApplication where applicationserverid=? and applicationid=?", railApplicationServer.getIdapplicationserver(), applicationid);
		conn.setApplicationPath(rail.getApplicationpath());
		UserAndResourcestatus userAndResourcestatus = new UserAndResourcestatus();
		userAndResourcestatus.setApplicationid(applicationid);
		userAndResourcestatus.setUserid(userid);
		userAndResourcestatus.setStatus(UserAndResourcestatus.CONNECTION_STATUS);
		userAndResourcestatus.setId(con.getConnectionticket());
		this.databaseFacade.persist(userAndResourcestatus);
		conn.setStauts(UserAndResourcestatus.CONNECTION_STATUS);
		int error = 0;
		try {
			if (user.getDomainid() == Domain.DEFAULT_DOMAIN_ID) {
				error = this.createUser(railApplicationServer.getServername(), user.getUsername(), sessionFacade.getCurrentSession().getPassword());
				if (error == 0x80020005) {
					error = this.updateUserPassword(railApplicationServer.getServername(), user.getUsername(), sessionFacade.getCurrentSession().getPassword());
				}
			}
		} catch (Exception e) {
			error = RailAppError.RAIL_ERR.getError();
		} finally {
			if (error != 0) {
				throw new CommonException(error);
			}
		}
		railApplicationServer.setConnections(connections);
		establishRailConnectionStrategy(railApplicationServer);
		return conn;
	}

	/**
	 * @throws Exception
	 * @Title: updateUserPassword
	 * @Description: TODO(这里用一句话描述这个方法的作用)
	 * @param @param servername
	 * @param @param username
	 * @param @param password
	 * @param @return 设定文件
	 * @return int 返回类型
	 * @throws
	 */
	private int updateUserPassword(String servername, String username, String password) throws Exception {
		Job<String> job = VdiAgentClientImpl.updateUserPassword(servername, username, password);
		waitResultByJob(job);
		return job.getError();
	}

	/**
	 * @Title: createUser
	 * @Description: TODO(这里用一句话描述这个方法的作用)
	 * @param @param servername
	 * @param @param username
	 * @param @param password
	 * @param @return 设定文件
	 * @return int 返回类型
	 * @throws
	 */
	private int createUser(String servername, String username, String password) {
		try {
			Job<String> job = VdiAgentClientImpl.createUser(servername, username, password);
			waitResultByJob(job);
			return job.getError();
		} catch (Exception e) {
			return RailAppError.RAIL_ERR.getError();
		}
	}

	/**
	 * @Title: waitResultByJob
	 * @Description: TODO(这里用一句话描述这个方法的作用)
	 * @param @param job 设定文件
	 * @return void 返回类型
	 * @throws
	 */
	private void waitResultByJob(Job<String> job) {
		// TODO Auto-generated method stub
		while (true) {
			try {
				VdiAgentClientImpl.queryAsyncJobResult(job);
			} catch (Exception e) {
				break;
			}
			if (job.getStatus().equals(JobStatus.SUCCESSFUL)) {
				break;
			}
		}
	}

	private Connection createConnection(com.opzoon.vdi.core.app.domain.RailApplicationServer railApplicationServer, User user) {
		// Evan
		ConnectionInfo connectionInfo = this.connectionManager.establishConnection(railApplicationServer.getServername(), configuration.getBrokerName(),
				configuration.getBrokerIP());
		Connection connection = connectionFacade.createNewConnection(connectionInfo, railApplicationServer.getServertype(),
				railApplicationServer.getIdapplication(), sessionFacade.getCurrentSession().getIdsession(), null, -1, 1);
		connection.setUsername(user.getUsername());
		connection.setResourcetype(257);
		connection.setPassword(sessionFacade.getCurrentSession().getPassword());
		if (StringUtils.isEmpty(railApplicationServer.getJointype()) || !railApplicationServer.getJointype().equalsIgnoreCase("domain")) {
			connection.setDomain(railApplicationServer.getJoinname());
		} else {
			connection.setDomain("");
		}

		return connection;
	}

	/**
	 * Title: establishRailConnection :
	 * 
	 * @param @param railApplicationServer
	 * @param @return 设定文件
	 * @return ResourceTypeAndId 返回类型
	 * @throws
	 */
	public void establishRailConnectionStrategy(RailApplicationServer railApplicationServer) {
		synchronized (this) {
			log.trace("establishRailConnection :: synchronized start . startTime [" + new Date().getTime() + "]");
			int connection = railApplicationServer.getConnections() + 1;
			railApplicationServer.setConnections(connection);
			this.updateApplicationServer(railApplicationServer);
		}
	}

	public void destroyRailConnection(String connectionticket) {

		synchronized (this) {
			log.trace("destroyRailConnection :");
			Connection connection = (Connection) this.databaseFacade.findFirst("from Connection where  connectionticket=? and resourcetype=257",
					connectionticket);
			Integer idapplication = connection.getResourceid();
			RailApplication railApplication = this.databaseFacade.load(RailApplication.class, idapplication);
			RailApplicationServer railApplicationServer = this.databaseFacade.load(RailApplicationServer.class, railApplication.getApplicationserverid());
			if (railApplicationServer != null) {
				int cons = railApplicationServer.getConnections() - 1;
				railApplicationServer.setConnections(cons < 0 ? 0 : cons);
				this.databaseFacade.merge(railApplicationServer);
			}
			UserAndResourcestatus userAndResourcestatus = this.databaseFacade.load(UserAndResourcestatus.class, connectionticket);
			if (userAndResourcestatus != null) {
				userAndResourcestatus.setStatus(UserAndResourcestatus.DESTROYCONN_STATUS);
				this.databaseFacade.merge(userAndResourcestatus);
				this.databaseFacade.remove(userAndResourcestatus);
			}
		}
	}

	/**
	 * Title: listRailResource Description:根据用户id查询虚拟应用 包括列出用户所属的组,组织单元下的 虚拟应用
	 * 
	 * @param @param userid
	 * @param @return
	 * @return List<RailApplicationView>
	 * @throws
	 */
	@SuppressWarnings("unchecked")
	public List<RailApplicationView> listRailResource(Integer userid) {
		if (userid == -1) {
			userid = sessionFacade.getCurrentSession().getUserid();
			log.trace("");
			log.info("user :: [" + userid + "]");
		}
		List<RailApplicationView> listRailApplicationViews = new ArrayList<RailApplicationView>();
		Set<String> applicationIds = new HashSet<String>();
		List<RailApplicationToUser> ras = (List<RailApplicationToUser>) this.databaseFacade.find("from RailApplicationToUser where userid=?", userid);
		for (RailApplicationToUser railApplicationToUser : ras) {
			applicationIds.add(railApplicationToUser.getApplicationid());
		}
		User user = this.databaseFacade.load(User.class, userid);
		Domain domain = null;
		if (user.getDomainid() != 0) {
			domain = this.databaseFacade.load(Domain.class, user.getDomainid());
		}
		if (user != null) {

			Integer orgrationid = user.getOrganizationid();
			if (orgrationid != null) {
				List<RailApplicationToOrganization> ros = (List<RailApplicationToOrganization>) this.databaseFacade.find(
						"from RailApplicationToOrganization where organizationid=?", orgrationid);
				for (RailApplicationToOrganization railApplicationToOrganization : ros) {
					applicationIds.add(railApplicationToOrganization.getApplicationid());

				}
			}
			GroupElement groupE = (GroupElement) this.databaseFacade.findFirst("from GroupElement where elementid=? and elementtype=?", userid,
					GroupElement.ELEMENT_TYPE_USER);

			if (groupE != null) {
				List<RailApplicationToGroup> gs = (List<RailApplicationToGroup>) this.databaseFacade.find("from RailApplicationToGroup where groupid=?",
						groupE.getGroupid());
				for (RailApplicationToGroup railApplicationToGroup : gs) {
					applicationIds.add(railApplicationToGroup.getApplicationid());
				}
			}

			GroupElement groupE2 = (GroupElement) this.databaseFacade.findFirst("from GroupElement where elementid=? and elementtype=?", orgrationid,
					GroupElement.ELEMENT_TYPE_ORGANIZATION);

			if (groupE2 != null) {
				List<RailApplicationToGroup> gs2 = (List<RailApplicationToGroup>) this.databaseFacade.find("from RailApplicationToGroup where groupid=?",
						groupE2.getGroupid());
				for (RailApplicationToGroup railApplicationToGroup : gs2) {
					applicationIds.add(railApplicationToGroup.getApplicationid());
				}
			}

			for (String applicationid : applicationIds) {
				RailApplicationView railApplicationView = this.databaseFacade.load(RailApplicationView.class, applicationid);

				if (railApplicationView != null) {
					List<RailApplication> ls = (List<RailApplication>) this.databaseFacade.find(
							"from RailApplication where applicationid=? and published=true and status=1", applicationid);
					int staus = 256;
					for (RailApplication railApplication : ls) {
						RailApplicationServer server = this.databaseFacade.load(RailApplicationServer.class, railApplication.getApplicationserverid());
						String domainname = "";
						if (domain != null) {
							domainname = domain.getDomainname() + "";
						}

						if (StringUtils.isEmpty(server.getJointype()) || !server.getJointype().equalsIgnoreCase("domain")) {
							server.setJoinname("");
						}
						if (domainname.equalsIgnoreCase(server.getJoinname())) {
							staus = RailApplication.ONLINE;
							break;
						}
					}
					railApplicationView.setStatus(staus);
					listRailApplicationViews.add(railApplicationView);

					UserAndResourcestatus sAndResourcestatus = (UserAndResourcestatus) this.databaseFacade.findFirst(
							"from UserAndResourcestatus where userid=? and applicationid=?", userid, applicationid);
					if (sAndResourcestatus != null)
						railApplicationView.setConnectionstatus(sAndResourcestatus.getStatus());
					else {
						railApplicationView.setConnectionstatus(4);
					}
				}
			}
		}

		return listRailApplicationViews;
	}

	/**
	 * @throws Exception
	 * @param s
	 *            Title: getRailApplicationIcon Description:
	 * @param @param applicationid
	 * @param @return
	 * @return RailApplicationIcon
	 * @throws
	 */
	@SuppressWarnings("unchecked")
	public RailApplicationIcon getRailApplicationIcon(String servername, String applicationid) throws Exception {
		RailApplicationIcon applicationicon = null;
		if (servername == null) {
			List<RailApplication> server = (List<RailApplication>) this.databaseFacade.find("from RailApplication where status=? and applicationid=?", 1,
					applicationid);
			for (RailApplication apllication : server) {
				try {
					applicationicon = VdiAgentClientImpl.getRailApplicationIcon(apllication.getServername(), applicationid);
					if (applicationicon != null) {
						break;
					} else {
						continue;
					}
				} catch (Exception e) {
					continue;
				}
			}
		} else {
			applicationicon = VdiAgentClientImpl.getRailApplicationIcon(servername, applicationid);
		}

		return applicationicon;
	}
}
