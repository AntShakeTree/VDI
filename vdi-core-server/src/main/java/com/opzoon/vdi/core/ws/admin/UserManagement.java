package com.opzoon.vdi.core.ws.admin;

import static com.opzoon.vdi.core.domain.Administrator.ADMIN_TARGET_ALL;
import static com.opzoon.vdi.core.domain.Administrator.ADMIN_TARGET_GROUP;
import static com.opzoon.vdi.core.domain.Administrator.ADMIN_TARGET_ORGANIZATION;
import static com.opzoon.vdi.core.domain.Domain.DEFAULT_DOMAIN_ID;
import static com.opzoon.vdi.core.domain.Domain.DOMAIN_TYPE_APACHE;
import static com.opzoon.vdi.core.domain.Domain.DOMAIN_TYPE_LOCAL;
import static com.opzoon.vdi.core.domain.Domain.DOMAIN_TYPE_MSAD;
import static com.opzoon.vdi.core.domain.Organization.DEFAULT_ORGANIZATION_ID;
import static com.opzoon.vdi.core.domain.Session.LOGIN_TYPE_ADMIN;
import static com.opzoon.vdi.core.domain.Session.LOGIN_TYPE_SUPER_ADMIN;
import static com.opzoon.vdi.core.domain.Session.LOGIN_TYPE_USER;
import static com.opzoon.vdi.core.facade.CommonException.BAD_REQUEST;
import static com.opzoon.vdi.core.facade.CommonException.NO_ERRORS;
import static com.opzoon.vdi.core.util.Checksum.getHex;
import static com.opzoon.vdi.core.util.ConditionUtils.numberEquals;
import static com.opzoon.vdi.core.util.ConditionUtils.numberNotEquals;
import static com.opzoon.vdi.core.util.StringUtils.allInBound;
import static com.opzoon.vdi.core.util.StringUtils.nullToBlankString;
import static com.opzoon.vdi.core.ws.WebServiceHelper.fixListParam;

import com.opzoon.appstatus.executor.dao.TraceDao;
import com.opzoon.ohvc.domain.Head;
import com.opzoon.vdi.core.app.request.TraceReq;
import com.opzoon.vdi.core.app.response.ListTrace;
import com.opzoon.vdi.core.app.response.ListTraceRes;
import com.opzoon.vdi.core.domain.RSAKey;
import com.opzoon.vdi.core.domain.User;
import com.opzoon.vdi.core.facade.CommonException;
import com.opzoon.vdi.core.facade.UserFacade;
import com.opzoon.vdi.core.facade.transience.SessionFacade;
import com.opzoon.vdi.core.ws.Services;
import com.opzoon.vdi.core.ws.Services.NullResponse;
import com.opzoon.vdi.core.ws.WebServiceHelper.Validater;
import com.opzoon.vdi.core.ws.vo.admin.user.ListSessionsParam;
import com.opzoon.vdi.core.ws.vo.admin.user.ListSessionsResponse;
import com.opzoon.vdi.core.ws.vo.admin.user.ListStandardRSAKeysParam;
import com.opzoon.vdi.core.ws.vo.admin.user.ListStandardRSAKeysResponse;
import com.opzoon.vdi.core.ws.vo.admin.user.ListUsersParam;
import com.opzoon.vdi.core.ws.vo.admin.user.ListUsersResponse;
import com.opzoon.vdi.core.ws.vo.admin.user.PrivilegeParam;
import com.opzoon.vdi.core.ws.vo.admin.user.RSAKeyIdAndError;
import com.opzoon.vdi.core.ws.vo.admin.user.RSAKeyIdsResponse;
import com.opzoon.vdi.core.ws.vo.admin.user.SessionIdWrapper;
import com.opzoon.vdi.core.ws.vo.admin.user.SessionList;
import com.opzoon.vdi.core.ws.vo.admin.user.StandardRSAKeyIds;
import com.opzoon.vdi.core.ws.vo.admin.user.StandardRSAKeyList;
import com.opzoon.vdi.core.ws.vo.admin.user.StandardRSAKeys;
import com.opzoon.vdi.core.ws.vo.admin.user.UserIdAndError;
import com.opzoon.vdi.core.ws.vo.admin.user.UserIdResponse;
import com.opzoon.vdi.core.ws.vo.admin.user.UserIdWrapper;
import com.opzoon.vdi.core.ws.vo.admin.user.UserIdsParam;
import com.opzoon.vdi.core.ws.vo.admin.user.UserIdsResponse;
import com.opzoon.vdi.core.ws.vo.admin.user.UserList;

/**
 * 用户管理业务实现.
 */
public class UserManagement {

	private UserFacade userFacade;
	private SessionFacade sessionFacade;
	private TraceDao traceDao;
	
	public TraceDao getTraceDao() {
		return traceDao;
	}

	public void setTraceDao(TraceDao traceDao) {
		this.traceDao = traceDao;
	}

	/**
	 * 创建本地用户.
	 * 
	 * @param user 用户实体.
	 * @return 用户ID响应.<br />
	 *         {@link com.opzoon.vdi.core.facade.CommonException#NO_ERRORS}: 成功;<br />
	 *         {@link com.opzoon.vdi.core.facade.CommonException#BAD_REQUEST}: 参数校验失败 .<br />
	 *         {@link com.opzoon.vdi.core.facade.CommonException#FORBIDDEN}: 没有创建此用户的权限 (当前用户非全局管理员或对此域/组织没有修改权限).<br />
	 *         {@link com.opzoon.vdi.core.facade.CommonException#CONFLICT}: 用户名冲突.
	 */
	public UserIdResponse createUser(User user) {
		UserIdResponse response = new UserIdResponse();
		int error = this.validateAndFixUser(user, false);
		if (error != NO_ERRORS) {
			response.getHead().setError(error);
			return response;
		}
		try {
			error = userFacade.createUser(user);
		} catch (CommonException e) {
			response.getHead().setError(e.getError());
			return response;
		}
		if (error != NO_ERRORS) {
			response.getHead().setError(error);
			return response;
		}
		UserIdWrapper userIdWrapper = new UserIdWrapper();
		response.setBody(userIdWrapper);
		userIdWrapper.setIduser(user.getIduser());
		return response;
	}

	/**
	 * 删除本地用户.
	 * 
	 * @param userIdParam 用户ID参数.
	 * @return 空响应.<br />
	 *         {@link com.opzoon.vdi.core.facade.CommonException#NO_ERRORS}: 成功;<br />
	 *         {@link com.opzoon.vdi.core.facade.CommonException#BAD_REQUEST}: 参数错误 .<br />
	 *         {@link com.opzoon.vdi.core.facade.CommonException#CONFLICT}: 删除自己 .<br />
	 *         {@link com.opzoon.vdi.core.facade.CommonException#FORBIDDEN}: 没有删除此用户的权限 (当前用户非全局管理员或对此用户 (或用户所在的域或组织) 没有修改权限).<br />
	 *         {@link com.opzoon.vdi.core.facade.CommonException#NOT_FOUND}: 删除不存在的用户.
	 */
	public UserIdsResponse deleteUser(UserIdsParam userIdsParam) {
		UserIdsResponse response = new UserIdsResponse();
		if (userIdsParam.getIduser() == null || userIdsParam.getIduser().length < 1) {
			response.getHead().setError(BAD_REQUEST);
			return response;
		}
		for (int iduser : userIdsParam.getIduser()) {
			if (iduser < 0) {
				response.addStatus(new UserIdAndError(iduser, Services.err.error(BAD_REQUEST)));
				continue;
			}
			try {
				userFacade.deleteUser(iduser);
			} catch (CommonException e) {
				response.addStatus(new UserIdAndError(iduser, Services.err.error(e.getError())));
				continue;
			}
		}
		return response;
	}

	/**
	 * 更新本地用户.
	 * 
	 * @param user 用户实体.
	 * @return 空响应.<br />
	 *         {@link com.opzoon.vdi.core.facade.CommonException#NO_ERRORS}: 成功;<br />
	 *         {@link com.opzoon.vdi.core.facade.CommonException#BAD_REQUEST}: 参数校验失败 .<br />
	 *         {@link com.opzoon.vdi.core.facade.CommonException#NOT_FOUND}: 用户不存在 .<br />
	 *         {@link com.opzoon.vdi.core.facade.CommonException#FORBIDDEN}: 没有修改此用户的权限 (当前用户非全局管理员或对此用户 (或用户所在的域或组织) 没有修改权限).<br />
	 *         {@link com.opzoon.vdi.core.facade.CommonException#CONFLICT}: 用户名冲突.
	 */
	public NullResponse updateUser(User user) {
		NullResponse response = new NullResponse();
		int error = this.validateAndFixUser(user, true);
		if (error != NO_ERRORS) {
			response.getHead().setError(error);
			return response;
		}
		try {
			error = userFacade.updateUser(user);
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
	 * 列举全部用户.
	 * 
	 * @param listUsersParam 列举用户参数.
	 * @return 列举用户响应.<br />
	 *         {@link com.opzoon.vdi.core.facade.CommonException#NO_ERRORS}: 成功;<br />
	 *         {@link com.opzoon.vdi.core.facade.CommonException#BAD_REQUEST}: 参数校验失败 .<br />
	 */
	public ListUsersResponse listUsers(ListUsersParam listUsersParam) {
		ListUsersResponse response = new ListUsersResponse();
		if (listUsersParam.getIduser() < -1
				|| listUsersParam.getDomainid() < -1
				|| listUsersParam.getOrganizationid() < -1
				|| listUsersParam.getGroupid() < -1) {
			response.getHead().setError(BAD_REQUEST);
			return response;
		}
		if (numberNotEquals(listUsersParam.getUsertype(), -1)
				&& numberNotEquals(listUsersParam.getUsertype(), DOMAIN_TYPE_LOCAL)
				&& numberNotEquals(listUsersParam.getUsertype(), DOMAIN_TYPE_MSAD)
				&& numberNotEquals(listUsersParam.getUsertype(), DOMAIN_TYPE_APACHE)) {
			response.getHead().setError(BAD_REQUEST);
			return response;
		}
		fixListParam(listUsersParam, "iduser");
		response.setBody(new UserList());
		response.getBody().copyFrom(listUsersParam);
		int[] amountContainer = new int[1];
		response.getBody().setList(userFacade.findUsers(
				listUsersParam.getIduser(),
				listUsersParam.getUsertype(),
				listUsersParam.getOrganizationid(),
				listUsersParam.getGroupid(),
				listUsersParam.getDomainid(),
				listUsersParam.getNorsa() != 0,
				listUsersParam,
				amountContainer));
		response.getBody().setAmount(amountContainer[0]);
		return response;
	}

	/**
	 * 列举全部会话. 注意, 由于会话的状态动态变化, 获取的分页会存在不确定的结果.
	 * 
	 * @param listSessionsParam 列举会话参数.
	 * @return 列举会话响应.
	 */
	public ListSessionsResponse listSessions(ListSessionsParam listSessionsParam) {
		ListSessionsResponse response = new ListSessionsResponse();
		if (listSessionsParam.getUserid() < -1) {
			response.getHead().setError(BAD_REQUEST);
			return response;
		}
		if (numberNotEquals(listSessionsParam.getLogintype(), -1)
				&& numberNotEquals(listSessionsParam.getLogintype(), LOGIN_TYPE_SUPER_ADMIN)
				&& numberNotEquals(listSessionsParam.getLogintype(), LOGIN_TYPE_ADMIN)
				&& numberNotEquals(listSessionsParam.getLogintype(), LOGIN_TYPE_USER)) {
			response.getHead().setError(BAD_REQUEST);
			return response;
		}
		fixListParam(listSessionsParam, "idsession");
		response.setBody(new SessionList());
		response.getBody().copyFrom(listSessionsParam);
		int[] amountContainer = new int[1];
		response.getBody().setList(sessionFacade.findSessions(
				listSessionsParam.getUserid(),
				listSessionsParam.getLogintype(),
				listSessionsParam,
				amountContainer));
		response.getBody().setAmount(amountContainer[0]);
		return response;
	}

	/**
	 * 终止会话, 后台清理相关资源.
	 * 
	 * @param sessionIdWrapper 会话ID容器.
	 * @return 空响应.<br />
	 *         {@link com.opzoon.vdi.core.facade.CommonException#NO_ERRORS}: 成功;<br />
	 *         {@link com.opzoon.vdi.core.facade.CommonException#NOT_FOUND}: 会话不存在.<br />
	 *         {@link com.opzoon.vdi.core.facade.CommonException#FORBIDDEN}: 没有修改此用户的权限 (当前用户非全局管理员或对此用户 (或用户所在的域或组织) 没有修改权限).<br />
	 *         {@link com.opzoon.vdi.core.facade.CommonException#CONFLICT}: 试图终止自己的会话.
	 */
	public NullResponse disconnectSession(SessionIdWrapper sessionIdWrapper) {
		NullResponse response = new NullResponse();
		if (sessionIdWrapper.getIdsession() < 1) {
			response.getHead().setError(BAD_REQUEST);
			return response;
		}
		int error = userFacade.disconnectSession(sessionIdWrapper.getIdsession());
		if (error != NO_ERRORS) {
			response.getHead().setError(error);
			return response;
		}
		return response;
	}

	public NullResponse assignPrivilege(PrivilegeParam privilegeParam) {
		NullResponse response = new NullResponse();
		int error = this.validatePrivilegeParam(privilegeParam);
		if (error != NO_ERRORS) {
			response.getHead().setError(error);
			return response;
		}
		try {
			error = userFacade.assignPrivilege(privilegeParam.getUserid(), privilegeParam.getTargettype(), privilegeParam.getTargetid());
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

	public NullResponse unassignPrivilege(PrivilegeParam privilegeParam) {
		NullResponse response = new NullResponse();
		int error = this.validatePrivilegeParam(privilegeParam);
		if (error != NO_ERRORS) {
			response.getHead().setError(error);
			return response;
		}
		try {
			error = userFacade.unassignPrivilege(privilegeParam.getUserid(), privilegeParam.getTargettype(), privilegeParam.getTargetid());
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

  public RSAKeyIdsResponse importStandardRSAKeys(StandardRSAKeys standardRSAKeys)
  {
    RSAKeyIdsResponse response = new RSAKeyIdsResponse();
    if (standardRSAKeys.getRsakeys() == null || standardRSAKeys.getRsakeys().size() < 1) {
      response.getHead().setError(BAD_REQUEST);
      return response;
    }
    for (final RSAKey rsaKey : standardRSAKeys.getRsakeys())
    {
      if (rsaKey.getKeyid() == null || rsaKey.getPdata() == null) {
        response.addStatus(new RSAKeyIdAndError(-1, rsaKey.getKeyid(), Services.err.error(BAD_REQUEST)));
        continue;
      }
      try {
        userFacade.importStandardRSAKey(rsaKey);
      } catch (CommonException e) {
        response.addStatus(new RSAKeyIdAndError(-1, rsaKey.getKeyid(), Services.err.error(e.getError())));
        continue;
      }
    }
    return response;
  }

  public RSAKeyIdsResponse assignStandardRSAKeys(
      StandardRSAKeyIds standardRSAKeyIds)
  {
    RSAKeyIdsResponse response = new RSAKeyIdsResponse();
    if (standardRSAKeyIds.getRsakeyid() == null || standardRSAKeyIds.getRsakeyid().length < 1
        || standardRSAKeyIds.getUserid() == null || standardRSAKeyIds.getUserid().length != standardRSAKeyIds.getRsakeyid().length) {
      response.getHead().setError(BAD_REQUEST);
      return response;
    }
    for (int i = 0; i < standardRSAKeyIds.getRsakeyid().length; i++)
    {
      int rsakeyid = standardRSAKeyIds.getRsakeyid()[i];
      int userid = standardRSAKeyIds.getUserid()[i];
      if (rsakeyid < 1 || userid < 0) {
        response.addStatus(new RSAKeyIdAndError(rsakeyid, null, Services.err.error(BAD_REQUEST)));
        continue;
      }
      try {
        userFacade.assignStandardRSAKey(rsakeyid, userid);
      } catch (CommonException e) {
        response.addStatus(new RSAKeyIdAndError(rsakeyid, null, Services.err.error(e.getError())));
        continue;
      }
    }
    return response;
  }

  public RSAKeyIdsResponse unassignStandardRSAKeys(
      StandardRSAKeyIds standardRSAKeyIds)
  {
    RSAKeyIdsResponse response = new RSAKeyIdsResponse();
    if (standardRSAKeyIds.getRsakeyid() == null || standardRSAKeyIds.getRsakeyid().length < 1) {
      response.getHead().setError(BAD_REQUEST);
      return response;
    }
    for (int rsakeyid : standardRSAKeyIds.getRsakeyid()) {
      if (rsakeyid < 1) {
        response.addStatus(new RSAKeyIdAndError(rsakeyid, null, Services.err.error(BAD_REQUEST)));
        continue;
      }
      try {
        userFacade.unassignStandardRSAKey(rsakeyid);
      } catch (CommonException e) {
        response.addStatus(new RSAKeyIdAndError(rsakeyid, null, Services.err.error(e.getError())));
        continue;
      }
    }
    return response;
  }

  public RSAKeyIdsResponse enableStandardRSAKeys(
      StandardRSAKeyIds standardRSAKeyIds)
  {
    RSAKeyIdsResponse response = new RSAKeyIdsResponse();
    if (standardRSAKeyIds.getRsakeyid() == null || standardRSAKeyIds.getRsakeyid().length < 1) {
      response.getHead().setError(BAD_REQUEST);
      return response;
    }
    for (int rsakeyid : standardRSAKeyIds.getRsakeyid()) {
      if (rsakeyid < 1) {
        response.addStatus(new RSAKeyIdAndError(rsakeyid, null, Services.err.error(BAD_REQUEST)));
        continue;
      }
      try {
        userFacade.enableStandardRSAKey(rsakeyid);
      } catch (CommonException e) {
        response.addStatus(new RSAKeyIdAndError(rsakeyid, null, Services.err.error(e.getError())));
        continue;
      }
    }
    return response;
  }

  public RSAKeyIdsResponse disableStandardRSAKeys(
      StandardRSAKeyIds standardRSAKeyIds)
  {
    RSAKeyIdsResponse response = new RSAKeyIdsResponse();
    if (standardRSAKeyIds.getRsakeyid() == null || standardRSAKeyIds.getRsakeyid().length < 1) {
      response.getHead().setError(BAD_REQUEST);
      return response;
    }
    for (int rsakeyid : standardRSAKeyIds.getRsakeyid()) {
      if (rsakeyid < 1) {
        response.addStatus(new RSAKeyIdAndError(rsakeyid, null, Services.err.error(BAD_REQUEST)));
        continue;
      }
      try {
        userFacade.disableStandardRSAKey(rsakeyid);
      } catch (CommonException e) {
        response.addStatus(new RSAKeyIdAndError(rsakeyid, null, Services.err.error(e.getError())));
        continue;
      }
    }
    return response;
  }

  public RSAKeyIdsResponse deleteStandardRSAKeys(
      StandardRSAKeyIds standardRSAKeyIds)
  {
    RSAKeyIdsResponse response = new RSAKeyIdsResponse();
    if (standardRSAKeyIds.getRsakeyid() == null || standardRSAKeyIds.getRsakeyid().length < 1) {
      response.getHead().setError(BAD_REQUEST);
      return response;
    }
    for (int rsakeyid : standardRSAKeyIds.getRsakeyid()) {
      if (rsakeyid < 1) {
        response.addStatus(new RSAKeyIdAndError(rsakeyid, null, Services.err.error(BAD_REQUEST)));
        continue;
      }
      try {
        userFacade.deleteStandardRSAKey(rsakeyid);
      } catch (CommonException e) {
        response.addStatus(new RSAKeyIdAndError(rsakeyid, null, Services.err.error(e.getError())));
        continue;
      }
    }
    return response;
  }

  public ListStandardRSAKeysResponse listStandardRSAKeys(
      ListStandardRSAKeysParam listStandardRSAKeysParam)
  {
    ListStandardRSAKeysResponse response = new ListStandardRSAKeysResponse();
    fixListParam(listStandardRSAKeysParam, "idrsakey");
    response.setBody(new StandardRSAKeyList());
    response.getBody().copyFrom(listStandardRSAKeysParam);
    int[] amountContainer = new int[1];
    response.getBody().setList(userFacade.findStandardRSAKeys(
        listStandardRSAKeysParam.getUnassignedonly() != 0,
        listStandardRSAKeysParam,
        amountContainer));
    response.getBody().setAmount(amountContainer[0]);
    return response;
  }

	public void setUserFacade(UserFacade userFacade) {
		this.userFacade = userFacade;
	}

	public void setSessionFacade(SessionFacade sessionFacade) {
		this.sessionFacade = sessionFacade;
	}

	private int validateAndFixUser(User user, final boolean forUpdate) {
		return new Validater<User>() {
			@Override
			public int validationAndFix(User user) {
				if (forUpdate) {
					if (user.getIduser() == null || user.getIduser() < 0) {
						return BAD_REQUEST;
					}
					if (user.getUsername() != null) {
						user.setUsername(user.getUsername().trim());
						if (user.getUsername().length() < 1) {
							return BAD_REQUEST;
						}
					}
					if (user.getPassword() != null) {
						if (!userFacade.checkPassword(user.getPassword())) {
							return BAD_REQUEST;
						}
					}
					if (user.getRealname() != null) {
						user.setRealname(user.getRealname().trim());
					}
					if (user.getIdcardtype() != null) {
						user.setIdcardtype(user.getIdcardtype().trim());
					}
					if (user.getIdcard() != null) {
						user.setIdcard(user.getIdcard().trim());
					}
					if (user.getEmail() != null) {
						user.setEmail(user.getEmail().trim());
					}
					if (user.getAddress() != null) {
						user.setAddress(user.getAddress().trim());
					}
					if (user.getTelephone() != null) {
						user.setTelephone(user.getTelephone().trim());
					}
					if (user.getNotes() != null) {
						user.setNotes(user.getNotes().trim());
					}
				} else {
					user.setIduser(null);
					user.setUsername(nullToBlankString(user.getUsername()).trim());
					if(user.getPassword() == null)
					{
	          user.setPassword("");
					}
					if (user.getUsername().length() < 1) {
						return BAD_REQUEST;
					}
					if (!userFacade.checkPassword(user.getPassword())) {
						return BAD_REQUEST;
					}
					user.setRealname(nullToBlankString(user.getRealname()).trim());
					user.setIdcardtype(nullToBlankString(user.getIdcardtype()).trim());
					user.setIdcard(nullToBlankString(user.getIdcard()).trim());
					user.setEmail(nullToBlankString(user.getEmail()).trim());
					user.setAddress(nullToBlankString(user.getAddress()).trim());
					user.setTelephone(nullToBlankString(user.getTelephone()).trim());
					user.setNotes(nullToBlankString(user.getNotes()).trim());
				}
				if (!allInBound(
						127,
						user.getUsername(), user.getPassword(), user.getRealname(),
						user.getIdcardtype(), user.getIdcard(), user.getEmail(),
						user.getAddress(), user.getTelephone(), user.getNotes())) {
					return BAD_REQUEST;
				}
				if (!user.getUsername().matches("^[^\\\\/\"\\[\\]:|<>+=;\\,\\?\\*@]+$")) {
					return BAD_REQUEST;
				}
				user.setUsertype(DOMAIN_TYPE_LOCAL);
				user.setDomainid(DEFAULT_DOMAIN_ID);
				user.setOrganizationid(DEFAULT_ORGANIZATION_ID);
				if (user.getPassword() != null) {
					user.setPassword(getHex(user.getPassword().getBytes()));
				}
				return NO_ERRORS;
			}
		}.validationAndFix(user);
	}

	private int validatePrivilegeParam(PrivilegeParam privilegeParam) {
		if (privilegeParam.getUserid() < 0) {
			return BAD_REQUEST;
		}
		if (numberNotEquals(privilegeParam.getTargettype(), ADMIN_TARGET_ALL)
				&& numberNotEquals(privilegeParam.getTargettype(), ADMIN_TARGET_ORGANIZATION)
				&& numberNotEquals(privilegeParam.getTargettype(), ADMIN_TARGET_GROUP)) {
			return BAD_REQUEST;
		}
		if (numberEquals(privilegeParam.getTargettype(), ADMIN_TARGET_ORGANIZATION)
				&& privilegeParam.getTargetid() < 0) {
			return BAD_REQUEST;
		}
		if (numberEquals(privilegeParam.getTargettype(), ADMIN_TARGET_GROUP)
				&& privilegeParam.getTargetid() < 1) {
			return BAD_REQUEST;
		}
		if (numberEquals(privilegeParam.getTargettype(), ADMIN_TARGET_ALL)
				&& privilegeParam.getTargetid() != 0) {
			return BAD_REQUEST;
		}
		return NO_ERRORS;
	}

	public ListTraceRes listTrace(TraceReq user) {
		ListTraceRes res =new ListTraceRes();
		Head head=new Head();
		head.setError(0);
		res.setHead(head);
		ListTrace listTrace=new ListTrace();
		listTrace.setList(traceDao.listByUser(user.getTargetid(), user));
		res.setBody(listTrace);
		res.setPage(user);
		return res;
	}

}
