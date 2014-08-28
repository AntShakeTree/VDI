package com.vdi.facade.impl;

import org.springframework.stereotype.Service;

import com.vdi.common.Session;
import com.vdi.facade.BaseFacad;
import com.vdi.support.desktop.lls.domain.task.Task;
import com.vdi.vo.res.Header;
import com.vdi.vo.res.Job;
import com.vdi.vo.res.JobResponse;
@Service
public class BaseFacadImpl implements BaseFacad{

	@Override
	public JobResponse queryJob(Job job) {
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
