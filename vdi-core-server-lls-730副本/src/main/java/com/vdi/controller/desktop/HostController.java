package com.vdi.controller.desktop;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.vdi.dao.desktop.domain.HostEntity;
import com.vdi.facade.HostFacad;
import com.vdi.vo.req.HostIdReq;
import com.vdi.vo.res.HostResponse;
import com.vdi.vo.res.ListHostResponse;

import static com.vdi.controller.BaseController.CONTEXT_TYPE;
@Controller
public class HostController {
	public @Autowired HostFacad hostFacad;
	
	@RequestMapping(value="/listHosts",method=RequestMethod.POST,produces={CONTEXT_TYPE},consumes={CONTEXT_TYPE})
	@ResponseBody
	public ListHostResponse listHosts(HostEntity entity){
		return	hostFacad.listHost(entity);
	}
	@RequestMapping(value="/getHost",method=RequestMethod.POST,produces={CONTEXT_TYPE},consumes={CONTEXT_TYPE})
	@ResponseBody
	HostResponse getHost(HostIdReq req){
		return	hostFacad.getHost(req);
	}
}
