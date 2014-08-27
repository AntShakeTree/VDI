package com.opzoon.appstatus.manager;

import java.io.IOException;
import java.util.List;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.recipes.cache.ChildData;
import org.apache.curator.framework.recipes.cache.PathChildrenCache;
import org.apache.curator.framework.recipes.cache.PathChildrenCacheEvent;
import org.apache.curator.framework.recipes.cache.PathChildrenCacheListener;
import org.apache.curator.utils.ZKPaths;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.opzoon.appstatus.common.AppStatusConstants;
import com.opzoon.appstatus.common.ParseJSON;
import com.opzoon.appstatus.common.exception.AppstatusExceptionHandle;
import com.opzoon.appstatus.common.exception.custom.AppstatusDatabaseException;
import com.opzoon.appstatus.domain.ClusterState;
import com.opzoon.appstatus.domain.Node;
import com.opzoon.appstatus.domain.NodeState;
import com.opzoon.appstatus.domain.NodeUpdate;
import com.opzoon.appstatus.domain.TaskMessage;
import com.opzoon.appstatus.domain.req.NodeReq;
import com.opzoon.appstatus.executor.dao.AppStatusDao;
import com.opzoon.appstatus.executor.zookeeper.ZooKeeperAction;
import com.opzoon.appstatus.manager.NodeManager.ServiceType;
import com.opzoon.appstatus.manager.handle.MessageHandle;
import com.opzoon.appstatus.manager.mastertask.MasterTask;
import com.opzoon.appstatus.manager.mastertask.impl.UpdateDBTask;
import com.opzoon.vdi.core.controller.Controller;

@Component
public class ClusterManager {
	
	private static Logger log = Logger.getLogger(ClusterManager.class);
	
	private CuratorFramework client;
	
	private PathChildrenCache cache;
	@Autowired
	private AppStatusDao appStatusDao;
	@Autowired
	private Controller controller;
	@Autowired
	private ZooKeeperAction zooKeeperAction;
	@Autowired
	private MasterManager masterManager;
	@Autowired
	private MessageHandle messageHandle;
	
	public void start() throws Exception {
		cache = new PathChildrenCache(client, AppStatusConstants.CLUSTER_PATH, true);
		addClusterNodeListener();
		cache.start();
	}

	public void stop() throws IOException {
		if(null != cache) cache.close();
	}
	
	private void addClusterNodeListener() {
		log.info("<=AppStatus=> addClusterNodeListener");
		PathChildrenCacheListener plistener = new PathChildrenCacheListener() {
			
			@Override
			public void childEvent(CuratorFramework client, PathChildrenCacheEvent event) throws Exception {
				ChildData childData = event.getData();
				if(null != childData) {
					String data = ZKPaths.getNodeFromPath(childData.getPath());
					switch (event.getType()) {
					case CHILD_ADDED: {
						log.info("<=AppStatus=> addClusterNodeListener Node added: " + data);
						NodeReq nodeReq = new NodeReq();
						nodeReq.setNodeAddress(data);
						Node node = appStatusDao.findNode(nodeReq);
						if (null == node) {
							throw AppstatusExceptionHandle.throwAppstatusException(new AppstatusDatabaseException());
						}
						node.setNodeState(NodeState.RUNNING);
						node.setClusterState(zooKeeperAction.isStandaloneZooKeeper() ? ClusterState.SINGLE : ClusterState.CLUSTER);
						
						NodeUpdate nodeUpdate = new NodeUpdate(node.getId(), new String[]{"clusterState", "nodeState"}, new Object[]{node.getClusterState(), node.getNodeState().getValue()});
						MasterTask task = new UpdateDBTask(nodeUpdate, appStatusDao);
						masterManager.processTask(task);
						
						controller.notice(node);
						break;
					}
					case CHILD_UPDATED: {
						log.info("<=AppStatus=> addClusterNodeListener Node update: " + new String(childData.getData()));
						TaskMessage taskMessage = ParseJSON.parseTaskMessage(new String(childData.getData()));
						controller.noticeMessage(taskMessage.getContent());
						break;
					}
					case CHILD_REMOVED: {
						log.info("<=AppStatus=> addClusterNodeListener Node remove: " + data);
						NodeReq nodeReq = new NodeReq();
						nodeReq.setNodeAddress(data);
						Node node = appStatusDao.findNode(nodeReq);
						if (null == node) {
							throw AppstatusExceptionHandle.throwAppstatusException(new AppstatusDatabaseException());
						}
						// 判断节点是主动移除还是被动丢失
						if (ServiceType.ADD == node.getServiceType()) {
							node.setNodeState(NodeState.LOST);
							node.setClusterState(ClusterState.DOWN);
						} else if (ServiceType.REMOVE == node.getServiceType()) {
							node.setNodeState(NodeState.READY);
							node.setClusterState(ClusterState.EMPTY);
						}

						NodeUpdate nodeUpdate = new NodeUpdate(node.getId(), new String[]{"clusterState", "nodeState"}, new Object[]{node.getClusterState(), node.getNodeState().getValue()});
						MasterTask task = new UpdateDBTask(nodeUpdate, appStatusDao);
						masterManager.processTask(task);
						
						// 数据库中的集群状态和通知给controller的集群状态是不同的
						// 能够检测到节点丢失，证明集群是可用的，所以通知controller的集群状态只能是cluster或single
						node.setClusterState(zooKeeperAction.isStandaloneZooKeeper() ? ClusterState.SINGLE : ClusterState.CLUSTER);
						controller.notice(node);
						break;
					}
					default:
						break;
					}
				}
			}
		};
		cache.getListenable().addListener(plistener);
	}
	
	public void createClusterNode(String name, String data) {
		// 先要删除原先的节点
		NodeManager.removeNode(client, AppStatusConstants.CLUSTER_PATH, name);
		NodeManager.createNode(client, AppStatusConstants.CLUSTER_PATH, name, data);
	}
	
	public String getClusterNodeData(String name) {
		return NodeManager.getNodeData(client, AppStatusConstants.CLUSTER_PATH, name);
	}
	
	public boolean isClusterNodeExists(String name) {
		return NodeManager.isNodeExists(client, AppStatusConstants.CLUSTER_PATH, name);
	}
	
	public void removeClusterNode(String name) {
		NodeManager.removeNode(client, AppStatusConstants.CLUSTER_PATH, name);
	}
	
	public void saveOrUpdateClusterNode(String name, String data) {
		NodeManager.saveOrUpdateNode(client, AppStatusConstants.CLUSTER_PATH, name, data);
	}
	
	public void saveOrUpdateLocalClusterNode(String data) {
		saveOrUpdateClusterNode(NodeManager.getLocalNodeAddress(), data);
	}
	
	public List<String> listClusterNodes() {
		return NodeManager.listNodes(client, AppStatusConstants.CLUSTER_PATH);
	}
	
	public ClusterManager setClient(CuratorFramework client) {
		this.client = client;
		return this;
	}
	
	public boolean isStandaloneCluster() {
		return zooKeeperAction.isStandaloneZooKeeper();
	}
	
}
