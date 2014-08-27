/**
 * 
 * @Title AppStatusService.java
 * @Description AppStatus模块提供的服务接口实现
 * Copyright: Copyright (c) 2013, Opzoon and/or its affiliates. All rights reserved.
 * OPZOON PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 * 
 * @author NY
 * @date 2013-10-22 上午11:02:07
 * 
 */
package com.opzoon.appstatus.facade.impl;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.concurrent.atomic.AtomicBoolean;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import com.opzoon.appstatus.common.AppStatusConstants;
import com.opzoon.appstatus.common.ParseJSON;
import com.opzoon.appstatus.common.ZkCrudUtil;
import com.opzoon.appstatus.common.exception.AppstatusExceptionHandle;
import com.opzoon.appstatus.common.exception.AppstatusRestException;
import com.opzoon.appstatus.common.exception.custom.AppstatusAuthenticateException;
import com.opzoon.appstatus.common.exception.custom.AppstatusForbidAddClusterException;
import com.opzoon.appstatus.common.exception.custom.AppstatusUpdateNotFinishedException;
import com.opzoon.appstatus.domain.ClusterState;
import com.opzoon.appstatus.domain.MessageType;
import com.opzoon.appstatus.domain.Node;
import com.opzoon.appstatus.domain.NodeState;
import com.opzoon.appstatus.domain.NodeUpdate;
import com.opzoon.appstatus.domain.PersistentMessage;
import com.opzoon.appstatus.domain.TaskMessage;
import com.opzoon.appstatus.domain.TaskState;
import com.opzoon.appstatus.domain.req.NodeAddressList;
import com.opzoon.appstatus.domain.req.NodeReq;
import com.opzoon.appstatus.domain.res.AppStatusResponse;
import com.opzoon.appstatus.domain.res.ListNodesBody;
import com.opzoon.appstatus.executor.dao.AppStatusDao;
import com.opzoon.appstatus.facade.AppStatusService;
import com.opzoon.appstatus.manager.ClusterManager;
import com.opzoon.appstatus.manager.MasterManager;
import com.opzoon.appstatus.manager.NodeManager;
import com.opzoon.appstatus.manager.PersistentMessageManager;
import com.opzoon.appstatus.manager.handle.AuthenticateHandle;
import com.opzoon.appstatus.manager.handle.IPConfigHandle;
import com.opzoon.appstatus.queue.SendQueueExecutor;
import com.opzoon.ohvc.common.Constants;
import com.opzoon.ohvc.common.OpzoonUtils;
import com.opzoon.ohvc.domain.Head;
import com.opzoon.ohvc.session.Session;
import com.opzoon.ohvc.session.State;
import com.opzoon.vdi.core.controller.Controller;
import com.opzoon.vdi.core.controller.TaskInfo;
import com.opzoon.vdi.core.controller.executor.CreateDesktopPoolExecutor;
import com.opzoon.vdi.core.util.BeanUtils;

/**
 * 
 * AppStatus模块提供的服务接口实现。
 * 
 * @author david,zhengyi,ningyu
 * @version 3.0 date: lastest time 2013-11-08
 * 
 */
@Service("appStatusService")
public class AppStatusServiceImpl implements AppStatusService {

	private static Logger logger = Logger.getLogger(AppStatusServiceImpl.class);
	@Autowired
	private SendQueueExecutor sendQueueExecutor;
	@Autowired
	private AppStatusDao appStatusDao;
	/*
	 * @Autowired private NodeManager nodeManager;
	 */
	@Autowired
	private Controller controller;
	@Autowired
	private AuthenticateHandle authenticateHandle;
	@Autowired
	private ClusterManager clusterManager;
	@Autowired
	private MasterManager masterManager;
	@Autowired
	private PersistentMessageManager persistentMessageManager;
	@Autowired
	private IPConfigHandle iPConfigHandle;

	private static AtomicBoolean isRun = new AtomicBoolean(false);

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.opzoon.appstatus.facade.AppStatusService#listNodes(com.opzoon.appstatus
	 * .domain.req.NodeReq)
	 */
	@Override
	public AppStatusResponse<Node> listNodes(NodeReq req)
			throws AppstatusRestException {
		logger.info("<=AppStatus=> listNodes: " + req);
		logger.debug("<=AppStatus=> listNodes: isRun!" + isRun.get());
		// ~~~~~~~~~~~~~~~
		//runThreadUpdateNodeState();
		ListNodesBody<Node> body = new ListNodesBody<Node>();
		List<Node> nodes = this.appStatusDao.findNodes(req);

		Head head = new Head();
		head.setError(0);
		head.setMessage("OK");

		AppStatusResponse<Node> resp = new AppStatusResponse<Node>();
		resp.setHead(head);

		body.setList(nodes);
		body.setAmount(nodes.size());
		if (getStatisticsInfo(nodes).containsKey("availableNum")) {
			body.setAvailableNum(getStatisticsInfo(nodes).get("availableNum")
					.size());
		}
		if (getStatisticsInfo(nodes).containsKey("unAvailableNum")) {
			body.setUnAvailableNum(getStatisticsInfo(nodes).get(
					"unAvailableNum").size());
		}
		resp.setBody(body);

		return resp;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.opzoon.appstatus.facade.AppStatusService#updateNodes(com.opzoon.appstatus
	 * .domain.req.NodeAddressList)
	 */
	@Override
	public AppStatusResponse<Node> updateNodes(NodeAddressList list)
			throws AppstatusRestException {
		logger.info("<=AppStatus=> updateNodes :" + list);

		if (!this.sendQueueExecutor.isUpdateFinished()) {
			throw AppstatusExceptionHandle
					.throwAppstatusException(new AppstatusUpdateNotFinishedException());
		}

		for (Node node : list.getNodes()) {
			if (0 == node.getServiceType()
					&& (NodeState.ERROR.equals(node.getNodeState()) || NodeState.LOST
							.equals(node.getNodeState()))) {
				throw AppstatusExceptionHandle
						.throwAppstatusException(new AppstatusForbidAddClusterException());
			}
		}

		Collection<Node> nodes = this.sendQueueExecutor.collectionNodes(list
				.getNodes());
		for (Node node : nodes) {
			if (0 == node.getServiceType()) {
				this.appStatusDao.updateNode(new NodeUpdate(node.getId(),
						new String[] { "clusterState", "nodeState",
								"senderAddress", "clusterConfigure",
								"serviceType", "master" }, new Object[] {
								ClusterState.DOWN, NodeState.ERROR.getValue(),
								node.getSenderAddress(),
								node.getClusterConfigure(),
								node.getServiceType(), false }));
			} else if (-1 == node.getServiceType()) {
				/*
				 * this.appStatusDao.updateNode(new NodeUpdate(node.getId(), new
				 * String[] { "clusterState", "nodeState", "senderAddress",
				 * "clusterConfigure", "serviceType", "master" }, new Object[] {
				 * node.getClusterState(), node.getNodeState().getValue(),
				 * node.getSenderAddress(), node.getClusterConfigure(),
				 * node.getServiceType(), false }));
				 */
				if (ClusterState.DOWN == node.getClusterState()) {
					this.appStatusDao.updateNode(new NodeUpdate(node.getId(),
							new String[] { "clusterState", "nodeState",
									"senderAddress", "clusterConfigure",
									"serviceType", "master" }, new Object[] {
									node.getClusterState(),
									node.getNodeState().getValue(),
									node.getSenderAddress(),
									node.getClusterConfigure(),
									node.getServiceType(), false }));
				} else {
					this.appStatusDao.updateNode(new NodeUpdate(node.getId(),
							new String[] { "clusterState", "nodeState",
									"senderAddress", "clusterConfigure",
									"serviceType", "master" }, new Object[] {
									ClusterState.EMPTY,
									NodeState.READY.getValue(),
									node.getSenderAddress(),
									node.getClusterConfigure(),
									node.getServiceType(), false }));
				}
			}
		}

		this.sendQueueExecutor.sendNodes();

		this.sendQueueExecutor.setUpdateFinished(false);

		AppStatusResponse<Node> resp = new AppStatusResponse<Node>();

		Head head = new Head();
		head.setError(0);
		head.setMessage("OK");

		resp.setHead(head);

		return resp;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.opzoon.appstatus.facade.AppStatusService#deleteNodes(com.opzoon.appstatus
	 * .domain.req.NodeAddressList)
	 */
	@Override
	public AppStatusResponse<Node> deleteNodes(NodeAddressList list) {
		logger.info("<=AppStatus=> deleteNodes :" + list.toString());

		AppStatusResponse<Node> resp = new AppStatusResponse<Node>();

		Set<Serializable> ids = new TreeSet<Serializable>();
		for (Node node : list.getNodes()) {
			ids.add(node.getId());
		}
		appStatusDao.deleteEntitiesByIds(Node.class, ids);

		Head head = new Head();
		head.setError(0);
		head.setMessage("OK");
		// maxiaochao
		// 替换缓存内容
		State<Node> state = Session.getStateBySeed(Constants.NODES_SEED);
		try {
			state.openDoor();

			for (Node node : list.getNodes()) {
				state.removeCache(node.getNodeAddress());
			}
			state.closeDoor();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		}
		// maxiaochao
		resp.setHead(head);

		return resp;
	}

	@Override
	public void publishTaskMessage(String content) {
		logger.info("<=AppStatus=> publishTaskMessage :" + content);
		try {
			Thread.sleep(50);
		} catch (InterruptedException e) {
			logger.info("<=AppStatus=> publishTaskMessage exception :" + e);
		}
		TaskMessage message = new TaskMessage();
		message.setMessageState(TaskState.PUBLISH);
		message.setContent(content);
		// 分发任务
		clusterManager.saveOrUpdateLocalClusterNode(ParseJSON
				.toTaskMessage(message));
	}

	@Override
	public void acceptTaskMessage(String content) {
		logger.info("<=AppStatus=> acceptTaskMessage :" + content);
		TaskMessage message = new TaskMessage();
		message.setMessageState(TaskState.ACCEPT);
		message.setContent(content);
		// 通知任务已经被抢走
		clusterManager.saveOrUpdateLocalClusterNode(ParseJSON
				.toTaskMessage(message));
	}

	@Override
	public void finishTaskMessage(String content) {
		TaskMessage message = new TaskMessage();
		message.setMessageState(TaskState.FINISHED);
		message.setContent(content);
		clusterManager.saveOrUpdateLocalClusterNode(ParseJSON
				.toTaskMessage(message));
	}

	@Override
	public void errorTaskMessage(String content) {
		TaskMessage message = new TaskMessage();
		message.setMessageState(TaskState.ERROR);
		message.setContent(content);
		clusterManager.saveOrUpdateLocalClusterNode(ParseJSON
				.toTaskMessage(message));
	}

	@Override
	public AppStatusResponse<Node> changeNetworkInterface(String oldIPAddress,
			String newIPAddress) throws AppstatusRestException {
		// TODO Auto-generated method stub
		boolean resultFlag = iPConfigHandle.changeClusterHostIP(oldIPAddress,
				newIPAddress);
		int resultCode = 0;
		String msg = "Some errors occured during execute step " + resultCode;
		if (resultFlag) {
			msg = "OK";
		}

		logger.info("<=AppStatus=> changeNetworkInterface msg=" + msg + ".");

		Head head = new Head();
		head.setError(resultCode);
		head.setMessage(msg);

		AppStatusResponse<Node> resp = new AppStatusResponse<Node>();
		resp.setHead(head);

		ListNodesBody<Node> body = new ListNodesBody<Node>();
		body.setList(null);
		resp.setBody(body);

		return resp;
	}

	@Override
	public boolean isInCluster() {
		/*
		 * boolean result = false; String ipAddress =
		 * NodeManager.getLocalNodeAddress(); if ("127.0.0.1".equals(ipAddress))
		 * { logger.fatal("<=AppStatus=> this host IP is 127.0.0.1, return !");
		 * return false; } NodeReq nodeReq = new NodeReq(); Node node = null;
		 * try { nodeReq.setNodeAddress(ipAddress);
		 * logger.debug("<=AppStatus=> isInCluster: ipAddress =" + ipAddress +
		 * ", Node = " + node + "."); node = appStatusDao.findNode(nodeReq); if
		 * (node != null && node.getId() != 0 && node.getNodeState() ==
		 * NodeState.RUNNING) { result = true; } } catch (Exception e) {
		 * logger.debug
		 * ("<=AppStatus=> isInCluster: Some error occured. ipAddress =" +
		 * ipAddress + ", Node = " + node + ", e = " + e); result = false; }
		 * return result;
		 */
		boolean result = false;
		String ipAddress = NodeManager.getLocalNodeAddress();
		if ("127.0.0.1".equals(ipAddress)) {
			logger.fatal("<=AppStatus=> this host IP is 127.0.0.1, return !");
			return false;
		}
		try {
			logger.debug("<=AppStatus=> isInCluster: ipAddress = " + ipAddress
					+ ".");
			result = clusterManager.isClusterNodeExists(ipAddress);
		} catch (Exception e) {
			logger.debug("<=AppStatus=> isInCluster: Some error occured. ipAddress = "
					+ ipAddress + ", e = " + e);
			result = false;
		}
		logger.debug("<=AppStatus=> isInCluster: result = " + result + ".");
		return result;
	}

	@Override
	public AppStatusResponse<?> testTaskExecute() {
		TaskInfo task = new TaskInfo();
		task.setExecutorClass(CreateDesktopPoolExecutor.class);
		task.setPara1("3");
		controller.sendTask(task);

		Head head = new Head();
		head.setError(0);
		head.setJobId(task.getId());
		head.setMessage("no error");

		AppStatusResponse<List<Node>> resp = new AppStatusResponse<List<Node>>();
		resp.setHead(head);

		return resp;
	}

	@Override
	public void publishPersistentMessage(PersistentMessage message)
			throws AppstatusRestException {
		message.setMessageType(MessageType.PersistenMessage);
		String data = ParseJSON.getGSON().toJson(message);
		logger.info("<=AppStatus=> publishPersistentMessage :" + data);
		try {
			if (NodeManager.getCLIENT() != null) {
				ZkCrudUtil
						.setData(NodeManager.getCLIENT(), NodeManager
								.makeNodePath(AppStatusConstants.MESSAGE_PATH,
										getMessageId(message.getId())), data
								.getBytes());
			}
		} catch (Exception e) {
			throw AppstatusExceptionHandle.throwAppstatusException(e);
		}
	}

	@Override
	public void removePersistentMessage(String messageId)
			throws AppstatusRestException {
		logger.info("<=AppStatus=> removePersistentMessage " + messageId);
		try {
			if (NodeManager.getCLIENT() != null)
				ZkCrudUtil.delete(NodeManager.getCLIENT(), NodeManager
						.makeNodePath(AppStatusConstants.MESSAGE_PATH,
								getMessageId(messageId)));
		} catch (Exception e) {
			throw AppstatusExceptionHandle.throwAppstatusException(e);
		}
	}

	@Override
	public PersistentMessage getPersistentMessageById(String messageId)
			throws AppstatusRestException {
		String message = "{}";
		try {
			logger.info("<=AppStatus=> getPersistentMessageById " + messageId);
			if (NodeManager.getCLIENT() != null) {
				message = ZkCrudUtil.getData(NodeManager.getCLIENT(),
						NodeManager.makeNodePath(
								AppStatusConstants.MESSAGE_PATH,
								getMessageId(messageId)));
				logger.info("<=AppStatus=> getPersistentMessageById " + message);
			}
		} catch (Exception e) {
			throw AppstatusExceptionHandle.throwAppstatusException(e);
		}
		return ParseJSON.getGSON().fromJson(message, PersistentMessage.class);
	}

	private static String getMessageId(String messageId) {
		return AppStatusConstants.MESSAGE_HEAD + OpzoonUtils.MD5(messageId);
	}

	@Override
	public AppStatusResponse<Object> checkAppStatusLogin(String userName,
			String password) throws AppstatusRestException {
		AppStatusResponse<Object> response = new AppStatusResponse<Object>();
		if (authenticateHandle.checkLogin(userName, password)) {
			Head head = new Head();
			head.setError(0);
			response.setHead(head);
		} else {
			throw AppstatusExceptionHandle
					.throwAppstatusException(new AppstatusAuthenticateException());
		}
		return response;
	}

	/**
	 * 获得可用主机和非可用主机的信息。
	 * 
	 * @param nodes
	 *            需要统计的节点的列表
	 * @return 统计后的节点集合
	 */
	private Map<String, List<Node>> getStatisticsInfo(List<Node> nodes) {
		Map<String, List<Node>> statisticsInfo = new HashMap<String, List<Node>>();

		List<Node> availableNode = new ArrayList<Node>();
		List<Node> unAvailableNode = new ArrayList<Node>();

		for (Node node : nodes) {
			if (node.getNodeState() == NodeState.RUNNING) {
				availableNode.add(node);
			} else if ((node.getNodeState() == NodeState.LOST)
					|| (node.getNodeState() == NodeState.ERROR)) {
				unAvailableNode.add(node);
			}
		}
		statisticsInfo.put("availableNum", availableNode);
		statisticsInfo.put("unAvailableNum", unAvailableNode);

		return statisticsInfo;
	}

	@Override
	public List<Node> listClusterNodes() throws AppstatusRestException {
		List<String> nodeNames = clusterManager.listClusterNodes();
		logger.info("<=AppStatus=> listClusterNodes: " + nodeNames);
		List<Node> nodes = new ArrayList<Node>();
		if (nodeNames != null) {
			for (String name : nodeNames) {
				Node node = new Node();
				node.setNodeAddress(name);
				nodes.add(node);
			}
		}
		return nodes;
	}

	@Override
	public Node getMasterNode() throws AppstatusRestException {
		String master = masterManager.getCurrentMasterNodeData();
		logger.info("<=AppStatus=> ###zhengyi### getMasterNode: " + master);
		Node node = new Node();
		if (master != null) {
			node.setNodeAddress(master);
			return node;
		}
		return null;
	}

	public static boolean accordToNodes(NodeReq req, Node node) {
		Node newNode = new Node();
		newNode.setId(req.getId());
		newNode.setClusterState(req.getClusterState());
		if (req.getNodeState() != -1)
			newNode.setNodeState(NodeState.parseNodestate(req.getNodeState()));
		newNode.setServiceType(req.getServiceType());

		HashMap<String, Object> map = BeanUtils
				.putPerpertiesAndValueMap(newNode);
		HashMap<String, Object> map2 = BeanUtils.putPerpertiesAndValueMap(node);
		for (String key : map.keySet()) {
			if (!map2.get(key).equals(map.get(key))) {
				logger.debug("accordToNodes ::" + key
						+ "=========================================="
						+ map2.get(key) + "======================="
						+ map.get(key));
				return false;
			}
		}

		return true;
	}

	@Scheduled(fixedDelay = 10000)
	public void runThreadUpdateNodeState() {
		logger.debug("runThreadUpdateNodeState :::===>");
		if (this.getMasterNode() != null) {
			String address = this.getMasterNode().getNodeAddress();
			if (address.equals(NodeManager.getLocalNodeAddress())) {
				logger.debug("<=AppStatus=> listNodes: thread !" + isRun.get());
				try {
					StringBuilder sb = new StringBuilder();
					List<Node> n2s = listClusterNodes();
					if (n2s.size() == 1) {
						NodeReq nodeReq = new NodeReq();
						nodeReq.setNodeAddress(n2s.get(0).getNodeAddress().trim());
						Node nodedb = appStatusDao.findNode(nodeReq);
						nodedb.setNodeState(NodeState.RUNNING);
						nodedb.setMaster(true);
						nodedb.setClusterState(ClusterState.CLUSTER);
						appStatusDao.updateNode(nodedb);
					} else {
						for (Node node : n2s) {
							NodeReq nodeReq = new NodeReq();
							nodeReq.setNodeAddress(node.getNodeAddress().trim());
							Node nodedb = appStatusDao.findNode(nodeReq);
							nodedb.setNodeState(NodeState.RUNNING);
							nodedb.setClusterState(ClusterState.CLUSTER);
							nodedb.setMaster(false);
							appStatusDao.updateNode(nodedb);
							sb.append(" {=node=}" + nodedb);
						}
						Node masterNode = getMasterNode();
						if (masterNode != null) {
							NodeReq nodeReq = new NodeReq();
							nodeReq.setNodeAddress(masterNode.getNodeAddress().trim());
							Node nodedb = appStatusDao.findNode(nodeReq);
							nodedb.setNodeState(NodeState.RUNNING);
							nodedb.setClusterState(ClusterState.CLUSTER);
							nodedb.setMaster(true);
							appStatusDao.updateNode(nodedb);
							sb.append(" {=masterNode=}" + nodedb);

						}
					}
					// ================step 1 node change ====
					Map<String, Object> where = new HashMap<String, Object>();
					Map<String, Object> notwhere = new HashMap<String, Object>();
					// ====================================
					NodeUpdate nodeUpdate = new NodeUpdate();
					nodeUpdate.setColumnNames(new String[] { "nodeState" });
					nodeUpdate.setColumnValues(new Object[] { NodeState.ERROR
							.getValue() });
					where.put("nodeState", NodeState.LOST.getValue());
					where.put("serviceType", -1);
					nodeUpdate.setNotWhere(notwhere);
					nodeUpdate.setWhere(where);
					appStatusDao.updateNode(nodeUpdate);

					// ==========update set nodestate where state is
					// not error
					// ===================
					NodeUpdate nodeUpdateReady = new NodeUpdate();
					nodeUpdateReady.setColumnNames(new String[] { "nodeState" });
					nodeUpdateReady.setColumnValues(new Object[] { NodeState.READY
							.getValue() });
					notwhere.clear();
					notwhere.put("nodeState", NodeState.ERROR.getValue());
					notwhere.put("serviceType", 0);
					nodeUpdateReady.setNotWhere(notwhere);
					appStatusDao.updateNode(nodeUpdateReady);
					// ================change
					// clusterState====================

					if (n2s.size() == 0) {
						NodeUpdate updateCluster = new NodeUpdate();
						updateCluster.setColumnNames(new String[] { "clusterState",
								"nodeState" });
						updateCluster.setColumnValues(new Object[] { ClusterState.DOWN,
								NodeState.LOST.getValue() });
						notwhere.clear();
						where.clear();
						where.put("serviceType", 0);
						notwhere.put("clusterState", ClusterState.EMPTY);
						updateCluster.setNotWhere(notwhere);
						updateCluster.setWhere(where);
						appStatusDao.updateNode(updateCluster);
					}
					logger.info("listNodes :::========>" + sb.toString());
				} catch (Exception e) {
					logger.error("listNodes ::: updateNodes error", e);
				}
			}
			
		}else{
			return;
		}
	}

	@Override
	public boolean isStandalone() throws AppstatusRestException {
		logger.info("<=AppStatus=> isStandalone");
//		AppStatusResponse<Node> resp = new AppStatusResponse<Node>();
//		
//		Head head = new Head();
//		head.setError(0);
//		head.setMessage("OK");
//		
//		IsStandaloneBody<Node> body = new IsStandaloneBody<Node>();
//		body.setStandalone(clusterManager.isStandaloneCluster());
//
//		resp.setHead(head);
//		resp.setBody(body);
		
		return clusterManager.isStandaloneCluster();
	}

}