package com.opzoon.vdi.core.facade.transience;

import static com.opzoon.vdi.core.domain.AsyncJob.ASYNC_JOB_STATUS_FAILURE;
import static com.opzoon.vdi.core.domain.AsyncJob.ASYNC_JOB_STATUS_RUNNING;
import static com.opzoon.vdi.core.domain.AsyncJob.ASYNC_JOB_STATUS_SUCCESS;
import static com.opzoon.vdi.core.facade.CommonException.NO_ERRORS;
import static com.opzoon.vdi.core.facade.FacadeHelper.count;
import static com.opzoon.vdi.core.facade.FacadeHelper.pagingFind;
import static com.opzoon.vdi.core.util.ConditionUtils.numberEquals;
import static com.opzoon.vdi.core.util.ConditionUtils.numberNotEquals;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.opzoon.vdi.core.domain.AsyncJob;
import com.opzoon.vdi.core.facade.StorageFacade;
import com.opzoon.vdi.core.facade.FacadeHelper.PagingInfo;

/**
 * 异步任务相关业务接口.
 */
public class AsyncJobFacade {

	private StorageFacade storageFacade;

	/**
	 * 保存异步任务.
	 * 
	 * @param asynJobName 任务名称.
	 * @param threadId 线程ID.
	 * @return 任务ID.
	 */
	public int saveAsyncJob(String asynJobName, Object asyncJobResult, long threadId) {
		AsyncJob job = new AsyncJob();
		job.setCmd(asynJobName);
		job.setCreatetime(new Date());
		job.setHandle("");
		job.setJobprocstatus(0);
		job.setJobstatus(ASYNC_JOB_STATUS_RUNNING);
		job.setJobresult(asyncJobResult == null ? "" : asyncJobResult.toString());
		storageFacade.persist(job);
		return job.getJobid();
	}

	/**
	 * 结束异步任务.
	 * 
	 * @param jobId 任务ID.
	 * @param error 错误代码.
	 */
	public void finishAsyncJob(Integer jobId, int error, Object result) {
//		storageFacade.update(
//				"update AsyncJob set jobstatus = ?, jobprocstatus = ?, jobresultcode = ?, jobresult = ? where jobId = ?",
//				numberEquals(error, NO_ERRORS) ? ASYNC_JOB_STATUS_SUCCESS : ASYNC_JOB_STATUS_FAILURE,
//				100,
//				error,
//				result == null ? "" : result.toString(),
//				jobId);
	}

	/**
	 * 分页查询异步任务.
	 * 
	 * @param cmd 任务名称. null为忽略.
	 * @param jobstatus 任务状态. -1为忽略.
	 * @param pagingInfo 分页信息.
	 * @param amountContainer 查询结果的总数量的容器.
	 * @return 查询结果列表.
	 */
	@SuppressWarnings("unchecked")
	public List<AsyncJob> findAsyncJobs(String cmd, int jobstatus,
			PagingInfo pagingInfo, int[] amountContainer) {
		StringBuilder whereClause = new StringBuilder("from AsyncJob where 1 = 1");
		List<Object> params = new ArrayList<Object>();
		if (cmd != null) {
			whereClause.append(" and cmd = ?");
			params.add(cmd);
		}
		if (numberNotEquals(jobstatus, -1)) {
			whereClause.append(" and jobstatus = ?");
			params.add(jobstatus);
		}
		Object[] paramsArray = params.toArray();
		count(storageFacade, "jobid", whereClause, paramsArray, amountContainer);
		return pagingFind(storageFacade, whereClause, paramsArray, pagingInfo);
	}

	/**
	 * 根据ID获取异步任务.
	 * 
	 * @param jobid 任务ID.
	 * @return 异步任务实体.
	 */
	public AsyncJob findAsyncJob(int jobid) {
		return storageFacade.load(AsyncJob.class, jobid);
	}

	public void setStorageFacade(StorageFacade storageFacade) {
		this.storageFacade = storageFacade;
	}

	public boolean justCanceledConnection(int userid, Date startDate) {
		Integer jobid = (Integer) storageFacade.findFirst(
				"select jobid from AsyncJob where cmd = ? and createtime > ? and jobresult = ?",
				"destroyConnectionOfCurrentUserByTicket", startDate, Integer.toString(userid));
		if (jobid != null) {
			storageFacade.update(
					"delete from AsyncJob where jobid = ?",
					jobid);
			return true;
		}
		return false;
	}

}
