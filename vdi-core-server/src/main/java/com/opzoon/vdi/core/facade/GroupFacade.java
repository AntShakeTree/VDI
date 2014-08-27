package com.opzoon.vdi.core.facade;

import static com.opzoon.vdi.core.domain.Administrator.ADMIN_TARGET_GROUP;
import static com.opzoon.vdi.core.domain.Domain.DEFAULT_DOMAIN_ID;
import static com.opzoon.vdi.core.domain.GroupElement.ELEMENT_TYPE_GROUP;
import static com.opzoon.vdi.core.domain.GroupElement.ELEMENT_TYPE_ORGANIZATION;
import static com.opzoon.vdi.core.domain.GroupElement.ELEMENT_TYPE_USER;
import static com.opzoon.vdi.core.domain.ResourceAssignment.RESOURCE_VISITOR_TYPE_GROUP;
import static com.opzoon.vdi.core.facade.CommonException.CONFLICT;
import static com.opzoon.vdi.core.facade.CommonException.FORBIDDEN;
import static com.opzoon.vdi.core.facade.CommonException.NOT_FOUND;
import static com.opzoon.vdi.core.facade.CommonException.NO_ERRORS;
import static com.opzoon.vdi.core.facade.FacadeHelper.count;
import static com.opzoon.vdi.core.facade.FacadeHelper.exists;
import static com.opzoon.vdi.core.facade.FacadeHelper.pagingFind;
import static com.opzoon.vdi.core.util.ConditionUtils.numberNotEquals;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.persistence.EntityExistsException;

import com.opzoon.vdi.core.RunnableWithException;
import com.opzoon.vdi.core.domain.Group;
import com.opzoon.vdi.core.domain.GroupElement;
import com.opzoon.vdi.core.domain.Organization;
import com.opzoon.vdi.core.domain.RestrictionStrategyAssignment;
import com.opzoon.vdi.core.domain.User;
import com.opzoon.vdi.core.facade.FacadeHelper.PagingInfo;
import com.opzoon.vdi.core.facade.transience.SessionFacade;

/**
 * 组相关业务接口.
 */
public class GroupFacade {

	private DatabaseFacade databaseFacade;
	private UserFacade userFacade;
	private OrganizationFacade organizationFacade;
	private ResourceFacade resourceFacade;
	private SessionFacade sessionFacade;

  @SuppressWarnings("unchecked")
  public List<Integer> findDirectGroupsOfUser(int iduser) {
    return (List<Integer>) databaseFacade.find(
        "select groupid from GroupElement where elementid = ? and elementtype = ?",
        iduser, ELEMENT_TYPE_USER);
  }

  @SuppressWarnings("unchecked")
  public List<Integer> findDirectGroupsOfOrganization(int idorganization) {
    return (List<Integer>) databaseFacade.find(
        "select groupid from GroupElement where elementid = ? and elementtype = ?",
        idorganization, ELEMENT_TYPE_ORGANIZATION);
  }
	
	/**
	 * 创建组.
	 * 
	 * @param group 组实体.
	 * @return 错误代码.<br />
	 *         {@link com.opzoon.vdi.core.facade.CommonException#NO_ERRORS}: 成功;<br />
	 *         {@link com.opzoon.vdi.core.facade.CommonException#CONFLICT}: 重复的组名.<br />
	 *         {@link com.opzoon.vdi.core.facade.CommonException#FORBIDDEN}: 权限不足.
	 * @throws CommonException 
	 */
	public int createGroup(Group group) throws CommonException {
		if (exists(databaseFacade.findFirst(
				"select count(idgroup) from Group where groupname = ?",
				group.getGroupname()))) {
			return CONFLICT;
		}
		if (!exists(databaseFacade.findFirst(
				"select count(iddomain) from Domain where iddomain = ?",
				group.getDomainid()))) {
			return CONFLICT;
		}
		if (!userFacade.isSuperAdmin(sessionFacade.getCurrentSession().getUserid())) {
			return FORBIDDEN;
		}
		try {
			databaseFacade.persist(group);
		} catch (EntityExistsException e) {
			throw new CommonException(CONFLICT);
		}
		return NO_ERRORS;
	}

	/**
	 * 删除组.
	 * 
	 * @param idgroup 组ID.
	 * @return 错误代码.<br />
	 *         {@link com.opzoon.vdi.core.facade.CommonException#NO_ERRORS}: 成功;<br />
	 *         {@link com.opzoon.vdi.core.facade.CommonException#NOT_FOUND}: 组不存在.<br />
	 *         {@link com.opzoon.vdi.core.facade.CommonException#FORBIDDEN}: 权限不足.
	 */
	public int deleteGroup(final int idgroup) {
		if (!exists(databaseFacade.findFirst(
				"select count(idgroup) from Group where idgroup = ?",
				idgroup))) {
			return NOT_FOUND;
		}
		if ((!userFacade.isSuperAdmin(sessionFacade.getCurrentSession().getUserid()))
				&& (!userFacade.canManageGroup(idgroup))) {
			return FORBIDDEN;
		}
		return this.forceDeleteGroup(idgroup);
	}
	
  public int forceDeleteGroup(final int idgroup) {
    if (!exists(databaseFacade.findFirst(
        "select count(idgroup) from Group where idgroup = ?",
        idgroup))) {
      return NOT_FOUND;
    }
    if ((!userFacade.isSuperAdmin(sessionFacade.getCurrentSession().getUserid()))
        && (!userFacade.canManageGroup(idgroup))) {
      return FORBIDDEN;
    }
    final Set<Integer> users = this.findUsers(idgroup);
    try
    {
      resourceFacade.autoClearResources(
          users,
          true,
          new RunnableWithException() {
            @Override
            public void run() {
              databaseFacade.update(
                  "delete from ResourceAssignment where visitortype = ? and visitorid = ?",
                  RESOURCE_VISITOR_TYPE_GROUP, idgroup);
              databaseFacade.update(
                  "delete from RestrictionStrategyAssignment where targettype = ? and targetid = ?",
                  RestrictionStrategyAssignment.RESTRICTION_STRATEGY_ASSIGNMENT_TARGET_TYPE_GROUP, idgroup);
              databaseFacade.update(
                  "delete from Group where idgroup = ?",
                  idgroup);
              databaseFacade.update(
                  "delete from GroupElement where groupid = ?",
                  idgroup);
              databaseFacade.update(
                  "delete from Administrator where targettype = ? and targetid = ?",
                  ADMIN_TARGET_GROUP, idgroup);
              databaseFacade.update(
                  "delete from GroupElement where elementtype = ? and elementid = ?",
                  ELEMENT_TYPE_GROUP, idgroup);
              //~~~~~~~~~~~~~maxiaochao~~~~~~~~~~~~~rail~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
              databaseFacade.update("delete from RailApplicationToGroup where groupid=?",idgroup);
              //~~~~~~~~~~~~~maxiaochao~~~~~~~~~~~~~rail~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
            }
          });
    } catch (CommonException e)
    {
      // TODO Auto-generated catch block
      e.printStackTrace();
      return e.getError();
    }
//    for (Integer user : users) {
//      resourceFacade.disconnectUnassignedDesktops(user, false);
//    }
    return NO_ERRORS;
  }

	/**
	 * 更新组.
	 * 
	 * @param group 组实体.
	 * @return 错误代码.<br />
	 *         {@link com.opzoon.vdi.core.facade.CommonException#NO_ERRORS}: 成功;<br />
	 *         {@link com.opzoon.vdi.core.facade.CommonException#NOT_FOUND}: 组未找到.<br />
	 *         {@link com.opzoon.vdi.core.facade.CommonException#CONFLICT}: 重复的组名.<br />
	 *         {@link com.opzoon.vdi.core.facade.CommonException#FORBIDDEN}: 权限不足.
	 * @throws CommonException 重复的组名. 包含代码{@link com.opzoon.vdi.core.facade.CommonException#CONFLICT}.
	 */
	public int updateGroup(Group group) throws CommonException {
		if (!exists(databaseFacade.findFirst(
				"select count(idgroup) from Group where idgroup = ?",
				group.getIdgroup()))) {
			return NOT_FOUND;
		}
		if (group.getGroupname() != null) {
			if (exists(databaseFacade.findFirst(
					"select count(idgroup) from Group where groupname = ? and idgroup != ?",
					group.getGroupname(), group.getIdgroup()))) {
				return CONFLICT;
			}
		}
		if ((!userFacade.isSuperAdmin(sessionFacade.getCurrentSession().getUserid()))
				&& (!userFacade.canManageGroup(group.getIdgroup()))) {
			return FORBIDDEN;
		}
		StringBuilder updateClause = new StringBuilder("update Group set idgroup = idgroup");
		List<Object> params = new ArrayList<Object>();
		if (group.getGroupname() != null) {
			updateClause.append(", groupname = ?");
			params.add(group.getGroupname());
		}
		if (group.getNotes() != null) {
			updateClause.append(", notes = ?");
			params.add(group.getNotes());
		}
		updateClause.append(" where idgroup = ?");
		params.add(group.getIdgroup());
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
	 * 分页查询组.
	 * 
	 * @param idgroup 组ID. -1为忽略.
	 * @param pagingInfo 分页信息.
	 * @param amountContainer 查询结果的总数量的容器.
	 * @return 查询结果列表.
	 */
	@SuppressWarnings("unchecked")
	public List<Group> findGroups(int idgroup, int domainid, int grouptype, PagingInfo pagingInfo, int[] amountContainer) {
		StringBuilder whereClause = new StringBuilder("from Group where 1 = 1");
		List<Object> params = new ArrayList<Object>();
		if (idgroup > -1) {
			whereClause.append(" and idgroup = ?");
			params.add(idgroup);
		}
		if (domainid > -1) {
			whereClause.append(" and domainid = ?");
			params.add(domainid);
		}
		// FIXME Magic numbers.
		if (grouptype == 0) {
			whereClause.append(" and domainid = ?");
			params.add(0);
		} else if (grouptype == 1) {
			whereClause.append(" and domainid != ?");
			params.add(0);
		}
		whereClause.append(FacadeHelper.keyword(pagingInfo, params));
		Object[] paramsArray = params.toArray();
		count(databaseFacade, "idgroup", whereClause, paramsArray, amountContainer);
		List<Group> groups = pagingFind(databaseFacade, whereClause, paramsArray, pagingInfo);
		for (Group group : groups) {
			group.setUseramount(((Long) databaseFacade.findFirst(
					"select count(idgroupelement) from GroupElement where groupid = ? and elementtype = ?",
					group.getIdgroup(), ELEMENT_TYPE_USER)).intValue());
			group.setDomainname((String) databaseFacade.findFirst(
					"select domainname from Domain where iddomain = ?",
					group.getDomainid()));
		}
		return groups;
	}

	/**
	 * 将用户加入组中.
	 * 
	 * @param userid 用户ID.
	 * @param groupid 组ID.
	 * @return 错误代码.<br />
	 *         {@link com.opzoon.vdi.core.facade.CommonException#NO_ERRORS}: 成功;<br />
	 *         {@link com.opzoon.vdi.core.facade.CommonException#CONFLICT}: 关系已存在.
	 * @throws CommonException 
	 */
	public int addUserToGroup(int userid, int groupid) throws CommonException {
		int error = this.validateUserAndGroup(userid, groupid);
		if (error != NO_ERRORS) {
			return error;
		}
		if (exists(databaseFacade.findFirst(
				"select count(idgroupelement) from GroupElement where groupid = ? and elementid = ? and elementtype = ?",
				groupid, userid, ELEMENT_TYPE_USER))) {
			return CONFLICT;
		}
		GroupElement groupElement = new GroupElement();
		groupElement.setElementid(userid);
		groupElement.setElementtype(ELEMENT_TYPE_USER);
		groupElement.setGroupid(groupid);
		try {
			databaseFacade.persist(groupElement);
		} catch (EntityExistsException e) {
			throw new CommonException(CONFLICT);
		}
		return NO_ERRORS;
	}

	/**
	 * 将用户从组中删除.
	 * 
	 * @param userid 用户ID.
	 * @param groupid 组ID.
	 * @return 错误代码.<br />
	 *         {@link com.opzoon.vdi.core.facade.CommonException#NO_ERRORS}: 成功;<br />
	 *         {@link com.opzoon.vdi.core.facade.CommonException#NOT_FOUND}: 关系不存在.
	 */
	public int deleteUserFromGroup(final int userid, final int groupid) {
		int error = this.validateUserAndGroup(userid, groupid);
		if (error != NO_ERRORS) {
			return error;
		}
		if (!exists(databaseFacade.findFirst(
				"select count(idgroupelement) from GroupElement where groupid = ? and elementid = ? and elementtype = ?",
				groupid, userid, ELEMENT_TYPE_USER))) {
			return NOT_FOUND;
		}
//		resourceFacade.disconnectUnassignedDesktops(userid, false);
    try
    {
      resourceFacade.autoClearResources(
          Arrays.asList(new Integer[] { userid }),
          true,
          new RunnableWithException() {
            @Override
            public void run() {
              databaseFacade.update(
                  "delete from GroupElement where groupid = ? and elementid = ? and elementtype = ?",
                  groupid, userid, ELEMENT_TYPE_USER);
            }
          });
    } catch (CommonException e)
    {
      // TODO Auto-generated catch block
      e.printStackTrace();
      return e.getError();
    }
		return NO_ERRORS;
	}

	public int addOrganizationToGroup(int organizationid, int groupid) throws CommonException {
		int error = this.validateOrganizationAndGroup(organizationid, groupid);
		if (error != NO_ERRORS) {
			return error;
		}
		if (exists(databaseFacade.findFirst(
				"select count(idgroupelement) from GroupElement where groupid = ? and elementid = ? and elementtype = ?",
				groupid, organizationid, ELEMENT_TYPE_ORGANIZATION))) {
			return CONFLICT;
		}
		GroupElement groupElement = new GroupElement();
		groupElement.setElementid(organizationid);
		groupElement.setElementtype(ELEMENT_TYPE_ORGANIZATION);
		groupElement.setGroupid(groupid);
		try {
			databaseFacade.persist(groupElement);
		} catch (EntityExistsException e) {
			throw new CommonException(CONFLICT);
		}
		return NO_ERRORS;
	}

	public int deleteOrganizationFromGroup(final int organizationid, final int groupid) {
		int error = this.validateOrganizationAndGroup(organizationid, groupid);
		if (error != NO_ERRORS) {
			return error;
		}
		if (!exists(databaseFacade.findFirst(
				"select count(idgroupelement) from GroupElement where groupid = ? and elementid = ? and elementtype = ?",
				groupid, organizationid, ELEMENT_TYPE_ORGANIZATION))) {
			return NOT_FOUND;
		}
    final Set<Integer> users = organizationFacade.findUsers(organizationid);
//		for (Integer user : users) {
//			resourceFacade.disconnectUnassignedDesktops(user, false);
//		}
    try
    {
      resourceFacade.autoClearResources(
          users,
          true,
          new RunnableWithException() {
            @Override
            public void run() {
              databaseFacade.update(
                  "delete from GroupElement where groupid = ? and elementid = ? and elementtype = ?",
                  groupid, organizationid, ELEMENT_TYPE_ORGANIZATION);
            }
          });
    } catch (CommonException e)
    {
      // TODO Auto-generated catch block
      e.printStackTrace();
      return e.getError();
    }
		return NO_ERRORS;
	}
	
	@SuppressWarnings("unchecked")
	public Set<Integer> findUsers(int idgroup) {
		Set<Integer> users = new HashSet<Integer>();
		users.addAll((List<Integer>) databaseFacade.find(
				"select elementid from GroupElement where groupid = ? and elementtype = ?",
				idgroup, ELEMENT_TYPE_USER));
		List<Integer> organizations = (List<Integer>) databaseFacade.find(
				"select elementid from GroupElement where groupid = ? and elementtype = ?",
				idgroup, ELEMENT_TYPE_ORGANIZATION);
		for (Integer organization : organizations) {
			users.addAll(organizationFacade.findUsers(organization));
		}
		return users;
	}

	public int deleteElementIfConflictInDomain(int groupid, int elementid, int elementtype) {
		int domainId = this.findDomainId(groupid);
		int newDomainId = this.findDomainIdOfElement(groupid, elementid, elementtype);
		if (numberNotEquals(domainId, newDomainId)) {
			databaseFacade.update(
					"delete from GroupElement where groupid = ? and elementid = ? and elementtype = ?",
					groupid, elementid, elementtype);
			return CONFLICT;
		}
		return NO_ERRORS;
	}

	public int findDomainId(int groupid) {
//		Integer firstAssignedPoolOfGroup = resourceFacade.findFirstAssignedPoolOfGroup(groupid);
//		if (firstAssignedPoolOfGroup != null) {
//			Integer domainIdOfPool = resourceFacade.findDomainIdOfPool(firstAssignedPoolOfGroup);
//			if (domainIdOfPool == null) {
//				// NO ONE CAN JOIN THE GROUP.
//				return -1;
//			} else {
//				return domainIdOfPool;
//			}
//		}
//		GroupElement firstElement = (GroupElement) databaseFacade.findFirst(
//				"from GroupElement where groupid = ?",
//				groupid);
//		if (firstElement != null) {
//			return this.findDomainIdOfElement(groupid, firstElement);
//		}
		Group group = databaseFacade.load(Group.class, groupid);
		return group.getDomainid();
	}

	public void setDatabaseFacade(DatabaseFacade databaseFacade) {
		this.databaseFacade = databaseFacade;
	}
	
	public void setUserFacade(UserFacade userFacade) {
		this.userFacade = userFacade;
	}

	public void setOrganizationFacade(OrganizationFacade organizationFacade) {
		this.organizationFacade = organizationFacade;
	}

	public void setResourceFacade(ResourceFacade resourceFacade) {
		this.resourceFacade = resourceFacade;
	}

	public void setSessionFacade(SessionFacade sessionFacade) {
		this.sessionFacade = sessionFacade;
	}

	private int validateUserAndGroup(int userid, int groupid) {
		if (!userFacade.isSuperAdmin(sessionFacade.getCurrentSession().getUserid())) {
			if ((!userFacade.canManageUser(userid)) || (!userFacade.canManageGroup(groupid))) {
				return FORBIDDEN;
			}
		}
		if (!exists(databaseFacade.findFirst(
				"select count(iduser) from User where iduser = ?",
				userid))) {
			return NOT_FOUND;
		}
		if (!exists(databaseFacade.findFirst(
				"select count(idgroup) from Group where idgroup = ?",
				groupid))) {
			return NOT_FOUND;
		}
		return NO_ERRORS;
	}

	private int validateOrganizationAndGroup(int organizationid, int groupid) {
		if (!userFacade.isSuperAdmin(sessionFacade.getCurrentSession().getUserid())) {
			if ((!userFacade.canManageOrganization(organizationid)) || (!userFacade.canManageGroup(groupid))) {
				return FORBIDDEN;
			}
		}
		if (!exists(databaseFacade.findFirst(
				"select count(idorganization) from Organization where idorganization = ? and domainid = ?",
				organizationid, DEFAULT_DOMAIN_ID))) {
			return NOT_FOUND;
		}
		if (!exists(databaseFacade.findFirst(
				"select count(idgroup) from Group where idgroup = ?",
				groupid))) {
			return NOT_FOUND;
		}
		return NO_ERRORS;
	}

//	private int findDomainIdOfElement(int groupid, GroupElement groupElement) {
//		return this.findDomainIdOfElement(groupid, groupElement.getElementid(), groupElement.getElementtype());
//	}

	private int findDomainIdOfElement(int groupid, int elementid, int elementtype) {
		switch (elementtype) {
		case GroupElement.ELEMENT_TYPE_USER:
			User user = databaseFacade.load(User.class, elementid);
			return user.getDomainid();
		default:
			Organization organization = databaseFacade.load(Organization.class, elementid);
			return organization.getDomainid();
		}
	}

	public boolean deleteIfMaxReached(Group group) {
		Long count = (Long) databaseFacade.findFirst(
				"select count(idgroup) from Group");
		if (count > 100) {// FIXME
			databaseFacade.update(
					"delete from Group where idgroup = ?",
					group.getIdgroup());
			return true;
		}
		return false;
	}

}
