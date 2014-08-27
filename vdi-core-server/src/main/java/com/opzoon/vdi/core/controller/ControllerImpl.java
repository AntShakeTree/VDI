package com.opzoon.vdi.core.controller;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.opzoon.appstatus.domain.ClusterState;
import com.opzoon.appstatus.domain.Node;
import com.opzoon.appstatus.domain.NodeState;
import com.opzoon.appstatus.facade.AppStatusService;
import com.opzoon.appstatus.manager.NodeManager;
import com.opzoon.ohvc.session.ExcecutorUtil;
import com.opzoon.vdi.core.controller.executor.ExecutorBase;
import com.opzoon.vdi.core.controller.executor.ExecutorBase.ExecuteResult;
import com.opzoon.vdi.core.facade.CommonException;
import com.opzoon.vdi.core.facade.TaskFacade;

@Service("controller")
public class ControllerImpl extends Controller {

	private static final Logger m_logger = LoggerFactory.getLogger(ControllerImpl.class);
	
	@Autowired
	private TaskFacade taskFacde;
	@Autowired
	private AppStatusService appStatusService;
	
    private ScheduledExecutorService taskScheduler;

	private String nodeName = null;
	private ClusterState clusterStatus = ClusterState.EMPTY;
	private boolean isMaster = false;
	private boolean myselfSubmitTask = false;
	
	private Set<String> runningNodeSet = new HashSet<String>();
	private Set<ClusterOpertator> operators = new HashSet<ClusterOpertator>();

	public ControllerImpl() {
	//	try {
			nodeName = NodeManager.getLocalNodeAddress();
	/*	} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}*/
	}
	public Runnable sendInitTask()
	{
        return new Runnable() {
            @Override
            public void run() {
    			m_logger.info("I am master, start taskFacde.getInitTask");
            	List<TaskInfo> taskList = taskFacde.getInitTask();
    			m_logger.info("init time taskList count is " + taskList.size());
            	for(TaskInfo task: taskList)
            	{
            		m_logger.info("send init task " + task.toString());
    				sendTaskToCluster(task);
            	}
            }
        };
	}
	
	public Runnable getOvertimeTask()
	{
        return new Runnable() {
            @Override
            public void run() {
    			m_logger.info("I am master, start taskFacde.getOvertimeTask");
            	List<TaskInfo> taskList = taskFacde.getOvertimeTask();
    			m_logger.info("over time taskList count is " + taskList.size());
            	for(TaskInfo task: taskList)
            	{
            		task.setTakeOver(true);
    				sendTaskToCluster(task);
            	}
            	taskList = taskFacde.getRunningTask();
    			m_logger.info("running taskList count is " + taskList.size());
            	for(TaskInfo task: taskList)
            	{
            		if(runningNodeSet.contains(task.getExecutor()))
            			continue;
            		task.setTakeOver(true);
    				sendTaskToCluster(task);
            	}
            }
        };
	}
	
	@Override
	public boolean sendTask(TaskInfo task) {
		m_logger.info("clusterStatus is " + clusterStatus + " executor " + task.getExecutorClass());
		Class<?> executorClass = task.getExecutorClass();
		if(executorClass == null)
		{
			m_logger.info("executorClass is null");
			return false;
		}
		task.setId(null);
		task.setStatus(Controller.TASK_STATUS_INIT);
		task.setSender(nodeName);
		task = taskFacde.createTask(task);
		
		// single mode
		if(clusterStatus == ClusterState.SINGLE)
		{
			submitTask(task);
			return true;
		}
		//====================================================================
		// cluster mode and cluster state is cluster
		if(clusterStatus == ClusterState.CLUSTER)
		{
			return sendTaskToCluster(task);
		}
		return false;
	}

	@Override
	public void notice(Node node) {
		ClusterState oldStatus = clusterStatus;
		clusterStatus = node.getClusterState();
		if(oldStatus != clusterStatus)
		{
			for(ClusterOpertator operator: operators)
			{
				operator.onClusterChange(clusterStatus);
			}
		}
		
		m_logger.info("getNodeState " + node.getNodeState() + " master: " + 
				node.isMaster() + " name: " + node.getNodeAddress() + " name2:" + nodeName);
		if(!myselfSubmitTask && node.getNodeState() == NodeState.RUNNING)
		{
			myselfSubmitTask = true;
			submitOneServerTask(nodeName);
		}
		if(node.getNodeState() == NodeState.RUNNING && node.isMaster() && nodeName.equals(node.getNodeAddress()))
		{
//			if(oldStatus != ClusterState.CLUSTER)
//			{
				sendInitTask();
//			}
			m_logger.info("I am master, start getOvertimeTask");
			isMaster = true;
			taskScheduler = Executors.newScheduledThreadPool(1, new NamedThreadFactory("taskOvertime"));
			taskScheduler.scheduleAtFixedRate(getOvertimeTask(),  60 * 1000, 5* 60*1000, TimeUnit.MILLISECONDS);
		}
		
		if(isMaster && node.isMaster() && !nodeName.equals(node.getNodeAddress()))
		{
			if(taskScheduler != null)
			{
				m_logger.info("I am not master, taskScheduler will shutdown");
				taskScheduler.shutdown();
				taskScheduler = null;
			}
			
			isMaster = false;
		}
		if(isMaster && node.getNodeState() == NodeState.LOST && clusterStatus == ClusterState.CLUSTER)
		{
			submitOneServerTask(node.getNodeAddress());
		}
		
		if(node.getNodeState() == NodeState.RUNNING)
		{
			runningNodeSet.add(node.getNodeAddress());
		}
		else if(node.getNodeState() == NodeState.LOST)
		{
			runningNodeSet.remove(node.getNodeAddress());
		}
	}

	@Override
	public void noticeMessage(String message)
	{
		try {
			m_logger.info("receive: " + message);
			m_logger.info("clusterStatus: " + clusterStatus);
			if(clusterStatus != ClusterState.CLUSTER)
				return;
			
			TaskInfo task = new TaskInfo(message);
			if(task.getType() == null || task.getType().intValue() == 0)
			{
				if(!taskFacde.rushTask(task.getId(), this.nodeName))
					return;
				
				Thread.sleep(30);
				TaskEntity taskEntity = taskFacde.getDatabaseFacade()
						.load(TaskEntity.class, task.getId());
				if(taskEntity == null)
					return;
				if(!nodeName.equals(taskEntity.getExecutor()))
				{
					m_logger.error("task controller node: " + nodeName 
							+ ", executor: " + taskEntity.getExecutor());
					return;
				}
			}
			else if(task.getType().intValue() == 0)
			{
				submitTask(task);
				return;
			}
			
			if(clusterStatus == ClusterState.CLUSTER)
			{
				submitTask(task);
			}
		} catch (Exception e) {
			m_logger.error("get the error task info ", e);
		}
	}
	
	private void submitOneServerTask(String server)
	{
		List<TaskInfo> tasks = taskFacde.getOneServerTask(server);
		for(TaskInfo task: tasks)
		{
			m_logger.info("controller submit my task: " + task.getId() + " " + nodeName);
			task.setTakeOver(true);
			submitTask(task);
		}
	}
	
	private boolean sendTaskToCluster(TaskInfo task)
	{
		m_logger.info("<=AppStatus=> sendTaskToCluster ");
		String message = task.toString();
		appStatusService.publishTaskMessage(message);
		m_logger.info("send task to cluster, executor " + task.getExecutorClass());
		return true;
	}


	private void submitTask(TaskInfo task) {
		m_logger.info("<=AppStatus=> submitTask ");
		taskFacde.updateTaskStatus(task.getId(), Controller.TASK_STATUS_RECEIVED, nodeName);
		Runnable runnable = getExecutorRunnable(task);
		m_logger.info("start submit task, executor " + task.getExecutorClass());
		ExcecutorUtil.execute(runnable);
		m_logger.info("end submit task, executor " + task.getExecutorClass());
	}

	private Runnable getExecutorRunnable(final TaskInfo task) {
		return new Runnable() {
			public void run() {
				Class<?> executorClass = task.getExecutorClass();
				taskFacde.updateTaskStatus(task.getId(), Controller.TASK_STATUS_EXECUTTING);
				try {
					ExecutorBase cmdObj = (ExecutorBase) executorClass.newInstance();
					cmdObj = TaskSpringContext.inject(cmdObj);
					ExecuteResult result = cmdObj.execute(task);
					
					//update task status
					if (result.getErrorCode() == 0) {
						taskFacde.updateTaskStatus(task.getId(), Controller.TASK_STATUS_FINISHED);
					} else {
						String error = String.format("code: %d, info: %s",
								result.getErrorCode(), result.getErrorString());
						taskFacde.updateTaskError(task.getId(), Controller.TASK_STATUS_ERROR, error);
					}

				} catch (Throwable e) {
					// TODO build new exception for executor.
					m_logger.error("task " + task.getId() + " " + task.getExecutorClass() + "execution exception occurs:  ");
					String errorInfo = e.getMessage();
					if(e instanceof CommonException)
					{
						errorInfo = task.getExecutorClass() + String.format(" CommonException: %08x" , ((CommonException)e).getError());
						m_logger.error(errorInfo);
					}
					m_logger.error("", e);
					String error = String.format("code: %d, info: %s", -1, errorInfo);
					taskFacde.updateTaskError(task.getId(), Controller.TASK_STATUS_ERROR, error);
				}
			}
		};
	}
	@Test
	public void testSet()
	{
		Set<String> runningNodeSet = new HashSet<String>();
		runningNodeSet.add("20.2.100.105");
		String str = "20.2.100.105";
		System.out.println(runningNodeSet.contains(str));
		runningNodeSet.remove(str);
		System.out.println(runningNodeSet.size());
	}
	
	@Override
	public void registerClusterOpertator(ClusterOpertator operator) {
		operators.add(operator);
	}
}
