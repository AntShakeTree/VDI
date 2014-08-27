/**   
  * Title: SendQueue.java 
 * Package com.opzoon.appstatus.queue 
 * Description:
 * @author david  
 * @Date 2013-7-17 下午5:28:46 
 * @version V1.0   
 */
package com.opzoon.appstatus.queue;

import java.util.Collection;
import java.util.Map;
import java.util.Queue;
import java.util.Timer;
import java.util.TimerTask;
import java.util.TreeSet;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import javax.jms.JMSException;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.opzoon.appstatus.common.AppStatusConstants;
import com.opzoon.appstatus.common.ParseJSON;
import com.opzoon.appstatus.common.exception.AppstatusRestException;
import com.opzoon.appstatus.domain.Node;
import com.opzoon.appstatus.domain.req.NodeReq;
import com.opzoon.appstatus.executor.dao.AppStatusDao;
import com.opzoon.appstatus.manager.NodeManager;
import com.opzoon.appstatus.manager.handle.MessageHandle;
import com.opzoon.ohvc.session.ExcecutorUtil;

/**
 * ClassName: SendQueue Description: 分发队列
 * 
 * @author David
 * @date 2013-7-17 下午5:28:46
 */
@Component("sendQueueExecutor")
public class SendQueueExecutor {

	private static Queue<Node> SENDQUEUE = new ConcurrentLinkedQueue<Node>();
	@Autowired
	private AppStatusDao appStatusDao;
	@Autowired
	private MessageHandle messageHandle;
	private static Logger log = Logger.getLogger(SendQueueExecutor.class);
	//private static final CountDownLatch queueLatch = new CountDownLatch(1);
	private CountDownLatch queueLatch;
	
	private AtomicInteger nodeAmount; 
	
	private Timer timer;
	/*public SendQueueExecutor() {
		if(null == SENDQUEUE) {
			SENDQUEUE = new ConcurrentLinkedQueue<Node>();
		}
	}*/

	// update操作是否完成
	private boolean isUpdateFinished = true;
	
	public boolean isUpdateFinished() {
		return isUpdateFinished;
	}

	public void setUpdateFinished(boolean isUpdateFinished) {
		this.isUpdateFinished = isUpdateFinished;
		updateFinishedTimer();
	}
	
	/**
	 * @author zhengyi
	 * 在有主机无法返回完成确认时，标志位将在延迟时间后自动改变
	 */
	private void updateFinishedTimer() {
		timer = new Timer();
		timer.schedule(new TimerTask() {
			@Override
			public void run() {
				isUpdateFinished = true;
			}
		}, AppStatusConstants.TIMER_DELAY_MILLISECOND);
	}

	/**
	 * @throws AppstatusRestException 
	 * @Title: collectionNodes
	 * @Description: 收集 队列
	 * @param nodes
	 * @return void 返回类型
	 * @throws
	 */
	public Collection<Node> collectionNodes(Collection<Node> list) throws AppstatusRestException {
		// 收集collection
		Collection<Node> nodes = new TreeSet<Node>();
		for (Node node : generateListReq(list)) {
			// 添加发送者
			node.setSenderAddress(NodeManager.getLocalNodeAddress());
			nodes.add(node);
		}
		ParseJSON.toClusterConfigures(nodes);
		log.info("<=AppStatus=> collectionNodes all nodes" + nodes);
		nodeAmount = new AtomicInteger(nodes.size());
		SENDQUEUE.addAll(nodes);
		
		return nodes;
	}

	/**
	 * 
	 * @Title: sendNodes
	 * @Description: 发送节点
	 * @return void
	 * @throws
	 */
	public void sendNodes() {
		log.info("<=AppStatus=> sendNodes start.");
		
		ExcecutorUtil.execute(new Runnable() {
			public void run() {
				while (!SENDQUEUE.isEmpty()) {
					queueLatch = new CountDownLatch(1);
					Node node = SENDQUEUE.poll();
					//node有可能为null
					if(null == node) continue;
					try {
						
						messageHandle.sendNode(node);
						queueLatch.await(AppStatusConstants.LATCH_TIMEOUT_SECOND, TimeUnit.SECONDS);
						log.info("<=AppStatus=> sendNodes go on continue send node..");
					} catch (JMSException e) {
						log.error("<=AppStatus=>  sendNodes Exception[" + e + "]");
					} catch (InterruptedException e) {
						log.error("<=AppStatus=>  sendNodes Exception[" + e + "]");
					}
				}
			}
		});
	}
	
	/**
	 * 向队列中的全部节点发送心跳消息
	 * @throws JMSException
	 */
	public void sendHeartBeat()
	{
		log.debug("<=AppStatus=> ----- sendHeartbeat ----- start.");
		Node node = null;
		while (SENDQUEUE.peek() != null)
		{
			node = SENDQUEUE.poll();
			node.setDescInfo("AppstatusHeartbeat");
			log.debug("<=AppStatus=> ----- sendHeartbeat ----- poll out a node = " + node);
			try
			{
				messageHandle.sendNode(node);
			}
			catch (JMSException e)
			{
				log.error("<=Appstatus=> -- ChangeIPAddress -- Some problem have ouccred during send JMS heartbeat message, see details: " + e);
			}
		}
		log.debug("<=AppStatus=> ----- sendHeartbeat ----- end.");
	}
	public Collection<Node> collectionHeartbeatNodes(Collection<Node> nodes)
	{
		SENDQUEUE.addAll(nodes);
		return nodes;
	}

	/**
	 * 向队列中的全部节点发送集群更新消息
	 */
	public void sendUpdateNodes()
	{
		log.info("<=AppStatus=> sendNodes start.");
		
		while (!SENDQUEUE.isEmpty())
		{
			log.debug("<=AppStatus=> sendNodes for update is not empty.");
			Node node = SENDQUEUE.poll();
			if (null == node) continue;
			try
			{
				messageHandle.sendNode(node);
				log.info("<=AppStatus=> sendNodes for update has invoked!");
			}
			catch (JMSException e)
			{
				log.error("<=AppStatus=>  sendNodes Exception[" + e + "]");
			}
		}
	}
	public Collection<Node> collectionUpdateNodes(Collection<Node> nodes)
	{
		SENDQUEUE.addAll(nodes);
		return nodes;
	}

	/**
	 * 
	 * @Title: noticeMessageReceived
	 * @Description: 让队列继续处理发送节点
	 */
	public void noticeMessageReceived() {
		log.info("<=AppStatus=> noticeMessageReceived countDown");
		queueLatch.countDown();
	}

	public MessageHandle getMessageHandle() {
		return messageHandle;
	}

	public void setMessageHandle(MessageHandle messageHandle) {
		this.messageHandle = messageHandle;
	}

	/**
	 * @author zhengyi 
	 * @return Collection<Node> list 将请求的数据与数据库中的serviceType=0的数据组合，形成请求数据
	 * @throws AppstatusRestException 
	 */
	private Collection<Node> generateListReq(Collection<Node> list) throws AppstatusRestException {
		Collection<Node> listReq = new TreeSet<Node>();
		// 根据list中nodeId查询数据库，更新node
		for(Node node : list) {
			NodeReq nodeReq = new NodeReq();
			nodeReq.setId(node.getId());
			Node findNode = this.appStatusDao.findNode(nodeReq);
			node.setNodeState(findNode.getNodeState());
			node.setClusterState(findNode.getClusterState());
			listReq.add(node);
		}
		
		NodeReq nodeReq = new NodeReq();
		nodeReq.setServiceType(0);
		for(Node node : this.appStatusDao.findNodes(nodeReq)) {
			listReq.add(node);
		}
		
		return listReq;
	}
	
	public synchronized void noticeStartCompleted() {
		log.info("<=AppStatus=> noticeStartCompleted nodeAmount is " + nodeAmount);
		if(0 == nodeAmount.decrementAndGet()) {
			timer.cancel();
			try {
				Thread.sleep(20000);
			} catch (InterruptedException e) {
				log.error("<=AppStatus=> noticeStartCompleted Exception[" + e + "]");
			}
			isUpdateFinished = true;
		}
	}
}
