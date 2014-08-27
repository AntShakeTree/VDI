package com.opzoon.vdi.core.ws.admin;

import static com.opzoon.vdi.core.facade.CommonException.BAD_REQUEST;
import static com.opzoon.vdi.core.facade.CommonException.MULTI_STATUS;
import static com.opzoon.vdi.core.facade.CommonException.NO_ERRORS;
import static com.opzoon.vdi.core.util.ConditionUtils.numberNotEquals;
import static com.opzoon.vdi.core.util.StringUtils.allInBound;
import static com.opzoon.vdi.core.util.StringUtils.nullToBlankString;
import static com.opzoon.vdi.core.ws.WebServiceHelper.fixListParam;

import java.io.Serializable;
import java.util.List;

import javax.xml.bind.annotation.XmlRootElement;

import com.opzoon.vdi.core.domain.Group;
import com.opzoon.vdi.core.domain.GroupElement;
import com.opzoon.vdi.core.facade.CommonException;
import com.opzoon.vdi.core.facade.FacadeHelper.PagingInfo;
import com.opzoon.vdi.core.facade.GroupFacade;
import com.opzoon.vdi.core.ws.Services;
import com.opzoon.vdi.core.ws.Services.CommonList;
import com.opzoon.vdi.core.ws.Services.MultiStatusResponse;
import com.opzoon.vdi.core.ws.Services.NullResponse;
import com.opzoon.vdi.core.ws.Services.Response;
import com.opzoon.vdi.core.ws.WebServiceHelper.Validater;
import com.opzoon.vdi.core.ws.admin.DesktopManagement.DesktopPoolAndError;
import com.opzoon.vdi.core.ws.admin.DesktopManagement.DesktopPoolListResponse;

/**
 * 组管理业务实现.
 */
public class GroupManagement {
	
	private GroupFacade groupFacade;

	/**
	 * 创建组.
	 * 
	 * @param group 组实体.
	 * @return 组ID响应.<br />
	 *         {@link com.opzoon.vdi.core.facade.CommonException#NO_ERRORS}: 成功;<br />
	 *         {@link com.opzoon.vdi.core.facade.CommonException#BAD_REQUEST}: 参数校验失败 .<br />
	 *         {@link com.opzoon.vdi.core.facade.CommonException#FORBIDDEN}: 没有创建此组的权限 (当前用户非全局管理员).<br />
	 *         {@link com.opzoon.vdi.core.facade.CommonException#CONFLICT}: 组名冲突.
	 */
	public GroupIdResponse createGroup(Group group) {
		GroupIdResponse response = new GroupIdResponse();
		int error = this.validationAndFixGroup(group, false);
		if (error != NO_ERRORS) {
			response.getHead().setError(error);
			return response;
		}
		try {
			error = groupFacade.createGroup(group);
		} catch (CommonException e) {
			response.getHead().setError(e.getError());
			return response;
		}
		if (error != NO_ERRORS) {
			response.getHead().setError(error);
			return response;
		}
		if (groupFacade.deleteIfMaxReached(group)) {
			response.getHead().setError(CommonException.MAX_GROUP);
			return response;
		}
		GroupIdWrapper groupIdWrapper = new GroupIdWrapper();
		response.setBody(groupIdWrapper);
		groupIdWrapper.setIdgroup(group.getIdgroup());
		return response;
	}

	/**
	 * 删除组.
	 * 
	 * @param groupIdParam 组ID参数.
	 * @return 空响应.<br />
	 *         {@link com.opzoon.vdi.core.facade.CommonException#NO_ERRORS}: 成功;<br />
	 *         {@link com.opzoon.vdi.core.facade.CommonException#BAD_REQUEST}: 参数错误 .<br />
	 *         {@link com.opzoon.vdi.core.facade.CommonException#FORBIDDEN}: 没有删除此组的权限 (当前用户非全局管理员或对此组没有修改权限).<br />
	 *         {@link com.opzoon.vdi.core.facade.CommonException#NOT_FOUND}: 删除不存在的组.
	 */
	public GroupListResponse deleteGroup(GroupIdParam groupIdParam) {
    GroupListResponse response = new GroupListResponse();
    for (int groupid : groupIdParam.getIdgroup()) {
      if (groupid < 1) {
        response.addStatus(new GroupAndError(groupid, Services.err.error(BAD_REQUEST)));
        continue;
      }
      int error = groupFacade.deleteGroup(groupid);
      if (numberNotEquals(error, NO_ERRORS)) {
        response.addStatus(new GroupAndError(groupid, Services.err.error(error)));
        continue;
      }
    }
    return response;
	}

	/**
	 * 更新组.
	 * 
	 * @param group 组实体.
	 * @return 空响应.<br />
	 *         {@link com.opzoon.vdi.core.facade.CommonException#NO_ERRORS}: 成功;<br />
	 *         {@link com.opzoon.vdi.core.facade.CommonException#BAD_REQUEST}: 参数校验失败 .<br />
	 *         {@link com.opzoon.vdi.core.facade.CommonException#NOT_FOUND}: 组不存在 .<br />
	 *         {@link com.opzoon.vdi.core.facade.CommonException#FORBIDDEN}: 没有修改此组的权限 (当前用户非全局管理员或对此组没有修改权限).<br />
	 *         {@link com.opzoon.vdi.core.facade.CommonException#CONFLICT}: 组名冲突.
	 */
	public NullResponse updateGroup(Group group) {
		NullResponse response = new NullResponse();
		int error = this.validationAndFixGroup(group, true);
		if (error != NO_ERRORS) {
			response.getHead().setError(error);
			return response;
		}
		try {
			error = groupFacade.updateGroup(group);
		} catch (CommonException e) {
			response.getHead().setError(e.getError());
			return response;
		}
		if (error != NO_ERRORS) {
			response.getHead().setError(error);
			return response;
		}
		return response;
	}

	/**
	 * 列举全部组.
	 * 
	 * @param listGroupsParam 列举组参数.
	 * @return 列举组响应.
	 */
	public ListGroupsResponse listGroups(ListGroupsParam listGroupsParam) {
		ListGroupsResponse response = new ListGroupsResponse();
		if (listGroupsParam.getIdgroup() < -1) {
			response.getHead().setError(BAD_REQUEST);
			return response;
		}
		if (listGroupsParam.getDomainid() < -1) {
			response.getHead().setError(BAD_REQUEST);
			return response;
		}
		// FIXME Magic numbers.
		if (listGroupsParam.getGrouptype() < -1
				&& listGroupsParam.getGrouptype() > 1) {
			response.getHead().setError(BAD_REQUEST);
			return response;
		}
		fixListParam(listGroupsParam, "idgroup");
		response.setBody(new GroupList());
		response.getBody().copyFrom(listGroupsParam);
		int[] amountContainer = new int[1];
		response.getBody().setList(groupFacade.findGroups(
				listGroupsParam.getIdgroup(),
				listGroupsParam.getDomainid(),
				listGroupsParam.getGrouptype(),
				listGroupsParam,
				amountContainer));
		response.getBody().setAmount(amountContainer[0]);
		return response;
	}

	/**
	 * 将用户加入组.
	 * 
	 * @param userAndGroup 用户ID和组ID.
	 * @return 空响应.<br />
	 *         {@link com.opzoon.vdi.core.facade.CommonException#NO_ERRORS}: 成功;<br />
	 *         {@link com.opzoon.vdi.core.facade.CommonException#BAD_REQUEST}: 参数校验失败.<br />
	 *         {@link com.opzoon.vdi.core.facade.CommonException#NOT_FOUND}: 组或用户不存在.<br />
	 *         {@link com.opzoon.vdi.core.facade.CommonException#FORBIDDEN}: 没有修改此组或用户的权限 (当前用户非全局管理员或对此组/用户 (或用户所在的域或组织) 没有修改权限).<br />
	 *         {@link com.opzoon.vdi.core.facade.CommonException#CONFLICT}: 此用户已在组中.
	 */
	public UserIdAndGroupListResponse addUserToGroup(UserAndGroup userAndGroup) {
		return this.userToGroup(userAndGroup, false);
	}

	/**
	 * 将用户从组中删除.
	 * 
	 * @param userAndGroup 用户ID和组ID.
	 * @return 空响应.<br />
	 *         {@link com.opzoon.vdi.core.facade.CommonException#NO_ERRORS}: 成功;<br />
	 *         {@link com.opzoon.vdi.core.facade.CommonException#BAD_REQUEST}: 参数校验失败.<br />
	 *         {@link com.opzoon.vdi.core.facade.CommonException#NOT_FOUND}: 组或用户不存在, 或用户不在此组中.<br />
	 *         {@link com.opzoon.vdi.core.facade.CommonException#FORBIDDEN}: 没有修改此组或用户的权限 (当前用户非全局管理员或对此组/用户 (或用户所在的域或组织) 没有修改权限).
	 */
	public UserIdAndGroupListResponse deleteUserFromGroup(UserAndGroup userAndGroup) {
		return this.userToGroup(userAndGroup, true);
	}

	public OrganizationIdAndGroupListResponse addOrganizationToGroup(
			OrganizationAndGroup organizationAndGroup) {
		return this.organizationToGroup(organizationAndGroup, false);
	}

	public OrganizationIdAndGroupListResponse deleteOrganizationFromGroup(
			OrganizationAndGroup organizationAndGroup) {
		return this.organizationToGroup(organizationAndGroup, true);
	}

	public void setGroupFacade(GroupFacade groupFacade) {
		this.groupFacade = groupFacade;
	}

	private int validationAndFixGroup(Group group, final boolean forUpdate) {
		return new Validater<Group>() {
			@Override
			public int validationAndFix(Group group) {
				if (forUpdate) {
					if (group.getIdgroup() == null || group.getIdgroup() < 1) {
						return BAD_REQUEST;
					}
					if (group.getGroupname() != null) {
						group.setGroupname(group.getGroupname().trim());
						if (group.getGroupname().length() < 1) {
							return BAD_REQUEST;
						}
					}
					if (group.getNotes() != null) {
						group.setNotes(group.getNotes().trim());
					}
				} else {
					group.setIdgroup(null);
					group.setGroupname(nullToBlankString(group.getGroupname()).trim());
					if (group.getGroupname().length() < 1) {
						return BAD_REQUEST;
					}
					if (group.getDomainid() < 0) {
						return BAD_REQUEST;
					}
					group.setNotes(nullToBlankString(group.getNotes()));
				}
				if (!allInBound(
						255,
						group.getGroupname(), group.getNotes())) {
					return BAD_REQUEST;
				}
				if (!group.getGroupname().matches("^[^\\\\/\"\\[\\]:|<>+=;\\,\\?\\*@]+$")) {
					return BAD_REQUEST;
				}
				return NO_ERRORS;
			}
		}.validationAndFix(group);
	}

	private UserIdAndGroupListResponse userToGroup(UserAndGroup userAndGroup,
			boolean deleting) {
		UserIdAndGroupListResponse response = new UserIdAndGroupListResponse();
		if (userAndGroup.getUserid() == null
				|| userAndGroup.getUserid().length < 1
				|| userAndGroup.getGroupid() == null
				|| userAndGroup.getGroupid().length < 1) {
			response.getHead().setError(BAD_REQUEST);
			return response;
		}
		if (userAndGroup.getUserid().length > 1 && userAndGroup.getGroupid().length > 1 ) {
			response.getHead().setError(BAD_REQUEST);
			return response;
		}
		for (int userid : userAndGroup.getUserid()) {
			if (userid < 0) {
				response.getHead().setError(MULTI_STATUS);
				for (int groupid : userAndGroup.getGroupid()) {
					response.addStatus(new UserIdAndGroupAndError(userid, groupid, Services.err.error(BAD_REQUEST)));
				}
				continue;
			}
			for (int groupid : userAndGroup.getGroupid()) {
				if (groupid < 1) {
					response.addStatus(new UserIdAndGroupAndError(userid, groupid, Services.err.error(BAD_REQUEST)));
					continue;
				}
				int error = NO_ERRORS;
				try {
					error = deleting ?
							groupFacade.deleteUserFromGroup(userid, groupid) :
								groupFacade.addUserToGroup(userid, groupid);
				} catch (CommonException e) {
					response.addStatus(new UserIdAndGroupAndError(userid, groupid, Services.err.error(e.getError())));
					continue;
				}
				if (error != NO_ERRORS) {
					response.addStatus(new UserIdAndGroupAndError(userid, groupid, Services.err.error(error)));
				}
				if (!deleting) {
					error = groupFacade.deleteElementIfConflictInDomain(groupid, userid, GroupElement.ELEMENT_TYPE_USER);
					if (error != NO_ERRORS) {
						response.addStatus(new UserIdAndGroupAndError(userid, groupid, Services.err.error(error)));
					}
				}
			}
		}
		return response;
	}

	private OrganizationIdAndGroupListResponse organizationToGroup(OrganizationAndGroup organizationAndGroup,
			boolean deleting) {
		OrganizationIdAndGroupListResponse response = new OrganizationIdAndGroupListResponse();
		if (organizationAndGroup.getOrganizationid() == null
				|| organizationAndGroup.getOrganizationid().length < 1
				|| organizationAndGroup.getGroupid() == null
				|| organizationAndGroup.getGroupid().length < 1) {
			response.getHead().setError(BAD_REQUEST);
			return response;
		}
		if (organizationAndGroup.getOrganizationid().length > 1 && organizationAndGroup.getGroupid().length > 1 ) {
			response.getHead().setError(BAD_REQUEST);
			return response;
		}
		for (int organizationid : organizationAndGroup.getOrganizationid()) {
			if (organizationid < 0) {
				response.getHead().setError(MULTI_STATUS);
				for (int groupid : organizationAndGroup.getGroupid()) {
					response.addStatus(new OrganizationIdAndGroupAndError(organizationid, groupid, Services.err.error(BAD_REQUEST)));
				}
				continue;
			}
			for (int groupid : organizationAndGroup.getGroupid()) {
				if (groupid < 1) {
					response.addStatus(new OrganizationIdAndGroupAndError(organizationid, groupid, Services.err.error(BAD_REQUEST)));
					continue;
				}
				int error = NO_ERRORS;
				try {
					error = deleting ?
							groupFacade.deleteOrganizationFromGroup(organizationid, groupid) :
								groupFacade.addOrganizationToGroup(organizationid, groupid);
				} catch (CommonException e) {
					response.addStatus(new OrganizationIdAndGroupAndError(organizationid, groupid, Services.err.error(e.getError())));
					continue;
				}
				if (error != NO_ERRORS) {
					response.addStatus(new OrganizationIdAndGroupAndError(organizationid, groupid, Services.err.error(error)));
				}
				if (!deleting) {
					error = groupFacade.deleteElementIfConflictInDomain(groupid, organizationid, GroupElement.ELEMENT_TYPE_ORGANIZATION);
					if (error != NO_ERRORS) {
						response.addStatus(new OrganizationIdAndGroupAndError(organizationid, groupid, Services.err.error(error)));
					}
				}
			}
		}
		return response;
	}

	@XmlRootElement(name = "idgroup")
	public static class GroupIdParam implements Serializable {

		private static final long serialVersionUID = 1L;

		private int[] idgroup;
		
		public int[] getIdgroup() {
			return idgroup;
		}
		public void setIdgroup(int[] idgroup) {
			this.idgroup = idgroup;
		}
		
	}

	@XmlRootElement(name = "listParam")
	public static class ListGroupsParam extends PagingInfo implements Serializable {

		private static final long serialVersionUID = 1L;

		private int idgroup;
		private int domainid;
		private int grouptype;
		
		public int getIdgroup() {
			return idgroup;
		}
		public void setIdgroup(int idgroup) {
			this.idgroup = idgroup;
		}
		public int getDomainid() {
			return domainid;
		}
		public void setDomainid(int domainid) {
			this.domainid = domainid;
		}
		/**
		 * @return 0: Local, 1: LDAP.
		 */
		public int getGrouptype() {
			return grouptype;
		}
		public void setGrouptype(int grouptype) {
			this.grouptype = grouptype;
		}
		
	}

	@XmlRootElement(name = "param")
	public static class UserAndGroup implements Serializable {

		private static final long serialVersionUID = 1L;

		private int[] userid;
		private int[] groupid;
		
		public int[] getUserid() {
			return userid;
		}
		public void setUserid(int[] userid) {
			this.userid = userid;
		}
		public int[] getGroupid() {
			return groupid;
		}
		public void setGroupid(int[] groupid) {
			this.groupid = groupid;
		}
		
	}

	@XmlRootElement(name = "param")
	public static class OrganizationAndGroup implements Serializable {

		private static final long serialVersionUID = 1L;

		private int[] organizationid;
		private int[] groupid;
		
		public int[] getOrganizationid() {
			return organizationid;
		}
		public void setOrganizationid(int[] organizationid) {
			this.organizationid = organizationid;
		}
		public int[] getGroupid() {
			return groupid;
		}
		public void setGroupid(int[] groupid) {
			this.groupid = groupid;
		}
		
	}

	@XmlRootElement(name = "idgroup")
	public static class GroupIdWrapper implements Serializable {

		private static final long serialVersionUID = 1L;

		private int idgroup;
		
		public int getIdgroup() {
			return idgroup;
		}
		public void setIdgroup(int idgroup) {
			this.idgroup = idgroup;
		}
		
	}

	@XmlRootElement(name = "response")
	public static class UserIdAndGroupListResponse extends MultiStatusResponse<UserIdAndGroupAndError> implements Serializable {

		private static final long serialVersionUID = 1L;
		
		private List<UserIdAndGroupAndError> body;

		@Override
		public List<UserIdAndGroupAndError> getBody() {
			return body;
		}
		@Override
		public void setBody(List<UserIdAndGroupAndError> body) {
			this.body = body;
		}
		
	}

	@XmlRootElement(name = "response")
	public static class OrganizationIdAndGroupListResponse extends MultiStatusResponse<OrganizationIdAndGroupAndError> implements Serializable {

		private static final long serialVersionUID = 1L;
		
		private List<OrganizationIdAndGroupAndError> body;

		@Override
		public List<OrganizationIdAndGroupAndError> getBody() {
			return body;
		}
		@Override
		public void setBody(List<OrganizationIdAndGroupAndError> body) {
			this.body = body;
		}
		
	}

	@XmlRootElement(name = "response")
	public static class GroupIdResponse extends Response<GroupIdWrapper> implements Serializable {

		private static final long serialVersionUID = 1L;
		
		private GroupIdWrapper body;

		@Override
		public GroupIdWrapper getBody() {
			return body;
		}
		@Override
		public void setBody(GroupIdWrapper body) {
			this.body = body;
		}
		
	}

	@XmlRootElement(name = "response")
	public static class ListGroupsResponse extends Response<GroupList> implements Serializable {

		private static final long serialVersionUID = 1L;
		
		private GroupList body;
		
		public GroupList getBody() {
			return body;
		}
		public void setBody(GroupList body) {
			this.body = body;
		}
		
	}

  @XmlRootElement(name = "response")
  public static class GroupListResponse extends MultiStatusResponse<GroupAndError> implements Serializable {

    private static final long serialVersionUID = 1L;
    
    private List<GroupAndError> body;

    @Override
    public List<GroupAndError> getBody() {
      return body;
    }
    @Override
    public void setBody(List<GroupAndError> body) {
      this.body = body;
    }
    
  }
	
	public static class GroupList extends CommonList<Group> implements Serializable {

		private static final long serialVersionUID = 1L;

		private List<Group> list;
		
		@Override
		public List<Group> getList() {
			return list;
		}
		@Override
		public void setList(List<Group> list) {
			this.list = list;
		}
		
	}
	
	public static class UserIdAndGroupAndError implements Serializable {

		private static final long serialVersionUID = 1L;

		private int userid;
		private int groupid;
		private int error;
		
		public UserIdAndGroupAndError(int userid, int groupid, int error) {
			this.userid = userid;
			this.groupid = groupid;
			this.error = error;
		}
		public int getUserid() {
			return userid;
		}
		public void setUserid(int userid) {
			this.userid = userid;
		}
		public int getGroupid() {
			return groupid;
		}
		public void setGroupid(int groupid) {
			this.groupid = groupid;
		}
		public int getError() {
			return error;
		}
		public void setError(int error) {
			this.error = error;
		}
		
	}
	
	public static class OrganizationIdAndGroupAndError implements Serializable {

		private static final long serialVersionUID = 1L;

		private int organizationid;
		private int groupid;
		private int error;
		
		public OrganizationIdAndGroupAndError(int organizationid, int groupid, int error) {
			this.organizationid = organizationid;
			this.groupid = groupid;
			this.error = error;
		}
		public int getOrganizationid() {
			return organizationid;
		}
		public void setOrganizationid(int organizationid) {
			this.organizationid = organizationid;
		}
		public int getGroupid() {
			return groupid;
		}
		public void setGroupid(int groupid) {
			this.groupid = groupid;
		}
		public int getError() {
			return error;
		}
		public void setError(int error) {
			this.error = error;
		}
		
	}
  
  public static class GroupAndError implements Serializable {

    private static final long serialVersionUID = 1L;

    private int groupid;
    private int error;
    
    public GroupAndError(int groupid, int error) {
      this.groupid = groupid;
      this.error = error;
    }
    public int getGroupid()
    {
      return groupid;
    }
    public void setGroupid(int groupid)
    {
      this.groupid = groupid;
    }
    public int getError() {
      return error;
    }
    public void setError(int error) {
      this.error = error;
    }
    
  }

}
