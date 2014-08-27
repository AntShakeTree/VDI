package com.opzoon.appstatus.manager;

import java.io.IOException;
import java.util.Queue;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ConcurrentLinkedQueue;
//import java.util.concurrent.CountDownLatch;
//import java.util.concurrent.TimeUnit;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.recipes.cache.PathChildrenCache;
import org.apache.curator.framework.recipes.cache.PathChildrenCacheEvent;
import org.apache.curator.framework.recipes.cache.PathChildrenCacheListener;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.opzoon.appstatus.common.AppStatusConstants;
import com.opzoon.appstatus.common.exception.AppstatusExceptionHandle;
import com.opzoon.appstatus.common.exception.custom.AppstatusDatabaseException;
import com.opzoon.appstatus.domain.Node;
import com.opzoon.appstatus.domain.NodeUpdate;
import com.opzoon.appstatus.domain.req.NodeReq;
import com.opzoon.appstatus.executor.dao.AppStatusDao;
import com.opzoon.appstatus.manager.mastertask.MasterTask;
import com.opzoon.appstatus.manager.mastertask.impl.UpdateDBTask;
import com.opzoon.vdi.core.controller.Controller;

@Component
public class MasterManager {
	private static Logger log = Logger.getLogger(MasterManager.class);
	
	private CuratorFramework client;
	
	private PathChildrenCache cache;
	// 当前的master，初始时为none
	// 该属性会被多线程共享，要考虑多线程问题
	private static volatile String master = "none";
	@Autowired
	private AppStatusDao appStatusDao;
	@Autowired
	private Controller controller;
	// master锁
	//private CountDownLatch masterLatch;
	
	private static Queue<MasterTask> taskQueue = new ConcurrentLinkedQueue<MasterTask>();
	
	private static Timer timer = new Timer();
	
	public void start() throws Exception {
		cache = new PathChildrenCache(client, AppStatusConstants.MASTER_PATH, true);
		addMasterNodeListener();
		cache.start();
		//startMasterMonitor();
	}

	public void stop() throws IOException {
		if(null != cache) cache.close();
		//stopMasterMonitor();
	}
	
	private void addMasterNodeListener() {
		log.info("<=AppStatus=> addMasterNodeListener");
		PathChildrenCacheListener plistener = new PathChildrenCacheListener() {
			@Override
			public void childEvent(CuratorFramework client, PathChildrenCacheEvent event) throws Exception {
				
				switch (event.getType()) {
				case CHILD_ADDED: {
					log.info("<=AppStatus=> addMasterNodeListener, before add, master is " + master);
					// master创建，加锁，等此节点LOST才可以创建新的节点
					//masterLatch = new CountDownLatch(1);
					// 更改master为新主机IP
					master = getMasterNodeData(AppStatusConstants.MASTER_NAME);
					
					log.info("<=AppStatus=> addMasterNodeListener, add master is " + master);
					NodeReq nodeReq = new NodeReq();
					nodeReq.setNodeAddress(master);
					Node node = appStatusDao.findNode(nodeReq);
					if(null == node) {
						throw AppstatusExceptionHandle.throwAppstatusException(new AppstatusDatabaseException());
					}
					node.setMaster(true);
					
					NodeUpdate nodeUpdate = new NodeUpdate(node.getId(), new String[]{"master"}, new Object[]{node.isMaster()});
					MasterTask task = new UpdateDBTask(nodeUpdate, appStatusDao);
					processTask(task);
					
					controller.notice(node);
					
					break;
				}
				
				case CHILD_REMOVED: {
					log.info("<=AppStatus=> addMasterNodeListener, before remove, master is " + master);
					// 创建master
					NodeReq nodeReq = new NodeReq();
					nodeReq.setNodeAddress(master);
					// 更改master为none
					master = "none";
					Node node = appStatusDao.findNode(nodeReq);
					if(null == node) {
						throw AppstatusExceptionHandle.throwAppstatusException(new AppstatusDatabaseException());
					}
					node.setMaster(false);
					NodeUpdate nodeUpdate = new NodeUpdate(node.getId(), new String[]{"master"}, new Object[]{node.isMaster()});
					MasterTask task = new UpdateDBTask(nodeUpdate, appStatusDao);
					processTask(task);
					
					controller.notice(node);
					log.info("<=AppStatus=> addMasterNodeListener, remove master is none");
					// master选举出来就不会改选，除非LOST
					// 每台主机都争抢建master，谁抢上谁是
					createMasterNodeByLatch(AppStatusConstants.MASTER_NAME, NodeManager.getLocalNodeAddress());
					
					break;
				}

				default:
					break;
				}
			}
		};
		cache.getListenable().addListener(plistener);
	}
	
	public boolean processTask(MasterTask task) {
		try {
			// 该延时是为了等待master状态的更新（如果master掉了，则可能master状态未置成none就触发master任务）
			Thread.sleep(100);
		} catch (InterruptedException e) {
			log.info("<=AppStatus=> processTask, exception:" + e);
		}
		String address = NodeManager.getLocalNodeAddress();
		boolean isFinish;
		if("none".equals(master)) {
			log.info("<=AppStatus=> processTask, master is none, queue add:" + task.toString());
			//如果没有master，则将任务入队列
			synchronized (taskQueue) {
				taskQueue.add(task);
			}
			isFinish = false;
		}
		else {
			//如果存在master，则判断自己是否是master
			try {
				if(address.equals(master)) {
					log.info("<=AppStatus=> masterUpdateNode, i am master, node=" + task.toString() + ", address=" + address + ", master=" + master);
					// 执行任务之前先检查一下队列里有没有任务
					checkTaskQueue();
					task.process();
				}
				else {
					log.info("<=AppStatus=> masterUpdateNode, i am not master, master is " + master + ", clear queue");
					taskQueue.clear();
				}
				isFinish = true;
			}
			catch (Exception e) {
				isFinish = false;
				log.error("<=AppStatus=> masterTask exception " + e);
			}
		}
		return isFinish;
	}
	
	private void checkTaskQueue() {
		//检查队列是否为空，不为空则执行队列内的任务
		while (!taskQueue.isEmpty()) {
			log.info("<=AppStatus=> checkTaskQueue, execute taskqueue");
			if(!processTask(taskQueue.poll())) break;
		}
	}
	
	public void createMasterNode(String name, String data) {
		log.info("<=AppStatus=> createMasterNode:" + data);
		NodeManager.createNode(client, AppStatusConstants.MASTER_PATH, name, data);
	}
	
	public String getMasterNodeData(String name) {
		log.info("<=AppStatus=> getMasterNodeData");
		return NodeManager.getNodeData(client, AppStatusConstants.MASTER_PATH, name);
	}
	
	public String getCurrentMasterNodeData() {
		log.info("<=AppStatus=> getCurrentMasterNodeData");
		return NodeManager.getNodeData(client, AppStatusConstants.MASTER_PATH, AppStatusConstants.MASTER_NAME);
	}
	
	public void removeMasterNode(String name) {
		log.info("<=AppStatus=> removeMasterNode");
		NodeManager.removeNode(client, AppStatusConstants.MASTER_PATH, name);
	}
	
	public void saveOrUpdateMasterNode(String name, String data) {
		log.info("<=AppStatus=> saveOrUpdateMasterNode:" + data);
		NodeManager.saveOrUpdateNode(client, AppStatusConstants.MASTER_PATH, name, data);
	}
	
	public boolean isMasterNodeExists(String name) {
		return NodeManager.isNodeExists(client, AppStatusConstants.MASTER_PATH, name);
	}

	public MasterManager setClient(CuratorFramework client) {
		this.client = client;
		return this;
	}
	
	public String getMaster() {
		return master;
	}
	// master节点丢失10s后，才会有其他主机建立master节点
	/*public void createMasterNodeByLatch(String name, String data) {
		log.info("<=AppStatus=> createMasterNodeByLatch:" + data);
		if(null != masterLatch) {
			try {
				masterLatch.await(AppStatusConstants.MASTER_TIMEOUT_SECOND, TimeUnit.SECONDS);
			} catch (InterruptedException e) {
				log.info("<=AppStatus=> createMasterNodeByLock exception:" + e);
			}
		}
		createMasterNode(name, data);
	}*/
	public void createMasterNodeByLatch(String name, String data) {
		log.info("<=AppStatus=> createMasterNodeByLatch:" + data);
		do {
			String masterNode = getMasterNodeData(name);
			if (masterNode != null && !"".equals(masterNode)) {
				return;
			} else {
				try {
					int time = (int)(Math.random() * 5000);
					Thread.sleep(time > 3000 ? time : 3000);
					createMasterNode(name, data);
				} catch (InterruptedException e) {
					log.info("<=AppStatus=> createMasterNodeByLatch exception:" + e);
				}
			}
		} while (true);
	}
	
	// 检测是否存在master节点，每隔1分钟执行一次
	/*private void startMasterMonitor() {
		timer.schedule(new TimerTask() {

			@Override
			public void run() {
				log.info("<=AppStatus=> startMasterMonitor");
				if(client != null) {
					if(!isMasterNodeExists(AppStatusConstants.MASTER_NAME)) {
						createMasterNode(AppStatusConstants.MASTER_NAME, NodeManager.getLocalNodeAddress());
					}
				}
			}
			
		}, 60000, 60000);
	}
	
	private void stopMasterMonitor() {
		timer.cancel();
	}*/

}
