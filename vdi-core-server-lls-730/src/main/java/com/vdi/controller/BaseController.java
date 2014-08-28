/**
 * Project Name:vdi-core-server-lls
 * File Name:TestController.java
 * Package Name:com.vdi.controller
 * Date:2014年8月7日上午8:54:23
 * Copyright (c) 2014 All Rights Reserved.
 *
 */

package com.vdi.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.vdi.facade.BaseFacad;
import com.vdi.vo.res.Job;
import com.vdi.vo.res.JobResponse;

@Controller
public class BaseController {

	public static final String CONTEXT_TYPE = "application/json";
	private @Autowired BaseFacad baseFacad;


	@RequestMapping(value = "/queryJob", method = RequestMethod.POST, produces = { CONTEXT_TYPE })
	public @ResponseBody JobResponse queryJob(Job job) {

		return baseFacad.queryJob(job);
	}
}
