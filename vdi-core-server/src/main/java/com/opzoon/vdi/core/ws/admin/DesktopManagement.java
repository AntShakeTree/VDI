package com.opzoon.vdi.core.ws.admin;

import static com.opzoon.vdi.core.cloud.CloudManagerHelper.findDrivers;
import static com.opzoon.vdi.core.domain.AsyncJob.ASYNC_JOB_STATUS_FAILURE;
import static com.opzoon.vdi.core.domain.AsyncJob.ASYNC_JOB_STATUS_RUNNING;
import static com.opzoon.vdi.core.domain.AsyncJob.ASYNC_JOB_STATUS_SUCCESS;
import static com.opzoon.vdi.core.domain.DesktopPoolEntity.DESKTOP_POOL_ASSIGNMENT_DEDICATED;
import static com.opzoon.vdi.core.domain.DesktopPoolEntity.DESKTOP_POOL_ASSIGNMENT_FLOATING;
import static com.opzoon.vdi.core.domain.DesktopPoolEntity.DESKTOP_POOL_SOURCE_AUTO;
import static com.opzoon.vdi.core.domain.DesktopPoolEntity.DESKTOP_POOL_SOURCE_MANUAL;
import static com.opzoon.vdi.core.domain.ResourceAssignment.RESOURCE_TYPE_APPLICATION;
import static com.opzoon.vdi.core.domain.ResourceAssignment.RESOURCE_TYPE_POOL;
import static com.opzoon.vdi.core.domain.ResourceAssignment.RESOURCE_VISITOR_TYPE_GROUP;
import static com.opzoon.vdi.core.domain.ResourceAssignment.RESOURCE_VISITOR_TYPE_ORGANIZATION;
import static com.opzoon.vdi.core.domain.ResourceAssignment.RESOURCE_VISITOR_TYPE_USER;
import static com.opzoon.vdi.core.facade.CommonException.BAD_REQUEST;
import static com.opzoon.vdi.core.facade.CommonException.CONFLICT;
import static com.opzoon.vdi.core.facade.CommonException.MULTI_STATUS;
import static com.opzoon.vdi.core.facade.CommonException.NOT_FOUND;
import static com.opzoon.vdi.core.facade.CommonException.NO_ERRORS;
import static com.opzoon.vdi.core.facade.CommonException.UNKNOWN;
import static com.opzoon.vdi.core.facade.FacadeHelper.exists;
import static com.opzoon.vdi.core.util.ConditionUtils.numberEquals;
import static com.opzoon.vdi.core.util.ConditionUtils.numberNotEquals;
import static com.opzoon.vdi.core.util.StringUtils.allInBound;
import static com.opzoon.vdi.core.util.StringUtils.nullToBlankString;
import static com.opzoon.vdi.core.ws.WebServiceHelper.fixListParam;

import java.io.Serializable;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import javax.xml.bind.annotation.XmlRootElement;

import com.opzoon.ohvc.driver.opzooncloud.OpzoonCloudDriver;
import com.opzoon.ohvc.response.UsernameOrPasswordException;
import com.opzoon.ohvc.session.Session;
import com.opzoon.vdi.core.cloud.CloudManagerHelper;
import com.opzoon.vdi.core.domain.AsyncJob;
import com.opzoon.vdi.core.domain.CloudManagerEntity;
import com.opzoon.vdi.core.domain.Connection;
import com.opzoon.vdi.core.domain.Desktop;
import com.opzoon.vdi.core.domain.DesktopPoolEntity;
import com.opzoon.vdi.core.domain.RestrictionStrategy;
import com.opzoon.vdi.core.domain.RestrictionStrategyAssignment;
import com.opzoon.vdi.core.domain.UserVolume;
import com.opzoon.vdi.core.facade.CloudManagerFacade;
import com.opzoon.vdi.core.facade.CloudManagerFacade.Template;
import com.opzoon.vdi.core.facade.CommonException;
import com.opzoon.vdi.core.facade.DesktopFacade;
import com.opzoon.vdi.core.facade.DesktopPoolFacade;
import com.opzoon.vdi.core.facade.FacadeHelper.PagingInfo;
import com.opzoon.vdi.core.facade.ResourceFacade;
import com.opzoon.vdi.core.facade.ResourceFacade.Resource;
import com.opzoon.vdi.core.facade.transience.AsyncJobFacade;
import com.opzoon.vdi.core.facade.transience.ConnectionFacade;
import com.opzoon.vdi.core.ws.Services;
import com.opzoon.vdi.core.ws.Services.CommonList;
import com.opzoon.vdi.core.ws.Services.MultiStatusResponse;
import com.opzoon.vdi.core.ws.Services.NullResponse;
import com.opzoon.vdi.core.ws.Services.Response;
import com.opzoon.vdi.core.ws.WebServiceHelper.Validater;
import com.opzoon.vdi.core.ws.admin.DesktopManagement.CloudManagerEntityIdResponse;
import com.opzoon.vdi.core.ws.vo.admin.user.UserIdParam;

/**
 * 桌面管理业务实现.
 */
public class DesktopManagement {

	private DesktopFacade desktopFacade;
	private DesktopPoolFacade desktopPoolFacade;
	private CloudManagerFacade cloudManagerFacade;
	private ResourceFacade resourceFacade;
	private AsyncJobFacade asyncJobFacade;
	private ConnectionFacade connectionFacade;

	/**
	 * 列举所有可用来创建虚拟桌面池的模板.
	 * 
	 * @param listTemplatesParam
	 *            列举模板参数.
	 * @return 列举模板响应.
	 */
	public ListTemplatesResponse listTemplates(
			ListTemplatesParam listTemplatesParam) {
		ListTemplatesResponse response = new ListTemplatesResponse();
		if (listTemplatesParam.getCloudmanagerid() < 1) {
			response.getHead().setError(BAD_REQUEST);
			return response;
		}
		fixListParam(listTemplatesParam, null);
		response.setBody(new TemplateList());
		response.getBody().copyFrom(listTemplatesParam);
		final List<Template> templates = new LinkedList<Template>();
		int error = cloudManagerFacade.findAllTemplates(
				listTemplatesParam.getCloudmanagerid(), templates,
				listTemplatesParam.isLink());
		if (error != NO_ERRORS) {
			response.getHead().setError(error);
			return response;
		}
		response.getBody().setAmount(templates.size());
		response.getBody().setList(listTemplatesParam.subList(templates));
		return response;
	}

	/**
	 * 创建桌面池.
	 * 
	 * @param desktopPool
	 *            桌面池实体.
	 * @return 桌面池ID响应.<br />
	 *         {@link com.opzoon.vdi.core.facade.CommonException#NO_ERRORS}: 成功;<br />
	 *         {@link com.opzoon.vdi.core.facade.CommonException#BAD_REQUEST}:
	 *         参数校验失败;<br />
	 *         {@link com.opzoon.vdi.core.facade.CommonException#FORBIDDEN}:
	 *         权限不足.
	 */
	public DesktopPoolIdResponse createDesktopPool(
			final DesktopPoolEntity desktopPool) {
		DesktopPoolIdResponse response = new DesktopPoolIdResponse();
		int error = this.validationAndFixDesktopPool(desktopPool, false);
		if (error != NO_ERRORS) {
			response.getHead().setError(error);
			return response;
		}
		try {
			error = desktopPoolFacade.createDesktopPool(desktopPool);
			if (numberEquals(desktopPool.getVmsource(),
					DESKTOP_POOL_SOURCE_AUTO)) {
				desktopPoolFacade.adjustDesktopCount(desktopPool);
			}
		} catch (CommonException e) {
			response.getHead().setError(e.getError());
			return response;
		}
		if (error != NO_ERRORS) {
			response.getHead().setError(error);
			return response;
		}
		DesktopPoolIdWrapper desktopPoolIdWrapper = new DesktopPoolIdWrapper();
		response.setBody(desktopPoolIdWrapper);
		desktopPoolIdWrapper.setIddesktoppool(desktopPool.getIddesktoppool());
		return response;
	}

	public NullResponse updateDesktopPool(DesktopPoolEntity desktopPool) {
		NullResponse response = new NullResponse();
		int error = this.validationAndFixDesktopPool(desktopPool, true);
		if (error != NO_ERRORS) {
			response.getHead().setError(error);
			return response;
		}
		error = desktopPoolFacade.updateDesktopPool(desktopPool);
		if (error != NO_ERRORS) {
			response.getHead().setError(error);
			return response;
		}
		return response;
	}

	/**
	 * 删除桌面池.
	 * 
	 * @param desktopPoolIdParam
	 *            桌面池ID参数.
	 * @return 空响应.<br />
	 *         {@link com.opzoon.vdi.core.facade.CommonException#NO_ERRORS}: 成功;<br />
	 *         {@link com.opzoon.vdi.core.facade.CommonException#NOT_FOUND}:
	 *         删除不存在的桌面池;<br />
	 *         {@link com.opzoon.vdi.core.facade.CommonException#FORBIDDEN}:
	 *         权限不足.
	 */
	public DesktopPoolListResponse deleteDesktopPool(
			DesktopPoolIdParam desktopPoolIdParam) {
		DesktopPoolListResponse response = new DesktopPoolListResponse();
		for (int desktoppoolid : desktopPoolIdParam.getIddesktoppool()) {
			if (desktoppoolid < 0) {
				response.addStatus(new DesktopPoolAndError(desktoppoolid,
						Services.err.error(BAD_REQUEST)));
				continue;
			}
			int error = desktopPoolFacade.deleteDesktopPool(desktoppoolid,
					desktopPoolIdParam.getForce() != 0);
			if (numberNotEquals(error, NO_ERRORS)) {
				response.addStatus(new DesktopPoolAndError(desktoppoolid,
						Services.err.error(error)));
				continue;
			}
		}
		return response;
	}

	/**
	 * 列举桌面池.
	 * 
	 * @param listDesktopPoolsParam
	 *            列举桌面池参数.
	 * @return 列举桌面池响应.
	 */
	public ListDesktopPoolsResponse listDesktopPools(
			ListDesktopPoolsParam listDesktopPoolsParam) {
		ListDesktopPoolsResponse response = new ListDesktopPoolsResponse();
		if (listDesktopPoolsParam.getCloudmanagerid() < -1) {
			response.getHead().setError(BAD_REQUEST);
			return response;
		}
		fixListParam(listDesktopPoolsParam, "iddesktoppool");
		response.setBody(new DesktopPoolList());
		response.getBody().copyFrom(listDesktopPoolsParam);
		int[] amountContainer = new int[1];
		response.getBody().setList(
				desktopPoolFacade.findDesktopPools(
						listDesktopPoolsParam.getCloudmanagerid(),
						listDesktopPoolsParam.getDesktoppoolid(),
						listDesktopPoolsParam, amountContainer));
		response.getBody().setAmount(amountContainer[0]);
		return response;
	}

	/**
	 * 将桌面池分配给用户使用.
	 * 
	 * @param userAndDesktopPool
	 *            用户ID和桌面池ID.
	 * @return 空响应.<br />
	 *         {@link com.opzoon.vdi.core.facade.CommonException#NO_ERRORS}: 成功;<br />
	 *         {@link com.opzoon.vdi.core.facade.CommonException#BAD_REQUEST}:
	 *         参数校验失败.<br />
	 *         {@link com.opzoon.vdi.core.facade.CommonException#CONFLICT}:
	 *         此用户已被分配给此桌面池;<br />
	 *         {@link com.opzoon.vdi.core.facade.CommonException#FORBIDDEN}:
	 *         权限不足.
	 */
	public UserAndPoolListResponse assignDesktopPoolToUser(
			UserAndDesktopPool userAndDesktopPool) {
		return (UserAndPoolListResponse) this.targetToPool(userAndDesktopPool,
				false);
	}

	/**
	 * 取消分配给用户使用的桌面池.
	 * 
	 * @param userAndDesktopPool
	 *            用户ID和桌面池ID.
	 * @return 空响应.<br />
	 *         {@link com.opzoon.vdi.core.facade.CommonException#NO_ERRORS}: 成功;<br />
	 *         {@link com.opzoon.vdi.core.facade.CommonException#BAD_REQUEST}:
	 *         参数校验失败.<br />
	 *         {@link com.opzoon.vdi.core.facade.CommonException#NOT_FOUND}:
	 *         此用户未曾被分配给此桌面池;<br />
	 *         {@link com.opzoon.vdi.core.facade.CommonException#FORBIDDEN}:
	 *         权限不足.
	 */
	public UserAndPoolListResponse unassignDesktopPoolToUser(
			UserAndDesktopPool userAndDesktopPool) {
		return (UserAndPoolListResponse) this.targetToPool(userAndDesktopPool,
				true);
	}

	/**
	 * 将桌面池分配给组使用.
	 * 
	 * @param groupAndDesktopPool
	 *            组ID和桌面池ID.
	 * @return 空响应.<br />
	 *         {@link com.opzoon.vdi.core.facade.CommonException#NO_ERRORS}: 成功;<br />
	 *         {@link com.opzoon.vdi.core.facade.CommonException#BAD_REQUEST}:
	 *         参数校验失败.<br />
	 *         {@link com.opzoon.vdi.core.facade.CommonException#CONFLICT}:
	 *         此组已被分配给此桌面池;<br />
	 *         {@link com.opzoon.vdi.core.facade.CommonException#FORBIDDEN}:
	 *         权限不足.
	 */
	public GroupAndPoolListResponse assignDesktopPoolToGroup(
			GroupAndDesktopPool groupAndDesktopPool) {
		return (GroupAndPoolListResponse) this.targetToPool(
				groupAndDesktopPool, false);
	}

	/**
	 * 取消分配给组使用的桌面池.
	 * 
	 * @param groupAndDesktopPool
	 *            组ID和桌面池ID.
	 * @return 空响应.<br />
	 *         {@link com.opzoon.vdi.core.facade.CommonException#NO_ERRORS}: 成功;<br />
	 *         {@link com.opzoon.vdi.core.facade.CommonException#BAD_REQUEST}:
	 *         参数校验失败.<br />
	 *         {@link com.opzoon.vdi.core.facade.CommonException#NOT_FOUND}:
	 *         此组未曾被分配给此桌面池;<br />
	 *         {@link com.opzoon.vdi.core.facade.CommonException#FORBIDDEN}:
	 *         权限不足.
	 */
	public GroupAndPoolListResponse unassignDesktopPoolToGroup(
			GroupAndDesktopPool groupAndDesktopPool) {
		return (GroupAndPoolListResponse) this.targetToPool(
				groupAndDesktopPool, true);
	}

	public OrganizationAndPoolListResponse assignDesktopPoolToOrganization(
			OrganizationAndDesktopPool organizationAndDesktopPool) {
		return (OrganizationAndPoolListResponse) this.targetToPool(
				organizationAndDesktopPool, false);
	}

	public OrganizationAndPoolListResponse unassignDesktopPoolToOrganization(
			OrganizationAndDesktopPool organizationAndDesktopPool) {
		return (OrganizationAndPoolListResponse) this.targetToPool(
				organizationAndDesktopPool, true);
	}

	/**
	 * (异步)销毁桌面, 通常用来解决那些无法通过重新启动修复的桌面.
	 * 
	 * @param desktopIdParam
	 *            桌面ID参数.
	 * @return 空响应(含任务ID)<br />
	 *         {@link com.opzoon.vdi.core.facade.CommonException#NO_ERRORS}: 成功;<br />
	 *         {@link com.opzoon.vdi.core.facade.CommonException#FORBIDDEN}:
	 *         权限不足.
	 */
	public NullResponse destroyDesktop(final DesktopIdParam desktopIdParam) {
		NullResponse response = new NullResponse();
		if (desktopIdParam.getDesktopid() < 1) {
			response.getHead().setError(BAD_REQUEST);
			return response;
		}
		int[] errorContainer = new int[] { NO_ERRORS };
		int jobId = desktopFacade.destroyDesktop(desktopIdParam.getDesktopid(),
				errorContainer);
		if (numberNotEquals(errorContainer[0], NO_ERRORS)) {
			response.getHead().setError(errorContainer[0]);
			return response;
		}
		if (jobId == -1) {
			response.getHead().setError(UNKNOWN);
			return response;
		}
		response.getHead().setJobid(jobId);
		return response;
	}

	/**
	 * (异步)通过Hypervisor重启桌面, 存在数据丢失的风险.
	 * 
	 * @param desktopIdParam
	 *            桌面ID参数.
	 * @return 空响应(含任务ID)<br />
	 *         {@link com.opzoon.vdi.core.facade.CommonException#NO_ERRORS}: 成功;<br />
	 *         {@link com.opzoon.vdi.core.facade.CommonException#FORBIDDEN}:
	 *         权限不足.
	 */
	public NullResponse rebootDesktop(final DesktopIdParam desktopIdParam) {
		NullResponse response = new NullResponse();
		if (desktopIdParam.getDesktopid() < 1) {
			response.getHead().setError(BAD_REQUEST);
			return response;
		}
		int[] errorContainer = new int[] { NO_ERRORS };
		int jobId = desktopFacade.rebootDesktop(desktopIdParam.getDesktopid(),
				errorContainer);
		if (numberNotEquals(errorContainer[0], NO_ERRORS)) {
			response.getHead().setError(errorContainer[0]);
			return response;
		}
		if (jobId == -1) {
			response.getHead().setError(UNKNOWN);
			return response;
		}
		response.getHead().setJobid(jobId);
		return response;
	}

	/**
	 * (异步)通过Hypervisor启动桌面.
	 * 
	 * @param desktopIdParam
	 *            桌面ID参数.
	 * @return 空响应(含任务ID).
	 */
	public NullResponse startDesktop(final DesktopIdParam desktopIdParam) {
		NullResponse response = new NullResponse();
		if (desktopIdParam.getDesktopid() < 1) {
			response.getHead().setError(BAD_REQUEST);
			return response;
		}
		int[] errorContainer = new int[] { NO_ERRORS };
		int jobId = desktopFacade.startDesktop(desktopIdParam.getDesktopid(),
				errorContainer);
		if (numberNotEquals(errorContainer[0], NO_ERRORS)) {
			response.getHead().setError(errorContainer[0]);
			return response;
		}
		if (jobId == -1) {
			response.getHead().setError(UNKNOWN);
			return response;
		}
		response.getHead().setJobid(jobId);
		return response;
	}

	/**
	 * (异步)通过Hypervisor关闭桌面, 存在数据丢失的风险.
	 * 
	 * @param desktopIdParam
	 *            桌面ID参数.
	 * @return 空响应(含任务ID).
	 */
	public NullResponse stopDesktop(final DesktopIdParam desktopIdParam) {
		NullResponse response = new NullResponse();
		if (desktopIdParam.getDesktopid() < 1) {
			response.getHead().setError(BAD_REQUEST);
			return response;
		}
		int[] errorContainer = new int[] { NO_ERRORS };
		int jobId = desktopFacade.stopDesktop(desktopIdParam.getDesktopid(),
				errorContainer);
		if (numberNotEquals(errorContainer[0], NO_ERRORS)) {
			response.getHead().setError(errorContainer[0]);
			return response;
		}
		if (jobId == -1) {
			response.getHead().setError(UNKNOWN);
			return response;
		}
		response.getHead().setJobid(jobId);
		return response;
	}

	public NullResponse rebootDesktopOS(DesktopIdParam desktopIdParam) {
		NullResponse response = new NullResponse();
		if (desktopIdParam.getDesktopid() < 1) {
			response.getHead().setError(BAD_REQUEST);
			return response;
		}
		int[] errorContainer = new int[] { NO_ERRORS };
		int jobId = desktopFacade.rebootDesktopOS(
				desktopIdParam.getDesktopid(), errorContainer);
		if (numberNotEquals(errorContainer[0], NO_ERRORS)) {
			response.getHead().setError(errorContainer[0]);
			return response;
		}
		if (jobId == -1) {
			response.getHead().setError(UNKNOWN);
			return response;
		}
		response.getHead().setJobid(jobId);
		return response;
	}

	public NullResponse stopDesktopOS(DesktopIdParam desktopIdParam) {
		NullResponse response = new NullResponse();
		if (desktopIdParam.getDesktopid() < 1) {
			response.getHead().setError(BAD_REQUEST);
			return response;
		}
		int[] errorContainer = new int[] { NO_ERRORS };
		int jobId = desktopFacade.stopDesktopOS(desktopIdParam.getDesktopid(),
				errorContainer);
		if (numberNotEquals(errorContainer[0], NO_ERRORS)) {
			response.getHead().setError(errorContainer[0]);
			return response;
		}
		if (jobId == -1) {
			response.getHead().setError(UNKNOWN);
			return response;
		}
		response.getHead().setJobid(jobId);
		return response;
	}

	/**
	 * 列举桌面.
	 * 
	 * @param listDesktopsParam
	 *            列举桌面参数.
	 * @return 列举桌面响应.
	 */
	public ListDesktopsResponse listDesktops(ListDesktopsParam listDesktopsParam) {
		ListDesktopsResponse response = new ListDesktopsResponse();
		if (listDesktopsParam.getDesktoppoolid() < -1
				|| listDesktopsParam.getDesktopid() < -1) {
			response.getHead().setError(BAD_REQUEST);
			return response;
		}
		fixListParam(listDesktopsParam, "iddesktop");
		response.setBody(new DesktopList());
		response.getBody().copyFrom(listDesktopsParam);
		int[] amountContainer = new int[1];
		response.getBody().setList(
				desktopFacade.findDesktops(
						listDesktopsParam.getDesktoppoolid(),
						listDesktopsParam.getDesktopid(), listDesktopsParam,
						amountContainer));
		response.getBody().setAmount(amountContainer[0]);
		return response;
	}

	/**
	 * 添加虚拟化管理平台.
	 * 
	 * @param cloudManagerEntity
	 *            平台实体.
	 * @return 平台ID响应.<br />
	 *         {@link com.opzoon.vdi.core.facade.CommonException#NO_ERRORS}: 成功;<br />
	 *         {@link com.opzoon.vdi.core.facade.CommonException#BAD_REQUEST}:
	 *         参数校验失败;<br />
	 *         {@link com.opzoon.vdi.core.facade.CommonException#FORBIDDEN}:
	 *         权限不足.
	 */
	public CloudManagerEntityIdResponse addCloudManager(
			CloudManagerEntity cloudManagerEntity) {
		CloudManagerEntityIdResponse response = new CloudManagerEntityIdResponse();
		int error = this.validationAndFixCloudManager(cloudManagerEntity);
		if (error != NO_ERRORS) {
			response.getHead().setError(error);
			return response;
		}
		// FIXME
		boolean found = false;
		List<String> drivers = findDrivers();
		for (String driver : drivers) {
			if (driver.equals(cloudManagerEntity.getClouddrivername())) {
				found = true;
				break;
			}
		}
		if (!found) {
			response.getHead().setError(NOT_FOUND);
			return response;
		}
		try {
			error = cloudManagerFacade.createCloudManager(cloudManagerEntity);
		} catch (CommonException e) {
			response.getHead().setError(e.getError());
			return response;
		}
		if (error != NO_ERRORS) {
			response.getHead().setError(error);
			return response;
		}
		CloudManagerEntityIdWrapper cloudManagerEntityIdWrapper = new CloudManagerEntityIdWrapper();
		response.setBody(cloudManagerEntityIdWrapper);
		cloudManagerEntityIdWrapper.setIdcloudmanager(cloudManagerEntity
				.getIdcloudmanager());
		return response;
	}

	/**
	 * 删除虚拟化管理平台.
	 * 
	 * @param cloudManagerEntityIdParam
	 *            平台ID参数.
	 * @return 平台ID响应.<br />
	 *         {@link com.opzoon.vdi.core.facade.CommonException#NO_ERRORS}: 成功;<br />
	 *         {@link com.opzoon.vdi.core.facade.CommonException#NOT_FOUND}:
	 *         删除不存在的虚拟化管理平台;<br />
	 *         {@link com.opzoon.vdi.core.facade.CommonException#FORBIDDEN}:
	 *         权限不足.
	 */
	public NullResponse deleteCloudManager(
			CloudManagerEntityIdParam cloudManagerEntityIdParam) {
		NullResponse response = new NullResponse();
		if (cloudManagerEntityIdParam.getIdcloudmanager() < 1) {
			response.getHead().setError(BAD_REQUEST);
			return response;
		}
		try {
			cloudManagerFacade.deleteCloudManager(cloudManagerEntityIdParam
					.getIdcloudmanager());
		} catch (CommonException e) {
			response.getHead().setError(e.getError());
			return response;
		}
		return response;
	}

	public ListCloudDriversResponse listCloudDrivers() {
		ListCloudDriversResponse response = new ListCloudDriversResponse();
		response.setBody(findDrivers());
		return response;
	}

	/**
	 * 列举虚拟化管理平台.
	 * 
	 * @param listCloudManagersParam
	 *            列举平台参数.
	 * @return 列举平台响应.
	 */
	public ListCloudManagersResponse listCloudManagers(
			ListCloudManagersParam listCloudManagersParam) {
		ListCloudManagersResponse response = new ListCloudManagersResponse();
		if (listCloudManagersParam.getIdcloudmanager() < -1) {
			response.getHead().setError(BAD_REQUEST);
			return response;
		}
		response.setBody(cloudManagerFacade.findCloudManagers(
				listCloudManagersParam.getIdcloudmanager(),
				listCloudManagersParam.getClouddrivername(),
				listCloudManagersParam));
		return response;
	}

	/**
	 * 查询异步任务状态.
	 * 
	 * @param asyncJobidParam
	 *            任务ID状态.
	 * @return 异步任务响应.<br />
	 *         {@link com.opzoon.vdi.core.facade.CommonException#NO_ERRORS}: 成功;<br />
	 *         {@link com.opzoon.vdi.core.facade.CommonException#NOT_FOUND}:
	 *         任务不存在.
	 */
	public EstablishConnectionAsyncJobResponse queryAsyncJobResult(
			AsyncJobIdParam asyncJobidParam) {
		EstablishConnectionAsyncJobResponse response = new EstablishConnectionAsyncJobResponse();
		if (asyncJobidParam.getJobid() < 1) {
			response.getHead().setError(BAD_REQUEST);
			return response;
		}
		AsyncJob job = asyncJobFacade.findAsyncJob(asyncJobidParam.getJobid());
		if (job == null) {
			response.getHead().setError(NOT_FOUND);
			return response;
		}
		EstablishConnectionAsyncJob j = new EstablishConnectionAsyncJob();
		j.setCmd(job.getCmd());
		j.setCreatetime(job.getCreatetime());
		j.setHandle(null);
		j.setJobid(job.getJobid());
		j.setJobprocstatus(job.getJobprocstatus());
		j.setJobresult(null);
		j.setJobresultcode(Services.err.error(job.getJobresultcode()));
		j.setJobstatus(job.getJobstatus());
		if (job.getJobstatus() == AsyncJob.ASYNC_JOB_STATUS_SUCCESS
				&& job.getCmd().equals("establishConnection")) {
			j.setJobresult(connectionFacade.findConnection(Integer.parseInt(job
					.getJobresult().toString())));
			resourceFacade.decorateConnection(j.getJobresult());
		}
		response.setBody(j);
		return response;
	}

	/**
	 * 列举异步任务.
	 * 
	 * @param listAsyncJobsParam
	 *            列举异步任务参数.
	 * @return 列举异步任务响应.
	 */
	public ListAsyncJobsResponse listAsyncJobs(
			ListAsyncJobsParam listAsyncJobsParam) {
		ListAsyncJobsResponse response = new ListAsyncJobsResponse();
		if (numberNotEquals(listAsyncJobsParam.getJobstatus(), -1)
				&& numberNotEquals(listAsyncJobsParam.getJobstatus(),
						ASYNC_JOB_STATUS_RUNNING)
				&& numberNotEquals(listAsyncJobsParam.getJobstatus(),
						ASYNC_JOB_STATUS_FAILURE)
				&& numberNotEquals(listAsyncJobsParam.getJobstatus(),
						ASYNC_JOB_STATUS_SUCCESS)) {
			response.getHead().setError(BAD_REQUEST);
			return response;
		}
		fixListParam(listAsyncJobsParam, "jobid");
		response.setBody(new AsyncJobList());
		response.getBody().copyFrom(listAsyncJobsParam);
		int[] amountContainer = new int[1];
		response.getBody().setList(
				asyncJobFacade.findAsyncJobs(listAsyncJobsParam.getCmd(),
						listAsyncJobsParam.getJobstatus(), listAsyncJobsParam,
						amountContainer));
		response.getBody().setAmount(amountContainer[0]);
		return response;
	}

	/**
	 * 列举连接.
	 * 
	 * @param listConnectionsParam
	 *            列举连接参数.
	 * @return 列举连接响应.<br />
	 *         {@link com.opzoon.vdi.core.facade.CommonException#NO_ERRORS}: 成功;<br />
	 *         {@link com.opzoon.vdi.core.facade.CommonException#BAD_REQUEST}:
	 *         参数校验失败 .
	 */
	public ListConnectionsResponse listConnections(
			ListConnectionsParam listConnectionsParam) {
		ListConnectionsResponse response = new ListConnectionsResponse();
		if (listConnectionsParam.getSessionid() < -1
				|| listConnectionsParam.getResourceid() < -1) {
			response.getHead().setError(BAD_REQUEST);
			return response;
		}
		if ((numberNotEquals(listConnectionsParam.getResourcetype(), -1) || listConnectionsParam
				.getResourceid() > -1)
				&& numberNotEquals(listConnectionsParam.getResourcetype(),
						RESOURCE_TYPE_POOL)
				&& numberNotEquals(listConnectionsParam.getResourcetype(),
						RESOURCE_TYPE_APPLICATION)) {
			response.getHead().setError(BAD_REQUEST);
			return response;
		}
		fixListParam(listConnectionsParam, "idconnection");
		response.setBody(new ConnectionList());
		response.getBody().copyFrom(listConnectionsParam);
		int[] amountContainer = new int[1];
		response.getBody().setList(
				connectionFacade.findConnections(
						listConnectionsParam.getSessionid(),
						listConnectionsParam.getResourcetype(),
						listConnectionsParam.getResourceid(),
						listConnectionsParam.getBrokername(),
						listConnectionsParam, amountContainer));
		response.getBody().setAmount(amountContainer[0]);
		return response;
	}

	/**
	 * 中止连接，后台清理相关资源.
	 * 
	 * @param connectionIdParam
	 *            连接ID.
	 * @return 空响应.<br />
	 *         {@link com.opzoon.vdi.core.facade.CommonException#NO_ERRORS}: 成功;<br />
	 *         {@link com.opzoon.vdi.core.facade.CommonException#BAD_REQUEST}:
	 *         参数校验失败 .<br />
	 *         {@link com.opzoon.vdi.core.facade.CommonException#NOT_FOUND}:
	 *         找不到指定连接 .<br />
	 *         {@link com.opzoon.vdi.core.facade.CommonException#FORBIDDEN}:
	 *         没有修改此连接所有者的权限 .
	 */
	public NullResponse disconnectConnection(ConnectionIdParam connectionIdParam) {
		NullResponse response = new NullResponse();
		if (connectionIdParam.getIdconnection() < 1) {
			response.getHead().setError(BAD_REQUEST);
			return response;
		}
		int error = resourceFacade.disconnectConnection(connectionIdParam
				.getIdconnection());
		if (numberNotEquals(error, NO_ERRORS)) {
			response.getHead().setError(error);
			return response;
		}
		return response;
	}

	public ListAssignmentsResponse listAssignments(
			ListAssignmentsParam listAssignmentsParam) {
		ListAssignmentsResponse response = new ListAssignmentsResponse();
		if (numberNotEquals(listAssignmentsParam.getResourcetype(), -1)
				&& numberNotEquals(listAssignmentsParam.getResourcetype(),
						RESOURCE_TYPE_POOL)
				&& numberNotEquals(listAssignmentsParam.getResourcetype(),
						RESOURCE_TYPE_APPLICATION)) {
			response.getHead().setError(BAD_REQUEST);
			return response;
		}
		if (listAssignmentsParam.getUserid() == null) {
			listAssignmentsParam.setUserid(-2);
		}
		if (listAssignmentsParam.getOrganizationid() == null) {
			listAssignmentsParam.setOrganizationid(-2);
		}
		if (listAssignmentsParam.getGroupid() == null) {
			listAssignmentsParam.setGroupid(-2);
		}
		if (listAssignmentsParam.getResourceid() < -1
				|| listAssignmentsParam.getUserid() < -2
				|| listAssignmentsParam.getOrganizationid() < -2
				|| listAssignmentsParam.getGroupid() < -2) {
			response.getHead().setError(BAD_REQUEST);
			return response;
		}
		fixListParam(listAssignmentsParam, "resourceid");
		response.setBody(new ResourceList());
		response.getBody().copyFrom(listAssignmentsParam);
		int[] amountContainer = new int[1];
		if (listAssignmentsParam.getUserid() == -1
				&& listAssignmentsParam.getOrganizationid() < -1
				&& listAssignmentsParam.getGroupid() < -1) {
			response.getBody().setList(
					resourceFacade.findUserAssignments(
							listAssignmentsParam.getResourcetype(),
							listAssignmentsParam.getResourceid(),
							listAssignmentsParam, amountContainer));
		} else if (listAssignmentsParam.getUserid() < -1
				&& listAssignmentsParam.getOrganizationid() == -1
				&& listAssignmentsParam.getGroupid() < -1) {
			response.getBody().setList(
					resourceFacade.findOrganizationAssignments(
							listAssignmentsParam.getResourcetype(),
							listAssignmentsParam.getResourceid(),
							listAssignmentsParam, amountContainer));
		} else if (listAssignmentsParam.getUserid() < -1
				&& listAssignmentsParam.getOrganizationid() < -1
				&& listAssignmentsParam.getGroupid() == -1) {
			response.getBody().setList(
					resourceFacade.findGroupAssignments(
							listAssignmentsParam.getResourcetype(),
							listAssignmentsParam.getResourceid(),
							listAssignmentsParam, amountContainer));
		} else {
			response.getBody().setList(
					resourceFacade.findAssignments(
							listAssignmentsParam.getResourcetype(),
							listAssignmentsParam.getResourceid(),
							listAssignmentsParam.getUserid(),
							listAssignmentsParam.getOrganizationid(),
							listAssignmentsParam.getGroupid(),
							listAssignmentsParam, amountContainer));
		}
		response.getBody().setAmount(amountContainer[0]);
		return response;
	}

	public NullResponse createUserVolume(UserVolume userVolume) {
		NullResponse response = new NullResponse();
		int error = this.validationUserVolume(userVolume);
		if (error != NO_ERRORS) {
			response.getHead().setError(error);
			return response;
		}
		error = desktopFacade.createUserVolume(userVolume.getCloudmanagerid(),
				userVolume.getUserid(), userVolume.getSize(),
				userVolume.getVolumename());
		if (error != NO_ERRORS) {
			response.getHead().setError(error);
			return response;
		}
		return response;
	}

	public NullResponse deleteUserVolume(UserIdAndVolumeIdParam userIdParam) {
		NullResponse response = new NullResponse();
		if (userIdParam.getUserid() < 0) {
			response.getHead().setError(BAD_REQUEST);
			return response;
		}
		int error = desktopFacade.deleteUserVolumes(userIdParam.getUserid(),
				userIdParam.getVolumeid());
		if (error != NO_ERRORS) {
			response.getHead().setError(error);
			return response;
		}
		return response;
	}

	public NullResponse eraseUserVolume(UserIdAndVolumeIdParam userIdParam) {
		NullResponse response = new NullResponse();
		if (userIdParam.getUserid() < 0) {
			response.getHead().setError(BAD_REQUEST);
			return response;
		}
		int error = desktopFacade.eraseUserVolumes(userIdParam.getUserid(),
				userIdParam.getVolumeid());
		if (error != NO_ERRORS) {
			response.getHead().setError(error);
			return response;
		}
		return response;
	}

	public ListUserVolumesResponse listUserVolumes(UserIdParam userIdParam) {
		ListUserVolumesResponse response = new ListUserVolumesResponse();
		if (userIdParam.getUserid() < -1) {
			response.getHead().setError(BAD_REQUEST);
			return response;
		}
		fixListParam(userIdParam, "iduservolume");
		response.setBody(new UserVolumeList());
		response.getBody().copyFrom(userIdParam);
		int[] amountContainer = new int[1];
		response.getBody().setList(
				desktopFacade.findUserVolumes(userIdParam.getUserid(),
						userIdParam, amountContainer));
		response.getBody().setAmount(amountContainer[0]);
		return response;
	}

	public ListRestrictionStrategiesResponse listRestrictionStrategies(
			ListRestrictionStrategiesParam listRestrictionStrategiesParam) {
		ListRestrictionStrategiesResponse response = new ListRestrictionStrategiesResponse();
		fixListParam(listRestrictionStrategiesParam, "idrestrictionstrategy");
		response.setBody(new RestrictionStrategyList());
		response.getBody().copyFrom(listRestrictionStrategiesParam);
		int[] amountContainer = new int[1];
		response.getBody().setList(
				desktopFacade.findRestrictionStrategies(
						listRestrictionStrategiesParam, amountContainer));
		response.getBody().setAmount(amountContainer[0]);
		return response;
	}

	public RestrictionStrategyResponse createRestrictionStrategy(
			RestrictionStrategy restrictionStrategy) {
		RestrictionStrategyResponse response = new RestrictionStrategyResponse();
		int error = this.validationAndFixRestrictionStrategy(
				restrictionStrategy, false);
		if (error != NO_ERRORS) {
			response.getHead().setError(error);
			return response;
		}
		try {
			error = desktopFacade
					.createRestrictionStrategy(restrictionStrategy);
		} catch (CommonException e) {
			response.getHead().setError(e.getError());
			return response;
		}
		if (error != NO_ERRORS) {
			response.getHead().setError(error);
			return response;
		}
		RestrictionStrategyIdWrapper restrictionStrategyIdWrapper = new RestrictionStrategyIdWrapper();
		response.setBody(restrictionStrategyIdWrapper);
		restrictionStrategyIdWrapper
				.setIdrestrictionstrategy(restrictionStrategy
						.getIdrestrictionstrategy());
		return response;
	}

	public NullResponse updateRestrictionStrategy(
			RestrictionStrategy restrictionStrategy) {
		NullResponse response = new NullResponse();
		int error = this.validationAndFixRestrictionStrategy(
				restrictionStrategy, true);
		if (error != NO_ERRORS) {
			response.getHead().setError(error);
			return response;
		}
		error = desktopFacade.updateRestrictionStrategy(restrictionStrategy);
		if (error != NO_ERRORS) {
			response.getHead().setError(error);
			return response;
		}
		return response;
	}

	public ListRestrictionStrategyAssignmentsResponse listRestrictionStrategyAssignments(
			ListRestrictionStrategyAssignmentsParam listRestrictionStrategyAssignmentsParam) {
		ListRestrictionStrategyAssignmentsResponse response = new ListRestrictionStrategyAssignmentsResponse();
		if (listRestrictionStrategyAssignmentsParam.getResourcetype() == null) {
			listRestrictionStrategyAssignmentsParam.setResourcetype(-1);
			listRestrictionStrategyAssignmentsParam.setResourceid(-1);
		}
		if (listRestrictionStrategyAssignmentsParam.getDomainid() < -1) {
			response.getHead().setError(BAD_REQUEST);
			return response;
		}
		if (numberNotEquals(
				listRestrictionStrategyAssignmentsParam.getResourcetype(), -1)
				&& numberNotEquals(
						listRestrictionStrategyAssignmentsParam
								.getResourcetype(),
						RESOURCE_TYPE_POOL)
				&& numberNotEquals(
						listRestrictionStrategyAssignmentsParam
								.getResourcetype(),
						RESOURCE_TYPE_APPLICATION)) {
			response.getHead().setError(BAD_REQUEST);
			return response;
		}
		if (listRestrictionStrategyAssignmentsParam.getUserid() == null) {
			listRestrictionStrategyAssignmentsParam.setUserid(-2);
		}
		if (listRestrictionStrategyAssignmentsParam.getOrganizationid() == null) {
			listRestrictionStrategyAssignmentsParam.setOrganizationid(-2);
		}
		if (listRestrictionStrategyAssignmentsParam.getGroupid() == null) {
			listRestrictionStrategyAssignmentsParam.setGroupid(-2);
		}
		if (listRestrictionStrategyAssignmentsParam.getResourceid() < -1
				|| listRestrictionStrategyAssignmentsParam.getStrategyid() < -1
				|| listRestrictionStrategyAssignmentsParam.getUserid() < -2
				|| listRestrictionStrategyAssignmentsParam.getOrganizationid() < -2
				|| listRestrictionStrategyAssignmentsParam.getGroupid() < -2) {
			response.getHead().setError(BAD_REQUEST);
			return response;
		}
		fixListParam(listRestrictionStrategyAssignmentsParam,
				"idrestrictionstrategyassignment");
		response.setBody(new RestrictionStrategyAssignmentList());
		response.getBody().copyFrom(listRestrictionStrategyAssignmentsParam);
		int[] amountContainer = new int[1];
		// if (listRestrictionStrategyAssignmentsParam.getUserid() == -1
		// && listRestrictionStrategyAssignmentsParam.getOrganizationid() < -1
		// && listRestrictionStrategyAssignmentsParam.getGroupid() < -1) {
		// response.getBody().setList(resourceFacade.findUserAssignments(
		// listRestrictionStrategyAssignmentsParam.getResourcetype(),
		// listRestrictionStrategyAssignmentsParam.getResourceid(),
		// listRestrictionStrategyAssignmentsParam,
		// amountContainer));
		// } else if (listRestrictionStrategyAssignmentsParam.getUserid() < -1
		// && listRestrictionStrategyAssignmentsParam.getOrganizationid() == -1
		// && listRestrictionStrategyAssignmentsParam.getGroupid() < -1) {
		// response.getBody().setList(resourceFacade.findOrganizationAssignments(
		// listRestrictionStrategyAssignmentsParam.getResourcetype(),
		// listRestrictionStrategyAssignmentsParam.getResourceid(),
		// listRestrictionStrategyAssignmentsParam,
		// amountContainer));
		// } else if (listRestrictionStrategyAssignmentsParam.getUserid() < -1
		// && listRestrictionStrategyAssignmentsParam.getOrganizationid() < -1
		// && listRestrictionStrategyAssignmentsParam.getGroupid() == -1) {
		// response.getBody().setList(resourceFacade.findGroupAssignments(
		// listRestrictionStrategyAssignmentsParam.getResourcetype(),
		// listRestrictionStrategyAssignmentsParam.getResourceid(),
		// listRestrictionStrategyAssignmentsParam,
		// amountContainer));
		// } else {
		response.getBody()
				.setList(
						desktopFacade.findRestrictionStrategyAssignments(
								listRestrictionStrategyAssignmentsParam
										.getResourcetype(),
								listRestrictionStrategyAssignmentsParam
										.getResourceid(),
								listRestrictionStrategyAssignmentsParam
										.getStrategyid(),
								listRestrictionStrategyAssignmentsParam
										.getDomainid(),
								listRestrictionStrategyAssignmentsParam
										.getTargettype(),
								listRestrictionStrategyAssignmentsParam,
								amountContainer));
		// }
		response.getBody().setAmount(amountContainer[0]);
		return response;
	}

	public StrategyAssignmentResponse assignRestrictionStrategy(
			RestrictionStrategyAssignmentParam restrictionStrategyAssignmentParam) {
		StrategyAssignmentResponse response = new StrategyAssignmentResponse();
		int error = this
				.validationRestrictionStrategyParam(restrictionStrategyAssignmentParam);
		if (error != NO_ERRORS) {
			response.getHead().setError(error);
			return response;
		}
		for (int i = 0; i < restrictionStrategyAssignmentParam.getTargettype().length; i++) {
			int targettype = restrictionStrategyAssignmentParam.getTargettype()[i];
			int targetid = restrictionStrategyAssignmentParam.getTargetid()[i];
			error = this.validationRestrictionStrategyAssignment(targettype,
					targetid);
			if (error != NO_ERRORS) {
				response.addStatus(new StrategyAssignment(targettype, targetid,
						Services.err.error(error)));
				continue;
			}
			try {
				error = desktopFacade.assignRestrictionStrategy(
						restrictionStrategyAssignmentParam.getStrategyid(),
						targettype, targetid);
			} catch (CommonException e) {
				error = CONFLICT;
			}
			if (error != NO_ERRORS) {
				response.addStatus(new StrategyAssignment(targettype, targetid,
						Services.err.error(error)));
				continue;
			}
		}
		return response;
	}

	public StrategyAssignmentResponse unassignRestrictionStrategy(
			RestrictionStrategyAssignmentParam restrictionStrategyAssignmentParam) {
		StrategyAssignmentResponse response = new StrategyAssignmentResponse();
		int error = this
				.validationRestrictionStrategyParam(restrictionStrategyAssignmentParam);
		if (error != NO_ERRORS) {
			response.getHead().setError(error);
			return response;
		}
		for (int i = 0; i < restrictionStrategyAssignmentParam.getTargettype().length; i++) {
			int targettype = restrictionStrategyAssignmentParam.getTargettype()[i];
			int targetid = restrictionStrategyAssignmentParam.getTargetid()[i];
			error = this.validationRestrictionStrategyAssignment(targettype,
					targetid);
			if (error != NO_ERRORS) {
				response.addStatus(new StrategyAssignment(targettype, targetid,
						Services.err.error(error)));
				continue;
			}
			try {
				error = desktopFacade.unassignRestrictionStrategy(
						restrictionStrategyAssignmentParam.getStrategyid(),
						targettype, targetid);
			} catch (CommonException e) {
				error = CONFLICT;
			}
			if (error != NO_ERRORS) {
				response.addStatus(new StrategyAssignment(targettype, targetid,
						Services.err.error(error)));
				continue;
			}
		}
		return response;
	}

	public RestrictionStrategyListResponse deleteRestrictionStrategy(
			RestrictionStrategyListParam restrictionStrategyAssignmentParam) {
		RestrictionStrategyListResponse response = new RestrictionStrategyListResponse();
		for (int groupid : restrictionStrategyAssignmentParam.getStrategyid()) {
			if (groupid < 1) {
				response.addStatus(new RestrictionStrategyAndError(groupid,
						Services.err.error(BAD_REQUEST)));
				continue;
			}
			int error = NO_ERRORS;
			try {
				error = desktopFacade.deleteRestrictionStrategy(groupid);
			} catch (CommonException e) {
				response.addStatus(new RestrictionStrategyAndError(groupid,
						Services.err.error(e.getError())));
				continue;
			}
			if (numberNotEquals(error, NO_ERRORS)) {
				response.addStatus(new RestrictionStrategyAndError(groupid,
						Services.err.error(error)));
				continue;
			}
		}
		return response;
	}

	public AddDesktopByIPAddressResponse verifyDesktopByIPAddress(
			DesktopPoolAndIPAddress desktopPoolAndIPAddress) {
		AddDesktopByIPAddressResponse response = new AddDesktopByIPAddressResponse();
		for (String ipaddress : desktopPoolAndIPAddress.getIpaddress()) {
			if (ipaddress == null) {
				response.addStatus(new IPAddressAndError(ipaddress, BAD_REQUEST));
				continue;
			}
			//避免重复提交 修改：maxiaochao bugid：
			if(Session.getCache("AddDesktopByIPAddressResponse"+ipaddress)==null){
				Session.setCache("AddDesktopByIPAddressResponse"+ipaddress, ipaddress,3,TimeUnit.SECONDS);
			}else{
				response.addStatus(new IPAddressAndError(ipaddress, BAD_REQUEST));
				continue;
			}
			try {
				desktopFacade.verifyDesktopByIPAddress(ipaddress);
			} catch (CommonException e) {
				response.addStatus(new IPAddressAndError(ipaddress, e
						.getError()));
				
				continue;
			}

		}
		return response;
	}

	public AddDesktopByIPAddressResponse addDesktopByIPAddress(
			DesktopPoolAndIPAddress desktopPoolAndIPAddress) {
		AddDesktopByIPAddressResponse response = new AddDesktopByIPAddressResponse();
		if (desktopPoolAndIPAddress.getDesktoppoolid() < 1) {
			response.getHead().setError(BAD_REQUEST);
			return response;
		}
		boolean error = false;
		for (String ipaddress : desktopPoolAndIPAddress.getIpaddress()) {
			if (ipaddress == null) {
				response.addStatus(new IPAddressAndError(ipaddress, BAD_REQUEST));
				continue;
			}
		
			try {
				response.addStatus(new IPAddressAndError(desktopFacade
						.addDesktopByIPAddress(
								desktopPoolAndIPAddress.getDesktoppoolid(),
								ipaddress)));
			} catch (CommonException e) {
				error = true;
				response.addStatus(new IPAddressAndError(ipaddress,
						Services.err.error(e.getError())));
				continue;
			}
		}
		if (!error) {
			response.getHead().setError(CommonException.NO_ERRORS);
		}
		return response;
	}

	public NullResponse resizeDesktopPool(DesktopPoolAndSize desktopPoolAndSize) {
		NullResponse response = new NullResponse();
		if (desktopPoolAndSize.getDesktoppoolid() < 1
				|| desktopPoolAndSize.getMaxdesktops() == 0) {
			response.getHead().setError(BAD_REQUEST);
			return response;
		}
		int[] errorContainer = new int[] { NO_ERRORS };
		int jobId = desktopPoolFacade.resizeDesktopPool(
				desktopPoolAndSize.getDesktoppoolid(),
				desktopPoolAndSize.getMaxdesktops(), errorContainer);
		desktopPoolFacade.notifyRresizing(
				desktopPoolAndSize.getDesktoppoolid(),
				desktopPoolAndSize.getMaxdesktops());
		if (numberNotEquals(errorContainer[0], NO_ERRORS)) {
			response.getHead().setError(errorContainer[0]);
			return response;
		}
		if (jobId == -1) {
			response.getHead().setError(UNKNOWN);
			return response;
		}
		response.getHead().setJobid(jobId);
		return response;
	}

	public DesktopListResponse deleteDesktops(DesktopIds desktopIds) {
		DesktopListResponse response = new DesktopListResponse();
		if (desktopIds.getDesktopid() == null
				|| desktopIds.getDesktopid().length < 1) {
			response.getHead().setError(BAD_REQUEST);
			return response;
		}
		for (int desktopid : desktopIds.getDesktopid()) {
			if (desktopid < 1) {
				response.getHead().setError(MULTI_STATUS);
				response.addStatus(new DesktopAndError(desktopid, Services.err
						.error(BAD_REQUEST)));
				continue;
			}
		}
		int error = desktopPoolFacade.deleteDesktops(desktopIds.getDesktopid(),
				desktopIds.getForce() != 0);
		if (error != NO_ERRORS) {
			response.getHead().setError(error);
		}
		return response;
	}

	public void setDesktopFacade(DesktopFacade desktopFacade) {
		this.desktopFacade = desktopFacade;
	}

	public void setDesktopPoolFacade(DesktopPoolFacade desktopPoolFacade) {
		this.desktopPoolFacade = desktopPoolFacade;
	}

	public void setCloudManagerFacade(CloudManagerFacade cloudManagerFacade) {
		this.cloudManagerFacade = cloudManagerFacade;
	}

	public void setResourceFacade(ResourceFacade resourceFacade) {
		this.resourceFacade = resourceFacade;
	}

	public void setAsyncJobFacade(AsyncJobFacade asyncJobFacade) {
		this.asyncJobFacade = asyncJobFacade;
	}

	public void setConnectionFacade(ConnectionFacade connectionFacade) {
		this.connectionFacade = connectionFacade;
	}

	private int validationUserVolume(UserVolume userVolume) {
		return new Validater<UserVolume>() {
			@Override
			public int validationAndFix(UserVolume userVolume) {
				if (userVolume.getUserid() < 0
						|| userVolume.getCloudmanagerid() < 1
						|| userVolume.getSize() < 1) {
					return BAD_REQUEST;
				}
				return NO_ERRORS;
			}
		}.validationAndFix(userVolume);
	}

	private int validationAndFixDesktopPool(DesktopPoolEntity desktopPool,
			final boolean forUpdate) {
		return new Validater<DesktopPoolEntity>() {
			@Override
			public int validationAndFix(DesktopPoolEntity desktopPool) {
				if (forUpdate) {
					if (desktopPool.getIddesktoppool() == null
							|| desktopPool.getIddesktoppool() < 1) {
						return BAD_REQUEST;
					}
					if (desktopPool.getPoolname() != null) {
						desktopPool.setPoolname(desktopPool.getPoolname()
								.trim());
						if (desktopPool.getPoolname().length() < 1) {
							return BAD_REQUEST;
						}
					}
					if (desktopPool.getNotes() != null) {
						desktopPool.setNotes(desktopPool.getNotes().trim());
					}
					if (!allInBound(100, desktopPool.getPoolname(),
							desktopPool.getNotes())) {
						return BAD_REQUEST;
					}
				} else {
					desktopPool.setIddesktoppool(null);
					desktopPool.setPoolname(nullToBlankString(desktopPool
							.getPoolname()));
					desktopPool.setVmnamepatterrn(nullToBlankString(desktopPool
							.getVmnamepatterrn()));
					desktopPool
							.setComputernamepattern(nullToBlankString(desktopPool
									.getComputernamepattern()));
					if (desktopPool.getPoolname().length() < 1) {
						return BAD_REQUEST;
					}
					desktopPool.setDomainbinddn(null);
					desktopPool.setDomainbindpass(null);
					desktopPool.setDomainname(null);
					if (numberNotEquals(desktopPool.getVmsource(),
							DESKTOP_POOL_SOURCE_AUTO)
							&& numberNotEquals(desktopPool.getVmsource(),
									DESKTOP_POOL_SOURCE_MANUAL)) {
						return BAD_REQUEST;
					}
					if (numberNotEquals(desktopPool.getAssignment(),
							DESKTOP_POOL_ASSIGNMENT_FLOATING)
							&& numberNotEquals(desktopPool.getAssignment(),
									DESKTOP_POOL_ASSIGNMENT_DEDICATED)) {
						return BAD_REQUEST;
					}
					if (numberNotEquals(desktopPool.getVmsource(),
							DESKTOP_POOL_SOURCE_MANUAL)
							&& desktopPool.getCloudmanagerid() < 1) {
						return BAD_REQUEST;
					}
					if (numberEquals(desktopPool.getVmsource(),
							DESKTOP_POOL_SOURCE_AUTO)
							&& desktopPool.getTemplateid() == null) {
						return BAD_REQUEST;
					}
					if (numberEquals(desktopPool.getVmsource(),
							DESKTOP_POOL_SOURCE_MANUAL)
							&& desktopPool.getTemplateid() != null) {
						return BAD_REQUEST;
					}
					if (desktopPool.getMaxdesktops() < 1) {
						return BAD_REQUEST;
					}
					if (desktopPool.getSparedesktops() < 0) {
						return BAD_REQUEST;
					}
					if (desktopPool.getSparedesktops() > desktopPool
							.getMaxdesktops()) {
						desktopPool.setSparedesktops(desktopPool
								.getMaxdesktops());
					}
					desktopPool.setNotes(nullToBlankString(desktopPool
							.getNotes()));
					if (desktopPool.getTemplateid() != null
							&& !allInBound(255, desktopPool.getTemplateid())) {
						return BAD_REQUEST;
					}
					if (!allInBound(100, desktopPool.getPoolname(),
							desktopPool.getVmnamepatterrn(),
							desktopPool.getComputernamepattern(),
							desktopPool.getNotes())) {
						return BAD_REQUEST;
					}
					if (desktopPool.getStrategyid() == null) {
						desktopPool.setStrategyid(0);// FIXME
					}
					if (desktopPool.getUnassignmentdelay() < 0) {
						return BAD_REQUEST;
					}
				}
				return NO_ERRORS;
			}
		}.validationAndFix(desktopPool);
	}

	private int validationAndFixCloudManager(
			CloudManagerEntity cloudManagerEntity) {
		return new Validater<CloudManagerEntity>() {
			@Override
			public int validationAndFix(CloudManagerEntity cloudManagerEntity) {
				cloudManagerEntity.setIdcloudmanager(null);
				cloudManagerEntity.setCloudname(nullToBlankString(
						cloudManagerEntity.getCloudname()).trim());
				cloudManagerEntity.setUsername(nullToBlankString(
						cloudManagerEntity.getUsername()).trim());
				cloudManagerEntity.setPassword(nullToBlankString(
						cloudManagerEntity.getPassword()).trim());
				cloudManagerEntity.setDomain(nullToBlankString(
						cloudManagerEntity.getDomain()).trim());
				cloudManagerEntity.setBaseurl(nullToBlankString(
						cloudManagerEntity.getBaseurl()).trim());
				cloudManagerEntity.setNotes(nullToBlankString(
						cloudManagerEntity.getNotes()).trim());
				cloudManagerEntity.setClouddrivername(nullToBlankString(
						cloudManagerEntity.getClouddrivername()).trim());
				if (cloudManagerEntity.getCloudname().length() < 1
						|| cloudManagerEntity.getUsername().length() < 1
						|| cloudManagerEntity.getPassword().length() < 1
						|| cloudManagerEntity.getBaseurl().length() < 1
						|| cloudManagerEntity.getClouddrivername().length() < 1) {
					return BAD_REQUEST;
				}
				if (!allInBound(100, cloudManagerEntity.getCloudname(),
						cloudManagerEntity.getUsername(),
						cloudManagerEntity.getPassword(),
						cloudManagerEntity.getDomain(),
						cloudManagerEntity.getNotes())) {
					return BAD_REQUEST;
				}
				if (!allInBound(200, cloudManagerEntity.getBaseurl())) {
					return BAD_REQUEST;
				}
				if (cloudManagerEntity.getBaseurl().endsWith("/")) {
					cloudManagerEntity
							.setBaseurl(cloudManagerEntity.getBaseurl()
									.substring(
											0,
											cloudManagerEntity.getBaseurl()
													.length() - 1));
				}
				return NO_ERRORS;
			}
		}.validationAndFix(cloudManagerEntity);
	}

	private int validationAndFixRestrictionStrategy(
			RestrictionStrategy restrictionStrategy, final boolean forUpdate) {
		return new Validater<RestrictionStrategy>() {
			@Override
			public int validationAndFix(RestrictionStrategy restrictionStrategy) {
				if (forUpdate) {
					if (restrictionStrategy.getStrategyid() == null
							|| restrictionStrategy.getStrategyid() < 0) {
						return BAD_REQUEST;
					}
					restrictionStrategy
							.setIdrestrictionstrategy(restrictionStrategy
									.getStrategyid());
				} else {
					restrictionStrategy.setIdrestrictionstrategy(null);
					restrictionStrategy
							.setStrategyname(nullToBlankString(restrictionStrategy
									.getStrategyname()));
					if (restrictionStrategy.getStrategyname().length() < 1) {
						return BAD_REQUEST;
					}
					if (restrictionStrategy.getDisk() == null
							|| restrictionStrategy.getClipboard() == null
							|| restrictionStrategy.getAudio() == null
							|| restrictionStrategy.getUservolume() == null) {
						return BAD_REQUEST;
					}
					if (numberNotEquals(restrictionStrategy.getDisk(),
							RestrictionStrategy.UNDEFINED)
							&& numberNotEquals(restrictionStrategy.getDisk(),
									RestrictionStrategy.DISABLED)
							&& numberNotEquals(restrictionStrategy.getDisk(),
									RestrictionStrategy.DISK_READ_ONLY)
							&& numberNotEquals(restrictionStrategy.getDisk(),
									RestrictionStrategy.DISK_READ_WRITE)) {
						return BAD_REQUEST;
					}
					if (numberNotEquals(restrictionStrategy.getClipboard(),
							RestrictionStrategy.UNDEFINED)
							&& numberNotEquals(
									restrictionStrategy.getClipboard(),
									RestrictionStrategy.DISABLED)
							&& numberNotEquals(
									restrictionStrategy.getClipboard(),
									RestrictionStrategy.CLIPBOARD_UPSTREAM)
							&& numberNotEquals(
									restrictionStrategy.getClipboard(),
									RestrictionStrategy.CLIPBOARD_DOWNSTREAM)
							&& numberNotEquals(
									restrictionStrategy.getClipboard(),
									RestrictionStrategy.CLIPBOARD_BISTREAM)) {
						return BAD_REQUEST;
					}
					if (numberNotEquals(restrictionStrategy.getAudio(),
							RestrictionStrategy.UNDEFINED)
							&& numberNotEquals(restrictionStrategy.getAudio(),
									RestrictionStrategy.DISABLED)
							&& numberNotEquals(restrictionStrategy.getAudio(),
									RestrictionStrategy.AUDIO_DOWNSTREAM)
							&& numberNotEquals(restrictionStrategy.getAudio(),
									RestrictionStrategy.AUDIO_BISTREAM)) {
						return BAD_REQUEST;
					}
					if (numberNotEquals(restrictionStrategy.getUservolume(),
							RestrictionStrategy.UNDEFINED)
							&& numberNotEquals(
									restrictionStrategy.getUservolume(),
									RestrictionStrategy.DISABLED)
							&& numberNotEquals(
									restrictionStrategy.getUservolume(),
									RestrictionStrategy.USER_VOLUME_ALLOWED)) {
						return BAD_REQUEST;
					}
				}
				if (!allInBound(100, restrictionStrategy.getStrategyname())) {
					return BAD_REQUEST;
				}
				if (restrictionStrategy.getNotes() == null) {
					restrictionStrategy.setNotes("");
				}
				return NO_ERRORS;
			}
		}.validationAndFix(restrictionStrategy);
	}

	private int validationRestrictionStrategyParam(
			RestrictionStrategyAssignmentParam restrictionStrategyAssignmentParam) {
		if (restrictionStrategyAssignmentParam.getStrategyid() < 0) {
			return BAD_REQUEST;
		}
		if (restrictionStrategyAssignmentParam.getTargettype() == null
				|| restrictionStrategyAssignmentParam.getTargetid() == null
				|| restrictionStrategyAssignmentParam.getTargettype().length < 1
				|| restrictionStrategyAssignmentParam.getTargetid().length < 1
				|| restrictionStrategyAssignmentParam.getTargettype().length != restrictionStrategyAssignmentParam
						.getTargetid().length) {
			return BAD_REQUEST;
		}
		return NO_ERRORS;
	}

	private int validationRestrictionStrategyAssignment(int targettype,
			int targetid) {
		if (numberNotEquals(
				targettype,
				RestrictionStrategyAssignment.RESTRICTION_STRATEGY_ASSIGNMENT_TARGET_TYPE_GROUP)
				&& numberNotEquals(
						targettype,
						RestrictionStrategyAssignment.RESTRICTION_STRATEGY_ASSIGNMENT_TARGET_TYPE_ORGANIZATION)
				&& numberNotEquals(
						targettype,
						RestrictionStrategyAssignment.RESTRICTION_STRATEGY_ASSIGNMENT_TARGET_TYPE_RESOURCE)
				&& numberNotEquals(
						targettype,
						RestrictionStrategyAssignment.RESTRICTION_STRATEGY_ASSIGNMENT_TARGET_TYPE_USER)) {
			return BAD_REQUEST;
		}
		if (numberEquals(
				targettype,
				RestrictionStrategyAssignment.RESTRICTION_STRATEGY_ASSIGNMENT_TARGET_TYPE_USER)
				|| numberEquals(
						targettype,
						RestrictionStrategyAssignment.RESTRICTION_STRATEGY_ASSIGNMENT_TARGET_TYPE_ORGANIZATION)) {
			if (targetid < 0) {
				return BAD_REQUEST;
			}
		} else {
			if (targetid < 1) {
				return BAD_REQUEST;
			}
		}
		return NO_ERRORS;
	}

	@SuppressWarnings("unchecked")
	private Object targetToPool(TargetAndDesktopPool targetAndDesktopPool,
			boolean unassigning) {
		int targetType = (targetAndDesktopPool instanceof GroupAndDesktopPool) ? RESOURCE_VISITOR_TYPE_GROUP
				: (targetAndDesktopPool instanceof OrganizationAndDesktopPool ? RESOURCE_VISITOR_TYPE_ORGANIZATION
						: RESOURCE_VISITOR_TYPE_USER);
		@SuppressWarnings("rawtypes")
		MultiStatusResponse response = targetType == RESOURCE_VISITOR_TYPE_GROUP ? new GroupAndPoolListResponse()
				: (targetType == RESOURCE_VISITOR_TYPE_ORGANIZATION ? new OrganizationAndPoolListResponse()
						: new UserAndPoolListResponse());
		if (targetAndDesktopPool.getTarget() == null
				|| targetAndDesktopPool.getTarget().length < 1
				|| targetAndDesktopPool.getDesktoppoolid() == null
				|| targetAndDesktopPool.getDesktoppoolid().length < 1) {
			response.getHead().setError(BAD_REQUEST);
			return response;
		}
		if (targetAndDesktopPool.getTarget().length > 1
				&& targetAndDesktopPool.getDesktoppoolid().length > 1) {
			response.getHead().setError(BAD_REQUEST);
			return response;
		}
		boolean force = targetAndDesktopPool.getForce() != 0;
		for (int target : targetAndDesktopPool.getTarget()) {
			if ((targetType == RESOURCE_VISITOR_TYPE_GROUP && target < 1)
					|| (targetType != RESOURCE_VISITOR_TYPE_GROUP && target < 0)) {
				response.getHead().setError(MULTI_STATUS);
				for (int desktoppoolid : targetAndDesktopPool
						.getDesktoppoolid()) {
					response.addStatus(targetType == RESOURCE_VISITOR_TYPE_GROUP ? new GroupAndPoolAndError(
							target, desktoppoolid, Services.err
									.error(BAD_REQUEST))
							: (targetType == RESOURCE_VISITOR_TYPE_ORGANIZATION ? new OrganizationAndPoolAndError(
									target, desktoppoolid, Services.err
											.error(BAD_REQUEST))
									: new UserAndPoolAndError(target,
											desktoppoolid, Services.err
													.error(BAD_REQUEST))));
				}
				continue;
			}
			for (int i = 0; i < targetAndDesktopPool.getDesktoppoolid().length; i++) {
				int desktoppoolid = targetAndDesktopPool.getDesktoppoolid()[i];
				if (desktoppoolid < 1) {
					response.addStatus(targetType == RESOURCE_VISITOR_TYPE_GROUP ? new GroupAndPoolAndError(
							target, desktoppoolid, Services.err
									.error(BAD_REQUEST))
							: (targetType == RESOURCE_VISITOR_TYPE_ORGANIZATION ? new OrganizationAndPoolAndError(
									target, desktoppoolid, Services.err
											.error(BAD_REQUEST))
									: new UserAndPoolAndError(target,
											desktoppoolid, Services.err
													.error(BAD_REQUEST))));
					continue;
				}
				int error = NO_ERRORS;
				if (unassigning) {
					error = targetType == RESOURCE_VISITOR_TYPE_GROUP ? desktopPoolFacade
							.unassignDesktopPoolToGroup(desktoppoolid, target,
									force)
							: (targetType == RESOURCE_VISITOR_TYPE_ORGANIZATION ? desktopPoolFacade
									.unassignDesktopPoolToOrganization(
											desktoppoolid, target, force)
									: desktopPoolFacade
											.unassignDesktopPoolToUser(
													desktoppoolid, target,
													force));
				} else {
					try {
						error = targetType == RESOURCE_VISITOR_TYPE_GROUP ? desktopPoolFacade
								.assignDesktopPoolToGroup(desktoppoolid, target)
								: (targetType == RESOURCE_VISITOR_TYPE_ORGANIZATION ? desktopPoolFacade
										.assignDesktopPoolToOrganization(
												desktoppoolid, target)
										: desktopPoolFacade
												.assignDesktopPoolToUser(
														desktoppoolid, target));
					} catch (CommonException e) {
						response.addStatus(targetType == RESOURCE_VISITOR_TYPE_GROUP ? new GroupAndPoolAndError(
								target, desktoppoolid, Services.err.error(e
										.getError()))
								: (targetType == RESOURCE_VISITOR_TYPE_ORGANIZATION ? new OrganizationAndPoolAndError(
										target, desktoppoolid, Services.err
												.error(e.getError()))
										: new UserAndPoolAndError(target,
												desktoppoolid, Services.err
														.error(e.getError()))));
						continue;
					}
					if (desktopPoolFacade.deleteAssignmentIfConflictInDomain(
							targetType, target, desktoppoolid)) {
						response.addStatus(targetType == RESOURCE_VISITOR_TYPE_GROUP ? new GroupAndPoolAndError(
								target, desktoppoolid, Services.err
										.error(CONFLICT))
								: (targetType == RESOURCE_VISITOR_TYPE_ORGANIZATION ? new OrganizationAndPoolAndError(
										target, desktoppoolid, Services.err
												.error(CONFLICT))
										: new UserAndPoolAndError(target,
												desktoppoolid, Services.err
														.error(CONFLICT))));
						continue;
					}
					int desktopid = (targetType == RESOURCE_VISITOR_TYPE_USER
							&& targetAndDesktopPool.getDesktopid() != null && i < targetAndDesktopPool
							.getDesktopid().length) ? targetAndDesktopPool
							.getDesktopid()[i] : 0;
					if (desktopid > 0) {
						try {
							desktopFacade.preassignDesktop(desktopid, target);
						} catch (CommonException e) {
							// TODO Auto-generated catch block
							error = e.getError();
						}
					}
				}
				if (error != NO_ERRORS) {
					response.addStatus(targetType == RESOURCE_VISITOR_TYPE_GROUP ? new GroupAndPoolAndError(
							target, desktoppoolid, error)
							: (targetType == RESOURCE_VISITOR_TYPE_ORGANIZATION ? new OrganizationAndPoolAndError(
									target, desktoppoolid, error)
									: new UserAndPoolAndError(target,
											desktoppoolid, error)));
				}
			}
		}
		return response;
	}

	@XmlRootElement(name = "param")
	public static class DesktopPoolAndIPAddress implements Serializable {

		private static final long serialVersionUID = 1L;

		private int desktoppoolid;
		private String[] ipaddress;

		public int getDesktoppoolid() {
			return desktoppoolid;
		}

		public void setDesktoppoolid(int desktoppoolid) {
			this.desktoppoolid = desktoppoolid;
		}

		public String[] getIpaddress() {
			return ipaddress;
		}

		public void setIpaddress(String[] ipaddress) {
			this.ipaddress = ipaddress;
		}

	}

	@XmlRootElement(name = "param")
	public static class DesktopIds implements Serializable {

		private static final long serialVersionUID = 1L;

		private int[] desktopid;
		private int force;

		public int[] getDesktopid() {
			return desktopid;
		}

		public void setDesktopid(int[] desktopid) {
			this.desktopid = desktopid;
		}

		public int getForce() {
			return force;
		}

		public void setForce(int force) {
			this.force = force;
		}

	}

	@XmlRootElement(name = "param")
	public static class DesktopPoolAndSize implements Serializable {

		private static final long serialVersionUID = 1L;

		private int desktoppoolid;
		private int maxdesktops;

		public int getDesktoppoolid() {
			return desktoppoolid;
		}

		public void setDesktoppoolid(int desktoppoolid) {
			this.desktoppoolid = desktoppoolid;
		}

		public int getMaxdesktops() {
			return maxdesktops;
		}

		public void setMaxdesktops(int maxdesktops) {
			this.maxdesktops = maxdesktops;
		}

	}

	@XmlRootElement(name = "param")
	public static class UserIdAndVolumeIdParam implements Serializable {

		private static final long serialVersionUID = 1L;

		private int userid;
		private Integer volumeid;

		public int getUserid() {
			return userid;
		}

		public void setUserid(int userid) {
			this.userid = userid;
		}

		public Integer getVolumeid() {
			return volumeid;
		}

		public void setVolumeid(Integer volumeid) {
			this.volumeid = volumeid;
		}

	}

	@XmlRootElement(name = "listParam")
	public static class ListTemplatesParam extends PagingInfo implements
			Serializable {

		private static final long serialVersionUID = 1L;
		private boolean link = true;

		public boolean isLink() {
			return link;
		}

		public void setLink(boolean link) {
			this.link = link;
		}

		private int cloudmanagerid;

		public int getCloudmanagerid() {
			return cloudmanagerid;
		}

		public void setCloudmanagerid(int cloudmanagerid) {
			this.cloudmanagerid = cloudmanagerid;
		}

	}

	@XmlRootElement(name = "listParam")
	public static class ListDesktopsParam extends PagingInfo implements
			Serializable {

		private static final long serialVersionUID = 1L;

		private int desktoppoolid;
		private int desktopid;

		public int getDesktoppoolid() {
			return desktoppoolid;
		}

		public void setDesktoppoolid(int desktoppoolid) {
			this.desktoppoolid = desktoppoolid;
		}

		public int getDesktopid() {
			return desktopid;
		}

		public void setDesktopid(int desktopid) {
			this.desktopid = desktopid;
		}

	}

	@XmlRootElement(name = "iddesktoppool")
	public static class DesktopPoolIdParam implements Serializable {

		private static final long serialVersionUID = 1L;

		private int[] iddesktoppool;
		private int force;

		public int[] getIddesktoppool() {
			return iddesktoppool;
		}

		public void setIddesktoppool(int[] iddesktoppool) {
			this.iddesktoppool = iddesktoppool;
		}

		public int getForce() {
			return force;
		}

		public void setForce(int force) {
			this.force = force;
		}

	}

	@XmlRootElement(name = "desktopid")
	public static class DesktopIdParam implements Serializable {

		private static final long serialVersionUID = 1L;

		private int desktopid;

		public int getDesktopid() {
			return desktopid;
		}

		public void setDesktopid(int desktopid) {
			this.desktopid = desktopid;
		}

	}

	@XmlRootElement(name = "idcloudmanager")
	public static class CloudManagerEntityIdParam implements Serializable {

		private static final long serialVersionUID = 1L;

		private int idcloudmanager;

		public int getIdcloudmanager() {
			return idcloudmanager;
		}

		public void setIdcloudmanager(int idcloudmanager) {
			this.idcloudmanager = idcloudmanager;
		}

	}

	@XmlRootElement(name = "jobid")
	public static class AsyncJobIdParam implements Serializable {

		private static final long serialVersionUID = 1L;

		private int jobid;

		public int getJobid() {
			return jobid;
		}

		public void setJobid(int jobid) {
			this.jobid = jobid;
		}

	}

	@XmlRootElement(name = "listParam")
	public static class ListDesktopPoolsParam extends PagingInfo implements
			Serializable {

		private static final long serialVersionUID = 1L;

		private int desktoppoolid;
		private int cloudmanagerid;

		public int getDesktoppoolid() {
			return desktoppoolid;
		}

		public void setDesktoppoolid(int desktoppoolid) {
			this.desktoppoolid = desktoppoolid;
		}

		public int getCloudmanagerid() {
			return cloudmanagerid;
		}

		public void setCloudmanagerid(int cloudmanagerid) {
			this.cloudmanagerid = cloudmanagerid;
		}

	}

	@XmlRootElement(name = "listParam")
	public static class ListCloudManagersParam extends PagingInfo implements
			Serializable {

		private static final long serialVersionUID = 1L;

		private int idcloudmanager;
		private String clouddrivername;

		public int getIdcloudmanager() {
			return idcloudmanager;
		}

		public void setIdcloudmanager(int idcloudmanager) {
			this.idcloudmanager = idcloudmanager;
		}

		public String getClouddrivername() {
			return clouddrivername;
		}

		public void setClouddrivername(String clouddrivername) {
			this.clouddrivername = clouddrivername;
		}

	}

	@XmlRootElement(name = "listParam")
	public static class ListAsyncJobsParam extends PagingInfo implements
			Serializable {

		private static final long serialVersionUID = 1L;

		private String cmd;
		private int jobstatus;

		public String getCmd() {
			return cmd;
		}

		public void setCmd(String cmd) {
			this.cmd = cmd;
		}

		public int getJobstatus() {
			return jobstatus;
		}

		public void setJobstatus(int jobstatus) {
			this.jobstatus = jobstatus;
		}

	}

	@XmlRootElement(name = "listParam")
	public static class ListConnectionsParam extends PagingInfo implements
			Serializable {

		private static final long serialVersionUID = 1L;

		private int sessionid;
		private int resourcetype;
		private int resourceid;
		private String brokername;

		public int getSessionid() {
			return sessionid;
		}

		public void setSessionid(int sessionid) {
			this.sessionid = sessionid;
		}

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

		public String getBrokername() {
			return brokername;
		}

		public void setBrokername(String brokername) {
			this.brokername = brokername;
		}

	}

	@XmlRootElement(name = "listParam")
	public static class ListAssignmentsParam extends PagingInfo implements
			Serializable {

		private static final long serialVersionUID = 1L;

		private int resourcetype;
		private int resourceid;
		private Integer userid;
		private Integer organizationid;
		private Integer groupid;

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

		public Integer getUserid() {
			return userid;
		}

		public void setUserid(Integer userid) {
			this.userid = userid;
		}

		public Integer getOrganizationid() {
			return organizationid;
		}

		public void setOrganizationid(Integer organizationid) {
			this.organizationid = organizationid;
		}

		public Integer getGroupid() {
			return groupid;
		}

		public void setGroupid(Integer groupid) {
			this.groupid = groupid;
		}

	}

	@XmlRootElement(name = "restrictionstrategyid")
	public static class RestrictionStrategyListParam implements Serializable {

		private static final long serialVersionUID = 1L;

		private int[] strategyid;

		public int[] getStrategyid() {
			return strategyid;
		}

		public void setStrategyid(int[] strategyid) {
			this.strategyid = strategyid;
		}

	}

	@XmlRootElement(name = "listParam")
	public static class ListRestrictionStrategyAssignmentsParam extends
			PagingInfo implements Serializable {

		private static final long serialVersionUID = 1L;

		private int strategyid;
		private Integer resourcetype;
		private int resourceid;
		private Integer userid;
		private Integer organizationid;
		private Integer groupid;
		private int domainid;
		private int targettype;

		public int getStrategyid() {
			return strategyid;
		}

		public void setStrategyid(int strategyid) {
			this.strategyid = strategyid;
		}

		public Integer getResourcetype() {
			return resourcetype;
		}

		public void setResourcetype(Integer resourcetype) {
			this.resourcetype = resourcetype;
		}

		public int getResourceid() {
			return resourceid;
		}

		public void setResourceid(int resourceid) {
			this.resourceid = resourceid;
		}

		public Integer getUserid() {
			return userid;
		}

		public void setUserid(Integer userid) {
			this.userid = userid;
		}

		public Integer getOrganizationid() {
			return organizationid;
		}

		public void setOrganizationid(Integer organizationid) {
			this.organizationid = organizationid;
		}

		public Integer getGroupid() {
			return groupid;
		}

		public void setGroupid(Integer groupid) {
			this.groupid = groupid;
		}

		public int getDomainid() {
			return domainid;
		}

		public void setDomainid(int domainid) {
			this.domainid = domainid;
		}

		public int getTargettype() {
			return targettype;
		}

		public void setTargettype(int targettype) {
			this.targettype = targettype;
		}

	}

	@XmlRootElement(name = "listParam")
	public static class ListRestrictionStrategiesParam extends PagingInfo
			implements Serializable {

		private static final long serialVersionUID = 1L;

	}

	@XmlRootElement(name = "param")
	public static class RestrictionStrategyAssignmentParam implements
			Serializable {

		private static final long serialVersionUID = 1L;

		private int strategyid;
		private int[] targettype;
		private int[] targetid;

		public int getStrategyid() {
			return strategyid;
		}

		public void setStrategyid(int strategyid) {
			this.strategyid = strategyid;
		}

		public int[] getTargettype() {
			return targettype;
		}

		public void setTargettype(int[] targettype) {
			this.targettype = targettype;
		}

		public int[] getTargetid() {
			return targetid;
		}

		public void setTargetid(int[] targetid) {
			this.targetid = targetid;
		}

	}

	@XmlRootElement(name = "param")
	public static class TargetAndDesktopPool implements Serializable {

		private static final long serialVersionUID = 1L;

		private int[] target;
		private int[] desktoppoolid;
		private int[] desktopid;
		private int force;

		public int[] getTarget() {
			return target;
		}

		public void setTarget(int[] target) {
			this.target = target;
		}

		public int[] getDesktoppoolid() {
			return desktoppoolid;
		}

		public void setDesktoppoolid(int[] desktoppoolid) {
			this.desktoppoolid = desktoppoolid;
		}

		public int[] getDesktopid() {
			return desktopid;
		}

		public void setDesktopid(int[] desktopid) {
			this.desktopid = desktopid;
		}

		public int getForce() {
			return force;
		}

		public void setForce(int force) {
			this.force = force;
		}

	}

	@XmlRootElement(name = "param")
	public static class UserAndDesktopPool extends TargetAndDesktopPool
			implements Serializable {

		private static final long serialVersionUID = 1L;

		public void setUserid(int[] target) {
			super.setTarget(target);
		}

	}

	@XmlRootElement(name = "param")
	public static class GroupAndDesktopPool extends TargetAndDesktopPool
			implements Serializable {

		private static final long serialVersionUID = 1L;

		public void setGroupid(int[] target) {
			super.setTarget(target);
		}

	}

	@XmlRootElement(name = "param")
	public static class OrganizationAndDesktopPool extends TargetAndDesktopPool
			implements Serializable {

		private static final long serialVersionUID = 1L;

		public void setOrganizationid(int[] target) {
			super.setTarget(target);
		}

	}

	@XmlRootElement(name = "param")
	public static class ConnectionIdParam implements Serializable {

		private static final long serialVersionUID = 1L;

		private int idconnection;

		public int getIdconnection() {
			return idconnection;
		}

		public void setIdconnection(int idconnection) {
			this.idconnection = idconnection;
		}

	}

	@XmlRootElement(name = "iddesktoppool")
	public static class DesktopPoolIdWrapper implements Serializable {

		private static final long serialVersionUID = 1L;

		private int iddesktoppool;

		public int getIddesktoppool() {
			return iddesktoppool;
		}

		public void setIddesktoppool(int iddesktoppool) {
			this.iddesktoppool = iddesktoppool;
		}

	}

	@XmlRootElement(name = "iddesktoppool")
	public static class RestrictionStrategyIdWrapper implements Serializable {

		private static final long serialVersionUID = 1L;

		private int idrestrictionstrategy;

		public int getIdrestrictionstrategy() {
			return idrestrictionstrategy;
		}

		public void setIdrestrictionstrategy(int idrestrictionstrategy) {
			this.idrestrictionstrategy = idrestrictionstrategy;
		}

	}

	@XmlRootElement(name = "idcloudmanager")
	public static class CloudManagerEntityIdWrapper implements Serializable {

		private static final long serialVersionUID = 1L;

		private int idcloudmanager;

		public int getIdcloudmanager() {
			return idcloudmanager;
		}

		public void setIdcloudmanager(int idcloudmanager) {
			this.idcloudmanager = idcloudmanager;
		}

	}

	@XmlRootElement(name = "response")
	public static class ListTemplatesResponse extends Response<TemplateList>
			implements Serializable {

		private static final long serialVersionUID = 1L;

		private TemplateList body;

		public TemplateList getBody() {
			return body;
		}

		public void setBody(TemplateList body) {
			this.body = body;
		}

	}

	public static class UserVolumeList extends CommonList<UserVolume> implements
			Serializable {

		private static final long serialVersionUID = 1L;

		private List<UserVolume> list;

		@Override
		public List<UserVolume> getList() {
			return list;
		}

		@Override
		public void setList(List<UserVolume> list) {
			this.list = list;
		}

	}

	@XmlRootElement(name = "response")
	public static class ListDesktopsResponse extends Response<DesktopList>
			implements Serializable {

		private static final long serialVersionUID = 1L;

		private DesktopList body;

		public DesktopList getBody() {
			return body;
		}

		public void setBody(DesktopList body) {
			this.body = body;
		}

	}

	@XmlRootElement(name = "response")
	public static class ListCloudDriversResponse extends Response<List<String>>
			implements Serializable {

		private static final long serialVersionUID = 1L;

		private List<String> body;

		public List<String> getBody() {
			return body;
		}

		public void setBody(List<String> body) {
			this.body = body;
		}

	}

	@XmlRootElement(name = "response")
	public static class ListCloudManagersResponse extends
			Response<List<CloudManagerEntity>> implements Serializable {

		private static final long serialVersionUID = 1L;

		private List<CloudManagerEntity> body;

		public List<CloudManagerEntity> getBody() {
			return body;
		}

		public void setBody(List<CloudManagerEntity> body) {
			this.body = body;
		}

	}

	@XmlRootElement(name = "response")
	public static class ListConnectionsResponse extends
			Response<ConnectionList> implements Serializable {

		private static final long serialVersionUID = 1L;

		private ConnectionList body;

		public ConnectionList getBody() {
			return body;
		}

		public void setBody(ConnectionList body) {
			this.body = body;
		}

	}

	@XmlRootElement(name = "response")
	public static class ListAssignmentsResponse extends Response<ResourceList>
			implements Serializable {

		private static final long serialVersionUID = 1L;

		private ResourceList body;

		public ResourceList getBody() {
			return body;
		}

		public void setBody(ResourceList body) {
			this.body = body;
		}

	}

	@XmlRootElement(name = "response")
	public static class ListRestrictionStrategyAssignmentsResponse extends
			Response<RestrictionStrategyAssignmentList> implements Serializable {

		private static final long serialVersionUID = 1L;

		private RestrictionStrategyAssignmentList body;

		public RestrictionStrategyAssignmentList getBody() {
			return body;
		}

		public void setBody(RestrictionStrategyAssignmentList body) {
			this.body = body;
		}

	}

	@XmlRootElement(name = "response")
	public static class ListRestrictionStrategiesResponse extends
			Response<RestrictionStrategyList> implements Serializable {

		private static final long serialVersionUID = 1L;

		private RestrictionStrategyList body;

		public RestrictionStrategyList getBody() {
			return body;
		}

		public void setBody(RestrictionStrategyList body) {
			this.body = body;
		}

	}

	@XmlRootElement(name = "response")
	public static class DesktopPoolIdResponse extends
			Response<DesktopPoolIdWrapper> implements Serializable {

		private static final long serialVersionUID = 1L;

		private DesktopPoolIdWrapper body;

		@Override
		public DesktopPoolIdWrapper getBody() {
			return body;
		}

		@Override
		public void setBody(DesktopPoolIdWrapper body) {
			this.body = body;
		}

	}

	@XmlRootElement(name = "response")
	public static class CloudManagerEntityIdResponse extends
			Response<CloudManagerEntityIdWrapper> implements Serializable {

		private static final long serialVersionUID = 1L;

		private CloudManagerEntityIdWrapper body;

		@Override
		public CloudManagerEntityIdWrapper getBody() {
			return body;
		}

		@Override
		public void setBody(CloudManagerEntityIdWrapper body) {
			this.body = body;
		}

	}

	@XmlRootElement(name = "response")
	public static class UserAndPoolListResponse extends
			MultiStatusResponse<UserAndPoolAndError> implements Serializable {

		private static final long serialVersionUID = 1L;

		private List<UserAndPoolAndError> body;

		@Override
		public List<UserAndPoolAndError> getBody() {
			return body;
		}

		@Override
		public void setBody(List<UserAndPoolAndError> body) {
			this.body = body;
		}

	}

	@XmlRootElement(name = "response")
	public static class GroupAndPoolListResponse extends
			MultiStatusResponse<GroupAndPoolAndError> implements Serializable {

		private static final long serialVersionUID = 1L;

		private List<GroupAndPoolAndError> body;

		@Override
		public List<GroupAndPoolAndError> getBody() {
			return body;
		}

		@Override
		public void setBody(List<GroupAndPoolAndError> body) {
			this.body = body;
		}

	}

	@XmlRootElement(name = "response")
	public static class OrganizationAndPoolListResponse extends
			MultiStatusResponse<OrganizationAndPoolAndError> implements
			Serializable {

		private static final long serialVersionUID = 1L;

		private List<OrganizationAndPoolAndError> body;

		@Override
		public List<OrganizationAndPoolAndError> getBody() {
			return body;
		}

		@Override
		public void setBody(List<OrganizationAndPoolAndError> body) {
			this.body = body;
		}

	}

	@XmlRootElement(name = "response")
	public static class StrategyAssignmentResponse extends
			MultiStatusResponse<StrategyAssignment> implements Serializable {

		private static final long serialVersionUID = 1L;

		private List<StrategyAssignment> body;

		@Override
		public List<StrategyAssignment> getBody() {
			return body;
		}

		@Override
		public void setBody(List<StrategyAssignment> body) {
			this.body = body;
		}

	}

	@XmlRootElement(name = "response")
	public static class ListDesktopPoolsResponse extends
			Response<DesktopPoolList> implements Serializable {

		private static final long serialVersionUID = 1L;

		private DesktopPoolList body;

		public DesktopPoolList getBody() {
			return body;
		}

		public void setBody(DesktopPoolList body) {
			this.body = body;
		}

	}

	@XmlRootElement(name = "response")
	public static class ListAsyncJobsResponse extends Response<AsyncJobList>
			implements Serializable {

		private static final long serialVersionUID = 1L;

		private AsyncJobList body;

		public AsyncJobList getBody() {
			return body;
		}

		public void setBody(AsyncJobList body) {
			this.body = body;
		}

	}

	@XmlRootElement(name = "response")
	public static class RestrictionStrategyListResponse extends
			MultiStatusResponse<RestrictionStrategyAndError> implements
			Serializable {

		private static final long serialVersionUID = 1L;

		private List<RestrictionStrategyAndError> body;

		@Override
		public List<RestrictionStrategyAndError> getBody() {
			return body;
		}

		@Override
		public void setBody(List<RestrictionStrategyAndError> body) {
			this.body = body;
		}

	}

	public static class RestrictionStrategyAndError implements Serializable {

		private static final long serialVersionUID = 1L;

		private int strategyid;
		private int error;

		public RestrictionStrategyAndError(int strategyid, int error) {
			this.strategyid = strategyid;
			this.error = error;
		}

		public int getStrategyid() {
			return strategyid;
		}

		public void setStrategyid(int strategyid) {
			this.strategyid = strategyid;
		}

		public int getError() {
			return error;
		}

		public void setError(int error) {
			this.error = error;
		}

	}

	@XmlRootElement(name = "response")
	public static class AsyncJobResponse extends Response<AsyncJob> implements
			Serializable {

		private static final long serialVersionUID = 1L;

		private AsyncJob body;

		public AsyncJob getBody() {
			return body;
		}

		public void setBody(AsyncJob body) {
			this.body = body;
		}

	}

	@XmlRootElement(name = "response")
	public class EstablishConnectionAsyncJobResponse extends
			Response<EstablishConnectionAsyncJob> implements Serializable {

		private static final long serialVersionUID = 1L;

		private EstablishConnectionAsyncJob body;

		public EstablishConnectionAsyncJob getBody() {
			return body;
		}

		public void setBody(EstablishConnectionAsyncJob body) {
			this.body = body;
		}

	}

	public class EstablishConnectionAsyncJob implements Serializable {

		private static final long serialVersionUID = 1L;

		private Integer jobid;
		private String cmd;
		private Date createtime;
		private int jobstatus;
		private int jobprocstatus;
		private int jobresultcode;
		private String handle;
		private Connection jobresult;

		public Integer getJobid() {
			return jobid;
		}

		public void setJobid(Integer jobid) {
			this.jobid = jobid;
		}

		/**
		 * @return 发起任务的命令.
		 */
		public String getCmd() {
			return cmd;
		}

		public void setCmd(String cmd) {
			this.cmd = cmd;
		}

		/**
		 * @return 发起任务的时间.
		 */
		public Date getCreatetime() {
			return createtime;
		}

		public void setCreatetime(Date createtime) {
			this.createtime = createtime;
		}

		/**
		 * @return 任务状态代码. 参考{@link AsyncJob#ASYNC_JOB_STATUS_RUNNING},
		 *         {@link AsyncJob#ASYNC_JOB_STATUS_SUCCESS},
		 *         {@link AsyncJob#ASYNC_JOB_STATUS_FAILURE}.
		 */
		public int getJobstatus() {
			return jobstatus;
		}

		public void setJobstatus(int jobstatus) {
			this.jobstatus = jobstatus;
		}

		/**
		 * @return 任务进度信息, 0~100
		 */
		public int getJobprocstatus() {
			return jobprocstatus;
		}

		public void setJobprocstatus(int jobprocstatus) {
			this.jobprocstatus = jobprocstatus;
		}

		/**
		 * @return 任务执行错误码. 0表示无错误.
		 */
		public int getJobresultcode() {
			return jobresultcode;
		}

		public void setJobresultcode(int jobresultcode) {
			this.jobresultcode = jobresultcode;
		}

		/**
		 * @return 执行任务的线程/对象.
		 */
		public String getHandle() {
			return handle;
		}

		public void setHandle(String handle) {
			this.handle = handle;
		}

		/**
		 * @return 任务结果.
		 */
		public Connection getJobresult() {
			return jobresult;
		}

		public void setJobresult(Connection jobresult) {
			this.jobresult = jobresult;
		}

	}

	@XmlRootElement(name = "response")
	public static class ListUserVolumesResponse extends
			Response<UserVolumeList> implements Serializable {

		private static final long serialVersionUID = 1L;

		private UserVolumeList body;

		public UserVolumeList getBody() {
			return body;
		}

		public void setBody(UserVolumeList body) {
			this.body = body;
		}

	}

	@XmlRootElement(name = "response")
	public static class ListOwnedVolumesResponse extends
			Response<List<UserVolume>> implements Serializable {

		private static final long serialVersionUID = 1L;

		private List<UserVolume> body;

		public List<UserVolume> getBody() {
			return body;
		}

		public void setBody(List<UserVolume> body) {
			this.body = body;
		}

	}

	@XmlRootElement(name = "response")
	public static class RestrictionStrategyResponse extends
			Response<RestrictionStrategyIdWrapper> implements Serializable {

		private static final long serialVersionUID = 1L;

		private RestrictionStrategyIdWrapper body;

		@Override
		public RestrictionStrategyIdWrapper getBody() {
			return body;
		}

		@Override
		public void setBody(RestrictionStrategyIdWrapper body) {
			this.body = body;
		}

	}

	@XmlRootElement(name = "response")
	public static class AddDesktopByIPAddressResponse extends
			MultiStatusResponse<IPAddressAndError> implements Serializable {

		private static final long serialVersionUID = 1L;

		private List<IPAddressAndError> body;

		public List<IPAddressAndError> getBody() {
			return body;
		}

		public void setBody(List<IPAddressAndError> body) {
			this.body = body;
		}

	}

	@XmlRootElement(name = "response")
	public static class DesktopListResponse extends
			MultiStatusResponse<DesktopAndError> implements Serializable {

		private static final long serialVersionUID = 1L;

		private List<DesktopAndError> body;

		@Override
		public List<DesktopAndError> getBody() {
			return body;
		}

		@Override
		public void setBody(List<DesktopAndError> body) {
			this.body = body;
		}

	}

	@XmlRootElement(name = "response")
	public static class DesktopPoolListResponse extends
			MultiStatusResponse<DesktopPoolAndError> implements Serializable {

		private static final long serialVersionUID = 1L;

		private List<DesktopPoolAndError> body;

		@Override
		public List<DesktopPoolAndError> getBody() {
			return body;
		}

		@Override
		public void setBody(List<DesktopPoolAndError> body) {
			this.body = body;
		}

	}

	public static class TemplateList extends CommonList<Template> implements
			Serializable {

		private static final long serialVersionUID = 1L;

		private List<Template> list;

		@Override
		public List<Template> getList() {
			return list;
		}

		@Override
		public void setList(List<Template> list) {
			this.list = list;
		}

	}

	public static class DesktopList extends CommonList<Desktop> implements
			Serializable {

		private static final long serialVersionUID = 1L;

		private List<Desktop> list;

		@Override
		public List<Desktop> getList() {
			return list;
		}

		@Override
		public void setList(List<Desktop> list) {
			this.list = list;
		}

	}

	public static class AsyncJobList extends CommonList<AsyncJob> implements
			Serializable {

		private static final long serialVersionUID = 1L;

		private List<AsyncJob> list;

		@Override
		public List<AsyncJob> getList() {
			return list;
		}

		@Override
		public void setList(List<AsyncJob> list) {
			this.list = list;
		}

	}

	public static class DesktopPoolList extends CommonList<DesktopPoolEntity>
			implements Serializable {

		private static final long serialVersionUID = 1L;

		private List<DesktopPoolEntity> list;

		@Override
		public List<DesktopPoolEntity> getList() {
			return list;
		}

		@Override
		public void setList(List<DesktopPoolEntity> list) {
			this.list = list;
		}

	}

	public static class ConnectionList extends CommonList<Connection> implements
			Serializable {

		private static final long serialVersionUID = 1L;

		private List<Connection> list;

		@Override
		public List<Connection> getList() {
			return list;
		}

		@Override
		public void setList(List<Connection> list) {
			this.list = list;
		}

	}

	public static class ResourceList extends CommonList<Resource> implements
			Serializable {

		private static final long serialVersionUID = 1L;

		private List<Resource> list;

		@Override
		public List<Resource> getList() {
			return list;
		}

		@Override
		public void setList(List<Resource> list) {
			this.list = list;
		}

	}

	public static class RestrictionStrategyAssignmentList extends
			CommonList<RestrictionStrategyAssignment> implements Serializable {

		private static final long serialVersionUID = 1L;

		private List<RestrictionStrategyAssignment> list;

		@Override
		public List<RestrictionStrategyAssignment> getList() {
			return list;
		}

		@Override
		public void setList(List<RestrictionStrategyAssignment> list) {
			this.list = list;
		}

	}

	public static class RestrictionStrategyList extends
			CommonList<RestrictionStrategy> implements Serializable {

		private static final long serialVersionUID = 1L;

		private List<RestrictionStrategy> list;

		@Override
		public List<RestrictionStrategy> getList() {
			return list;
		}

		@Override
		public void setList(List<RestrictionStrategy> list) {
			this.list = list;
		}

	}

	public static class TargetAndPoolAndError implements Serializable {

		private static final long serialVersionUID = 1L;

		private int target;
		private int desktoppoolid;
		private int error;

		public TargetAndPoolAndError(int target, int desktoppoolid, int error) {
			this.target = target;
			this.desktoppoolid = desktoppoolid;
			this.error = error;
		}

		protected int getTarget() {
			return target;
		}

		public void setTarget(int target) {
			this.target = target;
		}

		public int getDesktoppoolid() {
			return desktoppoolid;
		}

		public void setDesktoppoolid(int desktoppoolid) {
			this.desktoppoolid = desktoppoolid;
		}

		public int getError() {
			return error;
		}

		public void setError(int error) {
			this.error = error;
		}

	}

	public static class UserAndPoolAndError extends TargetAndPoolAndError
			implements Serializable {

		public UserAndPoolAndError(int target, int desktoppoolid, int error) {
			super(target, desktoppoolid, error);
		}

		private static final long serialVersionUID = 1L;

		public int getUserid() {
			return super.getTarget();
		}

	}

	public static class GroupAndPoolAndError extends TargetAndPoolAndError
			implements Serializable {

		public GroupAndPoolAndError(int target, int desktoppoolid, int error) {
			super(target, desktoppoolid, error);
		}

		private static final long serialVersionUID = 1L;

		public int getGroupid() {
			return super.getTarget();
		}

	}

	public static class OrganizationAndPoolAndError extends
			TargetAndPoolAndError implements Serializable {

		public OrganizationAndPoolAndError(int target, int desktoppoolid,
				int error) {
			super(target, desktoppoolid, error);
		}

		private static final long serialVersionUID = 1L;

		public int getOrganizationid() {
			return super.getTarget();
		}

	}

	public static class StrategyAssignment implements Serializable {

		private static final long serialVersionUID = 1L;

		private int targettype;
		private int targetid;
		private int error;

		public StrategyAssignment(int targettype, int targetid, int error) {
			this.targettype = targettype;
			this.targetid = targetid;
			this.error = error;
		}

		public int getTargettype() {
			return targettype;
		}

		public void setTargettype(int targettype) {
			this.targettype = targettype;
		}

		public int getTargetid() {
			return targetid;
		}

		public void setTargetid(int targetid) {
			this.targetid = targetid;
		}

		public int getError() {
			return error;
		}

		public void setError(int error) {
			this.error = error;
		}

	}

	public static class IPAddressAndError implements Serializable {

		private static final long serialVersionUID = 1L;

		private Integer desktopid;
		private String ipaddress;
		private int error;

		public IPAddressAndError(Integer desktopid) {
			this.desktopid = desktopid;
		}

		public IPAddressAndError(String ipaddress, int error) {
			this.ipaddress = ipaddress;
			this.error = error;
		}

		public Integer getDesktopid() {
			return desktopid;
		}

		public void setDesktopid(Integer desktopid) {
			this.desktopid = desktopid;
		}

		public String getIpaddress() {
			return ipaddress;
		}

		public void setIpaddress(String ipaddress) {
			this.ipaddress = ipaddress;
		}

		public int getError() {
			return error;
		}

		public void setError(int error) {
			this.error = error;
		}

	}

	public static class DesktopAndError implements Serializable {

		private static final long serialVersionUID = 1L;

		private int desktopid;
		private int error;

		public DesktopAndError(int desktopid, int error) {
			this.desktopid = desktopid;
			this.error = error;
		}

		public int getDesktopid() {
			return desktopid;
		}

		public void setDesktopid(int desktopid) {
			this.desktopid = desktopid;
		}

		public int getError() {
			return error;
		}

		public void setError(int error) {
			this.error = error;
		}

	}

	public static class DesktopPoolAndError implements Serializable {

		private static final long serialVersionUID = 1L;

		private int desktoppoolid;
		private int error;

		public DesktopPoolAndError(int desktoppoolid, int error) {
			this.desktoppoolid = desktoppoolid;
			this.error = error;
		}

		public int getDesktoppoolid() {
			return desktoppoolid;
		}

		public void setDesktoppoolid(int desktoppoolid) {
			this.desktoppoolid = desktoppoolid;
		}

		public int getError() {
			return error;
		}

		public void setError(int error) {
			this.error = error;
		}

	}

	public CloudManagerEntityIdResponse updateCloudManager(
			CloudManagerEntity cloudManagerEntity) {
		CloudManagerEntityIdResponse response = new CloudManagerEntityIdResponse();
		int error = (cloudManagerEntity.getIdcloudmanager()==null||cloudManagerEntity.getIdcloudmanager()==0)?CommonException.BAD_REQUEST:NO_ERRORS;
		if (error != NO_ERRORS) {
			response.getHead().setError(error);
			return response;
		}
		// FIXME
		boolean found = false;
		List<String> drivers = findDrivers();
		for (String driver : drivers) {
			if (driver.equals(cloudManagerEntity.getClouddrivername())) {
				found = true;
				break;
			}
		}
		if (!found) {
			response.getHead().setError(NOT_FOUND);
			return response;
		}
		try {
			error = cloudManagerFacade.updateCloudManager(cloudManagerEntity);
			if (cloudManagerEntity.getClouddrivername().equals("com.opzoon.ohvc.driver.opzooncloud.OpzoonCloudDriver")) {
				try {
					OpzoonCloudDriver opzoonCloudDriver = new OpzoonCloudDriver();
					opzoonCloudDriver.setBaseUrl(cloudManagerEntity
							.getBaseurl());
					opzoonCloudDriver.login(cloudManagerEntity.getUsername(),
							cloudManagerEntity.getPassword(), null);
				} catch (Exception e) {
					error = CommonException.INVALID_AUTACATION_POLICY;
				}
			}
		} catch (CommonException e) {
			response.getHead().setError(e.getError());
			return response;
		}
		if (error != NO_ERRORS) {
			response.getHead().setError(error);
			return response;
		}
		CloudManagerEntityIdWrapper cloudManagerEntityIdWrapper = new CloudManagerEntityIdWrapper();

		response.setBody(cloudManagerEntityIdWrapper);
		cloudManagerEntityIdWrapper.setIdcloudmanager(cloudManagerEntity
				.getIdcloudmanager());
		return response;
	}

}
