/**   
 * @Title: IPConfigHandle.java 
 * Package com.opzoon.appstatus.manager.handle 
 * Description: 集群修改IP地址的相关操作
 * @author NingYu   
 * @date 2014-4-9 下午1:38:50 
 * @version v1.0   
 * BugId: 2868
 */
package com.opzoon.appstatus.manager.handle;

import static com.opzoon.vdi.core.util.ConditionUtils.numberNotEquals;

import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.datasource.SimpleDriverDataSource;
import org.springframework.stereotype.Component;

import com.opzoon.appstatus.common.AppStatusConstants;
import com.opzoon.appstatus.common.ReadProperties;
import com.opzoon.appstatus.domain.Node;
import com.opzoon.appstatus.manager.NodeManager;
import com.opzoon.appstatus.queue.SendQueueExecutor;
import com.opzoon.vdi.core.util.RuntimeUtils;
import com.opzoon.vdi.core.ws.admin.SystemManagement.NetworkAdapter;

/**
 * 用户在Web界面修改IP地址后，由本类来负责集群中的IP地址修改任务。
 * 
 * @author <a href="mailto:mynameisny@qq.com">Tony Joseph</a>
 * @version 2.0
 */
@Component(value = "iPConfigHandle")
public class IPConfigHandle
{
	private static Logger logger = Logger.getLogger(IPConfigHandle.class);

	@Autowired
	private SendQueueExecutor sendQueueExecutor;
	
	public static boolean receivedHeartbeat;
	
	public static final int SLEEP_TIME = 5000;
	
	public static final int MAX_SLEEP_RETRIES = 30;
	
	
	/**
	 * 打印执行步骤日志
	 * @param msg 步骤名称
	 */
	private static void printFormativeLog(String msg)
	{
		final String separator = System.getProperty("line.separator");
		logger.info(separator + "<=Appstatus=> ------------------------------------------------------------------------ <=Appstatus=>" + separator + " " + msg + " " + separator + "<=Appstatus=> ------------------------------------------------------------------------ <=Appstatus=>" + separator);
	}
	
	public boolean changeClusterHostIP(String oldIPAddress, String newIPAddress) 
	{
		boolean result = false;
		logger.info("<=Appstatus=> -- ChangeIPAddress Begin -- ");
		//-------------------------------------------------------------------------------------------------------------------------------
		
		NodeManager.getCLIENT().close();
		
		SimpleDriverDataSource dataSource = new SimpleDriverDataSource();
		dataSource.setDriverClass(com.mysql.jdbc.Driver.class);
		dataSource.setUrl(ReadProperties.readProp(AppStatusConstants.ENV_PATH, "database.url"));
		dataSource.setUsername(ReadProperties.readProp(AppStatusConstants.ENV_PATH, "database.username"));
		dataSource.setPassword(ReadProperties.readProp(AppStatusConstants.ENV_PATH, "database.password"));
		JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);
		
		// TODO Step 1.遍历全部的记录，将所有包含旧IP地址的nodeAddress、senderAddress和clusterConfigure字段修改为新IP地址。
		printFormativeLog("Step 1");
		logger.debug("<=Appstatus=> -- ChangeIPAddress -- Use jdbcTemplate rather than a JPA implements, because the C3P0 connection pool problem.");
		String querySQL = "SELECT * FROM appstatusnode n WHERE n.nodeAddress = ? OR n.senderAddress = ? OR n.clusterConfigure LIKE ?";
		List<Node> targetNodes = jdbcTemplate.query(querySQL, new Object[]{oldIPAddress, oldIPAddress, "%"+oldIPAddress+"%"}, new RowMapper<Node>()
		{
			@Override
			public Node mapRow(ResultSet rs, int rowNum) throws SQLException
			{
				return getNodeByResultSet(rs, 0);
			}
		});
		logger.debug("<=Appstatus=> -- ChangeIPAddress -- GetTargetNodeFromDB, querySQL = " + querySQL +", oldIPAddress = " + oldIPAddress + ", newIPAddress = " + newIPAddress + ", nodes need to modify are: = " + targetNodes + ".");
		if (targetNodes != null && targetNodes.size() > 0)
		{
			Node targetNode = null;
			Long nodeId = 0L;
			String sql = "";
			for (int i = 0; i < targetNodes.size(); i++)
			{
				targetNode = targetNodes.get(i);
				nodeId = targetNode.getId();
				logger.debug("<=Appstatus=> -- ChangeIPAddress -- [Loop" + i + "]: targetNode=" + targetNode + ".");
				if (oldIPAddress.equals(targetNode.getNodeAddress()))
				{
					logger.debug("<=Appstatus=> -- ChangeIPAddress -- Executing update nodeAddress field start.");
					sql = "UPDATE appstatusnode SET nodeAddress=? WHERE id=?";
					repeatableUpdate(jdbcTemplate, sql, new Object[]{newIPAddress, nodeId});
					logger.debug("<=Appstatus=> -- ChangeIPAddress -- Executing update nodeAddress field end.");
				}
				if (oldIPAddress.equals(targetNode.getNodeAddress()))
				{
					logger.debug("<=Appstatus=> -- ChangeIPAddress -- Executing update nodeState field start.");
					sql = "UPDATE appstatusnode SET nodeState=? WHERE id=?";
					repeatableUpdate(jdbcTemplate, sql, new Object[]{1, nodeId});
					logger.debug("<=Appstatus=> -- ChangeIPAddress -- Executing update nodeState field end.");
				}
				if (oldIPAddress.equals(targetNode.getSenderAddress()))
				{
					logger.debug("<=Appstatus=> -- ChangeIPAddress -- Executing update senderAddress field start.");
					sql = "UPDATE appstatusnode SET senderAddress=? WHERE id=?";
					repeatableUpdate(jdbcTemplate, sql, new Object[]{newIPAddress, nodeId});
					logger.debug("<=Appstatus=> -- ChangeIPAddress -- Executing update senderAddress field end.");
				}
				if (targetNode.getClusterConfigure() != null && targetNode.getClusterConfigure().contains(oldIPAddress) && targetNode.getClusterConfigure().contains(removeDotFromIPAddress(oldIPAddress)))
				{
					String clusterConfigure = targetNode.getClusterConfigure().replace(oldIPAddress, newIPAddress).replace(removeDotFromIPAddress(oldIPAddress), removeDotFromIPAddress(newIPAddress));
					logger.debug("<=Appstatus=> -- ChangeIPAddress -- Executing update clusterConfigure field start.");
					sql = "UPDATE appstatusnode SET clusterConfigure=? WHERE id=?";
					repeatableUpdate(jdbcTemplate, sql, new Object[]{clusterConfigure, nodeId});
					logger.debug("<=Appstatus=> -- ChangeIPAddress -- Executing update clusterConfigure field end.");
				}
			}
		}
		else
		{
			logger.error("<=Appstatus=> -- ChangeIPAddress -- NodeUpdate DB error: could not find any nodes in database.");
			return false;
		}
		
		// Step 2. 根据传入的新IP地址，修改appstatus.properties配置文件中的键addr对应的值。
		printFormativeLog("Step 2");
		if (newIPAddress != null && oldIPAddress != null)
		{
			try
			{
				Map<String, String> propsMap = ReadProperties.readPropertiesToMap(AppStatusConstants.PROPERTIES_PATH);
				if (propsMap != null && !propsMap.isEmpty() && propsMap.containsKey("addr"))
				{
					logger.debug("<=Appstatus=> -- ChangeIPAddress -- Executing update properties file start.");
					ReadProperties.replaceToMap(propsMap, "addr", newIPAddress);
					ReadProperties.writeToProperties(propsMap, AppStatusConstants.PROPERTIES_PATH);
					logger.debug("<=Appstatus=> -- ChangeIPAddress -- Executing update properties file end.");
				}
				else
				{
					logger.error("<=Appstatus=> -- ChangeIPAddress -- Read appstatus.properties error, the file maybe empty or do not contains the key: 'addr'.");
					return false;
				}
			}
			catch (IOException e)
			{
				logger.error("<=Appstatus=> -- ChangeIPAddress -- Read appstatus.properties error, see details: e = " + e + ".");
				return false;
			}
		}
		
		// TODO  Step 3.移除旧IP地址的生成的Broker Service并用新的IP地址生成Broker
		printFormativeLog("Step 3");
		try
		{
			MessageHandle.rebuildBroker();
		}
		catch (Exception e)
		{
			logger.error("<=Appstatus=> -- ChangeIPAddress -- RebuildBroker.");
			return false;
		}
		
		// TODO  Step 4.收集所有正处于集群中的节点（serviceType=0），向这些节点发送心跳消息，只要收到一个心跳回执消息，则认为使新地址发送JMS消息成功
		printFormativeLog("Step 4");
		logger.debug("<=Appstatus=> -- ChangeIPAddress -- Executing collect heartbeat node start.");
		Collection<Node> nodes4HeartBeat = repeatableCollectionNode(jdbcTemplate, "SELECT * FROM appstatusnode n WHERE n.serviceType = ?", new Object[]{0});
		if (nodes4HeartBeat != null && nodes4HeartBeat.size() < 1)
		{
			logger.error("<=Appstatus=> -- ChangeIPAddress -- some error occured during collect heartbeat nodes which need to send heartbeat.");
			return false;
		}
		sendQueueExecutor.collectionHeartbeatNodes(nodes4HeartBeat);
		logger.debug("<=Appstatus=> -- ChangeIPAddress -- Executing collect heartbeat node end.");
		logger.debug("<=Appstatus=> -- ChangeIPAddress -- Executing send JMS heartbeat start.");
		repeatableSendHeartBeat();
		logger.debug("<=Appstatus=> -- ChangeIPAddress -- Executing send JMS heartbeat end.");
		
		
		// TODO  Step 5.向集群中发送更新节点消息
		printFormativeLog("Step 5");
		logger.debug("<=Appstatus=> -- ChangeIPAddress -- Executing collect update node start.");
		Collection<Node> nodes4Update = repeatableCollectionNode(jdbcTemplate, "SELECT * FROM appstatusnode n WHERE n.serviceType = ?", new Object[]{0});
		if (nodes4Update != null && nodes4Update.size() < 1)
		{
			logger.error("<=Appstatus=> -- ChangeIPAddress -- some error occured during collect nodes which need to send for update.");
			return false;
		}
		sendQueueExecutor.collectionUpdateNodes(nodes4Update);
		logger.debug("<=Appstatus=> -- ChangeIPAddress -- Executing collect update node end.");
		logger.debug("<=Appstatus=> -- ChangeIPAddress -- Executing send JMS cluster update start.");
		sendQueueExecutor.sendUpdateNodes();
		logger.debug("<=Appstatus=> -- ChangeIPAddress -- Executing send JMS cluster update end.");
		
		//-------------------------------------------------------------------------------------------------------------------------------
		logger.info("<=Appstatus=> -- ChangeIPAddress End -- ");
		return result;
	}
	
	
	/**
	 * 不断尝试更新DB操作，直到成功为止
	 * @param updateReq 封装更新条件和内容的实体Bean
	 */
	private void repeatableUpdate(JdbcTemplate jdbcTemplate, String sql, Object[] params)
	{
		int i = 1;
		while (i < MAX_SLEEP_RETRIES)
		{
			try
			{
				jdbcTemplate.update(sql, params);
				break;
			}
			catch (Exception e)
			{
				logger.debug("<=Appstatus=> -- ChangeIPAddress -- Some problem have ouccred during update DB, see details: " + e);
				logger.debug("<=Appstatus=> -- ChangeIPAddress -- Some problem have ouccred during update DB, try again...");
				try
				{
					Thread.sleep(SLEEP_TIME);
				}
				catch (InterruptedException interruptedException)
				{
					logger.debug("<=Appstatus=> -- ChangeIPAddress -- Some problem have ouccred during updateDB sleep.");
				}
			}
			i++;
		}
	}
	
	/**
	 * 不断尝试收集节点操作，直到成功为止
	 * @param nodeList
	 * @return
	 */
	private Collection<Node> repeatableCollectionNode(JdbcTemplate jdbcTemplate, String sql, Object[] params)
	{
		Collection<Node> resultNodes = null;
		int i = 1;
		while (i < MAX_SLEEP_RETRIES)
		{
			try
			{
				logger.debug("<=Appstatus=> -- SQL for collect node is: " + sql + ", and params: " + params + ".");
				resultNodes = jdbcTemplate.query(sql, params, new RowMapper<Node>()
				{
					@Override
					public Node mapRow(ResultSet rs, int rowNum) throws SQLException
					{
						return getNodeByResultSet(rs, 0);
					}
				});
				logger.debug("<=Appstatus=> -- Collection of collect node is: " + resultNodes + ".");
				break;
			}
			catch (Exception e)
			{
				logger.debug("<=Appstatus=> -- ChangeIPAddress -- Some problem have ouccred during collect node from DB, see details: " + e);
				logger.debug("<=Appstatus=> -- ChangeIPAddress -- Some problem have ouccred during collect node from DB, try again...");
				try
				{
					Thread.sleep(SLEEP_TIME);
				}
				catch (InterruptedException interruptedException)
				{
					logger.debug("<=Appstatus=> -- ChangeIPAddress -- Some problem have ouccred during collect node sleep.");
				}
			}
			i++;
		}
		return resultNodes;
	}
	
	/**
	 * 不断尝试发送心跳消息操用，直到成功为止
	 */
	private void repeatableSendHeartBeat()
	{
		int i = 1;
		while (i < MAX_SLEEP_RETRIES)
		{
			try
			{
				sendQueueExecutor.sendHeartBeat();
				checkIfHeartbeatReceived();
				break;
			}
			catch (Exception e)
			{
				logger.debug("<=Appstatus=> -- ChangeIPAddress -- Some problem have ouccred during send JMS heartbeat message, see details: " + e);
				logger.debug("<=Appstatus=> -- ChangeIPAddress -- Some problem have ouccred during send JMS heartbeat message, try again...");
				try
				{
					Thread.sleep(SLEEP_TIME);
				}
				catch (InterruptedException interruptedException)
				{
					logger.debug("<=Appstatus=> -- ChangeIPAddress -- Some problem have ouccred during send heartbeat sleep.");
				}
			}
			i++;
		}
	}
	
	/**
	 * 检测NodeManager类中的类成员变量isHeartbeatReceived是否为true，如为假则抛出异常
	 * @throws Exception
	 */
	private void checkIfHeartbeatReceived() throws Exception
	{
		if (!NodeManager.isHeartbeatReceived)
		{
			throw new Exception("heartbeat reply has not be received yet.");
		}
	}

	/**
	 * 将结果集中字段值构造成Node对象
	 * @param rs
	 * @param serviceType
	 * @return
	 * @throws SQLException
	 */
	private Node getNodeByResultSet(ResultSet rs, int serviceType) throws SQLException
	{
		long id = rs.getLong("id");
		String nodeAddress = rs.getString("nodeAddress");
		String clusterState = rs.getString("clusterState");
		int nodeState = rs.getInt("nodeState");
		String descInfo = rs.getString("descInfo");
		String senderAddress = rs.getString("senderAddress");
		String clusterConfigure = rs.getString("clusterConfigure");
		Node node = new Node(id, nodeAddress, clusterState, nodeState, descInfo, clusterConfigure, senderAddress, serviceType);
		return node;
	}

	/**
	 * 移除IP地址中的圆点
	 */
	private String removeDotFromIPAddress(String ip)
	{
		if (ip != null)
		{
			return ip.replace(".", "");
		}
		return "";
	}

	/**
	 * 获得网卡的相关信息
	 */
	public static NetworkAdapter getNetworkAdapterInfo()
	{
		Map<String, String> routes = getRoutes();
		StringBuilder ifconfigResult = new StringBuilder();
		int error = RuntimeUtils.shell(ifconfigResult, "ifconfig | grep -E 'Ethernet|Bcast' | sed -E 's/(^[0-9a-zA-Z]+) +Link encap:Ethernet.*/\\1/' | sed -E 's/ +inet addr:([0-9\\.]+).+Mask:([0-9\\.]+)/ \\1 \\2/'");
		if (numberNotEquals(error, 0))
		{
			return null;
		}
		StringTokenizer st = new StringTokenizer(ifconfigResult.toString(), "\r\n");
		NetworkAdapter networkAdapter = null;
		while (st.hasMoreTokens())
		{
			String line = st.nextToken();
			if (line.startsWith(" "))
			{
				StringTokenizer stLine = new StringTokenizer(line);
				networkAdapter.setIpaddr(stLine.nextToken());
				networkAdapter.setNetmask(stLine.nextToken());
			}
			else
			{
				String ethname = line;
				networkAdapter = new NetworkAdapter();
				networkAdapter.setIdnetworkadapter(ethname);
				networkAdapter.setGateway(routes.get(ethname));
			}
		}
		return networkAdapter;
	}

	public static Map<String, String> getRoutes()
	{
		StringBuilder netstatResult = new StringBuilder();
		/*
		 * Output example:
		 * 
		 * 192.168.44.2 eth0
		 */
		int error = RuntimeUtils.shell(netstatResult, "netstat -nr | grep -E '^0.0.0.0' | awk '{print $2,$8}'");
		if (numberNotEquals(error, 0))
		{
			return null;
		}
		Map<String, String> routes = new HashMap<String, String>();
		StringTokenizer st = new StringTokenizer(netstatResult.toString(), "\r\n");
		while (st.hasMoreTokens())
		{
			String line = st.nextToken();
			StringTokenizer stLine = new StringTokenizer(line);
			String route = stLine.nextToken();
			String ethname = stLine.nextToken();
			routes.put(ethname, route);
		}
		return routes;
	}
}