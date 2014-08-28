package com.vdi.desktop.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.vdi.dao.desktop.domain.HostEntity;
import com.vdi.facade.HostFacad;
import com.vdi.vo.res.ListHostResponse;

@Controller
public class HostController {
	private static final String CONTEXT_TYPE = "application/json";
	public @Autowired HostFacad hostFacad;
	
	@RequestMapping(value="/listHost",produces={CONTEXT_TYPE})
	@ResponseBody
	public ListHostResponse listHost(HostEntity entity){
		return	hostFacad.listHost(entity);
	}
}
