package com.vdi.facade;

import com.vdi.vo.res.Job;
import com.vdi.vo.res.JobResponse;

public interface BaseFacad {
JobResponse queryJob(Job job);
}
