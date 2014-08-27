package com.opzoon.vdi.core.facade;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.opzoon.vdi.core.controller.Controller;
import com.opzoon.vdi.core.controller.TaskEntity;
import com.opzoon.vdi.core.controller.TaskInfo;

@Service("taskFacade")
public class TaskFacade
{

	@Autowired
	private DatabaseFacade databaseFacade;

	public TaskInfo createTask(TaskInfo taskInfo)
	{
		taskInfo.setId(null);
		TaskEntity task = new TaskEntity(taskInfo);
		task.setCreateTime(new Date());
		databaseFacade.persist(task);
		taskInfo.setId(task.getId());
		System.out.println("create Task " + taskInfo.toString());
		return taskInfo;
	}

	public void updateTaskStatus(int taskid, int status)
	{
		databaseFacade.update("update TaskEntity set status = ?, createTime = ? where id = ?", 
				status, new Date(), taskid);
	}

	public void updateTaskStatus(int taskid, int status, String executor)
	{
		databaseFacade.update("update TaskEntity set status = ?, executor = ?, createTime = ?  where id = ?", 
				status, executor, new Date(), taskid);
	}

	public void updateTaskError(int taskid, int status, String error)
	{
		if (error == null || error.isEmpty())
			return;
		if (error.length() > Controller.ERROR_LENGTH_MAX)
			error = error.substring(0, Controller.ERROR_LENGTH_MAX);

		databaseFacade.update("update TaskEntity set status = ?, error = ? where id = ?", status, error, taskid);
	}

	// TODO: need line lock
	public boolean rushTask(int taskid, String executor)
	{
		int result = databaseFacade.update("update TaskEntity set status = ?, executor = ?, createTime = ?  where id = ? and status <> ?", 
				Controller.TASK_STATUS_RECEIVED, executor, new Date(), taskid, Controller.TASK_STATUS_RECEIVED);
		if (result > 0)
			return true;
		return false;
	}

	public List<TaskInfo> getOneServerTask(String server)
	{
		List<TaskInfo> taskInfos = new ArrayList<TaskInfo>();
		if (server == null || server.isEmpty())
			return taskInfos;

		StringBuilder queryClause = new StringBuilder("from TaskEntity");
		List<Object> params = new ArrayList<Object>();
		queryClause.append(" where executor = ? and status < ? order by id");
		params.add(server);
		params.add(Controller.TASK_STATUS_FINISHED);

		Object[] paramsArray = params.toArray();
		List<TaskEntity> taskEntities = (List<TaskEntity>) databaseFacade.find(queryClause.toString(), paramsArray);
		if (taskEntities == null || taskEntities.size() <= 0)
			return taskInfos;

		for (TaskEntity task : taskEntities)
		{
			try
			{
				taskInfos.add(new TaskInfo(task));
			}
			catch (Exception e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return taskInfos;

	}

	public DatabaseFacade getDatabaseFacade() {
		return databaseFacade;
	}

	public void setDatabaseFacade(DatabaseFacade databaseFacade) {
		this.databaseFacade = databaseFacade;
	}
	public List<TaskInfo> getRunningTask()
	{
		List<Object> params = new ArrayList<Object>();
		params.add(Controller.TASK_STATUS_EXECUTTING);

		String hql = "from TaskEntity where status = ?";
		Object[] paramsArray = params.toArray();
		List<TaskEntity> taskEntities = (List<TaskEntity>) databaseFacade.find(
				hql, paramsArray);
		List<TaskInfo> taskInfos = new ArrayList<TaskInfo>();
		for (TaskEntity task : taskEntities) {
			try {
				taskInfos.add(new TaskInfo(task));
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return taskInfos;
	}

	public List<TaskInfo> getOvertimeTask()
	{
		Calendar cal = Calendar.getInstance();
		cal.add(Calendar.MINUTE, -Controller.MAX_TASK_MININTE);
		
		List<Object> params = new ArrayList<Object>();
		params.add(Controller.TASK_STATUS_INIT);
		params.add(cal.getTime());

		String hql = "from TaskEntity where status = ? and createtime < ?";
		
		Object[] paramsArray = params.toArray();
		List<TaskEntity> taskEntities = (List<TaskEntity>) databaseFacade.find(
				hql, paramsArray);
		List<TaskInfo> taskInfos = new ArrayList<TaskInfo>();
		for (TaskEntity task : taskEntities) {
			try {
				taskInfos.add(new TaskInfo(task));
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return taskInfos;
	}
	
	public List<TaskInfo> getInitTask()
	{
		List<Object> params = new ArrayList<Object>();
		params.add(Controller.TASK_STATUS_INIT);

		String hql = "from TaskEntity where status = ?";
		
		Object[] paramsArray = params.toArray();
		List<TaskEntity> taskEntities = (List<TaskEntity>) databaseFacade.find(
				hql, paramsArray);
		List<TaskInfo> taskInfos = new ArrayList<TaskInfo>();
		for (TaskEntity task : taskEntities) {
			try {
				taskInfos.add(new TaskInfo(task));
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return taskInfos;
	}
	
}
