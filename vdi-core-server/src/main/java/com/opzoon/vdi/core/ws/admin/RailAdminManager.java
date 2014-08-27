/**   
 * Title: RailManager.java 
 * Package com.opzoon.vdi.core.ws.admin 
 * Description: TODO 
 * @author David   
 * @date 2013-2-22 下午5:24:52 
 * @version V1.0   
 */
package com.opzoon.vdi.core.ws.admin;

import java.text.MessageFormat;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.log4j.Logger;

import com.opzoon.ohvc.common.RailAppError;
import com.opzoon.ohvc.common.RailResponse;
import com.opzoon.ohvc.domain.AgentRailApplication;
import com.opzoon.ohvc.domain.AgentRailApplicationServer;
import com.opzoon.ohvc.domain.Head;
import com.opzoon.ohvc.domain.RailInformation;
import com.opzoon.ohvc.response.RailResponseSupport;
import com.opzoon.ohvc.service.VdiAgentClientImpl;
import com.opzoon.vdi.core.app.domain.RailApplication;
import com.opzoon.vdi.core.app.domain.RailApplicationServer;
import com.opzoon.vdi.core.app.domain.RailApplicationView;
import com.opzoon.vdi.core.app.request.DeleteApplicationServerReq;
import com.opzoon.vdi.core.app.request.ListApplicationServersReq;
import com.opzoon.vdi.core.app.request.ListApplicationsReq;
import com.opzoon.vdi.core.app.request.ListRailAssignmentsReq;
import com.opzoon.vdi.core.app.request.PublishOrUnPubilshRailApplicationReq;
import com.opzoon.vdi.core.app.request.RailApplicationToGroupReq;
import com.opzoon.vdi.core.app.request.RailApplicationToOrganizationReq;
import com.opzoon.vdi.core.app.request.RailApplicationToUserReq;
import com.opzoon.vdi.core.app.request.RailApplicationViewReq;
import com.opzoon.vdi.core.app.response.ListApplicationServerRes;
import com.opzoon.vdi.core.app.response.ListApplicationsRes;
import com.opzoon.vdi.core.app.response.ListRailApplicationsViewRes;
import com.opzoon.vdi.core.facade.CommonException;
import com.opzoon.vdi.core.facade.RailDataManager;
import com.opzoon.vdi.core.util.BeanUtils;
import com.opzoon.vdi.core.util.Validator;
import com.opzoon.vdi.core.ws.admin.DesktopManagement.ListAssignmentsResponse;

/**
 * ClassName: RailManager Description: TODO
 * 
 * @author David
 * @date 2013-2-22 下午5:24:52
 */
public class RailAdminManager {
	private static Logger log = Logger.getLogger(RailAdminManager.class);
	private Validator validator;
	private RailDataManager railDataManagement;

	/**
	 * @return railDataManagement
	 */
	public RailDataManager getRailDataManagement() {
		return railDataManagement;
	}

	/**
	 * @param railDataManagement
	 *            the railDataManagement to set
	 */
	public void setRailDataManagement(RailDataManager railDataManagement) {
		this.railDataManagement = railDataManagement;
	}

	/**
	 * @return validator
	 */
	public Validator getValidator() {
		return validator;
	}

	public RailResponse<RailApplicationServer> addApplicationServer(RailApplicationServer applicationServer) throws CommonException {
		log.info("addApplicationServer enter.");
		RailResponse<RailApplicationServer> res = new com.opzoon.vdi.core.app.response.ApplicationServerResponse();
		Head head = new Head();
		res.setHead(head);
		// 验证
		boolean isValid = validator.validate(applicationServer, res.getHead());
		if (!isValid) {
			log.info("addApplicationServer validator failure. [" + isValid + "]");

			return res;
		}
		// 调用Agent 查询虚拟应用服务器的详细信息
		try {
			int timespn = 60;
			RailResponse<AgentRailApplicationServer> ress = VdiAgentClientImpl.getPerformanceCounter(applicationServer.getServername(), timespn);
			log.info("addApplicationServer :: agent performanceCounter .  timespn. [" + timespn + "]");
			// cp perperty
			// 操作数据库

			
			BeanUtils.copyProperties(applicationServer, ress.getBody(),false);
			// Vdi agent
			log.info("addApplicationServer :: agent getHostJoinInformation .  Servername [" + applicationServer.getServername() + "]");
			RailResponse<RailInformation> resRailInformation = VdiAgentClientImpl.getHostJoinInformation(applicationServer.getServername());
			// cp
			BeanUtils.copyProperties(applicationServer, resRailInformation.getBody(), false);

			log.info("addApplicationServer :: add applicationServer .  [" + applicationServer.getConnections() + "]");
		} catch (Exception e) {
			e.printStackTrace();
			log.error(e.getCause() + ":" + e.getMessage());
			throw new CommonException(0x8000001);
		}
		res.setBody(applicationServer);
		
		try {
			
			
			RailResponse<List<AgentRailApplication>> ls = VdiAgentClientImpl.listRailApplications(applicationServer.getServername());
			res.setHead(ls.getHead());
			if(ls.getBody()==null){
				return res;
			}
			if(res.getHead().getError()!=0){
				return res;
			}
			// 查询Agent 虚拟应用入库
			applicationServer.setAppinstalled(ls.getBody().size());
			railDataManagement.persistApplicationServer(applicationServer);
			// 虚拟应用
			log.info("addApplicationServer :: agent listRailApplications.[" + ls.getBody().size() + "");
			for (AgentRailApplication apllication : ls.getBody()) {
				// 关联外键
				RailApplication rail = new RailApplication();
				apllication.setApplicationserverid(applicationServer.getIdapplicationserver());
				apllication.setServername(applicationServer.getServername());
				BeanUtils.copyProperties(rail, apllication, false);
				railDataManagement.persistApplication(rail);
			}
		} catch (Exception e) {
			log.error(e.getMessage());
			throw new CommonException(0x8000001);
		}
		
		return res;
	}

	public RailResponse<RailApplicationServer> deleteApplicationServer(DeleteApplicationServerReq delApplicationServerReq) throws CommonException {
		log.trace("");
		RailResponse<RailApplicationServer> rs = new RailResponseSupport<RailApplicationServer>();
		Head head = new Head();
		rs.setHead(head);
		// 验证
		boolean isValid = validator.validate(delApplicationServerReq, rs.getHead());
		if (!isValid) {
			return rs;
		}
		RailApplicationServer railApplicationServer = railDataManagement.getApplicationServer(delApplicationServerReq.getIdapplicationserver());

		if (railApplicationServer.getConnections() <= 0) {
			railDataManagement.deleteApplicationServer(delApplicationServerReq.getIdapplicationserver());
		} else {
			railApplicationServer.setStatus(RailApplicationServer.RAIL_APPLICATION_SERVER_STATUS_DELEING);
			railDataManagement.updateApplicationServer(railApplicationServer);
		}
		return rs;
	}

	public ListApplicationServerRes listApplicationServers(ListApplicationServersReq req) throws CommonException {
		log.trace("");
		ListApplicationServerRes res = new ListApplicationServerRes();
		List<com.opzoon.vdi.core.app.domain.RailApplicationServer> rs;
		try {
			if (req.getApplicationid() != null && !"".equals(req.getApplicationid().trim())) {
				rs = this.railDataManagement.listApplicationServerByApplicationid(req.getApplicationid(),req.getPublished());
				res.setPage(null);

			} else {
				rs = this.railDataManagement.listPageRequest(req);
			}
			res.setBody(rs);
		} catch (Exception e) {
			log.error(e.getMessage() + ":[" + e.getCause() + "]");
			e.printStackTrace();
			res.setHead(new Head().setMessage(RailAppError.RAIL_ERR.getMessage()).setError(RailAppError.RAIL_ERR.getError()));
			return res;
		}
		res.setHead(new Head().setError(0));
		res.setPage(req);
		return res;
	}

	public ListApplicationsRes listRailApplications(ListApplicationsReq re) {
		log.trace("");
		ListApplicationsRes res = new ListApplicationsRes();
		Head head = new Head();
		head.setError(0);
		res.setHead(head);
		List<RailApplication> rs;
		try {
			rs = this.railDataManagement.listPageRequest(re);
			res.setBody(rs);
		} catch (Exception e) {
			head.setError(RailAppError.RAIL_ERR.getError());
			head.setMessage(RailAppError.RAIL_ERR.getMessage());
		}
		res.setPage(re);
		return res;
	}

	/*
	 * (非 Javadoc) <p>Title: listPublishedRailApplications</p> <p>Description: </p>
	 * @param req
	 * @return
	 * @see com.opzoon.vdi.core.ws.RailUserOperateService#listPublishedRailApplications(com.opzoon.ohvc.domain.RailApplicationView)
	 */

	public ListRailApplicationsViewRes listPublishedRailApplications(RailApplicationViewReq req) {
		log.trace("");
		ListRailApplicationsViewRes res = new ListRailApplicationsViewRes();
		Head head = new Head();
		res.setHead(head.setError(0));
		try {
			List<RailApplicationView> rs = this.railDataManagement.listPageRequest(req);
			res.setBody(rs);
		} catch (Exception e) {
			e.printStackTrace();
			res.getHead().setError(RailAppError.RAIL_ERR.getError());
			log.error(e.getMessage());
			return res;
		}
		res.setPage(req);
		return res;
	}

	public RailResponse<Object> publishRailApplication(PublishOrUnPubilshRailApplicationReq req) {
		log.trace("");
		Set<Integer> serverids = new HashSet<Integer>();
		RailResponse<Object> res = new RailResponseSupport<Object>();
		res.setHead(new Head().setError(0));
		if (!validator.validate(req, res.getHead())) {
			return res;
		}
		for (Integer id : req.getIdrailapplications()) {
			com.opzoon.vdi.core.app.domain.RailApplication rail = this.railDataManagement.getApplication(id);
			if (rail == null || rail.getPublished()) {
				continue;
			}

			log.info("is validator agent  exists rail application?");
			AgentRailApplication RailAppllication2 = new AgentRailApplication();
			BeanUtils.copyProperties(RailAppllication2, rail, false);

			try {
				RailResponse<List<AgentRailApplication>> railResponse = VdiAgentClientImpl.listRailApplications(rail.getServername());

				if (!railResponse.getBody().contains(RailAppllication2)) {
					log.info("is validator agent  exists rail application? false");
					this.railDataManagement.deleteApplication(id);
					res.setHead(res.getHead().setError(RailAppError.RAIL_HAVED_DEL_ERR.getError()).setMessage(RailAppError.RAIL_HAVED_DEL_ERR.getMessage()));
					continue;
				}
			} catch (Exception e) {
				e.printStackTrace();
				log.error("invork agent Exception ::" + e.getMessage());
				res.setHead(new Head().setError(RailAppError.RAIL_ERR.getError()));
				return res;
			}
			rail.setPublished(true);
			rail.setStatus(RailApplication.ONLINE);
			RailApplicationView railApplicationView = this.railDataManagement.getRailApplicationView(rail.getApplicationid());
			if (railApplicationView == null) {
				railApplicationView = new RailApplicationView();
				BeanUtils.copyProperties(railApplicationView, rail, false);
				this.railDataManagement.saveRailApplicationView(railApplicationView);
			}
			serverids.add(rail.getApplicationserverid());
			this.railDataManagement.updateRailApplication(rail);
		}
		this.railDataManagement.updateApplicationServerPublishCount(serverids);
		return res;
	}

	public RailResponse<Object> unpublishRailApplication(PublishOrUnPubilshRailApplicationReq req) {
		log.trace("");
		RailResponse<Object> res = new RailResponseSupport<Object>();
		res.setHead(new Head().setError(0));
		if (!validator.validate(req, res.getHead())) {
			return res;
		}
		Set<Integer> applicationServerids = new HashSet<Integer>();
		for (Integer id : req.getIdrailapplications()) {
			com.opzoon.vdi.core.app.domain.RailApplication railApplication = this.railDataManagement.getApplication(id);
			if (railApplication == null) {
				continue;
			}
			applicationServerids.add(railApplication.getApplicationserverid());
			railApplication.setPublished(false);
			this.railDataManagement.updateRailApplication(railApplication);
			this.railDataManagement.updateApplicationServerPublishCount(applicationServerids);
		}

		return res;
	}

	public RailResponse<Object> assignApplicationToUser(RailApplicationToUserReq req) {
		log.trace("");
		RailResponse<Object> res = new RailResponseSupport<Object>();
		res.setHead(new Head().setError(0));
		if (!validator.validate(req, res.getHead())) {
			return res;
		} else {
			RailApplicationView view = this.railDataManagement.getRailApplicationView(req.getApplicationid());
			if (view == null ) {
				Head head = res.getHead();
				head = head.getHeadByError(RailAppError.RAIL_HAVED_DEL_ERR);
				head.setMessage(MessageFormat.format(head.getMessage(), req.getApplicationid()));
				res.setHead(head);
				return res;
			}
		}

		this.railDataManagement.assignApplicationToUser(req);
		return res;
	}

	public RailResponse<Object> unassignApplicationToUser(RailApplicationToUserReq req) {
		log.trace("");
		RailResponse<Object> res = new RailResponseSupport<Object>();
		res.setHead(new Head().setError(0));
		if (!validator.validate(req, res.getHead())) {
			return res;
		}
		this.railDataManagement.unassignApplicationToUser(req);
		return res;
	}

	public RailResponse<Object> assignApplicationToOrganization(RailApplicationToOrganizationReq req) {
		log.trace("");
		RailResponse<Object> res = new RailResponseSupport<Object>();
		res.setHead(new Head().setError(0));
		if (!validator.validate(req, res.getHead())) {
			return res;
		} else {
			RailApplicationView view = this.railDataManagement.getRailApplicationView(req.getApplicationid());
			if (view == null ) {
				Head head = res.getHead();
				head = head.getHeadByError(RailAppError.RAIL_HAVED_DEL_ERR);
				head.setMessage(MessageFormat.format(head.getMessage(), req.getApplicationid()));
				res.setHead(head);
				return res;
			}
		}
		this.railDataManagement.assignApplicationToOrganization(req);
		return res;
	}
	/**
	 * 
	* @Title: unassignApplicationToOrganization 
	* @Description: TODO(这里用一句话描述这个方法的作用) 
	* @param @param req
	* @param @return    设定文件 
	* @return RailResponse<Object>    返回类型 
	* @throws
	 */
	public RailResponse<Object> unassignApplicationToOrganization(RailApplicationToOrganizationReq req) {
		log.trace("");
		RailResponse<Object> res = new RailResponseSupport<Object>();
		res.setHead(new Head().setError(0));
		if (!validator.validate(req, res.getHead())) {
			return res;
		}
		this.railDataManagement.unassignApplicationToOrganization(req);
		return res;
	}
	/**
	 * 
	* @Title: assignApplicationToGroup 
	* @Description: 分配虚拟应用给组
	* @return RailResponse<Object>    
	* @throws
	 */
	public RailResponse<Object> assignApplicationToGroup(RailApplicationToGroupReq req) {
		log.trace("");
		RailResponse<Object> res = new RailResponseSupport<Object>();
		res.setHead(new Head().setError(0));
		if (!validator.validate(req, res.getHead())) {
			return res;
		} else {
			RailApplicationView view = this.railDataManagement.getRailApplicationView(req.getApplicationid());
			if (view == null ) {
				Head head = res.getHead();
				head = head.getHeadByError(RailAppError.RAIL_HAVED_DEL_ERR);
				head.setMessage(MessageFormat.format(head.getMessage(), req.getApplicationid()));
				res.setHead(head);
				return res;
			}
		}
		this.railDataManagement.assignApplicationToGroup(req);
		return res;
	}
	/**
	 * 
	* @Title: unassignApplicationToGroup 
	* @Description:姐出虚拟应用分配给组 
	* @param {"applicationid":"asdfdsafsadfasf123_afddsf","groupid":324132}
	* @return {head:{error:0}} 
	* @throws
	 */
	public RailResponse<Object> unassignApplicationToGroup(RailApplicationToGroupReq req) {
		log.trace("");
		RailResponse<Object> res = new RailResponseSupport<Object>();
		res.setHead(new Head().setError(0));
		if (!validator.validate(req, res.getHead())) {
			return res;
		}
		this.railDataManagement.unassignApplicationToGroup(req);
		return res;
	}

	/**
	 * @param validator
	 *            the validator to set
	 */
	public void setValidator(Validator validator) {
		this.validator = validator;
	}

	/**
	 * Title: listRailAssignments Description:
	 * 
	 * @param  {userid:0},  {applicationid:"688191hvhgdjfasdf"}
	 * @return {resourceList} 
	 * @throws
	 */
	public ListAssignmentsResponse listRailAssignments(ListRailAssignmentsReq listAssignmentsParam) {
		log.trace("");
		if (listAssignmentsParam.getGroupid()!=null&&listAssignmentsParam.getGroupid() == -1) {
			return this.railDataManagement.listGroupByApplicationid(listAssignmentsParam);
		}

		if (listAssignmentsParam.getGroupid()!=null&&listAssignmentsParam.getGroupid() >= 0) {
			return this.railDataManagement.listResourceByGroupId(listAssignmentsParam);
		}

		if (listAssignmentsParam.getUserid()!=null&&listAssignmentsParam.getUserid() == -1) {
			return this.railDataManagement.listRailAssignmentsByUserId(listAssignmentsParam);
		}

		if (listAssignmentsParam.getUserid()!=null&&listAssignmentsParam.getUserid() >= 0) {
			return this.railDataManagement.listResourceByUserId(listAssignmentsParam);
		}

		if (listAssignmentsParam.getOrganizationid()!=null&&listAssignmentsParam.getOrganizationid() == -1) {
			return this.railDataManagement.listRailAssignmentsByOrganizationid(listAssignmentsParam);
		}

		if (listAssignmentsParam.getOrganizationid()!=null&&listAssignmentsParam.getOrganizationid() >= 0) {
			return this.railDataManagement.listResourceByOrganizationId(listAssignmentsParam);
		}
		return null;
	}

	/**
	 * @Title: updateApplicationServer
	 * @Description: 更新虚拟应用服务器
	 * @param 虚拟应用服务器
	 * @param 
	 * @return Head 返回类型
	 * @throws
	 */
	public Head updateApplicationServer(RailApplicationServer applicationServer) {
		log.trace("");
		Head head = new Head();
		head.setError(0);
		if (applicationServer.getIdapplicationserver() == null) {
			head.setError(RailAppError.RAIL_APP_SERVER_ID_ERR.getError());
			return head;
		} else {
			RailApplicationServer server = this.railDataManagement.findRailApplicationServerById(applicationServer.getIdapplicationserver());
			server.setNotes(applicationServer.getNotes());
			this.railDataManagement.updateApplicationServer(server);
		}
		return head;
	}

}