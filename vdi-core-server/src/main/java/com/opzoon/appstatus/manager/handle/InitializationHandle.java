/**
 * 
 * @Title InitializationHandle.java
 * @Description 在WEB容器启动时，执行初始化操作
 * Copyright: Copyright (c) 2013, Opzoon and/or its affiliates. All rights reserved.
 * OPZOON PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 * 
 * @author NY
 * @date 2013-10-22 上午11:02:07
 * 
 */
package com.opzoon.appstatus.manager.handle;

import java.io.IOException;
import java.net.UnknownHostException;
import java.util.List;

import javax.annotation.PostConstruct;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.opzoon.appstatus.common.ReadProperties;
import com.opzoon.appstatus.common.exception.AppstatusExceptionHandle;
import com.opzoon.appstatus.common.exception.AppstatusRestException;
import com.opzoon.appstatus.common.exception.custom.AppstatusDatabaseException;
import com.opzoon.appstatus.domain.ClusterState;
import com.opzoon.appstatus.domain.Node;
import com.opzoon.appstatus.domain.NodeState;
import com.opzoon.appstatus.domain.NodeUpdate;
import com.opzoon.appstatus.domain.req.NodeReq;
import com.opzoon.appstatus.executor.dao.AppStatusDao;
import com.opzoon.appstatus.executor.zookeeper.ZooKeeperAction;
import com.opzoon.appstatus.manager.NodeManager;
import com.opzoon.ohvc.common.Constants;
import com.opzoon.ohvc.session.ExcecutorUtil;
import com.opzoon.ohvc.session.Session;
import com.opzoon.ohvc.session.State;

/**
 * 
 * 在WEB容器启动时，执行初始化操作。
 * 
 * @author ningyu
 * @version V0.2.1023（迭代3）
 * 
 */
@Service(value = "initializationHandle")
public class InitializationHandle {
	private static Logger logger = Logger.getLogger(InitializationHandle.class);
	private String ipAddress;
	@Autowired
	private ZooKeeperAction zooKeeperAction;
	@Autowired
	private AppStatusDao appStatusDao;
	@Autowired
	private NodeManager nodeManager;

	@PostConstruct
	public void contextInitialized() {
		logger.debug("<=AppStatus=> [[ -- contextInitialized begin -- ]] ");
		Runnable task = new Runnable() {

			@Override
			public void run() {
				logger.debug("<=AppStatus=> [[ -- contextInitialized thread running -- ]] ");

				try {
					ipAddress = NodeManager.getLocalNodeAddress();
					if ("127.0.0.1".equals(ipAddress)) {
						logger.fatal("<=AppStatus=> this host IP is 127.0.0.1, return !");
						return;
					}
					zooKeeperAction.initZooKeeper();
				} catch (UnknownHostException e) {
					logger.fatal("<=AppStatus=> we can not found the current host's ip address!");
					return;
				} catch (IOException e) {
					logger.fatal("<=AppStatus=> initZooKeeper exception");
					return;
				}

				Node currentNode;
				try {
					currentNode = getNodeFromDB(ipAddress);
					if (currentNode != null) {
						logger.info("<=AppStatus=> contextInitialized NodeState is "
								+ currentNode.getNodeState());
						// add by ningyu 2014-04-09 bugId:3050 start
						if (currentNode.getServiceType() == -1) {
							/*
							 * 如果当前IP地址的节点在DB中的serviceType为-1，则表示该节点为未在服务状态，
							 * 需要在Tomcat启动时将节点的群状态修改为EMPTY、节点状态修改为READY。
							 */
							NodeUpdate nodeUpdate = new NodeUpdate(
									currentNode.getId(), new String[] {
											"clusterState", "nodeState" },
									new Object[] { ClusterState.EMPTY,
											NodeState.READY.getValue() });
							if (appStatusDao.updateNode(nodeUpdate) < 1) {
								logger.info("<=AppStatus=> contextInitialized Error --> NodeState's service type is -1, update DB to CLUSTER.EMPTY, NODE.READY, see: "
										+ currentNode.toString());
							}
						}// add by ningyu 2014-04-09 bugId:3050 end
						else {
							/*
							 * 如果当前IP地址的节点在DB中的serviceType为0，则表示该节点为仍在服务状态，
							 * 需要再继续判断当前IP地址节点在DB中状态是否为非READY状态
							 * ，（即LOST或者ERROR节点原为集群中节点但是目前处于异常状态
							 * ），则需要启动本机的ZK服务并执行节点恢复操作（创建客户端、添加监听）。
							 */
							if (currentNode.getNodeState() != NodeState.READY) {
								logger.info("<=AppStatus=> contextInitialized NodeState is not READY, start zk, node is "
										+ currentNode.toString());
								nodeManager.executeShell(currentNode);
								nodeManager.startNode(currentNode);
							}
						}
					} else {
						currentNode = new Node(ipAddress, ClusterState.EMPTY,
								NodeState.READY, null, null, null, -1, false,
								false);
						currentNode.setAuthentication(ReadProperties.readProp("key"));
						if (!insertNodeToDB(currentNode)) {
							logger.error("<=AppStatus=> Some error occured during insert a node to the DB.");
							return;
						}
					}
				} catch (AppstatusRestException e) {
					logger.error("<=AppStatus=> Some error occured during find the specified node from the DB.");
				}

			}
		};
		ExcecutorUtil.execute(task);

		logger.debug("<=AppStatus=> [[ -- contextInitialized end -- ]] ");
	}

	/**
	 * 根据指定IP地址从数据库中取得Node对象
	 * 
	 * @param ipAddress
	 *            需要查找的IP地址
	 * @throws AppstatusRestException
	 *             AppStatus自定义异常
	 */
	private Node getNodeFromDB(String ipAddress) throws AppstatusRestException {
		NodeReq nodeReq = new NodeReq();
		Node node = null;
		try {
			nodeReq.setNodeAddress(ipAddress);
			logger.debug("<=AppStatus=> getNodeFromDB: ipAddress =" + ipAddress
					+ ", Node = " + node + ".");
			node = appStatusDao.findNode(nodeReq);
		} catch (Exception e) {
			throw AppstatusExceptionHandle
					.throwAppstatusException(new AppstatusDatabaseException());
		}
		return node;
	}

	/**
	 * 将指定的Node持久化到数据库中
	 * 
	 * @param currentNode
	 *            需要保存的节点
	 * @return true
	 */
	private boolean insertNodeToDB(Node node) {
		appStatusDao.saveNode(node);
		NodeReq nodeReq = new NodeReq();
		try {
			List<Node> ls = appStatusDao.findNodes(nodeReq);
			State<Node> state = Session.getStateBySeed(Constants.NODES_SEED);
			state.openDoor();
			state.clearValidDataBySeed(Constants.NODES_SEED);
			for (Node n : ls) {
				state.putCache(node.getNodeAddress(), n);
			}
			state.putCache(node.getNodeAddress(), node);
			state.closeDoor();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (AppstatusRestException e1) {
			e1.printStackTrace();
		}

		logger.debug("<=AppStatus=> insertNodeToDB: node =" + node + ".");
		return true;
	}

	public ZooKeeperAction getZooKeeperAction() {
		return zooKeeperAction;
	}

	public void setZooKeeperAction(ZooKeeperAction zooKeeperAction) {
		this.zooKeeperAction = zooKeeperAction;
	}

	public AppStatusDao getAppStatusDao() {
		return appStatusDao;
	}

	public void setAppStatusDao(AppStatusDao appStatusDao) {
		this.appStatusDao = appStatusDao;
	}
}