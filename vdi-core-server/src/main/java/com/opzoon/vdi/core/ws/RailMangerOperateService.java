/**   
* Title: RailMangerService.java 
* @Package com.opzoon.vdi.core.ws 
* : TODO(用一句话描述该文件做什么) 
* @author david   
* @date 2013-1-25 下午1:59:50 
* @version V1.0   
*/
package com.opzoon.vdi.core.ws;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;

import com.opzoon.ohvc.common.RailResponse;
import com.opzoon.ohvc.domain.Head;
import com.opzoon.ohvc.domain.RailApplicationIcon;
import com.opzoon.ohvc.response.RailApplicationIconRes;
import com.opzoon.vdi.core.app.domain.RailApplicationServer;
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
import com.opzoon.vdi.core.ws.admin.DesktopManagement.ListAssignmentsResponse;

/** 
 * @ClassName: RailMangerService 
 * : 管理员操作
 * @author david 
 * @date 2013-1-25 下午1:59:50 
 *  
 */
@Path("/")
public interface RailMangerOperateService {
	String DEFAULT_CONTENT_TYPE = "application/opzoon-v4+json";
	/**
	 * 
	* Title: addApplicationServer 
	* :添加虚拟应用服务器的对外接口方法
	* @param applicationServer
	* @return   
	*  RailResponse<RailApplicationServer>
	* @throws
	 */
	@POST
	@Produces(DEFAULT_CONTENT_TYPE)
	@Consumes(DEFAULT_CONTENT_TYPE)
	@Path("/addApplicationServer")
	public  RailResponse<RailApplicationServer> addApplicationServer(RailApplicationServer applicationServer) throws CommonException;
	@POST
	@Produces(DEFAULT_CONTENT_TYPE)
	@Consumes(DEFAULT_CONTENT_TYPE)
	@Path("/updateApplicationServer")
	public  RailResponse<RailApplicationServer> updateApplicationServer(RailApplicationServer applicationServer) throws CommonException;
	/**
	 * 
	* Title: deleteApplicationServer 
	* : 删除虚拟应用服务器
	* @param  delApplicationServerReq
	* @param 
	* @param  CommonException
	* @return RailResponse<RailApplicationServer> 
	* @throws
	 */
	@POST
	@Produces(DEFAULT_CONTENT_TYPE)
	@Consumes(DEFAULT_CONTENT_TYPE)
	@Path("/deleteApplicationServer")
	public  RailResponse<RailApplicationServer> deleteApplicationServer(DeleteApplicationServerReq delApplicationServerReq) throws CommonException;
	/**
	 * 
	* Title: listApplicationServers 
	* : 查询虚拟应用服务器
	* @param 查询虚拟应用服务器请求代理类
	* @return Response<List<RailApplicationServer>> 返回虚拟应用服务器列表   
	* @throws
	 */
	@POST
	@Produces(DEFAULT_CONTENT_TYPE)
	@Consumes(DEFAULT_CONTENT_TYPE)
	@Path("/listApplicationServers")
	public ListApplicationServerRes listApplicationServers(ListApplicationServersReq req) throws CommonException;

	@POST
	@Produces(DEFAULT_CONTENT_TYPE)
	@Consumes(DEFAULT_CONTENT_TYPE)
	@Path("/getRailApplicationIcon")
	public RailApplicationIconRes getRailApplicationIcon(RailApplicationIcon req);

	@POST
	@Produces(DEFAULT_CONTENT_TYPE)
	@Consumes(DEFAULT_CONTENT_TYPE)
	@Path("/listRailAssignments")
	ListAssignmentsResponse listRailAssignments(ListRailAssignmentsReq listAssignmentsParam);
	/**
	 * 
	* Title: listRailApplications 
	* : 查询虚拟应用
	* @param ListApplicationServersReq 查询虚拟应用请求参数代理类
	* @param  
	* @return RailResponse<List<RailApllication>>
	* @throws
	 */
	@POST
	@Produces(DEFAULT_CONTENT_TYPE)
	@Consumes(DEFAULT_CONTENT_TYPE)
	@Path("/listRailApplications")
	ListApplicationsRes listRailApplications(ListApplicationsReq re);
	/**
	 * 
	* Title: ListRailApplicationsViewRes 
	* : 查询虚拟已经发布的应用服务器
	* @param 查询虚拟已经发布服务请求代理类
	* @return Response<List<RailApplicationServer>> 返回虚拟应用服务器列表   
	* @throws
	 */
	@POST
	@Produces(DEFAULT_CONTENT_TYPE)
	@Consumes(DEFAULT_CONTENT_TYPE)
	@Path("/listPublishedRailApplications")
	public ListRailApplicationsViewRes listPublishedRailApplications(RailApplicationViewReq req);

	/**
	 * 
	* Title: publishRailApplication 
	* : 发布虚拟应用 
	* @param PublishOrUnPubilshRailApplicationReq 请求代理
	* @return RailResponse<Object>    返回类型 
	* @throws
	 */
	@POST
	@Produces(DEFAULT_CONTENT_TYPE)
	@Consumes(DEFAULT_CONTENT_TYPE)
	@Path("/publishRailApplication")
	public RailResponse<Object> publishRailApplication(PublishOrUnPubilshRailApplicationReq req);
	/**
	 * 
	* Title: unpublishRailApplication 
	* : 取消发布虚拟应用
	* @param PublishOrUnPubilshRailApplicationReq 请求代理
	* @return RailResponse<Object>    返回类型 
	* @throws
	 */
	@POST
	@Produces(DEFAULT_CONTENT_TYPE)
	@Consumes(DEFAULT_CONTENT_TYPE)
	@Path("/unpublishRailApplication")
	public RailResponse<Object> unpublishRailApplication(PublishOrUnPubilshRailApplicationReq req);
	/**
	* Title: assignApplicationToUser 
	* :分配虚拟应用给用户
	* @param @param req
	* @return RailResponse<Object>    返回类型 
	* @throws
	 */
	@POST
	@Produces(DEFAULT_CONTENT_TYPE)
	@Consumes(DEFAULT_CONTENT_TYPE)
	@Path("/assignApplicationToUser")
	public RailResponse<Object> assignApplicationToUser(RailApplicationToUserReq req);
	/**
	* Title: unassignApplicationToUser 
	* :取消分配虚拟应用给用户
	* @param @param req
	* @return RailResponse<Object>    返回类型 
	* @throws
	 */
	@POST
	@Produces(DEFAULT_CONTENT_TYPE)
	@Consumes(DEFAULT_CONTENT_TYPE)
	@Path("/unassignApplicationToUser")
	public RailResponse<Object> unassignApplicationToUser(RailApplicationToUserReq req);
	/**
	 * Title: assignApplicationToOrganization 
	 * :分配虚拟应用给组织单元
	 * @param @param req
	 * @return RailResponse<Object>    返回类型 
	 * @throws
	 */
	@POST
	@Produces(DEFAULT_CONTENT_TYPE)
	@Consumes(DEFAULT_CONTENT_TYPE)
	@Path("/assignApplicationToOrganization")
	public RailResponse<Object> assignApplicationToOrganization(RailApplicationToOrganizationReq req);
	/**
	 * Title: unassignApplicationToOrganization 
	 * :取消分配虚拟应用给组织单元
	 * @param @param req
	 * @return RailResponse<Object>    返回类型 
	 * @throws
	 */
	@POST
	@Produces(DEFAULT_CONTENT_TYPE)
	@Consumes(DEFAULT_CONTENT_TYPE)
	@Path("/unassignApplicationToOrganization")
	public RailResponse<Object> unassignApplicationToOrganization(RailApplicationToOrganizationReq req);
	/**
	 * Title: assignApplicationToGroup 
	 * :分配虚拟应用给用户组
	 * @param @param req
	 * @return RailResponse<Object>    返回类型 
	 * @throws
	 */
	@POST
	@Produces(DEFAULT_CONTENT_TYPE)
	@Consumes(DEFAULT_CONTENT_TYPE)
	@Path("/assignApplicationToGroup")
	public RailResponse<Object> assignApplicationToGroup(RailApplicationToGroupReq req);
	/**
	 * Title: unassignApplicationToGroup 
	 * :取消分配虚拟应用给组
	 * @param @param req
	 * @return RailResponse<Object>    返回类型 
	 * @throws
	 */
	@POST
	@Produces(DEFAULT_CONTENT_TYPE)
	@Consumes(DEFAULT_CONTENT_TYPE)
	@Path("/unassignApplicationToGroup")
	public RailResponse<Object> unassignApplicationToGroup(RailApplicationToGroupReq req);

}
