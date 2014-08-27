package com.opzoon.vdi.core.facade;

import static com.opzoon.vdi.core.domain.Administrator.ADMIN_TARGET_ORGANIZATION;
import static com.opzoon.vdi.core.domain.Domain.DEFAULT_DOMAIN_ID;
import static com.opzoon.vdi.core.domain.GroupElement.ELEMENT_TYPE_ORGANIZATION;
import static com.opzoon.vdi.core.domain.Organization.DEFAULT_ORGANIZATION_ID;
import static com.opzoon.vdi.core.domain.ResourceAssignment.RESOURCE_VISITOR_TYPE_ORGANIZATION;
import static com.opzoon.vdi.core.facade.CommonException.CONFLICT;
import static com.opzoon.vdi.core.facade.CommonException.FORBIDDEN;
import static com.opzoon.vdi.core.facade.CommonException.NOT_FOUND;
import static com.opzoon.vdi.core.facade.CommonException.NO_ERRORS;
import static com.opzoon.vdi.core.facade.FacadeHelper.count;
import static com.opzoon.vdi.core.facade.FacadeHelper.exists;
import static com.opzoon.vdi.core.facade.FacadeHelper.pagingFind;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import javax.persistence.EntityExistsException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.opzoon.vdi.core.RunnableWithException;
import com.opzoon.vdi.core.domain.Organization;
import com.opzoon.vdi.core.domain.RestrictionStrategyAssignment;
import com.opzoon.vdi.core.facade.FacadeHelper.PagingInfo;
import com.opzoon.vdi.core.facade.transience.SessionFacade;

/**
 * 组织相关业务接口.
 */
public class OrganizationFacade {
	
	private static final Logger log = LoggerFactory.getLogger(OrganizationFacade.class);
	
	private DatabaseFacade databaseFacade;
	private UserFacade userFacade;
	private ResourceFacade resourceFacade;
	private SessionFacade sessionFacade;
  
  public List<Integer> findParents(int organizationid) {
    List<Integer> parents = new LinkedList<Integer>();
    for (Integer checkingOrganization = organizationid; checkingOrganization != null && checkingOrganization > -1; ) {
      if (checkingOrganization.intValue() != organizationid) {
        parents.add(checkingOrganization);
      }
      checkingOrganization = (Integer) databaseFacade.findFirst(
          "select parent from Organization where idorganization = ?",
          checkingOrganization);
    }
    return parents;
  }

	public int createOrganization(Organization organization) throws CommonException {
		if (exists(databaseFacade.findFirst(
				"select count(idorganization) from Organization where organizationname = ?",
				organization.getOrganizationname()))) {
			return CONFLICT;
		}
		if (!exists(databaseFacade.findFirst(
				"select count(idorganization) from Organization where idorganization = ?",
				organization.getParent()))) {
			return NOT_FOUND;
		}
		if ((!userFacade.isSuperAdmin(sessionFacade.getCurrentSession().getUserid()))
				&& (!userFacade.canManageOrganization(organization.getParent()))) {
			return FORBIDDEN;
		}
		try {
			databaseFacade.persist(organization);
		} catch (EntityExistsException e) {
			throw new CommonException(CONFLICT);
		}
		return NO_ERRORS;
	}

	public int deleteOrganization(int idorganization) {
		if ((!userFacade.isSuperAdmin(sessionFacade.getCurrentSession().getUserid()))
				&& (!userFacade.canManageOrganization(idorganization))) {
			return FORBIDDEN;
		}
		if (!exists(databaseFacade.findFirst(
				"select count(idorganization) from Organization where idorganization = ? and domainid = ?",
				idorganization, DEFAULT_DOMAIN_ID))) {
			return NOT_FOUND;
		}
		return this.forceDeleteOrganization(idorganization);
	}

	public int forceDeleteOrganization(final int idorganization) {
		final Set<Integer> organizationAndChildren = new HashSet<Integer>();
		organizationAndChildren.add(idorganization);
		this.findAllChildren(idorganization, organizationAndChildren);
		if (sessionFacade.getCurrentSession() != null) {
			Integer organizationOfCurrentUser = userFacade.findOrganizationOfUser(sessionFacade.getCurrentSession().getUserid());
			if (organizationOfCurrentUser != null && organizationAndChildren.contains(organizationOfCurrentUser)) {
				return CONFLICT;
			}
		}
		final Set<Integer> users = new HashSet<Integer>();
		for (Integer organization : organizationAndChildren) {
			users.addAll(userFacade.findUsersOfOrganization(organization));
		}
    try
    {
      resourceFacade.autoClearResources(
          users,
          true,
          new RunnableWithException() {
            @Override
            public void run() {
              for (int organization : organizationAndChildren) {
                databaseFacade.update(
                    "delete from Organization where idorganization = ?",
                    organization);
                databaseFacade.update(
                    "delete from GroupElement where elementtype = ? and elementid = ?",
                    ELEMENT_TYPE_ORGANIZATION, organization);
                databaseFacade.update(
                    "delete from Administrator where targettype = ? and targetid = ?",
                    ADMIN_TARGET_ORGANIZATION, idorganization);
                databaseFacade.update(
                    "delete from ResourceAssignment where visitortype = ? and visitorid = ?",
                    RESOURCE_VISITOR_TYPE_ORGANIZATION, idorganization);
                databaseFacade.update(
                    "delete from RestrictionStrategyAssignment where targettype = ? and targetid = ?",
                    RestrictionStrategyAssignment.RESTRICTION_STRATEGY_ASSIGNMENT_TARGET_TYPE_ORGANIZATION, idorganization);
              }
              for (Integer user : users) {
                try {
                  userFacade.forceDeleteUser(user);
                } catch (CommonException e) {
                  log.warn("Delete user failed.", e);
                }
              }
              //~~~~~~~~~~~~~maxiaochao~~~~~~~~~~~~~rail~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
              databaseFacade.update("delete from RailApplicationToOrganization where organizationid=?", idorganization);
              //~~~~~~~~~~~~~maxiaochao~~~~~~~~~~~~~rail~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
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

	public int updateOrganization(Organization organization) throws CommonException {
		if ((!userFacade.isSuperAdmin(sessionFacade.getCurrentSession().getUserid()))
				&& (!userFacade.canManageOrganization(organization.getIdorganization()))) {
			return FORBIDDEN;
		}
		if (!exists(databaseFacade.findFirst(
				"select count(idorganization) from Organization where idorganization = ? and domainid = ?",
				organization.getIdorganization(), 0))) {
			return NOT_FOUND;
		}
		StringBuilder updateClause = new StringBuilder("update Organization set idorganization = idorganization");
		List<Object> params = new ArrayList<Object>();
		if (organization.getOrganizationname() != null) {
			updateClause.append(", organizationname = ?");
			params.add(organization.getOrganizationname());
		}
		if (organization.getNotes() != null) {
			updateClause.append(", notes = ?");
			params.add(organization.getNotes());
		}
		updateClause.append(" where idorganization = ? and domainid = ?");
		params.add(organization.getIdorganization());
		params.add(0);
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
	 * 分页查询组织.
	 * 
	 * @param idorganization 组织ID. -1为忽略.
	 * @param organizationtype 组织类型. -1为忽略.
	 * @param domainid 域ID. -1为忽略.
	 * @param parent 上级组织ID. -2为忽略.
	 * @param pagingInfo 分页信息.
	 * @param amountContainer 查询结果的总数量的容器.
	 * @return 查询结果列表.
	 */
	@SuppressWarnings("unchecked")
	public List<Organization> findOrganizations(int idorganization, int organizationtype,
			int domainid, int parent, PagingInfo pagingInfo,
			int[] amountContainer) {
		StringBuilder whereClause = new StringBuilder("from Organization where 1 = 1");
		List<Object> params = new ArrayList<Object>();
		if (idorganization > -1) {
			whereClause.append(" and idorganization = ?");
			params.add(idorganization);
		}
		if (organizationtype > -1) {
			whereClause.append(" and organizationtype = ?");
			params.add(organizationtype);
		}
		if (domainid > -1) {
			whereClause.append(" and domainid = ?");
			params.add(domainid);
		}
		if (parent > -2) {
			whereClause.append(" and parent = ?");
			params.add(parent);
		}
		Object[] paramsArray = params.toArray();
		count(databaseFacade, "idorganization", whereClause, paramsArray, amountContainer);
		List<Organization> organizations = pagingFind(databaseFacade, whereClause, paramsArray, pagingInfo);
		for (Organization organization : organizations) {
			organization.setDomainname((String) databaseFacade.findFirst(
					"select domainname from Domain where iddomain= ?",
					organization.getDomainid()));
		}
		return organizations;
	}

	public int addUserToOrganization(final int userid, final int organizationid) {
		int error = this.validateUserAndOrganization(userid, organizationid);
		if (error != NO_ERRORS) {
			return error;
		}
		if (exists(databaseFacade.findFirst(
				"select count(iduser) from User where organizationid = ? and iduser = ?",
				organizationid, userid))) {
			return CONFLICT;
		}
    try
    {
      resourceFacade.autoClearResources(
          Arrays.asList(new Integer[] { userid }),
          true,
          new RunnableWithException() {
            @Override
            public void run() {
              databaseFacade.update(
                  "update User set organizationid = ? where iduser = ?",
                  organizationid, userid);
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

//	public int deleteUserFromOrganization(int userid, int organizationid) {
//		int error = this.validateUserAndOrganization(userid, organizationid);
//		if (error != NO_ERRORS) {
//			return error;
//		}
//		if (!exists(databaseFacade.findFirst(
//				"select count(iduser) from User where organizationid = ? and iduser = ?",
//				organizationid, userid))) {
//			return NOT_FOUND;
//		}
//		if (exists(databaseFacade.update(
//				"update User set organizationid = ? where iduser = ?",
//				DEFAULT_ORGANIZATION_ID, userid))) {
//			resourceFacade.disconnectUnassignedDesktops(userid, false);
//		}
//		return NO_ERRORS;
//	}
	
	public List<Integer> findSelfAndParents(int organizationid) {
		List<Integer> parents = new LinkedList<Integer>();
		for (Integer checkingOrganization = organizationid; checkingOrganization > -1; ) {
			if (!exists(databaseFacade.findFirst(
					"select count(idorganization) from Organization where idorganization = ?",
					checkingOrganization))) {
				break;
			}
			parents.add(checkingOrganization);
			checkingOrganization = (Integer) databaseFacade.findFirst(
					"select parent from Organization where idorganization = ?",
					checkingOrganization);
		}
		return parents;
	}
	
	public Set<Integer> findUsers(int organizationid) {
		Set<Integer> organizationAndChildren = new HashSet<Integer>();
		organizationAndChildren.add(organizationid);
		this.findAllChildren(organizationid, organizationAndChildren);
		Set<Integer> users = new HashSet<Integer>();
		for (Integer organization : organizationAndChildren) {
			users.addAll(userFacade.findUsersOfOrganization(organization));
		}
		return users;
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

	public void setSessionFacade(SessionFacade sessionFacade) {
		this.sessionFacade = sessionFacade;
	}
	
	private void findAllChildren(int organizationid,
			Set<Integer> organizationAndChildren) {
		List<Integer> children = this.findChildren(organizationid);
		organizationAndChildren.addAll(children);
		for (Integer child : children) {
			this.findAllChildren(child, organizationAndChildren);
		}
	}

	@SuppressWarnings("unchecked")
	private List<Integer> findChildren(
			int organizationid) {
		return (List<Integer>) databaseFacade.find(
				"select idorganization from Organization where parent = ?",
				organizationid);
	}

	private int validateUserAndOrganization(int userid, int organizationid) {
		if (!userFacade.isSuperAdmin(sessionFacade.getCurrentSession().getUserid())) {
			if ((!userFacade.canManageUser(userid)) || (!userFacade.canManageOrganization(organizationid))) {
				return FORBIDDEN;
			}
		}
		if (!exists(databaseFacade.findFirst(
				"select count(iduser) from User where iduser = ? and domainid = ?",
				userid, DEFAULT_DOMAIN_ID))) {
			return NOT_FOUND;
		}
		if (!exists(databaseFacade.findFirst(
				"select count(idorganization) from Organization where idorganization = ? and domainid = ?",
				organizationid, DEFAULT_DOMAIN_ID))) {
			return NOT_FOUND;
		}
		return NO_ERRORS;
	}

}
