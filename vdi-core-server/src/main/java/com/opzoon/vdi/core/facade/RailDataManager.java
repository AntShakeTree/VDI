/**   
 * Title: RailDataManagement.java 
 * @Package com.opzoon.vdi.core.facade 
 * : TODO(用一句话描述该文件做什么) 
 * @author Nathan   
 * @date 2013-1-29 下午2:16:40 
 * @version V1.0   
 */
package com.opzoon.vdi.core.facade;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;

import org.apache.log4j.Logger;

import com.opzoon.ohvc.common.OpzoonUtils;
import com.opzoon.ohvc.common.QueryUtil;
import com.opzoon.ohvc.request.PageRequest;
import com.opzoon.vdi.core.app.common.PageView;
import com.opzoon.vdi.core.app.domain.RailApplication;
import com.opzoon.vdi.core.app.domain.RailApplicationServer;
import com.opzoon.vdi.core.app.domain.RailApplicationToGroup;
import com.opzoon.vdi.core.app.domain.RailApplicationToOrganization;
import com.opzoon.vdi.core.app.domain.RailApplicationToUser;
import com.opzoon.vdi.core.app.domain.RailApplicationView;
import com.opzoon.vdi.core.app.request.ListRailAssignmentsReq;
import com.opzoon.vdi.core.app.request.RailApplicationToGroupReq;
import com.opzoon.vdi.core.app.request.RailApplicationToOrganizationReq;
import com.opzoon.vdi.core.app.request.RailApplicationToUserReq;
import com.opzoon.vdi.core.cloud.ConnectionManager;
import com.opzoon.vdi.core.cloud.ConnectionManager.ConnectionInfo;
import com.opzoon.vdi.core.domain.Connection;
import com.opzoon.vdi.core.domain.Domain;
import com.opzoon.vdi.core.domain.Group;
import com.opzoon.vdi.core.domain.Organization;
import com.opzoon.vdi.core.domain.Session;
import com.opzoon.vdi.core.domain.User;
import com.opzoon.vdi.core.facade.ResourceFacade.OrganizationInfo;
import com.opzoon.vdi.core.facade.ResourceFacade.Resource;
import com.opzoon.vdi.core.facade.ResourceFacade.UserInfo;
import com.opzoon.vdi.core.facade.UserFacade.GroupInfo;
import com.opzoon.vdi.core.facade.transience.ConnectionFacade;
import com.opzoon.vdi.core.facade.transience.SessionFacade;
import com.opzoon.vdi.core.support.Configuration;
import com.opzoon.vdi.core.util.BeanUtils;
import com.opzoon.vdi.core.ws.admin.DesktopManagement.ListAssignmentsResponse;
import com.opzoon.vdi.core.ws.admin.DesktopManagement.ResourceList;

/**
 * @ClassName: RailDataManagement : 虚拟应用数据管理
 * @author david
 * @param <T>
 * @date 2013-1-29 下午2:16:40
 */
public class RailDataManager {
	private static Logger log = Logger.getLogger(RailDataManager.class);
	private DatabaseFacade databaseFacade;
	private SessionFacade sessionFacade;
	private Configuration configuration;
	private ConnectionFacade connectionFacade;

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
	 * Title: persistApplication : 添加虚拟应用
	 * 
	 * @param
	 * @return
	 * @throws
	 */
	public void persistApplication(RailApplication apllication) {
		// Evan
		com.opzoon.vdi.core.app.domain.RailApplication rail = new com.opzoon.vdi.core.app.domain.RailApplication();
		BeanUtils.copyProperties(rail, apllication, false);
		RailApplication dao = (RailApplication) databaseFacade.findFirst("from RailApplication where servername=? and applicationid=?",
				apllication.getServername(), apllication.getApplicationid());
		if (dao != null) {
			BeanUtils.copyProperties(dao, rail, false);
			dao.setPublished(false);
			databaseFacade.merge(dao);
		} else {
			databaseFacade.persist(rail);
		}
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
		this.databaseFacade.merge(applicationserver);
	}

	/**
	 * Title: getCount : TODO(这里用一句话描述这个方法的作用)
	 * 
	 * @param @param string
	 * @param @param values
	 * @param @return 设定文件
	 * @return int 返回类型
	 * @throws
	 */
	public int getCount(String string, List<Object> values) {
		if (string.contains("select")) {
			string = string.substring(string.indexOf("from"));
		}
		// Evan
		return Integer.parseInt(this.databaseFacade.findFirst("select count(*) " + string, values.toArray()) + "");
	}

	@SuppressWarnings("unchecked")
	public <T> List<T> listPageRequest(PageRequest<T> req) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException {

		QueryUtil queryUtil = OpzoonUtils.getHqlByDomain(req);
		StringBuffer sb = new StringBuffer();
		sb.append(queryUtil.getHql());
		if (org.apache.commons.lang.StringUtils.isNotEmpty(req.getSortkey())) {
			sb.append(" order by ").append(req.getSortkey());
			if (req.getAscend() == 1) {
				sb.append(" asc");
			} else {
				sb.append(" desc");
			}
		}
		// Evan
		List<T> ls = null;
		if (req.getPage() != 0) {
			PageView pageView = new PageView(req.getPagesize(), req.getPage());
			ls = (List<T>) databaseFacade.find(pageView.getFirstResult(), pageView.getMaxresult(), sb.toString(), queryUtil.getValues().toArray());
		} else {
			ls = (List<T>) databaseFacade.find(sb.toString(), queryUtil.getValues().toArray());
		}
		req.setAmount(getCount(sb.toString(), queryUtil.getValues()));
		return ls;
	}

	/**
	 * Title: updateRailApplication : TODO(这里用一句话描述这个方法的作用)
	 * 
	 * @param @param RailApplication 设定文件
	 * @return void 返回类型
	 * @throws
	 */
	public void updateRailApplication(com.opzoon.vdi.core.app.domain.RailApplication railApplication) {
		this.databaseFacade.merge(railApplication);
	}

	/**
	 * Title: assignApplicationToUser : 分配给用户
	 * 
	 * @param @param req 设定文件
	 * @return void 返回类型
	 * @throws
	 */
	public void assignApplicationToUser(RailApplicationToUserReq req) {
		for (int id : req.getUserids()) {
			this.databaseFacade.merge(new RailApplicationToUser(req.getApplicationid(), id));
		}
		if (req.getUserid() != null && req.getUserid() != -1) {
			this.databaseFacade.merge(new RailApplicationToUser(req.getApplicationid(), req.getUserid()));
		}
	}

	/**
	 * Title: unassignApplicationToUser : TODO(这里用一句话描述这个方法的作用)
	 * 
	 * @param @param req 设定文件
	 * @return void 返回类型
	 * @throws
	 */
	public void unassignApplicationToUser(RailApplicationToUserReq req) {
		for (Integer id : req.getUserids()) {
			RailApplicationToUser reqx = (RailApplicationToUser) this.databaseFacade.findFirst("from RailApplicationToUser where applicationid=? and userid=?",
					req.getApplicationid(), id);
			if (reqx != null) {
				this.databaseFacade.remove(reqx);
			}
		}
		this.deleteRailApplicationViewByAssign(req.getApplicationid());
	}

	/**
	 * Title: assignApplicationToOrganization : assignApplicationToOrganization
	 * 
	 * @param @param req 设定文件
	 * @return void 返回类型
	 * @throws
	 */
	public void assignApplicationToOrganization(RailApplicationToOrganizationReq req) {
		for (Integer organizationid : req.getOrganizationids()) {
			this.databaseFacade.merge(new RailApplicationToOrganization(req.getApplicationid(), organizationid));
		}
		if (req.getOrganizationid() != null && req.getOrganizationid() != -1)
			this.databaseFacade.merge(new RailApplicationToOrganization(req.getApplicationid(), req.getOrganizationid()));
	}

	/**
	 * Title: unassignApplicationToOrganization : unassignApplicationToOrganization
	 * 
	 * @param @param req 设定文件
	 * @return void 返回类型
	 * @throws
	 */
	public void unassignApplicationToOrganization(RailApplicationToOrganizationReq req) {
		for (Integer id : req.getOrganizationids()) {
			RailApplicationToOrganization railApplicationToOrganization = new RailApplicationToOrganization(req.getApplicationid(), id);
			this.databaseFacade.update("delete from RailApplicationToOrganization where id=?", railApplicationToOrganization.getId());
		}
		this.deleteRailApplicationViewByAssign(req.getApplicationid());
	}

	/**
	 * @Title: deleteRailApplicationViewByAssign
	 * @Description: 根据分配情况删除虚拟应用
	 * @param @param applicationid 设定文件
	 * @return void 返回类型
	 * @throws
	 */
	public void deleteRailApplicationViewByAssign(String applicationid) {

		Object countOUser = this.databaseFacade.findFirst("select count(*) from RailApplicationToUser where applicationid=?", applicationid);
		Object countGroup = this.databaseFacade.findFirst("select count(*) from RailApplicationToGroup where applicationid=?", applicationid);
		Object counOrg = this.databaseFacade.findFirst("select count(*) from RailApplicationToOrganization where applicationid=?", applicationid);
		int countuser = 0;
		int countgroup = 0;
		int countorg = 0;
		if (counOrg != null) {
			countuser = Integer.parseInt(countOUser + "");
		}
		if (countGroup != null) {
			countgroup = Integer.parseInt(countGroup + "");
		}
		if (counOrg != null) {
			countorg = Integer.parseInt(counOrg + "");
		}
		if (countuser == 0 && countgroup == 0 && countorg == 0) {
			this.databaseFacade.update("delete from RailApplicationView where applicationid=? and published=false", applicationid);
		}
	}

	/**
	 * Title: assignApplicationToGroup : assignApplicationToGroup
	 * 
	 * @param @param req 设定文件
	 * @return void 返回类型
	 * @throws
	 */
	public void assignApplicationToGroup(RailApplicationToGroupReq req) {
		for (Integer id : req.getGroupids()) {
			this.databaseFacade.merge(new RailApplicationToGroup(req.getApplicationid(), id));
		}
		if (req.getGroupid() != null && req.getGroupid() != -1)
			this.databaseFacade.merge(req);
	}

	/**
	 * Title: unassignApplicationToGroup : unassignApplicationToGroup
	 * 
	 * @param @param req 设定文件
	 * @return void 返回类型
	 * @throws
	 */
	public void unassignApplicationToGroup(RailApplicationToGroupReq req) {
		for (Integer id : req.getGroupids()) {
			RailApplicationToGroup group = new RailApplicationToGroup(req.getApplicationid(), id);
			this.databaseFacade.update("delete from RailApplicationToGroup where id=?", group.getId());
		}
		this.deleteRailApplicationViewByAssign(req.getApplicationid());
	}

	/**
	 * Title: findApplicationServerByApplicationIdAndConnectionStrategy : 虚拟应用Id最小链接数策略查询出可以匹配的虚拟应用服务器
	 * 
	 * @param applicationid
	 * @param @return 设定文件
	 * @return List<RailApplicationServer> 返回类型
	 * @throws
	 */
	public Connection createConnectionByApplicationIdAndConnectionStrategy(Integer applicationid) {
		List<com.opzoon.vdi.core.app.domain.RailApplicationServer> railapplications = new ArrayList<com.opzoon.vdi.core.app.domain.RailApplicationServer>();
		Session session = this.sessionFacade.getCurrentSession();
		Integer userid = session.getUserid();
		User user = this.databaseFacade.load(User.class, userid);
		Integer domainid = user.getDomainid();
		Domain domain = this.databaseFacade.load(Domain.class, domainid);
		String domainname = domain.getDomainname() + "";
		@SuppressWarnings("unchecked")
		List<com.opzoon.vdi.core.app.domain.RailApplication> las = (List<com.opzoon.vdi.core.app.domain.RailApplication>) this.databaseFacade.find(
				"from RailApplication applicationid=? ", applicationid);

		for (com.opzoon.vdi.core.app.domain.RailApplication ra : las) {
			com.opzoon.vdi.core.app.domain.RailApplicationServer rs = this.databaseFacade.load(com.opzoon.vdi.core.app.domain.RailApplicationServer.class,
					ra.getApplicationserverid());
			if (domainname.equals(rs.getJoinname())) {
				railapplications.add(rs);
			}
		}
		com.opzoon.vdi.core.app.domain.RailApplicationServer railApplicationServer = railapplications.get(0);

		return this.createConnection(railApplicationServer, user);

	}

	private Connection createConnection(com.opzoon.vdi.core.app.domain.RailApplicationServer railApplicationServer, User user) {

		// Evan
		ConnectionInfo connectionInfo = this.connectionManager.establishConnection(railApplicationServer.getServername(), configuration.getBrokerName(),
				configuration.getBrokerIP());
		Connection connection = connectionFacade.createNewConnection(connectionInfo, railApplicationServer.getServertype(),
				railApplicationServer.getIdapplicationserver(), sessionFacade.getCurrentSession().getIdsession(), null, -1, 1);
		connection.setUsername(user.getUsername());
		connection.setPassword(sessionFacade.getCurrentSession().getPassword());
		connection.setDomain(railApplicationServer.getJoinname());
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
		synchronized (railApplicationServer) {
			log.info("establishRailConnection :: synchronized start . startTime [" + new Date().getTime() + "]");
			railApplicationServer.setConnections(railApplicationServer.getConnections() + 1);
			this.updateApplicationServer(railApplicationServer);
		}
		log.info("establishRailConnection :: synchronized end . endTime [" + new Date().getTime() + "]");
	}

	/**
	 * Title: deleteApplication : TODO(这里用一句话描述这个方法的作用)
	 * 
	 * @param @param RailApplication 设定文件
	 * @return void 返回类型
	 * @throws
	 */
	public void deleteApplication(Integer id) {
		this.databaseFacade.update("delete RailApplication where idrailapplication=?", id);
	}

	/**
	 * Title: getApplication : TODO(这里用一句话描述这个方法的作用)
	 * 
	 * @param @param idrailapplication
	 * @param @return 设定文件
	 * @return RailApplication 返回类型
	 * @throws
	 */
	public com.opzoon.vdi.core.app.domain.RailApplication getApplication(Integer id) {
		return this.databaseFacade.load(com.opzoon.vdi.core.app.domain.RailApplication.class, id);
	}

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
	 * Title: getRailApplicationView Description: 获得虚拟应用列表
	 * 
	 * @param applicationid
	 * @return RailApplicationView 返回类型
	 * @throws
	 */
	public RailApplicationView getRailApplicationView(String applicationid) {

		return this.databaseFacade.load(RailApplicationView.class, applicationid);
	}

	/**
	 * Title: saveRailApplicationView Description: 保持虚拟应用列表
	 * 
	 * @param rail
	 *            设定文件
	 * @return void 返回类型
	 * @throws
	 */
	public void saveRailApplicationView(RailApplicationView rail) {
		this.databaseFacade.merge(rail);
	}

	/**
	 * Title: findRailApplicationByApplicationId Description: 根据虚拟应用ID查询出所有的虚拟应用
	 * 
	 * @param @param applicationid
	 * @param @return 设定文件
	 * @return List<RailApplication> 返回类型
	 * @throws
	 */

	@SuppressWarnings("unchecked")
	public List<com.opzoon.vdi.core.app.domain.RailApplication> findRailApplicationByApplicationId(String applicationid) {
		return (List<com.opzoon.vdi.core.app.domain.RailApplication>) this.databaseFacade.find("from RailApplication where applicationid=?", applicationid);
	}

	/**
	 * Title: deleteApplicationView Description: 删除虚拟应用
	 * 
	 * @param applicationid
	 * @return void 返回类型
	 * @throws
	 */
	public void deleteApplicationView(String applicationid) {
		this.databaseFacade.remove(this.databaseFacade.load(RailApplicationView.class, applicationid));
	}

	/**
	 * Title: deleteApplicationToUser Description: deleteApplicationToUser
	 * 
	 * @param @param applicationid 设定文件
	 * @return void 返回类型
	 * @throws
	 */
	public void deleteApplicationToUser(String applicationid) {
		this.databaseFacade.update("delete from RailApplicationToUser where applicationid=?", applicationid);
	}

	/**
	 * Title: deleteApplicationToGroup Description: delete Application To Group
	 * 
	 * @param @param applicationid 设定文件
	 * @return void 返回类型
	 * @throws
	 */
	public void deleteApplicationToGroup(String applicationid) {
		this.databaseFacade.update("delete from RailApplicationToGroup where applicationid=?", applicationid);

	}

	/**
	 * @Title: deleteApplicationToOrgration
	 * @Description: deleteApplicationToOrgration
	 * @param @param applicationid 设定文件
	 * @return void 返回类型
	 * @throws
	 */
	public void deleteApplicationToOrganization(String applicationid) {
		this.databaseFacade.update("delete from RailApplicationToOrganization where applicationid=?", applicationid);
	}

	/**
	 * Title: listApplicationServerByApplicationid Description:
	 * 
	 * @param @param applicationid
	 * @param @return
	 * @return List<RailApplicationServer>
	 * @throws
	 */
	@SuppressWarnings("unchecked")
	public List<com.opzoon.vdi.core.app.domain.RailApplicationServer> listApplicationServerByApplicationid(String applicationid, Boolean published) {
		List<com.opzoon.vdi.core.app.domain.RailApplicationServer> rs = new ArrayList<com.opzoon.vdi.core.app.domain.RailApplicationServer>();
		String hql = "from RailApplication where  applicationid=?";

		List<com.opzoon.vdi.core.app.domain.RailApplication> RailAppllications = null;
		if (published != null) {
			hql += " and published=?";
			RailAppllications = (List<com.opzoon.vdi.core.app.domain.RailApplication>) this.databaseFacade.find(hql, applicationid, published);
		} else {
			RailAppllications = ((List<com.opzoon.vdi.core.app.domain.RailApplication>) this.databaseFacade.find(hql, applicationid));
		}

		for (com.opzoon.vdi.core.app.domain.RailApplication railApplication : RailAppllications) {
			com.opzoon.vdi.core.app.domain.RailApplicationServer railApplicationServer = this.databaseFacade.load(
					com.opzoon.vdi.core.app.domain.RailApplicationServer.class, railApplication.getApplicationserverid());
			railApplicationServer.setApplicationpath(railApplication.getApplicationpath());
			if (railApplicationServer != null) {
				railApplicationServer.setPublished(railApplication.getPublished());
				rs.add(railApplicationServer);
			}
		}

		return rs;
	}

	/**
	 * Title: listRailAssignmentsByGroupId Description:
	 * 
	 * @param @param groupid
	 * @param @return
	 * @return ResourceList
	 * @throws
	 */
	@SuppressWarnings("unchecked")
	public ListAssignmentsResponse listGroupByApplicationid(ListRailAssignmentsReq listAssignmentsParam) {
		log.trace("");
		ListAssignmentsResponse res = new ListAssignmentsResponse();
		ResourceList list = new ResourceList();

		List<Resource> rs = new ArrayList<Resource>();
		res.getHead();
		PageView pageview = new PageView(listAssignmentsParam.getPagesize(), listAssignmentsParam.getPage());
		List<RailApplicationToGroup> lrag = (List<RailApplicationToGroup>) this.databaseFacade.find(pageview.getFirstResult(), pageview.getMaxresult(),
				"from RailApplicationToGroup where applicationid=?", listAssignmentsParam.getApplicationid());
		// Evan
		Object amountO = this.databaseFacade.findFirst("select count(*) from RailApplicationToGroup where applicationid=?",
				listAssignmentsParam.getApplicationid());
		int amount = 0;
		log.trace("");
		if (amountO != null) {
			log.info("amount" + amountO);
			amount = Integer.parseInt(amountO + "");
		}
		List<GroupInfo> groups = new ArrayList<GroupInfo>();
		Resource resource = new Resource();
		resource.setGroups(groups);
		rs.add(resource);
		list.setList(rs);
		list.setAmount(amount);
		list.setPage(listAssignmentsParam.getPage());
		list.setPagesize(listAssignmentsParam.getPagesize());
		list.setSortkey(listAssignmentsParam.getSortkey());
		list.setAscend(listAssignmentsParam.getAscend());
		res.setBody(list);
		for (RailApplicationToGroup railApplicationToGroup : lrag) {
			Group group = this.databaseFacade.load(Group.class, railApplicationToGroup.getGroupid());
			GroupInfo gourpinfo = new GroupInfo();
			Domain domain = this.databaseFacade.load(Domain.class, group.getDomainid());

			if (group != null) {
				
				Object usermountO = databaseFacade.findFirst("select count(*)  from RailApplicationToGroup where groupid=?" ,  group.getIdgroup());
				int useramount = 0;
				if (usermountO != null) {
					log.info("amount" + amountO);
					useramount = Integer.parseInt(usermountO + "");
				}
				gourpinfo.setGroupid(group.getIdgroup());
				gourpinfo.setGroupnotes(group.getNotes());
				gourpinfo.setGroupname(group.getGroupname());
				gourpinfo.setDomainname(domain.getDomainname());
				gourpinfo.setUseramount(useramount);
				groups.add(gourpinfo);
			}
		}
		return res;
	}

	/**
	 * Title: listRailAssignmentsByUserId Description:
	 * 
	 * @param @param listAssignmentsParam
	 * @param @return
	 * @return ListAssignmentsResponse
	 * @throws
	 */
	public ListAssignmentsResponse listRailAssignmentsByUserId(ListRailAssignmentsReq listAssignmentsParam) {
		ListAssignmentsResponse res = new ListAssignmentsResponse();
		ResourceList list = new ResourceList();

		List<Resource> rs = new ArrayList<Resource>();
		res.getHead();
		PageView pageview = new PageView(listAssignmentsParam.getPagesize(), listAssignmentsParam.getPage());

		@SuppressWarnings("unchecked")
		List<RailApplicationToUser> lrag = (List<RailApplicationToUser>) this.databaseFacade.find(pageview.getFirstResult(), pageview.getMaxresult(),
				"from RailApplicationToUser where applicationid=?", listAssignmentsParam.getApplicationid());
		// Evan
		Object amountO = this.databaseFacade.findFirst("select count(*) from  RailApplicationToUser where applicationid=?",
				listAssignmentsParam.getApplicationid());
		int amount = 0;
		if (amountO != null) {
			amount = Integer.parseInt(amountO + "");
		}
		List<UserInfo> users = new ArrayList<UserInfo>();
		Resource resource = new Resource();
		resource.setUsers(users);
		rs.add(resource);
		list.setList(rs);
		list.setAmount(amount);
		list.setPage(listAssignmentsParam.getPage());
		list.setPagesize(listAssignmentsParam.getPagesize());
		list.setSortkey(listAssignmentsParam.getSortkey());
		list.setAscend(listAssignmentsParam.getAscend());
		res.setBody(list);
		for (RailApplicationToUser railApplicationToGroup : lrag) {
			User user = this.databaseFacade.load(User.class, railApplicationToGroup.getUserid());

			UserInfo userinfo = new UserInfo();
			if (user != null) {
				BeanUtils.copyProperties(userinfo, user, false);
				userinfo.setUserid(user.getIduser());
				userinfo.setUsernotes(user.getNotes());
				Domain domain = this.databaseFacade.load(Domain.class, user.getDomainid());
				userinfo.setDomainname(domain.getDomainname());
//				if (user.getOrganizationid() != null) {
					Organization organization = this.databaseFacade.load(Organization.class, user.getOrganizationid());
					userinfo.setOrganizationname(organization.getOrganizationname());
//				}
				users.add(userinfo);
			}
		}
		return res;
	}

	/**
	 * Title: listRailAssignmentsByOrganizationid Description:
	 * 
	 * @param @param listAssignmentsParam
	 * @param @return
	 * @return ListAssignmentsResponse
	 * @throws
	 */
	public ListAssignmentsResponse listRailAssignmentsByOrganizationid(ListRailAssignmentsReq listAssignmentsParam) {
		ListAssignmentsResponse res = new ListAssignmentsResponse();
		ResourceList list = new ResourceList();

		List<Resource> rs = new ArrayList<Resource>();
		res.getHead();
		PageView pageview = new PageView(listAssignmentsParam.getPagesize(), listAssignmentsParam.getPage());
		@SuppressWarnings("unchecked")
		List<RailApplicationToOrganization> lrag = (List<RailApplicationToOrganization>) this.databaseFacade.find(pageview.getFirstResult(),
				pageview.getMaxresult(), "from RailApplicationToOrganization where applicationid=?", listAssignmentsParam.getApplicationid());
		// Evan
		Object amountO = this.databaseFacade.findFirst("select count(*) from RailApplicationToOrganization where applicationid=?",
				listAssignmentsParam.getApplicationid());
		int amount = 0;
		if (amountO != null) {
			amount = Integer.parseInt(amountO + "");
		}
		List<OrganizationInfo> organizationInfos = new ArrayList<OrganizationInfo>();
		Resource resource = new Resource();
		resource.setOrganizations(organizationInfos);
		rs.add(resource);
		list.setList(rs);
		list.setAmount(amount);
		list.setPage(listAssignmentsParam.getPage());
		list.setPagesize(listAssignmentsParam.getPagesize());
		list.setSortkey(listAssignmentsParam.getSortkey());
		list.setAscend(listAssignmentsParam.getAscend());
		res.setBody(list);
		for (RailApplicationToOrganization organization : lrag) {
			Organization o = null;
			OrganizationInfo organizationInfo = new OrganizationInfo();
			if (organization.getOrganizationid() != null) {
				o = this.databaseFacade.load(Organization.class, organization.getOrganizationid());
			}
			if (o != null) {
				BeanUtils.copyProperties(organizationInfo, o, false);
				organizationInfo.setOrganizationid(o.getIdorganization());
				organizationInfo.setOrganizationnotes(o.getNotes());
				organizationInfo.setOrganizationtype(o.getOrganizationtype());
				organizationInfos.add(organizationInfo);
				organizationInfo.setOrganizationname(o.getOrganizationname());
				Domain domain = this.databaseFacade.load(Domain.class, o.getDomainid());
				if (domain != null) {
					organizationInfo.setDomainid(domain.getIddomain());
					organizationInfo.setDomainname(domain.getDomainname());
				}
				organizationInfo.setOrganizationtype(o.getOrganizationtype());
			}
		}
		return res;
	}

	/**
	 * Title: listResourceByGroupId Description:
	 * 
	 * @param @param listAssignmentsParam
	 * @param @return
	 * @return ListAssignmentsResponse
	 * @throws
	 */
	public ListAssignmentsResponse listResourceByGroupId(ListRailAssignmentsReq listAssignmentsParam) {
		log.trace("");
		return this
				.listResourceByUserSqlOrGroupSqlOrOrgSql(
						listAssignmentsParam,
						"from RailApplicationToGroup where groupid=? and 1=1 ",
						"select railapplicationview.applicationid,railapplicationview.applicationname,railapplicationview.applicationversion,railapplicationview.replication,railapplicationview.status,railapplicationview.published from railapplicationview join applicationtogroup on railapplicationview.applicationid=applicationtogroup.applicationid where applicationtogroup.groupid=?");
	}

	/**
	 * Title: listResourceByUserId Description:
	 * 
	 * @param @param listAssignmentsParam
	 * @param @return
	 * @return ListAssignmentsResponse
	 * @throws
	 */
	public ListAssignmentsResponse listResourceByUserId(ListRailAssignmentsReq listAssignmentsParam) {

		log.trace("");
		return this
				.listResourceByUserSqlOrGroupSqlOrOrgSql(
						listAssignmentsParam,
						"from RailApplicationToUser where userid=? and 1=1 ",
						"select railapplicationview.applicationid,railapplicationview.applicationname,railapplicationview.applicationversion,railapplicationview.replication,railapplicationview.status,railapplicationview.published from railapplicationview join applicationtouser on railapplicationview.applicationid=applicationtouser.applicationid where applicationtouser.userid=?");
	}

	/**
	 * Title: listResourceByOrganizationId Description:
	 * 
	 * @param @param listAssignmentsParam
	 * @param @return
	 * @return ListAssignmentsResponse
	 * @throws
	 */
	public ListAssignmentsResponse listResourceByOrganizationId(ListRailAssignmentsReq listAssignmentsParam) {
		log.trace("listResourceByOrganizationId :: ");
		if (listAssignmentsParam.getGroupid() != null && listAssignmentsParam.getGroupid() == -1)
			listAssignmentsParam.setGroupid(null);
		return this
				.listResourceByUserSqlOrGroupSqlOrOrgSql(
						listAssignmentsParam,
						"from RailApplicationToOrganization where organizationid=? and 1=1 ",
						"select railapplicationview.applicationid,railapplicationview.applicationname,railapplicationview.applicationversion,railapplicationview.replication,railapplicationview.status,railapplicationview.published from railapplicationview join applicationtoorganization on railapplicationview.applicationid=applicationtoorganization.applicationid where applicationtoorganization.organizationid=?");
	}

	private ListAssignmentsResponse listResourceByUserSqlOrGroupSqlOrOrgSql(ListRailAssignmentsReq listAssignmentsParam, String hql, String sql) {
		ListAssignmentsResponse res = new ListAssignmentsResponse();
		ResourceList list = new ResourceList();
		res.getHead();
		PageView pageview = new PageView(listAssignmentsParam.getPagesize(), listAssignmentsParam.getPage());
		StringBuilder hqlB = new StringBuilder(hql);
		StringBuilder sqlB = new StringBuilder(sql);
		List<Object> param = new ArrayList<Object>();
		if (listAssignmentsParam.getGroupid() != null && listAssignmentsParam.getGroupid() >= 0) {
			param.add(listAssignmentsParam.getGroupid());
		}
		if (listAssignmentsParam.getOrganizationid() != null && listAssignmentsParam.getOrganizationid() >= 0) {
			param.add(listAssignmentsParam.getOrganizationid());
		}
		if (listAssignmentsParam.getUserid() != null && listAssignmentsParam.getUserid() >= 0) {
			param.add(listAssignmentsParam.getUserid());
		}

		if (org.apache.commons.lang.StringUtils.isNotEmpty(listAssignmentsParam.getApplicationid())) {
			hqlB.append(" and applicationid=?");
			hqlB.append(param.add(listAssignmentsParam.getApplicationid()));
			sqlB.append(" and applicationid=?");
		}

		Object amountO = databaseFacade.findFirst("select count(*) " + hql.toString().substring(hql.toString().indexOf("from")), param.toArray());
		int amount = 0;
		log.trace("");
		if (amountO != null) {
			log.info("amount" + amountO);
			amount = Integer.parseInt(amountO + "");
		}
		List<?> listO = databaseFacade.findByNativeSQL(pageview.getFirstResult(), pageview.getMaxresult(), sql.toString(), param.toArray());
		List<Resource> revs = new ArrayList<Resource>();
		for (Object object : listO) {
			Object[] o = (Object[]) object;
			Resource resource = new Resource();
			resource.setApplicationid(o[0] + "");
			resource.setApplicationname(o[1] + "");
			resource.setApplicationversion(o[2] + "");
			resource.setReplication(Integer.parseInt((o[3] + "")));

			resource.setStatus(Integer.parseInt("" + o[4]));
			boolean publishedInt = Boolean.parseBoolean(o[5] + "");
			// boolean p = (publishedInt == 0 ? false : true);
			resource.setPublished(publishedInt);
			revs.add(resource);
		}
		list.setList(revs);
		list.setAmount(amount);
		list.setPage(listAssignmentsParam.getPage());
		list.setPagesize(listAssignmentsParam.getPagesize());
		list.setSortkey(listAssignmentsParam.getSortkey());
		list.setAscend(listAssignmentsParam.getAscend());
		res.setBody(list);
		return res;
	}

	public RailApplicationServer findRailApplicationServerById(Integer idapplication) {
		return this.databaseFacade.load(RailApplicationServer.class, idapplication);
	}

	/**
	 * @Title: updateApplicationServerPublishCount
	 * @Description: 发布时候更新发布数
	 * @param @param applicationServerids 设定文件
	 * @return void 返回类型
	 * @throws
	 */
	public void updateApplicationServerPublishCount(Set<Integer> applicationServerids) {
		log.trace("");
		for (Integer id : applicationServerids) {
			RailApplicationServer server = this.databaseFacade.load(RailApplicationServer.class, id);
			// Evan
			Object countPubulishO = this.databaseFacade.findFirst("select count(*) from RailApplication where published=? and applicationserverid=?", true, id);
			int countPubulish = 0;
			if (countPubulishO != null) {
				countPubulish = Integer.parseInt(countPubulishO + "");
			}
			server.setApppublished(countPubulish);
			this.updateApplicationServer(server);
		}
	}

	// private void delApplicationViewByNotAssign(){

	// }

}