package com.opzoon.vdi.core.ws;

import static com.opzoon.vdi.core.domain.ResourceAssignment.RESOURCE_TYPE_APPLICATION;
import static com.opzoon.vdi.core.domain.ResourceAssignment.RESOURCE_TYPE_POOL;
import static com.opzoon.vdi.core.facade.CommonException.BAD_REQUEST;
import static com.opzoon.vdi.core.facade.CommonException.ESTABLISH_CONNECTION_ERR;
import static com.opzoon.vdi.core.facade.CommonException.NO_ERRORS;
import static com.opzoon.vdi.core.facade.CommonException.UNKNOWN;
import static com.opzoon.vdi.core.util.ConditionUtils.numberNotEquals;

import java.io.Serializable;
import java.util.List;

import javax.xml.bind.annotation.XmlRootElement;

import com.opzoon.vdi.core.domain.Connection;
import com.opzoon.vdi.core.domain.Notification;
import com.opzoon.vdi.core.domain.RestrictionStrategy;
import com.opzoon.vdi.core.facade.CommonException;
import com.opzoon.vdi.core.facade.ResourceFacade;
import com.opzoon.vdi.core.facade.ResourceFacade.Resource;
import com.opzoon.vdi.core.facade.transience.AsyncJobFacade;
import com.opzoon.vdi.core.facade.transience.ConnectionFacade;
import com.opzoon.vdi.core.facade.transience.SessionFacade;
import com.opzoon.vdi.core.quartz.UkeyMonitorQuartz;
import com.opzoon.vdi.core.ws.Services.NullResponse;
import com.opzoon.vdi.core.ws.Services.Response;
import com.opzoon.vdi.core.ws.admin.DesktopManagement.ListOwnedVolumesResponse;
import com.opzoon.vdi.core.ws.admin.DesktopManagement.UserIdAndVolumeIdParam;

/**
 * 桌面使用业务实现.
 */
public class DesktopUsage {

	private ResourceFacade resourceFacade;
	//add by tanyunhua for import connection facade start------
	private ConnectionFacade connectionFacade;
	//add by tanyunhua for import connection facade end-------
	private SessionFacade sessionFacade;
	private AsyncJobFacade asyncJobFacade;

	/**
	 * 列举资源（包括虚拟桌面池、虚拟应用）.
	 * 
	 * @param resourceTypeParam 列举资源参数.
	 * @return 列举资源响应.<br />
	 *         {@link com.opzoon.vdi.core.facade.CommonException#NO_ERRORS}: 成功;<br />
	 *         {@link com.opzoon.vdi.core.facade.CommonException#BAD_REQUEST}: 参数校验失败 .
	 */
	public ListResourcesResponse listResources(
			ResourceTypeParam resourceTypeParam) {
		ListResourcesResponse response = new ListResourcesResponse();
		if (numberNotEquals(resourceTypeParam.getResourcetype(), -1)
				&& numberNotEquals(resourceTypeParam.getResourcetype(), RESOURCE_TYPE_POOL)
				&& numberNotEquals(resourceTypeParam.getResourcetype(), RESOURCE_TYPE_APPLICATION)) {
			response.getHead().setError(BAD_REQUEST);
			return response;
		}
		if (resourceTypeParam.getUserid() == null) {
			// TODO All similar situations.
			resourceTypeParam.setUserid(-1);
		}
		List<Resource> list = resourceFacade.findResources(resourceTypeParam.getResourcetype(), resourceTypeParam.getUserid(), false);
		for (Resource resource : list) {
			resource.setAvailableDesktop(null);
		}
		response.setBody(list);
		return response;
	}

	/**
	 * 建立连接.
	 * 
	 * @param resourceTypeAndId 资源类型和ID.
	 * @return 建立连接响应.<br />
	 *         {@link com.opzoon.vdi.core.facade.CommonException#NO_ERRORS}: 成功;<br />
	 *         {@link com.opzoon.vdi.core.facade.CommonException#BAD_REQUEST}: 参数校验失败 .<br />
	 *         {@link com.opzoon.vdi.core.facade.CommonException#UNKNOWN}: 建立连接失败 .
	 */
	public synchronized NullResponse establishConnection(
			ResourceTypeAndId resourceTypeAndId) {
		NullResponse response = new NullResponse();
		//add by zhanglu 2014-07-05 start
		if (1 == UkeyMonitorQuartz.isExpired){
			response.getHead().setError(ESTABLISH_CONNECTION_ERR);
		}
		//add by zhanglu 2014-07-05 end
		if (numberNotEquals(resourceTypeAndId.getResourcetype(), RESOURCE_TYPE_POOL)
				&& numberNotEquals(resourceTypeAndId.getResourcetype(), RESOURCE_TYPE_APPLICATION)) {
			response.getHead().setError(BAD_REQUEST);
			return response;
		}
		if (resourceTypeAndId.getResourceid() < 1) {
			response.getHead().setError(BAD_REQUEST);
			return response;
		}
		
		int[] errorContainer = new int[] { NO_ERRORS };
		int jobId = -1;
    try
    {
      jobId = resourceFacade.establishConnectionA(
      		resourceTypeAndId.getResourcetype(),
      		resourceTypeAndId.getResourceid(),
      		resourceTypeAndId.getBrokerprotocol());
    } catch (CommonException e)
    {
      // TODO Auto-generated catch block
      e.printStackTrace();
      errorContainer[0] = e.getError();
    }
		if (numberNotEquals(errorContainer[0], NO_ERRORS)) {
			response.getHead().setError(errorContainer[0]);
			return response;
		}
//		if (jobId == -1) {
//			response.getHead().setError(UNKNOWN);
//			return response;
//		}
		response.getHead().setJobid(jobId);
		return response;
	}

	/**
	 * 断开连接.
	 * 
	 * @param connectionTicketParam 连接的令牌.
	 * @return 空响应.<br />
	 *         {@link com.opzoon.vdi.core.facade.CommonException#NO_ERRORS}: 成功;<br />
	 *         {@link com.opzoon.vdi.core.facade.CommonException#BAD_REQUEST}: 参数校验失败 .<br />
	 *         {@link com.opzoon.vdi.core.facade.CommonException#NOT_FOUND}: 找不到要销毁的连接 .<br />
	 *         {@link com.opzoon.vdi.core.facade.CommonException#UNKNOWN}: 销毁连接失败 .
	 */
	public NullResponse destroyConnection(
			ConnectionTicketParam connectionTicketParam) {
		NullResponse response = new NullResponse();
		if (connectionTicketParam.getConnectionticket() != null
				&& connectionTicketParam.getConnectionticket().length() < 1) {
			response.getHead().setError(BAD_REQUEST);
			return response;
		}
		int error = resourceFacade.destroyConnectionNA(
        sessionFacade.getCurrentSession().getUserid(),
        sessionFacade.getCurrentSession().getIdsession(),
		    connectionTicketParam.getConnectionticket());
		if (numberNotEquals(error, NO_ERRORS)) {
			response.getHead().setError(error);
			return response;
		}
		return response;
	}

	public GetNotificationResponse getNotification() {
		GetNotificationResponse response = new GetNotificationResponse();
		response.setBody(resourceFacade.findNotifications());
		return response;
	}

	public NullResponse attachUserVolume(ResourceParam resourceParam) {
		NullResponse response = new NullResponse();
		if (resourceParam.getResourceid() < 1) {
			response.getHead().setError(BAD_REQUEST);
			return response;
		}
		int jobid = resourceFacade.attachUserVolumes(resourceParam.getResourceid(), resourceParam.getVolumeid());
		response.getHead().setJobid(jobid);
		return response;
	}

	public NullResponse detachUserVolume(ForceWrapper forceWrapper) {
		NullResponse response = new NullResponse();
		int jobid = resourceFacade.detachUserVolumes(forceWrapper.getVolumeid(), forceWrapper.isForce());
    response.getHead().setJobid(jobid);
		return response;
	}

	public ListOwnedVolumesResponse listOwnedVolumes() {
	  ListOwnedVolumesResponse response = new ListOwnedVolumesResponse();
		int[] errorContainer = new int[] { NO_ERRORS };
		response.setBody(resourceFacade.findOwnedVolumes(errorContainer));
		if (numberNotEquals(errorContainer[0], NO_ERRORS)) {
			response.getHead().setError(errorContainer[0]);
			return response;
		}
		return response;
	}

	public NullResponse eraseOwnedVolume(UserIdAndVolumeIdParam userIdParam) {
		NullResponse response = new NullResponse();
		int error = resourceFacade.eraseOwnedVolumes(userIdParam.getVolumeid());
		if (error != NO_ERRORS) {
			response.getHead().setError(error);
			return response;
		}
		return response;
	}

	public NullResponse stopAssignedDesktop(ResourceTypeAndId resourceTypeAndId) {
		NullResponse response = new NullResponse();
		if (resourceTypeAndId.getResourceid() < 1) {
			response.getHead().setError(BAD_REQUEST);
			return response;
		}
		int[] errorContainer = new int[] { NO_ERRORS };
		int jobId = resourceFacade.stopAssignedDesktop(
				resourceTypeAndId.getResourceid(),
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

	public NullResponse startAssignedDesktop(ResourceTypeAndId resourceTypeAndId) {
		NullResponse response = new NullResponse();
		if (resourceTypeAndId.getResourceid() < 1) {
			response.getHead().setError(BAD_REQUEST);
			return response;
		}
		int[] errorContainer = new int[] { NO_ERRORS };
		int jobId = resourceFacade.startAssignedDesktop(
				resourceTypeAndId.getResourceid(),
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

	public NullResponse restartAssignedDesktop(ResourceTypeAndId resourceTypeAndId) {
		NullResponse response = new NullResponse();
		if (resourceTypeAndId.getResourceid() < 1) {
			response.getHead().setError(BAD_REQUEST);
			return response;
		}
		int[] errorContainer = new int[] { NO_ERRORS };
		int jobId = resourceFacade.restartAssignedDesktop(
				resourceTypeAndId.getResourceid(),
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

	public GetRestrictionStrategyResponse getRestrictionStrategy(ResourceTypeAndId resourceTypeAndId) {
		GetRestrictionStrategyResponse response = new GetRestrictionStrategyResponse();
		if (resourceTypeAndId.getResourceid() < 1) {
			response.getHead().setError(BAD_REQUEST);
			return response;
		}
		int[] errorContainer = new int[] { NO_ERRORS };
		RestrictionStrategy restrictionStrategy = resourceFacade.findRestrictionStrategy(
				resourceTypeAndId.getResourceid(),
				errorContainer);
		if (numberNotEquals(errorContainer[0], NO_ERRORS)) {
			response.getHead().setError(errorContainer[0]);
			return response;
		}
		if (restrictionStrategy == null) {
			response.getHead().setError(UNKNOWN);
			return response;
		}
		response.setBody(restrictionStrategy);
		return response;
	}

	public void setResourceFacade(ResourceFacade resourceFacade) {
		this.resourceFacade = resourceFacade;
	}
	
	//add by tanyunhua for import connection facade start------
	public void setConnectionFacade(ConnectionFacade connectionFacade) {
		this.connectionFacade = connectionFacade;
	}
	//add by tanyunhua for import connection facade end-------

	public void setSessionFacade(SessionFacade sessionFacade) {
		this.sessionFacade = sessionFacade;
	}

	public void setAsyncJobFacade(AsyncJobFacade asyncJobFacade) {
		this.asyncJobFacade = asyncJobFacade;
	}

	@XmlRootElement(name = "param")
	public static class ResourceTypeAndId implements Serializable {

		private static final long serialVersionUID = 1L;

		private int resourcetype;
		private int resourceid;
		private int brokerprotocol;
		
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
		public int getBrokerprotocol() {
			return brokerprotocol;
		}
		public void setBrokerprotocol(int brokerprotocol) {
			this.brokerprotocol = brokerprotocol;
		}
		
	}

	@XmlRootElement(name = "param")
	public static class ResourceTypeParam implements Serializable {

		private static final long serialVersionUID = 1L;

		private Integer userid;
		private int resourcetype;
		
		public Integer getUserid() {
			return userid;
		}
		public void setUserid(Integer userid) {
			this.userid = userid;
		}
		public int getResourcetype() {
			return resourcetype;
		}
		public void setResourcetype(int resourcetype) {
			this.resourcetype = resourcetype;
		}
		
	}

	@XmlRootElement(name = "param")
	public static class ConnectionTicketParam implements Serializable {

		private static final long serialVersionUID = 1L;

		private String connectionticket;

		public String getConnectionticket() {
			return connectionticket;
		}
		public void setConnectionticket(String connectionticket) {
			this.connectionticket = connectionticket;
		}
		
	}

	//add by tanyunhua , for find desktop connection count;  end ------------
	@XmlRootElement(name = "param")
	public static class ResourceParam implements Serializable {

		private static final long serialVersionUID = 1L;

		private int resourcetype;
		private int resourceid;
		private Integer volumeid;
		
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
		public Integer getVolumeid() {
			return volumeid;
		}
		public void setVolumeid(Integer volumeid) {
			this.volumeid = volumeid;
		}
		
	}

	@XmlRootElement(name = "param")
	public static class ForceWrapper implements Serializable {

		private static final long serialVersionUID = 1L;

		private boolean force;
		private Integer volumeid;

		public boolean isForce() {
			return force;
		}
		public void setForce(boolean force) {
			this.force = force;
		}
		public Integer getVolumeid() {
			return volumeid;
		}
		public void setVolumeid(Integer volumeid) {
			this.volumeid = volumeid;
		}
		
	}

	@XmlRootElement(name = "response")
	public static class ListResourcesResponse extends Response<List<Resource>> implements Serializable {

		private static final long serialVersionUID = 1L;
		
		private List<Resource> body;
		
		public List<Resource> getBody() {
			return body;
		}
		public void setBody(List<Resource> body) {
			this.body = body;
		}
		
	}

	@XmlRootElement(name = "response")
	public static class ConnectionResponse extends Response<Connection> implements Serializable {

		private static final long serialVersionUID = 1L;
		
		private Connection body;
		
		public Connection getBody() {
			return body;
		}
		public void setBody(Connection body) {
			this.body = body;
		}
		
	}

	@XmlRootElement(name = "response")
	public static class GetNotificationResponse extends Response<List<Notification>> implements Serializable {

		private static final long serialVersionUID = 1L;
		
		private List<Notification> body;
		
		public List<Notification> getBody() {
			return body;
		}
		public void setBody(List<Notification> body) {
			this.body = body;
		}
		
	}

	@XmlRootElement(name = "response")
	public static class GetRestrictionStrategyResponse extends Response<RestrictionStrategy> implements Serializable {

		private static final long serialVersionUID = 1L;
		
		private RestrictionStrategy body;
		
		public RestrictionStrategy getBody() {
			return body;
		}
		public void setBody(RestrictionStrategy body) {
			this.body = body;
		}
		
	}

}
