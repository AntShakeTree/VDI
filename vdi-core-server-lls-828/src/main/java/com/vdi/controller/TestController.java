/**
 * Project Name:vdi-core-server-lls
 * File Name:TestController.java
 * Package Name:com.vdi.controller
 * Date:2014年8月7日上午8:54:23
 * Copyright (c) 2014 All Rights Reserved.
 *
*/

package com.vdi.controller;

import org.springframework.security.access.prepost.PostAuthorize;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.vdi.dao.user.domain.User;

@Controller
public class TestController {
	
	
	private static final String CONTEXT_TYPE = "application/json";

	@RequestMapping(value= "/getUser",method=RequestMethod.POST,produces={CONTEXT_TYPE},consumes={CONTEXT_TYPE})
	@PostAuthorize("hasAuthority('ADMIN')")
	public @ResponseBody User getUser(Object p){
		return new User();
	}
}
