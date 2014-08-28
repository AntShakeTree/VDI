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
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.vdi.common.Session;
import com.vdi.dao.user.domain.User;
import com.vdi.support.desktop.lls.domain.task.Task;
import com.vdi.vo.res.Header;
import com.vdi.vo.res.Job;
import com.vdi.vo.res.JobResponse;

@ControllerAdvice
public class BaseController {

	public static final String CONTEXT_TYPE = "application/json";



	@RequestMapping(value = "/queryJob", method = RequestMethod.POST, produces = { CONTEXT_TYPE })
	public @ResponseBody JobResponse queryJob(Job job) {
		Task task = (Task) Session.getCache(job.getJobid());
		JobResponse res = new JobResponse();
		res.setHead(new Header());
		job.setError(task.getErrorCode());
		job.setProgress(task.getProgress());
		if (task.getErrorCode() != 0) {
			task.setStatus(Job.FAIL);
			Session.removeCache(job.getJobid());
		} else {
			if(task.isTaskFinished(task)){
				job.setStatus( Job.SUCCESS);
				Session.removeCache(job.getJobid());
			}else{
				job.setStatus(Job.RUNNING);
			}
			
		}
		res.setBody(job);
		return res;
	}
}
