/**   
 * @Title: AppStatusController.java 
 * Package com.opzoon.appstatus.service 
 * Description: TODO(用一句话描述该文件做什么) 
 * @author David   
 * @date 2013-7-24 上午10:27:24 
 * @version V1.0   
 *//*
package com.opzoon.appstatus.facade;

import java.io.EOFException;
import java.io.Serializable;
import java.util.Set;

import org.apache.log4j.Logger;
import org.apache.openjpa.persistence.PersistenceException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.opzoon.appstatus.common.AppStatusConstants;
import com.opzoon.appstatus.domain.Node;
import com.opzoon.appstatus.domain.req.NodeAddressList;
import com.opzoon.appstatus.domain.req.NodeReq;
import com.opzoon.appstatus.domain.res.AppStatusResponse;
import com.opzoon.appstatus.facade.impl.AppStatusServiceImpl;
import com.opzoon.ohvc.domain.Head;

*//**
 * ClassName: AppStatusController Description: AppStatusController
 * 
 * @author david
 * @date 2013-7-24 上午10:27:24
 *//*
@Controller
@RequestMapping("/ws/")
public class AppStatusController
{
	private static Logger logger = Logger.getLogger(AppStatusController.class);
	
	@Autowired
	private AppStatusService appStatusService;
	
	@RequestMapping(value = "listNodes/", method = RequestMethod.POST)
	public @ResponseBody AppStatusResponse<Node> listNodes(@RequestBody NodeReq req)
	{
		return appStatusService.listNodes(req);
	}
	
	@RequestMapping(value = "updateNodes/", method = RequestMethod.POST)
	public @ResponseBody AppStatusResponse<Node> updateNodes(@RequestBody NodeAddressList list)
	{
		return appStatusService.updateNodes(list);
	}
	
	@RequestMapping(value = "deleteNodesByIds/", method = RequestMethod.POST)
	public @ResponseBody AppStatusResponse<Node> deleteNodesByIds(@RequestBody Set<Serializable> ids)
	{
		return appStatusService.deleteNodesByIds(ids);
	}
	
	@SuppressWarnings("deprecation")
	@RequestMapping(value = "testTaskExecute/", method = RequestMethod.POST)
	public @ResponseBody AppStatusResponse<?> testExecutor()
	{
		return appStatusService.testTaskExecute();
	}
	
	@ExceptionHandler(Exception.class)
	public @ResponseBody AppStatusResponse<?> handleException(Exception exception) {
		
		Head head = new Head();
		//数据格式不正确 或 没传json的{}
		if(HttpMessageNotReadableException.class.equals(exception.getClass()) || EOFException.class.equals(exception.getClass())) {
			head.setError(AppStatusConstants.ERROR_CODE.BAD_REQUEST).setMessage("请求格式或内容错误");
		}else if(PersistenceException.class.equals(exception.getClass())) {
			head.setError(AppStatusConstants.ERROR_CODE.DATABASE_UNTOUCHABLE).setMessage("数据库连接异常");
		}else {
			head.setError(AppStatusConstants.ERROR_CODE.UNKNOWN).setMessage("未知错误");
		}
		
		logger.info("superman" + exception.getClass());
		logger.info(exception);
		
		AppStatusResponse<Object> resp = new AppStatusResponse<Object>();
		resp.setHead(head);
		
		return resp;
	}
}

*/