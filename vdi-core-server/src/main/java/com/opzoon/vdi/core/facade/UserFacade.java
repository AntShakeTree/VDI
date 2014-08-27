package com.opzoon.vdi.core.facade;

import static com.opzoon.vdi.core.domain.Administrator.ADMIN_TARGET_ALL;
import static com.opzoon.vdi.core.domain.Administrator.ADMIN_TARGET_GROUP;
import static com.opzoon.vdi.core.domain.Administrator.ADMIN_TARGET_ORGANIZATION;
import static com.opzoon.vdi.core.domain.Domain.DEFAULT_DOMAIN_ID;
import static com.opzoon.vdi.core.domain.GroupElement.ELEMENT_TYPE_GROUP;
import static com.opzoon.vdi.core.domain.GroupElement.ELEMENT_TYPE_USER;
import static com.opzoon.vdi.core.domain.ResourceAssignment.RESOURCE_VISITOR_TYPE_USER;
import static com.opzoon.vdi.core.domain.Session.LOGIN_TYPE_ADMIN;
import static com.opzoon.vdi.core.domain.Session.LOGIN_TYPE_SUPER_ADMIN;
import static com.opzoon.vdi.core.domain.Session.LOGIN_TYPE_USER;
import static com.opzoon.vdi.core.facade.CommonException.BAD_REQUEST;
import static com.opzoon.vdi.core.facade.CommonException.CONFLICT;
import static com.opzoon.vdi.core.facade.CommonException.FORBIDDEN;
import static com.opzoon.vdi.core.facade.CommonException.LDAP_ABNORMAL;
import static com.opzoon.vdi.core.facade.CommonException.NOT_FOUND;
import static com.opzoon.vdi.core.facade.CommonException.NO_ERRORS;
import static com.opzoon.vdi.core.facade.CommonException.UNAUTHORIZED;
import static com.opzoon.vdi.core.facade.FacadeHelper.count;
import static com.opzoon.vdi.core.facade.FacadeHelper.exists;
import static com.opzoon.vdi.core.facade.FacadeHelper.pagingFind;
import static com.opzoon.vdi.core.util.Checksum.getHex;
import static com.opzoon.vdi.core.util.ConditionUtils.numberEquals;
import static com.opzoon.vdi.core.util.ConditionUtils.numberNotEquals;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import javax.naming.AuthenticationException;
import javax.naming.NamingException;
import javax.persistence.EntityExistsException;
import javax.xml.bind.annotation.XmlRootElement;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.opzoon.appstatus.facade.AppStatusService;
import com.opzoon.vdi.core.RunnableWithException;
import com.opzoon.vdi.core.domain.Administrator;
import com.opzoon.vdi.core.domain.Domain;
import com.opzoon.vdi.core.domain.Group;
import com.opzoon.vdi.core.domain.Organization;
import com.opzoon.vdi.core.domain.RSAKey;
import com.opzoon.vdi.core.domain.RestrictionStrategyAssignment;
import com.opzoon.vdi.core.domain.Session;
import com.opzoon.vdi.core.domain.User;
import com.opzoon.vdi.core.domain.UserVolume;
import com.opzoon.vdi.core.facade.FacadeHelper.PagingInfo;
import com.opzoon.vdi.core.facade.transience.SessionFacade;
import com.opzoon.vdi.core.util.LDAPUtils;
import com.opzoon.vdi.core.util.RSAUtils;
import com.opzoon.vdi.core.ws.vo.entrance.AuthenticationMethod;
import com.opzoon.vdi.core.ws.vo.entrance.AuthenticationMethodParamValue;

/**
 * 用户相关业务接口.
 */
public class UserFacade {
	
	private static final Logger log = LoggerFactory.getLogger(UserFacade.class);
	
	private DatabaseFacade databaseFacade;
	private OrganizationFacade organizationFacade;
	private ResourceFacade resourceFacade;
	private DesktopFacade desktopFacade;
	private SessionFacade sessionFacade;
	private AppStatusService appStatusService;

	public DatabaseFacade getDatabaseFacade() {
		return databaseFacade;
	}

	public String loadUserName(int iduser) {
		return (String) databaseFacade.findFirst(
				"select username from User where iduser = ?",
				iduser);
	}

	/**
	 * 登录.
	 * 
	 * @param username 用户名.
	 * @param password 口令.
	 * @param loginAsAdmin 登录身份是否为管理员.
	 * @param errorContainer 存放返回的错误代码的容器.
	 * @return 用户会话. 登录失败则为null.
	 */
	public Session login(
	    List<AuthenticationMethod> authenticationMethods,
	    List<AuthenticationMethodParamValue> params,
	    String domainguid,
	    boolean loginAsAdmin,
	    int[] errorContainer) {
	  if (!appStatusService.isInCluster())
    {
      errorContainer[0] = CommonException.NOT_IN_CLUSTER;
      return null;
    }
    String username = null;
    String password = null;
    String dynamiccode = null;
    String source="";
    String clienttype="";
    for (AuthenticationMethodParamValue param : params) {
      String paramValue = param.getParamvalue();
      if (paramValue == null
          || (!param.getParamname().equals("password") && paramValue.trim().length() < 1)) {
        errorContainer[0] = BAD_REQUEST;
        return null;
      }
      if (param.getParamname().equals("username")) {
        username = paramValue.trim().toLowerCase();
      } else if (param.getParamname().equals("password")) {
        password = paramValue;
      } else if (param.getParamname().equals("dynamiccode")) {
        dynamiccode = paramValue.trim();
      }else if(param.getParamname().equals("source")){
    	  source=paramValue.trim();
      }else if(param.getParamname().equals("clienttype")){
    	  clienttype=paramValue.trim();
      }
    }
		sessionFacade.setCurrentSession(null);
		final Integer iduser;
		if (domainguid == null || domainguid.length() < 1) {
			iduser = (Integer) databaseFacade.findFirst(
					"select iduser from User where deleted = 0 and username = ? and password = ? and domainid = ?",
					username, getHex(password.getBytes()), DEFAULT_DOMAIN_ID);
		} else {
			Domain domain = (Domain) databaseFacade.findFirst(
			    "from Domain where guid = ?",
			    domainguid);
			String userNamePrefix = domain.getDomainbinddn().indexOf("\\") > -1 ? domain.getDomainbinddn().substring(0, domain.getDomainbinddn().indexOf("\\") + 1) : "";
 			try {
 				LDAPUtils.createDirContext(
 						domain.getDomainservername(), domain.getDomainserverport(), userNamePrefix + username, password);
			} catch (AuthenticationException e) {
				log.warn("Authentication error on creating LDAP.", e);
				if (e.getExplanation().indexOf("data 773") > -1)
        {
	        errorContainer[0] = CommonException.PASSWORD_RESET_NEEDED;
        } else
        {
          errorContainer[0] = UNAUTHORIZED;
        }
				return null;
			} catch (NamingException e) {
				log.warn("Error on connect LDAP.", e);
				errorContainer[0] = LDAP_ABNORMAL;
				return null;
			}
			iduser = (Integer) databaseFacade.findFirst(
					"select iduser from User where deleted = 0 and username = ? and domainid = ?",
					username, domain.getIddomain());
		}
		if (iduser == null) {
			errorContainer[0] = UNAUTHORIZED;
			return null;
		}
		if (!loginAsAdmin)
    {
	    RSAKey rsaKey = (RSAKey) databaseFacade.findFirst(
	        "from RSAKey where ownerid = ? and disabled = ?",
	        iduser, 0);
	    if (rsaKey != null) {
	      if (dynamiccode == null || dynamiccode.length() < 1) {
	        errorContainer[0] = CommonException.RSA_REQ;
	        return null;
	      }
	      try {
	        if (!RSAUtils.authenticate(rsaKey.getPdata(), dynamiccode)) {
	          errorContainer[0] = UNAUTHORIZED;
	          return null;
	        }
	      } catch (Exception e) {
	        errorContainer[0] = CommonException.UNKNOWN;
	        return null;
	      }
	    }
    }
		return this.initSession(iduser, password,source ,clienttype,loginAsAdmin, errorContainer);
	}

	/**
	 * 登出.
	 */
	public void logout() {
		this.cleanUpSession(sessionFacade.getCurrentSession().getUserid(), sessionFacade.getCurrentSession().getIdsession(), Session.INVALIDATING_REASON_CODE_LOGGED_OUT);
	}
	
	/**
	 * 创建用户.
	 * 
	 * @param user 用户实体.
	 * @return 错误代码.<br />
	 *         {@link com.opzoon.vdi.core.facade.CommonException#NO_ERRORS}: 成功;<br />
	 *         {@link com.opzoon.vdi.core.facade.CommonException#CONFLICT}: 重复的用户名.<br />
	 *         {@link com.opzoon.vdi.core.facade.CommonException#FORBIDDEN}: 权限不足.
	 */
	public int createUser(User user) throws CommonException {
		if (exists(databaseFacade.findFirst(
				"select count(iduser) from User where username = ? and domainid = ? and deleted = 0",
				user.getUsername(), user.getDomainid()))) {
			return CONFLICT;
		}
		if ((!this.isSuperAdmin(sessionFacade.getCurrentSession().getUserid()))
				&& (!this.canManageOrganization(user.getOrganizationid()))) {
			return FORBIDDEN;
		}
		user.setGuid(user.getUsername());
    user.setDomainname(databaseFacade.load(Domain.class, user.getDomainid()).getDomainname());
    user.setOrganizationname(databaseFacade.load(Organization.class, user.getOrganizationid()).getOrganizationname());
		try {
			databaseFacade.persist(user);
		} catch (EntityExistsException e) {
			throw new CommonException(CONFLICT);
		}
		// FIXME Delete it. Just for auto creating a fixed-sized volume on user creating.
//		desktopFacade.tempCreateUserVolume(user.getIduser());
		return NO_ERRORS;
	}

	/**
	 * 删除用户.
	 * 
	 * @param idgroup 用户ID.
	 * @return 错误代码.<br />
	 *         {@link com.opzoon.vdi.core.facade.CommonException#NO_ERRORS}: 成功;<br />
	 *         {@link com.opzoon.vdi.core.facade.CommonException#NOT_FOUND}: 用户不存在.<br />
	 *         {@link com.opzoon.vdi.core.facade.CommonException#FORBIDDEN}: 权限不足.
	 */
	public void deleteUser(int iduser) throws CommonException {
		if (numberEquals(iduser, 0)) {
			throw new CommonException(FORBIDDEN);
		}
		if (numberEquals(iduser, sessionFacade.getCurrentSession().getUserid())) {
			throw new CommonException(CONFLICT);
		}
		if (!exists(databaseFacade.findFirst(
				"select count(iduser) from User where iduser = ? and domainid = ?",
				iduser, DEFAULT_DOMAIN_ID))) {
			throw new CommonException(NOT_FOUND);
		}
		if ((!this.isSuperAdmin(sessionFacade.getCurrentSession().getUserid()))
				&& (!this.canManageUser(iduser))) {
			throw new CommonException(FORBIDDEN);
		}
		this.forceDeleteUser(iduser);
		//~~~~~~~~~~~~~maxiaochao~~~~~~~~~~~~~rail~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
		databaseFacade.update("delete from RailApplicationToUser where userid=?",iduser);
		//~~~~~~~~~~~~~maxiaochao~~~~~~~~~~~~~rail~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
	}
	
	public void forceDeleteUser(final int iduser) throws CommonException {
		final User user = databaseFacade.load(User.class, iduser);
		databaseFacade.update(
				"delete from Administrator where userid = ?",
				iduser);
		if (!exists(databaseFacade.findFirst(
				"select count(idadministrator) from Administrator where targettype = ?",
				ADMIN_TARGET_ALL))) {
			throw new CommonException(CONFLICT);
		}
    resourceFacade.autoClearResources(
        Arrays.asList(new Integer[] { iduser }),
        true,
        new RunnableWithException() {
          @Override
          public void run() {
            databaseFacade.update(
                "update RSAKey set ownerid = -idrsakey, ownername = null where ownerid = ?",
                iduser);
            databaseFacade.update(
                "delete from GroupElement where elementtype = ? and elementid = ?",
                ELEMENT_TYPE_USER, iduser);
            databaseFacade.update(
                "delete from ResourceAssignment where visitortype = ? and visitorid = ?",
                RESOURCE_VISITOR_TYPE_USER, iduser);
            databaseFacade.update(
                "delete from RestrictionStrategyAssignment where targettype = ? and targetid = ?",
                RestrictionStrategyAssignment.RESTRICTION_STRATEGY_ASSIGNMENT_TARGET_TYPE_USER, iduser);
//            resourceFacade.disconnectUnassignedDesktops(iduser, true);
            sessionFacade.removeAllSessions(iduser);
            databaseFacade.update(
                "update User set deleted = iduser where iduser = ?",
                iduser);
          }
        });
//		if (!exists(databaseFacade.update(
//				"delete from User where iduser = ?",
//				iduser))) {
//			throw new CommonException(NOT_FOUND);
//		}
		desktopFacade.doDeleteUserVolumes(user, null);
//		Thread thread = new Thread() {
//			@Override
//			public void run() {
//			}
//			@Override
//			public String toString() {
//				return super.toString() + "THREAD doDeleteUserVolume " + user.getIduser();
//			}
//		};
//		com.opzoon.ohvc.session.ExcecutorUtil.execute(thread);
	}

	/**
	 * 更新用户.
	 * 
	 * @param group 用户实体.
	 * @return 错误代码.<br />
	 *         {@link com.opzoon.vdi.core.facade.CommonException#NO_ERRORS}: 成功;<br />
	 *         {@link com.opzoon.vdi.core.facade.CommonException#NOT_FOUND}: 用户未找到.<br />
	 *         {@link com.opzoon.vdi.core.facade.CommonException#CONFLICT}: 重复的用户名.<br />
	 *         {@link com.opzoon.vdi.core.facade.CommonException#FORBIDDEN}: 权限不足.
	 * @throws CommonException 重复的用户名. 包含代码{@link com.opzoon.vdi.core.facade.CommonException#CONFLICT}.
	 */
	public int updateUser(User user) throws CommonException {
		if (!exists(databaseFacade.findFirst(
				"select count(iduser) from User where iduser = ? and domainid = ?",
				user.getIduser(), DEFAULT_DOMAIN_ID))) {
			return NOT_FOUND;
		}
		if (user.getUsername() != null) {
			if (exists(databaseFacade.findFirst(
					"select count(iduser) from User where username = ? and domainid = ? and iduser != ? and deleted = ?",
					user.getUsername(), user.getDomainid(), user.getIduser(), 0))) {
				return CONFLICT;
			}
		}
		if (numberNotEquals(sessionFacade.getCurrentSession().getUserid(), user.getIduser())
				&& (!this.isSuperAdmin(sessionFacade.getCurrentSession().getUserid()))
				&& (!this.canManageUser(user.getIduser()))) {
			return FORBIDDEN;
		}
		StringBuilder updateClause = new StringBuilder("update User set iduser = iduser");
		List<Object> params = new ArrayList<Object>();
		if (user.getUsername() != null) {
			updateClause.append(", username = ?");
			params.add(user.getUsername());
		}
		if (user.getPassword() != null) {
			updateClause.append(", password = ?");
			params.add(user.getPassword());
		}
		if (user.getRealname() != null) {
			updateClause.append(", realname = ?");
			params.add(user.getRealname());
		}
		if (user.getIdcardtype() != null) {
			updateClause.append(", idcardtype = ?");
			params.add(user.getIdcardtype());
		}
		if (user.getIdcard() != null) {
			updateClause.append(", idcard = ?");
			params.add(user.getIdcard());
		}
		if (user.getEmail() != null) {
			updateClause.append(", email = ?");
			params.add(user.getEmail());
		}
		if (user.getAddress() != null) {
			updateClause.append(", address = ?");
			params.add(user.getAddress());
		}
		if (user.getTelephone() != null) {
			updateClause.append(", telephone = ?");
			params.add(user.getTelephone());
		}
		if (user.getNotes() != null) {
			updateClause.append(", notes = ?");
			params.add(user.getNotes());
		}
		updateClause.append(" where iduser = ? and domainid = ?");
		params.add(user.getIduser());
		params.add(DEFAULT_DOMAIN_ID);
		Object[] paramsArray = params.toArray();
		try {
			if (!exists(databaseFacade.update(updateClause.toString(), paramsArray))) {
				return NOT_FOUND;
			}
		} catch (EntityExistsException e) {
			throw new CommonException(CONFLICT);
		}
		return NO_ERRORS;
	}

	/**
	 * 分页查询用户.
	 * 
	 * @param iduser 用户ID. -1为忽略.
	 * @param usertype 用户类型. -1为忽略.
	 * @param organizationid 域ID. -1为忽略.
	 * @param groupid 组织ID. -1为忽略.
	 * @param domainid 组ID. -1为忽略.
	 * @param pagingInfo 分页信息.
	 * @param amountContainer 查询结果的总数量的容器.
	 * @return 查询结果列表.
	 */
	@SuppressWarnings("unchecked")
	public List<User> findUsers(int iduser, int usertype, int organizationid, int groupid,
			int domainid, boolean norsa, PagingInfo pagingInfo, int[] amountContainer) {
		StringBuilder selectClause = new StringBuilder("select u from User u");
		StringBuilder whereClause = new StringBuilder(" where 1 = 1 and deleted = ?");
		List<Object> params = new ArrayList<Object>();
		params.add(0);
		if (iduser > -1) {
			whereClause.append(" and u.iduser = ?");
			params.add(iduser);
		}
		if (usertype > -1) {
			whereClause.append(" and u.usertype = ?");
			params.add(usertype);
		}
		if (domainid > -1) {
			whereClause.append(" and u.domainid = ?");
			params.add(domainid);
		}
		if (organizationid > -1) {
			whereClause.append(" and u.organizationid = ?");
			params.add(organizationid);
		}
		if (groupid > -1) {
			selectClause.append(" left join u.groupElements ge");
			whereClause.append(" and ge.groupid = ? and ge.elementtype = ?");
			params.add(groupid);
			params.add(ELEMENT_TYPE_USER);
		}
    if (norsa)
    {
      whereClause.append(" and u.rsakeys.size = 0");
    }
		selectClause.append(whereClause);
		selectClause.append(FacadeHelper.keyword("u", pagingInfo, params));
		Object[] paramsArray = params.toArray();
		count(databaseFacade, "iduser", selectClause, paramsArray, amountContainer);
		List<User> users = pagingFind(databaseFacade, selectClause, paramsArray, pagingInfo);
		for (User user : users) {
			user.setPassword(null);
			user.setGroupElements(null);
			user.setRootadmin(this.isSuperAdmin(user.getIduser()) ? 1 : 0);
			user.setGroups(new LinkedList<GroupInfo>());
			List<Integer> groupIdsOfUser = this.findGroupsOfUser(user.getIduser());
			for (Integer groupId : groupIdsOfUser) {
				GroupInfo groupInfo = new GroupInfo();
				user.getGroups().add(groupInfo);
				Group group = databaseFacade.load(Group.class, groupId);
				groupInfo.setGroupid(groupId);
				groupInfo.setGroupname(group.getGroupname());
				groupInfo.setGroupnotes(group.getNotes());
			}
//			Domain domain = databaseFacade.load(Domain.class, user.getDomainid());
//			user.setDomainname(domain.getDomainname());
////			if (user.getOrganizationid() != null) {
//				Organization organization = databaseFacade.load(Organization.class, user.getOrganizationid());
//				user.setOrganizationname(organization.getOrganizationname());
////			}
			user.setAssignedrsa(exists(databaseFacade.findFirst(
					"select count(idrsakey) from RSAKey where ownerid = ?",
					user.getIduser())) ? 1 : 0);
		}
		return users;
	}

	/**
	 * 关闭用户会话.
	 * 
	 * @param idsession 会话ID.
	 * @return 错误代码.<br />
	 *         {@link com.opzoon.vdi.core.facade.CommonException#NO_ERRORS}: 成功;<br />
	 *         {@link com.opzoon.vdi.core.facade.CommonException#NOT_FOUND}: 会话不存在.<br />
	 *         {@link com.opzoon.vdi.core.facade.CommonException#CONFLICT}: 关闭自己的会话.
	 */
	public int disconnectSession(int idsession) {
		if (numberEquals(sessionFacade.getCurrentSession().getIdsession(), idsession)) {
			return CONFLICT;
		}
		Session session = sessionFacade.loadSession(idsession);
		if (session == null) {
			return NOT_FOUND;
		}
		if ((!this.isSuperAdmin(sessionFacade.getCurrentSession().getUserid()))
				&& !(this.canManageUser(session.getUserid()))) {
			return FORBIDDEN;
		}
		this.cleanUpSession(session.getUserid(), session.getIdsession(), Session.INVALIDATING_REASON_CODE_KILLED);
		return NO_ERRORS;
	}

	public int assignPrivilege(int iduser, int targettype, int targetid) throws CommonException {
		if (numberEquals(iduser, User.DEFAULT_ADMIN_ID)) {
			return FORBIDDEN;
		}
		if (numberEquals(sessionFacade.getCurrentSession().getUserid(), iduser)) {
			return CONFLICT;
		}
		if (exists(databaseFacade.findFirst(
				"select count(idadministrator) from Administrator where targettype = ? and targetid = ? and userid = ?",
				targettype, targetid, iduser))) {
			return CONFLICT;
		}
		if (!exists(databaseFacade.findFirst(
				"select count(iduser) from User where iduser = ?",
				iduser))) {
			return NOT_FOUND;
		}
		if (numberEquals(targettype, ADMIN_TARGET_ORGANIZATION)) {
			if (!exists(databaseFacade.findFirst(
					"select count(idorganization) from Organization where idorganization = ?",
					targetid))) {
				return NOT_FOUND;
			}
			if (!this.isSuperAdmin(sessionFacade.getCurrentSession().getUserid())) {
				if ((!this.canManageUser(iduser)) || (!this.canManageOrganization(targetid))) {
					return FORBIDDEN;
				}
			}
		} else if (numberEquals(targettype, ADMIN_TARGET_GROUP)) {
			if (!exists(databaseFacade.findFirst(
					"select count(idgroup) from Group where idgroup = ?",
					targetid))) {
				return NOT_FOUND;
			}
			if (!this.isSuperAdmin(sessionFacade.getCurrentSession().getUserid())) {
				if ((!this.canManageUser(iduser)) || (!this.canManageGroup(targetid))) {
					return FORBIDDEN;
				}
			}
		} else {
			if (!this.isSuperAdmin(sessionFacade.getCurrentSession().getUserid())) {
				return FORBIDDEN;
			}
		}
		Administrator administrator = new Administrator();
		administrator.setTargetid(targetid);
		administrator.setTargettype(targettype);
		administrator.setUserid(iduser);
		try {
			databaseFacade.persist(administrator);
		} catch (EntityExistsException e) {
			throw new CommonException(CONFLICT);
		}
		return NO_ERRORS;
	}

	public int unassignPrivilege(int iduser, int targettype, int targetid) throws CommonException {
		if (numberEquals(iduser, User.DEFAULT_ADMIN_ID)) {
			return FORBIDDEN;
		}
		if (numberEquals(sessionFacade.getCurrentSession().getUserid(), iduser)) {
			return CONFLICT;
		}
		if (!exists(databaseFacade.findFirst(
				"select count(idadministrator) from Administrator where targettype = ? and targetid = ? and userid = ?",
				targettype, targetid, iduser))) {
			return NOT_FOUND;
		}
		if (!this.isSuperAdmin(sessionFacade.getCurrentSession().getUserid())) {
			if (!this.canManageUser(iduser)) {
				return FORBIDDEN;
			}
		}
		databaseFacade.update(
				"delete from Administrator where targettype = ? and targetid = ? and userid = ?",
				targettype, targetid, iduser);
		if (numberEquals(targettype, ADMIN_TARGET_ALL)) {
			if (!exists(databaseFacade.findFirst(
					"select count(idadministrator) from Administrator where targettype = ?",
					ADMIN_TARGET_ALL))) {
				throw new CommonException(CONFLICT);
			}
		}
		return NO_ERRORS;
	}

	@SuppressWarnings("unchecked")
	public List<Integer> findGroupsOfUser(int userid) {
		return (List<Integer>) databaseFacade.find(
				"select groupid from GroupElement where elementid = ? and elementtype = ?",
				userid, ELEMENT_TYPE_USER);
	}

	public Integer findOrganizationOfUser(int userid) {
		User user = databaseFacade.load(User.class, userid);
		return user.getOrganizationid();
	}
	
	@SuppressWarnings("unchecked")
	public List<Integer> findUsersOfOrganization(int organizationid) {
		return (List<Integer>) databaseFacade.find(
				"select iduser from User where organizationid = ?",
				organizationid);
	}

	/**
	 * 清理过期的会话.
	 */
	public void cleanExpiredSessions() {
		List<Session> expiredSessions = sessionFacade.findExpiredSessions();
		for (Session expiredSession : expiredSessions) {
			this.cleanUpSession(expiredSession.getUserid(), expiredSession.getIdsession(), Session.INVALIDATING_REASON_CODE_TIMED_OUT);
		}
	}

	public void cleanAllSessions() {
		List<Session> sessions = sessionFacade.findAllSessions();
		for (Session session : sessions) {
			this.cleanUpSession(session.getUserid(), session.getIdsession(), Session.INVALIDATING_REASON_CODE_KILLED);
		}
	}

	public boolean userNotExists(Integer userid) {
		return !exists(databaseFacade.findFirst(
				"select count(iduser) from User where iduser = ?",
				userid));
	}

	/**
	 * 判断某用户是否为全局管理员.
	 * 
	 * @param iduser 用户ID.
	 * @return 是否为全局管理员.
	 */
	public boolean isSuperAdmin(int iduser) {
		return exists(databaseFacade.findFirst(
				"select count(idadministrator) from Administrator where userid = ? and targettype = ?",
				iduser, ADMIN_TARGET_ALL));
	}

	/**
	 * 判断当前用户是否可管理某用户.
	 * 
	 * @param iduser 用户ID.
	 * @return 是否可管理某用户.
	 */
	public boolean canManageUser(int iduser) {
		Integer organizationOfUser = (Integer) databaseFacade.findFirst(
				"select organizationid from User where iduser = ?",
				iduser);
		if (organizationOfUser == null) {
			// Can't happen ?
			return false;
		}
		if (this.canManageOrganization(organizationOfUser)) {
			return true;
		}
		@SuppressWarnings("unchecked")
		List<Integer> groupsOfUser = (List<Integer>) databaseFacade.find(
				"select groupid from GroupElement where elementid = ? and elementtype = ?",
				iduser, ELEMENT_TYPE_USER);
		for (Integer group : groupsOfUser) {
			if (this.canManageGroup(group)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * 判断当前用户是否可管理某组织.
	 * 
	 * @param organizationid 组织ID.
	 * @return 是否可管理某组织.
	 */
	public boolean canManageOrganization(int organizationid) {
		List<Integer> organizationAndParents = organizationFacade.findSelfAndParents(organizationid);
		for (Integer checkingOrganization : organizationAndParents) {
			if (exists(databaseFacade.findFirst(
					"select count(idadministrator) from Administrator where userid = ? and targettype = ? and targetid = ?",
					sessionFacade.getCurrentSession().getUserid(), ADMIN_TARGET_ORGANIZATION, checkingOrganization))) {
				return true;
			}
		}
		return false;
	}

	/**
	 * 判断当前用户是否可管理某组.
	 * 
	 * @param groupid 组ID.
	 * @return 是否可管理某组.
	 */
	public boolean canManageGroup(int groupid) {
		for (Integer checkingGroup = groupid; checkingGroup != null && checkingGroup > -1; ) {
			if (exists(databaseFacade.findFirst(
					"select count(idadministrator) from Administrator where userid = ? and targettype = ? and targetid = ?",
					sessionFacade.getCurrentSession().getUserid(), ADMIN_TARGET_GROUP, checkingGroup))) {
				return true;
			}
			checkingGroup = (Integer) databaseFacade.findFirst(
					"select groupid from GroupElement where elementtype = ? and elementid = ?",
					ELEMENT_TYPE_GROUP, checkingGroup);
		}
		return false;
	}

	public boolean checkIfHavingVolume(int userid) {
		return exists(databaseFacade.findFirst(
				"select count(iduser) from User where iduser = ? and storageid != null",
				userid));
	}

	public void createUserVolume(int userid, int cloudmanagerid, String volumename, String volumeId, long size) {
		UserVolume userVolume = new UserVolume();
		userVolume.setCloudmanagerid(cloudmanagerid);
		userVolume.setStorageid(volumeId);
		userVolume.setTotalsize(size);
		userVolume.setUsedsize(0);
		userVolume.setUserid(userid);
		userVolume.setVolumename(volumename);
		databaseFacade.persist(userVolume);
	}

	public void deleteUserVolumes(int userid) {
		databaseFacade.update(
				"delete from UserVolume where userid = ?",
				userid);
	}

	public void deleteUserVolume(int uservolumeid) {
		databaseFacade.update(
				"delete from UserVolume where iduservolume = ?",
				uservolumeid);
	}

	public void updateUserVolume(int uservolumeid, String volumeId) {
		databaseFacade.update(
				"update UserVolume set storageid = ? where iduservolume = ?",
				volumeId, uservolumeid);
	}

	public int updatePassword(String oldPassword, String newPassword) {
		String hashedOldPassword = getHex(oldPassword.getBytes());
		String hashedNewPassword = getHex(newPassword.getBytes());
		if (!exists(databaseFacade.findFirst(
				"select count(iduser) from User where iduser = ? and password = ? and domainid = ?",
				sessionFacade.getCurrentSession().getUserid(), hashedOldPassword, DEFAULT_DOMAIN_ID))) {
			return CommonException.ORIGINAL_PASSWORD_INVALID;
		}
		databaseFacade.update(
				"update User set password = ? where iduser = ? and domainid = ?",
				hashedNewPassword, sessionFacade.getCurrentSession().getUserid(), DEFAULT_DOMAIN_ID);
		sessionFacade.getCurrentSession().setPassword(newPassword);
		return NO_ERRORS;
	}

	public boolean checkPassword(String password) {
		return password != null
				&& password.length() > 3
				&& password.length() < 128;
	}

  public void importStandardRSAKey(RSAKey rsaKey) throws CommonException
  {
    if (!this.isSuperAdmin(sessionFacade.getCurrentSession().getUserid())) {
      throw new CommonException(FORBIDDEN);
    }
    rsaKey.setDisabled(0);
    rsaKey.setIdrsakey(null);
    rsaKey.setOwnerid(-1);
    rsaKey.setOwnername(null);
    rsaKey.setRsatype(RSAKey.RSA_TYPE_STANDARD);
    try
    {
      log.trace("RSA: " + rsaKey.getPdata());
      String newPdata = RSAUtils.parse(rsaKey.getPdata());
      rsaKey.setPdata(newPdata);
      databaseFacade.persist(rsaKey);
    } catch (Exception e)
    {
      log.warn("Exception", e);
      throw new CommonException(CommonException.CONFLICT);
    }
    rsaKey.setOwnerid(-rsaKey.getIdrsakey());
    databaseFacade.merge(rsaKey);
  }

  public void assignStandardRSAKey(int rsakeyid, int userid) throws CommonException
  {
    if (!this.isSuperAdmin(sessionFacade.getCurrentSession().getUserid())) {
      throw new CommonException(FORBIDDEN);
    }
    User user = databaseFacade.load(User.class, userid);
    try
    {
      databaseFacade.update(
          "update RSAKey set ownerid = ?, ownername = ? where idrsakey = ?",
          userid, user.getUsername(), rsakeyid);
    } catch (Exception e)
    {
      throw new CommonException(CommonException.CONFLICT);
    }
  }

  public void unassignStandardRSAKey(int rsakeyid) throws CommonException
  {
    if (!this.isSuperAdmin(sessionFacade.getCurrentSession().getUserid())) {
      throw new CommonException(FORBIDDEN);
    }
    databaseFacade.update(
        "update RSAKey set ownerid = ?, ownername = null where idrsakey = ?",
        -rsakeyid, rsakeyid);
  }

  public void enableStandardRSAKey(int rsakeyid) throws CommonException
  {
    if (!this.isSuperAdmin(sessionFacade.getCurrentSession().getUserid())) {
      throw new CommonException(FORBIDDEN);
    }
    databaseFacade.update(
        "update RSAKey set disabled = 0 where idrsakey = ?",
        rsakeyid);
  }

  public void disableStandardRSAKey(int rsakeyid) throws CommonException
  {
    if (!this.isSuperAdmin(sessionFacade.getCurrentSession().getUserid())) {
      throw new CommonException(FORBIDDEN);
    }
    databaseFacade.update(
        "update RSAKey set disabled = 1 where idrsakey = ?",
        rsakeyid);
  }

  public void deleteStandardRSAKey(int rsakeyid) throws CommonException
  {
    if (!this.isSuperAdmin(sessionFacade.getCurrentSession().getUserid())) {
      throw new CommonException(FORBIDDEN);
    }
    databaseFacade.update(
        "delete from RSAKey where idrsakey = ?",
        rsakeyid);
  }

  public List<RSAKey> findStandardRSAKeys(
      boolean unassignedOnly,
      PagingInfo pagingInfo, int[] amountContainer) {
    StringBuilder selectClause = new StringBuilder("from RSAKey where 1 = 1");
    List<Object> params = new ArrayList<Object>();
    if (unassignedOnly)
    {
      selectClause.append(" and ownerid < ?");
      params.add(0);
    }
    if (pagingInfo.getDataindex() != null &&pagingInfo.getDataindex().equals("status")) {
      if (pagingInfo.getContent().equals("0"))
      {
        selectClause.append(" and disabled = 0");
      } else if (pagingInfo.getContent().equals("1"))
      {
        selectClause.append(" and disabled = 1");
      } else
      {
        selectClause.append(" and ownerid != ?");
        params.add(-1);
      }
      pagingInfo.setDataindex(null);
    }
    selectClause.append(FacadeHelper.keyword(null, pagingInfo, params));
    Object[] paramsArray = params.toArray();
    count(databaseFacade, "idrsakey", selectClause, paramsArray, amountContainer);
    @SuppressWarnings("unchecked")
    List<RSAKey> rsakeys = pagingFind(databaseFacade, selectClause, paramsArray, pagingInfo);
    for (RSAKey rsaKey : rsakeys)
    {
      rsaKey.setPdata(null);
    }
    return rsakeys;
  }

	public void setDatabaseFacade(DatabaseFacade databaseFacade) {
		this.databaseFacade = databaseFacade;
	}

	public void setOrganizationFacade(OrganizationFacade organizationFacade) {
		this.organizationFacade = organizationFacade;
	}

	public void setResourceFacade(ResourceFacade resourceFacade) {
		this.resourceFacade = resourceFacade;
	}

	public void setDesktopFacade(DesktopFacade desktopFacade) {
		this.desktopFacade = desktopFacade;
	}

	public void setSessionFacade(SessionFacade sessionFacade) {
		this.sessionFacade = sessionFacade;
	}

  public void setAppStatusService(AppStatusService appStatusService)
  {
    this.appStatusService = appStatusService;
  }

	private Session initSession(int iduser, String password, String source,String clienttype,boolean loginAsAdmin, int[] errorContainer) {
		Session session = new Session();
		session.setSource(source);
		session.setClienttype(clienttype);
		session.setExpire(this.calculateNewExpire());
		if (loginAsAdmin) {
			if (this.isSuperAdmin(iduser)) {
				session.setLogintype(LOGIN_TYPE_SUPER_ADMIN);
			} else {
				if (exists(databaseFacade.findFirst(
						"select count(idadministrator) from Administrator where userid = ?",
						iduser))) {
					session.setLogintype(LOGIN_TYPE_ADMIN);
				} else {
					errorContainer[0] = FORBIDDEN;
					return null;
				}
			}
		} else {
			session.setLogintype(LOGIN_TYPE_USER);
		}
		session.setUserid(iduser);
		if (!loginAsAdmin) {
			this.cleanUpSession(iduser, null, Session.INVALIDATING_REASON_CODE_KICKED);
		} else {
			// TODO No way to replace ?
//			if (this.getCurrentSession() != null && this.getCurrentSession().getUserid() == iduser) {
//				this.cleanUpSession(iduser, this.getCurrentSession().getIdsession());
//			}
		}
		session.setPassword(password);
		session.setUsername((String) databaseFacade.findFirst(
						"select username from User where iduser = ?",
						iduser));
		sessionFacade.createNewSession(session);
		return session;
	}

	private void cleanUpSession(int iduser, Integer idsession, int invalidatingReasonCode) {
		resourceFacade.releaseConnections(iduser, idsession);
		sessionFacade.removeSession(iduser, idsession, invalidatingReasonCode);
	}
	
	private Date calculateNewExpire() {
		return new Date(System.currentTimeMillis() + 1000 * 60 * 30);// 30 minutes.
	}

	@XmlRootElement(name = "group")
	public static class GroupInfo implements Serializable {

		private static final long serialVersionUID = 1L;

		private int groupid;
		private String groupname;
		private String groupnotes;
		private int domaintype;
		private int useramount;
		private String domainname;
		
		public int getGroupid() {
			return groupid;
		}
		public void setGroupid(int groupid) {
			this.groupid = groupid;
		}
		public String getGroupname() {
			return groupname;
		}
		public void setGroupname(String groupname) {
			this.groupname = groupname;
		}
		public String getGroupnotes() {
			return groupnotes;
		}
		public void setGroupnotes(String groupnotes) {
			this.groupnotes = groupnotes;
		}
		public int getDomaintype() {
			return domaintype;
		}
		public void setDomaintype(int domaintype) {
			this.domaintype = domaintype;
		}
		public int getUseramount() {
			return useramount;
		}
		public void setUseramount(int useramount) {
			this.useramount = useramount;
		}
		/** 
		 * @return domainname 
		 */
		public String getDomainname() {
			return domainname;
		}
		/**
		 * @param domainname the domainname to set
		 */
		public void setDomainname(String domainname) {
			this.domainname = domainname;
		}
		
	}

}