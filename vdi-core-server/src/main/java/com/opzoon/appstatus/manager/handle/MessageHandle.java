package com.opzoon.appstatus.manager.handle;

import java.util.Map;

import javax.annotation.PostConstruct;
import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.MapMessage;
import javax.jms.MessageProducer;
import javax.jms.Session;

import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.activemq.broker.BrokerService;
import org.apache.activemq.broker.region.policy.PolicyEntry;
import org.apache.activemq.broker.region.policy.PolicyMap;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.opzoon.appstatus.common.AppStatusConstants;
import com.opzoon.appstatus.common.ReadProperties;
import com.opzoon.appstatus.domain.Node;
import com.opzoon.appstatus.manager.NodeManager;
import com.opzoon.appstatus.queue.SendQueueExecutor;

@Component("messageHandle")
public class MessageHandle {
	static Logger log = Logger.getLogger(MessageHandle.class);
	/*private transient ConnectionFactory factory;
	private transient Connection connection;
	private transient Session session;
	private transient MessageProducer producer;*/
	@Autowired
	private SendQueueExecutor sendQueueExecutor;
	
	public static BrokerService broker;
	
	/**
	 * 
	 * @author zhengyi
	 * 增加mq一种回复类型，startCompleted是在在此主机zookeeper建立节点后返回给发送主机
	 */
	public static class ReplyNumber {
		private final static int messageReceived = 1;
		private final static int startCompleted = 2;
		private final static int heartbeatReceived = 3;
		
		public static int getMessagereceived() {
			return messageReceived;
		}
		public static int getStartcompleted() {
			return startCompleted;
		}
		public static int getHeartbeatReceived() {
			return heartbeatReceived;
		}
	}

	/*public void close() throws JMSException {
		if (connection != null) {
			connection.close();
		}
	}*/

	public void sendNode(final Node node) throws JMSException {
		log.info("<=AppStatus=> sendNode  [" + node + "]");
		ConnectionFactory factory = new ActiveMQConnectionFactory(BrokerConfig.SENDER_URI(node.getNodeAddress()));
		Connection connection = factory.createConnection();
		connection.start();
		Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
		MessageProducer producer = session.createProducer(null);

		MapMessage mapMessage = session.createMapMessage();
		mapMessage.setLong("id", node.getId());
		mapMessage.setString("desc", node.getDescInfo());
		mapMessage.setString("nodeState", node.getNodeState().name());
		mapMessage.setString("clusterState", node.getClusterState().name());
		mapMessage.setString("nodeAddress", node.getNodeAddress());
		mapMessage.setString("senderAddress", node.getSenderAddress());
		mapMessage.setString("clusterConfigure", node.getClusterConfigure());
		mapMessage.setInt("serviceType", node.getServiceType());
		Destination destination = session.createQueue(BrokerConfig.QUEUENAME);
		producer.send(destination, mapMessage);
		producer.close();
		if (connection != null) {
			connection.close();
		}
		// close();
		// log.info(node);
	}

	public void sendReply(Node node, int replyNumber) throws JMSException {
		final String reply = node.getSenderAddress();
		// long id = node.getId();
		log.info("<=AppStatus=> sendReply nodeAddress is " + reply + " and replyNumber is " + replyNumber);
		ConnectionFactory factory = new ActiveMQConnectionFactory(BrokerConfig.SENDER_URI(reply));
		Connection connection = factory.createConnection();
		connection.start();
		Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
		MessageProducer producer = session.createProducer(null);

		MapMessage mapMessage = session.createMapMessage();
		mapMessage.setInt(AppStatusConstants.CONTINUE_PROCESS, replyNumber);
		// 发送回执的id要设置为0
		mapMessage.setLong("id", 0);
		Destination destination = session.createQueue(BrokerConfig.QUEUENAME);
		producer.send(destination, mapMessage);
		producer.close();
		if (connection != null) {
			connection.close();
		}
		//close();
	}
	
	public static void rebuildBroker() throws Exception
	{
		log.info("<=AppStatus=> rebuildBroker JMSBroker start");
		if (broker != null)
		{
			broker.removeConnector(broker.getConnectorByName(BrokerConfig.QUEUENAME));
			broker.stop();
			broker = new BrokerService();
			broker.addConnector(BrokerConfig.QUEUE_URI);
			broker.start();
		}
		log.info("<=AppStatus=> rebuildBroker JMSBroker end");
	}

	@PostConstruct
	public void startBroker() {
		try { 
			log.info("<=AppStatus=> startBroker start");
			// By default a broker always listens on vm://<broker name>
//			BrokerService broker = new BrokerService();
			broker = new BrokerService();
			// broker.setBrokerName("fast");

			// 设置activemq-data的生成路径，不设置会出现无法生成该目录的异常
			broker.setDataDirectory(AppStatusConstants.ACTIVEMQ_PATH);

			broker.setUseJmx(true);
			broker.getSystemUsage().getMemoryUsage()
					.setLimit(BrokerConfig.MEMORY_USAGE);
			// Set the Destination policies
			PolicyEntry policy = new PolicyEntry();
			// set a memory limit of 2mb for each destination
			policy.setMemoryLimit(1 * 1024 * 1024);
			// disable flow control
			broker.setPersistent(false);
			broker.getSystemUsage().getTempUsage()
					.setLimit(BrokerConfig.SYSTEM_USAGE);
			PolicyMap pMap = new PolicyMap();
			// configure the policy
			pMap.setDefaultEntry(policy);

			broker.setDestinationPolicy(pMap);
			broker.addConnector(BrokerConfig.QUEUE_URI);
			log.info("<=AppStatus=> startBroker end");
			broker.start();
		} catch (Exception e) {
			log.error("<=AppStatus=> startBroker  [" + e.getMessage() + "]");
		}
	}

	/**
	 * 
	 * ClassName: BrokerConfig Description: broker 配置内部类
	 * 
	 * @author david
	 * @date 2013-9-9 下午2:28:57 TODO :改名改成 61616配置
	 */
	private static class BrokerConfig {
		private final static String QUEUENAME = "nodeQueue";
		private final static String QUEUE_URI = ReadProperties
				.readProp("brokerURL");
		private final static int MEMORY_USAGE = 10 * 1024 * 1024;
		private final static int SYSTEM_USAGE = 50 * 1024 * 1024;

		/*
		 * 根据发送ip地址获得sender url
		 */
		private static String SENDER_URI(String SENDER) {
			log.debug("<sender> url : " + SENDER);
			return "tcp://" + SENDER + ":"
					+ ReadProperties.readProp("brokerPort");
		}
	}

	/**
	 * 
	 * @Title: getNodeByMap
	 * @Description: 从消息中解析Node
	 * @param @param message
	 * @param @return 设定文件
	 * @return Node 返回类型
	 * @throws
	 */
	public Node getNodeByMap(Map<String, Object> message) {
		if ((Long) message.get("id") != null && (Long) message.get("id") != 0l) {
			Long id = (Long) message.get("id");
			String nodeAddress = (String) message.get("nodeAddress");
			String clusterState = (String) message.get("clusterState");
			String nodeState = (String) message.get("nodeState");
			String desc = (String) message.get("desc");
			String config = (String) message.get("clusterConfigure");
			String senderAddress = (String) message.get("senderAddress");
			int serviceType = (Integer) message.get("serviceType");
			/*Node node = new Node(nodeAddress, clusterState, nodeState, desc, config, senderAddress, serviceType);
			node.setId(id);
			if ("ERROR".equalsIgnoreCase(nodeState)) {
				node.setNodeState(NodeState.ERROR.setValue(
						NodeState.VDICORE_APPSTATUE_ZK_NETWORK_ERROR)
						.setMessage("Inetnet error"));
			} else {
				node.setNodeState(NodeState.valueOf(nodeState));
			}*/
			Node node = new Node(id, nodeAddress, clusterState, nodeState, desc, config, senderAddress, serviceType);
			
			return node;
		} else {
			return null;
		}
	}

	/**
	 * 
	 * @Title: continueProcessNode
	 * @Description: 继续发送节点
	 * @param 设定文件
	 * @return void 返回类型
	 * @throws
	 */
	public void continueProcessNode(Map<String, Object> message) {
		/*if (message.get(AppStatusConstants.CONTINUE_PROCESS) != null
				&& (Integer) message.get(AppStatusConstants.CONTINUE_PROCESS) == ReplyNumber.messageReceived) {
			// 收到回复继续执行
			Long id = (Long) message.get("id");
			sendQueueExecutor.process(id);
		}*/
		log.debug("<= AppStatus => detect a message: " + message);
		if(message.get(AppStatusConstants.CONTINUE_PROCESS) != null) {
			if((Integer) message.get(AppStatusConstants.CONTINUE_PROCESS) == ReplyNumber.messageReceived) {
				// 收到回复，继续执行
				sendQueueExecutor.noticeMessageReceived();
			}else if((Integer) message.get(AppStatusConstants.CONTINUE_PROCESS) == ReplyNumber.startCompleted) {
				// 收到回复，通知发送者启动已完成
				sendQueueExecutor.noticeStartCompleted();
//			AtomicInteger in =new AtomicInteger(1);
//			in.incrementAndGet();
			}
			else if (((Integer) message.get(AppStatusConstants.CONTINUE_PROCESS) == ReplyNumber.heartbeatReceived))
			{
				// 修改心跳回跳回复已收到状态位为true, 通知IPConfigHandle继续执行
				NodeManager.isHeartbeatReceived = true;
				log.debug("<= AppStatus => isHeartbeatReceived: " + NodeManager.isHeartbeatReceived);
			}
		}
	}
}
