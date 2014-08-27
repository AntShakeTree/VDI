/**   
* Title: RailUserOperateService.java 
* @Package com.opzoon.vdi.core.ws 
* : TODO(用一句话描述该文件做什么) 
* @author david   
* @date 2013-1-25 下午2:18:46 
* @version V1.0   
*/
package com.opzoon.vdi.core.ws;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;

import com.opzoon.ohvc.common.RailResponse;
import com.opzoon.vdi.core.app.domain.RailApplicationToGroup;
import com.opzoon.vdi.core.app.domain.RailApplicationToOrganization;
import com.opzoon.vdi.core.app.domain.RailApplicationToUser;
import com.opzoon.vdi.core.app.request.ListApplicationsReq;
import com.opzoon.vdi.core.app.request.PublishOrUnPubilshRailApplicationReq;
import com.opzoon.vdi.core.app.request.RailApplicationResourceReq;
import com.opzoon.vdi.core.app.request.RailApplicationToGroupReq;
import com.opzoon.vdi.core.app.request.RailApplicationToOrganizationReq;
import com.opzoon.vdi.core.app.request.RailApplicationToUserReq;
import com.opzoon.vdi.core.app.request.RailApplicationViewReq;
import com.opzoon.vdi.core.app.request.RailConnectionReq;
import com.opzoon.vdi.core.app.response.ListApplicationsRes;
import com.opzoon.vdi.core.app.response.ListRailApplicationsViewRes;
import com.opzoon.vdi.core.app.response.RailApplicationResourceRes;
import com.opzoon.vdi.core.app.response.RailConnnectionRes;
import com.opzoon.vdi.core.facade.CommonException;
import com.opzoon.vdi.core.ws.DesktopUsage.ConnectionTicketParam;
import com.opzoon.vdi.core.ws.Services.NullResponse;

/** 
 * @ClassName: RailUserOperateService 
 * : 使用用户操作虚拟应用列表
 * @author david 
 * @date 2013-1-25 下午2:18:46 
 *  
 */
@Path("/")
public interface RailUserOperateService {
	String DEFAULT_CONTENT_TYPE = "application/opzoon-v4+json;";
	/**
	 * 
	* Title: establishRailConnection 
	* : 调用虚拟应用
	* @param RailConnectionReq
	* @return RailConnnectionRes 
	* @throws
	 */
	@POST
	@Produces(DEFAULT_CONTENT_TYPE)
	@Consumes(DEFAULT_CONTENT_TYPE)
	@Path("/establishRailConnection")
	public RailConnnectionRes establishRailConnection(RailConnectionReq req) throws CommonException;
	/**
	 * 
	* @Title: destroyRailConnection 
	* @Description: 断开虚拟应用 
	* @param  connectionTicketParam 
	* @return NullResponse    返回类型 
	* @throws
	 */
	@POST
	@Produces(DEFAULT_CONTENT_TYPE)
	@Consumes(DEFAULT_CONTENT_TYPE)
	@Path("/destroyRailConnection")
	public NullResponse destroyRailConnection(ConnectionTicketParam connectionTicketParam);
	@POST
	@Produces(DEFAULT_CONTENT_TYPE)
	@Consumes(DEFAULT_CONTENT_TYPE)
	@Path("/listRailResource")
	public RailApplicationResourceRes listRailResource(RailApplicationResourceReq req);
	
}
