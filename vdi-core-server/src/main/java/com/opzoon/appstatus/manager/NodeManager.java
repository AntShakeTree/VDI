package com.opzoon.appstatus.manager;

import java.io.IOException;
import java.net.Inet4Address;
import java.net.UnknownHostException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import javax.jms.JMSException;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.state.ConnectionState;
import org.apache.curator.framework.state.ConnectionStateListener;
import org.apache.curator.utils.ZKPaths;
import org.apache.log4j.Logger;
import org.apache.zookeeper.data.Stat;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.opzoon.appstatus.common.AppStatusConstants;
import com.opzoon.appstatus.common.ParseJSON;
import com.opzoon.appstatus.common.ReadProperties;
import com.opzoon.appstatus.common.RepairTools;
import com.opzoon.appstatus.common.ZkCrudUtil;
import com.opzoon.appstatus.common.ZookeeperClients;
import com.opzoon.appstatus.common.exception.AppstatusExceptionHandle;
import com.opzoon.appstatus.common.exception.AppstatusRestException;
import com.opzoon.appstatus.common.exception.custom.AppstatusDatabaseException;
import com.opzoon.appstatus.domain.ClusterConfigure;
import com.opzoon.appstatus.domain.ClusterState;
import com.opzoon.appstatus.domain.Node;
import com.opzoon.appstatus.domain.NodeConfig;
import com.opzoon.appstatus.domain.NodeState;
import com.opzoon.appstatus.domain.NodeUpdate;
import com.opzoon.appstatus.domain.RepairNode;
import com.opzoon.appstatus.domain.req.NodeReq;
import com.opzoon.appstatus.executor.Repair;
import com.opzoon.appstatus.executor.TomcatProcessRepair;
import com.opzoon.appstatus.executor.dao.AppStatusDao;
import com.opzoon.appstatus.executor.zookeeper.ZooKeeperAction;
import com.opzoon.appstatus.manager.handle.MessageHandle;
import com.opzoon.ohvc.common.ConfigUtil;
import com.opzoon.ohvc.session.DelayItem;
import com.opzoon.ohvc.session.Session;

@Component("nodeManager")
public class NodeManager {
	private static Logger log = Logger.getLogger(NodeManager.class);

	private static CuratorFramework client;

	@Autowired
	private ClusterManager clusterManager;
	@Autowired
	private MasterManager masterManager;
	@Autowired
	private PersistentMessageManager persistentMessageManager;
	@Autowired
	private MessageHandle messageHandle;
	@Autowired
	private ZooKeeperAction zooKeeperAction;
	@Autowired
	private AppStatusDao appStatusDao;

	public static boolean isHeartbeatReceived = false;

	// private static CuratorFramework CLIENT;

	private static volatile boolean isAvailable;

	protected static class ServiceType {
		protected final static int ADD = 0;
		protected final static int REMOVE = -1;
	}

	public void receiveNode(Map<String, Object> message) throws JMSException {
		log.info("<=AppStatus=> received : " + message.toString());
		// 收到回复继续发送node
		messageHandle.continueProcessNode(message);
		// 解析消息得到节点
		Node node = messageHandle.getNodeByMap(message);
		if (null == node) {
			return;
		}

		// 如果收到是心跳消息，即Node对象中descInfo属性的值是“AppstatusHeartbeat”，直接发送回复（不进行其他操作）
		if ("AppstatusHeartbeat".equals(node.getDescInfo())) {
			messageHandle.sendReply(node,
					MessageHandle.ReplyNumber.getHeartbeatReceived());
			log.info("<=AppStatus=> received heartbeat node: " + node
					+ ", flag = "
					+ MessageHandle.ReplyNumber.getHeartbeatReceived());
			return;
		}

		executeShell(node);
		// 收到消息，发送回复
		messageHandle.sendReply(node,
				MessageHandle.ReplyNumber.getMessagereceived());
		if (ServiceType.ADD == node.getServiceType()) {
			startNode(node);
		} else if (ServiceType.REMOVE == node.getServiceType()) {
			stopNode(node);
		}
	}

	public void startNode(Node node) {
		log.info("<=AppStatus=> startNode : " + node);

		NodeConfig config = getNodeConfig(node);
		String connectionString = config.getClientConectionConfig();
		log.info("<=AppStatus=> startNode " + connectionString);

		if (null != client)
			stopNode(node);
		client = ZookeeperClients.createSimple(connectionString);
		// CLIENT = client;
		addClientStateListener(node);
		try {
			clusterManager.setClient(client).start();
			masterManager.setClient(client).start();
			persistentMessageManager.setClient(client).start();
		} catch (Exception e) {
			log.info("<=AppStatus=> startNode exception : " + e);
		}
		client.start();
	}

	public void stopNode(Node node) {
		log.info("<=AppStatus=> stopNode : " + node);

		try {
			clusterManager.stop();
			masterManager.stop();
			persistentMessageManager.stop();
		} catch (IOException e) {
			log.info("<=AppStatus=> stopNode exception : " + e);
		}
		if (null != client)
			client.close();
	}

	private void addClientStateListener(final Node node) {
		log.info("<=AppStatus=> addClientStateListener");
		final String name = node.getNodeAddress();
		final String value = (null == node.getDescInfo()) ? NodeManager
				.getLocalNodeAddress() : node.getDescInfo();

		ConnectionStateListener clistener = new ConnectionStateListener() {

			@Override
			public void stateChanged(CuratorFramework client,
					ConnectionState newState) {
				switch (newState) {
				case CONNECTED: {
					//移除 修复队列
					RepairTools.removeRepair(name);
					// cluster
					log.info("<=AppStatus=> addClientStateListener CONNECTED");
					try {
						NodeReq nodeReq = new NodeReq();
						nodeReq.setNodeAddress(name);
						Node findNode = appStatusDao.findNode(nodeReq);
						if (null == findNode) {
							throw AppstatusExceptionHandle
									.throwAppstatusException(new AppstatusDatabaseException());
						}
						log.info("<=AppStatus=> CONNECTED node is "
								+ findNode.getServiceType());
						if (ServiceType.ADD == findNode.getServiceType()) {
							// 连接建立成功，立即触发sendReply方法
							messageHandle.sendReply(node,
									MessageHandle.ReplyNumber
											.getStartcompleted());
							findNode.setNodeState(NodeState.RUNNING);
							findNode.setClusterState(zooKeeperAction
									.isStandaloneZooKeeper() ? ClusterState.SINGLE
									: ClusterState.CLUSTER);
							NodeUpdate nodeUpdate = new NodeUpdate(
									findNode.getId(),
									new String[] { "clusterState", "nodeState" },
									new Object[] { findNode.getClusterState(),
											findNode.getNodeState().getValue() });
							appStatusDao.updateNode(nodeUpdate);
						}
					} catch (AppstatusRestException e) {
						log.error("<=AppStatus=> CONNECTED dao exception is "
								+ e);
					} catch (JMSException e) {
						log.error("<=AppStatus=> " + e);
					}
					clusterManager.createClusterNode(name, value);
					// master
					masterManager.createMasterNode(
							AppStatusConstants.MASTER_NAME, name);

					isAvailable = true;

					break;
				}
				case SUSPENDED: {
					log.info("<=AppStatus=> addClientStateListener SUSPENDED");
					try {
						NodeReq nodeReq = new NodeReq();
						nodeReq.setNodeAddress(name);
						Node findNode = appStatusDao.findNode(nodeReq);
						if (null == findNode) {
							throw AppstatusExceptionHandle
									.throwAppstatusException(new AppstatusDatabaseException());
						}
						log.info("<=AppStatus=> SUSPENDED node is "
								+ findNode.getServiceType());
						if (ServiceType.REMOVE == findNode.getServiceType()) {
							// 连接断开成功，立即触发sendReply方法
							messageHandle.sendReply(node,
									MessageHandle.ReplyNumber
											.getStartcompleted());
							findNode.setNodeState(NodeState.READY);
							findNode.setClusterState(ClusterState.EMPTY);
							findNode.setMaster(false);
							NodeUpdate nodeUpdate = new NodeUpdate(
									findNode.getId(), new String[] {
											"clusterState", "nodeState",
											"master" }, new Object[] {
											findNode.getClusterState(),
											findNode.getNodeState().getValue(),
											findNode.isMaster() });
							appStatusDao.updateNode(nodeUpdate);
						}
					} catch (AppstatusRestException e) {
						log.error("<=AppStatus=> SUSPENDED dao exception is "
								+ e);
					} catch (JMSException e) {
						log.error("<=AppStatus=> " + e);
					}

					isAvailable = false;

					break;
				}
				case RECONNECTED: {
					//移除 修复队列
					RepairTools.removeRepair(name);
					// cluster
					log.info("<=AppStatus=> addClientStateListener RECONNECTED");
					clusterManager.createClusterNode(name, value);
					// master
					masterManager.createMasterNode(
							AppStatusConstants.MASTER_NAME, name);

					isAvailable = true;

					break;
				}
				case LOST: {
					log.info("<=AppStatus=> addClientStateListener LOST");
					try {
						NodeReq nodeReq = new NodeReq();
						nodeReq.setNodeAddress(name);
						Node findNode = appStatusDao.findNode(nodeReq);
						if (null == findNode) {
							throw AppstatusExceptionHandle
									.throwAppstatusException(new AppstatusDatabaseException());
						}
						if (NodeState.RUNNING == findNode.getNodeState()
								&& !isAvailable) {
							NodeReq nodeReqList = new NodeReq();
							List<Node> nodeList = appStatusDao
									.findNodes(nodeReqList);
							for (Node thisNode : nodeList) {
								log.info("<=AppStatus=> addClientStateListener LOST, change database nodeId="
										+ thisNode.getId());
								if (ServiceType.ADD == thisNode
										.getServiceType()) {
									thisNode.setClusterState(ClusterState.DOWN);
									if (!NodeState.ERROR.equals(thisNode
											.getNodeState()))
										thisNode.setNodeState(NodeState.LOST);
									thisNode.setMaster(false);
									NodeUpdate nodeUpdate = new NodeUpdate(
											thisNode.getId(), new String[] {
													"clusterState",
													"nodeState", "master" },
											new Object[] {
													thisNode.getClusterState(),
													thisNode.getNodeState()
															.getValue(),
													thisNode.isMaster() });
									appStatusDao.updateNode(nodeUpdate);
								}
							}
						}
						//@Author maxiaochao
						if (findNode.getServiceType() != -1) {
							// dang tomcat 挂了时候修复它
							Repair repair = new TomcatProcessRepair();
							RepairNode repairNode = new RepairNode(name, ReadProperties.readProp("key"));
							if (!repair.normal(repairNode, "")) {
								if (masterManager.getCurrentMasterNodeData()
										.equals(getLocalNodeAddress())) {
									repair.repair(TomcatProcessRepair.REPAIRCOMMAND);
								}
							} else {
								if (masterManager.getCurrentMasterNodeData()
										.equals(getLocalNodeAddress())) {
								repairNode.setNeedRepair(true);
								repairNode
										.setCommand(TomcatProcessRepair.REPAIRCOMMAND);
								RepairTools.delayRepaire(repair, repairNode, 1,
										TimeUnit.MINUTES);
								}
							}
						}

					} catch (Exception e) {
						log.error("<=AppStatus=> some error occured during execute function: findNode Cause: "
								+ e.getCause() + " Message: " + e.getMessage());
					}
					break;
				}
				default:
					break;
				}
			}
		};
		client.getConnectionStateListenable().addListener(clistener);
	}

	public void executeShell(Node node) {
		log.info("<=AppStatus=> executeShell");
		NodeConfig config = getNodeConfig(node);
		switch (node.getServiceType()) {
		case ServiceType.ADD:
			// 调用shell 进行 zookeeper重启
			try {
				zooKeeperAction.updateZooKeeper(config.getServerConfig(),
						node.getMyid());
			} catch (AppstatusRestException e) {
				// 更新节点状态为Error
				log.error("<=AppStatus=> executeShell" + e.getMessage());
			}
			break;

		case ServiceType.REMOVE:
			try {
				zooKeeperAction.stopZooKeeper();
			} catch (AppstatusRestException e) {
				// 更新节点状态为Error
				log.error("<=AppStatus=> executeShell" + e.getMessage());
			}
			break;
		}
	}

	public static String makeNodePath(String path, String name) {
		/*
		 * if (name.contains("/")) {
		 * log.error("<=AppStatus=> createNode Invalid node name" + name); }
		 */
		return ZKPaths.makePath(path, name);
	}

	protected static void createNode(CuratorFramework client, String parent,
			String name, String data) {
		String path = makeNodePath(parent, name);
		log.info("<=AppStatus=> createNode : " + path);
		/*
		 * try { ZkCrudUtil.delete(client, path); } catch (Exception e) {
		 * log.error("<=AppStatus=> delete create node failure.." + e); }
		 */
		try {
			Stat stat = client.checkExists().forPath(parent);
			if (stat == null) {
				ZkCrudUtil.createEphemeral(client, path, data.getBytes());
			} else {
				ZkCrudUtil.createNotCreateParentEphemeral(client, path,
						data.getBytes());
			}
		} catch (Exception e) {
			log.error("<=AppStatus=> createNode create node failure.." + e);
		}
	}

	protected static String getNodeData(CuratorFramework client, String parent,
			String name) {
		String path = makeNodePath(parent, name);
		log.info("<=AppStatus=> getNodeData : " + path);
		String data = null;
		try {
			data = ZkCrudUtil.getData(client, path);
		} catch (Exception e) {
			log.error("<=AppStatus=> getNodeData get node data failure.." + e);
		}
		return data;
	}

	public static boolean isNodeExists(CuratorFramework client, String parent,
			String name) {
		boolean result = false;
		String path = makeNodePath(parent, name);
		log.info("<=AppStatus=> isNodeExists : " + path);
		try {
			result = ZkCrudUtil.isExists(client, path);
			log.info("<=AppStatus=> isNodeExists result: " + result);
		} catch (Exception e) {
			log.error("<=AppStatus=> isNodeExists get node data failure.." + e);
		}
		return result;
	}

	protected static void removeNode(CuratorFramework client, String parent,
			String name) {
		String path = makeNodePath(parent, name);
		log.info("<=AppStatus=> removeNode : " + path);
		try {
			Stat stat = client.checkExists().forPath(path);
			if (null != stat) {
				ZkCrudUtil.delete(client, path);
			}
		} catch (Exception e) {
			log.error("<=AppStatus=> removeNode remove node failure.." + e);
		}
	}

	protected static void saveOrUpdateNode(CuratorFramework client,
			String parent, String name, String data) {
		String path = makeNodePath(parent, name);
		log.info("<=AppStatus=> saveOrUpdateNode : " + path);
		try {
			ZkCrudUtil.setData(client, path, data.getBytes());
		} catch (Exception e) {
			log.error("<=AppStatus=> saveOrUpdateNode save node failure.." + e);
		}
	}

	protected static List<String> listNodes(CuratorFramework client,
			String parent) {
		List<String> result = null;
		try {
			result = ZkCrudUtil.watchedGetChildren(client, parent);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}

	public static String getLocalNodeAddress() {
		String ipAddressInProperties = ReadProperties.readProp("addr");
		if (null == ipAddressInProperties || "".equals(ipAddressInProperties)) {
			try {
				log.info("<=AppStatus=> Get local ip address from /etc/hosts file:"
						+ ipAddressInProperties
						+ ". Attention, this ops may by cause cluster faild!");
				ipAddressInProperties = Inet4Address.getLocalHost()
						.getHostAddress();
			} catch (UnknownHostException e) {
				log.error("<=AppStatus=> getLocalNodeAddress exception:" + e);
			}
		}
		return ipAddressInProperties;
	}

	private NodeConfig getNodeConfig(Node node) {
		List<ClusterConfigure> clusterConfigureList = ParseJSON
				.parseClusterConfigures(node.getClusterConfigure());
		StringBuilder clientConectionSb = new StringBuilder();
		StringBuilder configSb = new StringBuilder();
		for (ClusterConfigure clusterConfigure : clusterConfigureList) {
			int type = clusterConfigure.getServiceType();
			if (type == ServiceType.REMOVE) {
				continue;
			}
			String ip = clusterConfigure.getNodeAddress();
			long myId = clusterConfigure.getMyid();
			clientConectionSb.append(ip + ":2181,");
			configSb.append("server." + myId + "=" + ip + ":2888:3888;");
			// configSb.append("\r");
		}
		String clientConectionStr = clientConectionSb.toString();
		int lastIndexOfComma = clientConectionStr.lastIndexOf(",");
		if (lastIndexOfComma != -1) {
			clientConectionStr = clientConectionStr.substring(0,
					lastIndexOfComma);
		}
		return new NodeConfig().setClientConectionConfig(clientConectionStr)
				.setServerConfig(configSb.toString());
	}

	public static CuratorFramework getCLIENT() {
		// return CLIENT;
		return client;
	}

}
