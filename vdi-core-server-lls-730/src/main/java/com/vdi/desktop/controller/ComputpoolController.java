package com.vdi.desktop.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PostAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.vdi.dao.desktop.domain.ComputePoolEntity;
import com.vdi.facade.ComputePoolFacade;
import com.vdi.vo.req.DeleteComputePool;
import com.vdi.vo.res.ComputePoolRes;
import com.vdi.vo.res.Header;
import com.vdi.vo.res.JobResponse;
import com.vdi.vo.res.ListComputePool;
//import com.vdi.vo.res.ListComputerPool;

@Controller
public class ComputpoolController {
	private @Autowired ComputePoolFacade computePoolFacade;
	private static final String CONTEXT_TYPE = "application/json";
	

	@RequestMapping(value= "/listComputePool",method=RequestMethod.POST,produces={CONTEXT_TYPE},consumes={CONTEXT_TYPE})
	@PostAuthorize("hasAuthority('ADMIN')")
	public @ResponseBody ListComputePool listComputePool(ComputePoolEntity pool){
		//~ 
		return computePoolFacade.listComputePool(pool);
	}
	
	@RequestMapping(value= "/getUser",method=RequestMethod.POST,produces={CONTEXT_TYPE},consumes={CONTEXT_TYPE})
	@PostAuthorize("hasAuthority('ADMIN')")
	public @ResponseBody JobResponse createComputePool(ComputePoolEntity entity){
		return computePoolFacade.createComputePool(entity);
	}
	@RequestMapping(value="deleteComputePool",method=RequestMethod.POST)
	public @ResponseBody Header deleteComputePool(DeleteComputePool entity){
		return computePoolFacade.deleteComputePool(entity);
	}
	@RequestMapping(value="deleteComputePool",method=RequestMethod.POST)
	public @ResponseBody ComputePoolRes getComputePool(ComputePoolEntity entity){
		return computePoolFacade.getComputPool(entity);
	}
	
}
