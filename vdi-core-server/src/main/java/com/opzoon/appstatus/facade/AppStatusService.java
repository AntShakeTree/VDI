/**
 * 
 * @Title AppStatusService.java
 * @Description AppStatus模块提供的服务接口定义
 * Copyright: Copyright (c) 2013, Opzoon and/or its affiliates. All rights reserved.
 * OPZOON PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 * 
 * @author NY
 * @date 2013-10-22 上午11:02:07
 * 
 */
package com.opzoon.appstatus.facade;

import java.util.List;

import com.opzoon.appstatus.common.exception.AppstatusRestException;
import com.opzoon.appstatus.domain.Node;
import com.opzoon.appstatus.domain.PersistentMessage;
import com.opzoon.appstatus.domain.req.NodeAddressList;
import com.opzoon.appstatus.domain.req.NodeReq;
import com.opzoon.appstatus.domain.res.AppStatusResponse;

/**
 * 
 * AppStatus模块提供的服务接口定义。
 * 
 * @author david
 * 
 * @version V0.2.1023（迭代3） Date：2013-11-08
 */
public interface AppStatusService 
{
	/**
	 * 供Service层调用，指定范围查询，并返回结果全部满足条件的节点信息列表。
	 * @param req 查询条件
	 * @return 符合条件的节点列表
	 * @throws AppstatusRestException 
	 */
	public AppStatusResponse<Node> listNodes(NodeReq req) throws AppstatusRestException;

	/**
	 * 供Service层调用，添加或者删除集群中的节点。
	 * @param list 需要处理的节点地址列表
	 * @throws AppstatusRestException 
	 */
	public AppStatusResponse<Node> updateNodes(NodeAddressList list) throws AppstatusRestException;

	/**
	 * 供Service层调用，删除集群中的节点。
	 * <p>注意：</p>
	 * <p><i> -- 1、删除前需要确保该节点已经被成功移出集群。</i></p>
	 * <p><i> -- 2、如果只需要删除一个节点，也需要构成一个元素的NodeAddressList形式。</i></p>
	 * @param list 需要处理的节点列表
	 */
	public AppStatusResponse<Node> deleteNodes(NodeAddressList list);

	/**
	 * 供Controller层调用，发布任务时调用，通知其他Controller抢任务
	 * @param content 在Controller层实际使用的任务信息描述
	 */
	public void publishTaskMessage(String content);

	/**
	 * 供Controller层调用，抢到任务时调用，通知其他Controller任务已被抢走
	 * @param content 在Controller层实际使用的任务信息描述
	 */
	public void acceptTaskMessage(String content);

	/**
	 * 供Controller层调用，完成任务时调用，通知其他Controller任务已被完成
	 * @param content 在Controller层实际使用的任务信息描述
	 */
	public void finishTaskMessage(String content);

	/**
	 * 供Controller层调用，发生异常时调用，通知其他Controller此任务因为某种异常未被完成
	 * @param content 在Controller层实际使用的任务信息描述
	 */
	public void errorTaskMessage(String content);
	
	/**
	 * 供Facade层调用，当集群内某主机主动主动修改IP地址时调用
	 * @param oldIPAddress 修改前的旧IP地址
	 * @param newIPAddress 修改后的新IP地址
	 */
	public abstract AppStatusResponse<Node> changeNetworkInterface(String oldIPAddress, String newIPAddress) throws AppstatusRestException;

	/**
	 * 供Facade层调用，判断当前部署主机是否为集群中，判断方法为查询当前主机在DB中的状态是否为RUNNING状态
	 */
	public abstract boolean isInCluster();
	
	/**
	 * @deprecated 测试：模拟任务执行
	 * @return Header类型的响应对象（属性：error-错误代码，message-错误文字说明）
	 */
	public AppStatusResponse<?> testTaskExecute();

	/**
	 * 分布式发布持久化数据任务
	 * @param PersistentMessage 持久消息
	 */
	public void publishPersistentMessage(PersistentMessage message) throws AppstatusRestException;

	/**
	 * 移除持久消息
	 * @param 消息id messageId
	 */
	public void removePersistentMessage(String messageId) throws AppstatusRestException;

	/**
	 * 获取 PersistentMessage
	 * @param messageId
	 * @return PersistentMessage
	 */
	public PersistentMessage getPersistentMessageById(String messageId) throws AppstatusRestException;
	
	/**
	 * AppStatus登陆认证
	 * @param userName 用户名
	 * @param password 密码
	 * @return
	 */
	public AppStatusResponse<Object> checkAppStatusLogin(String userName, String password) throws AppstatusRestException;
	
	/**
	 * 调用该接口可以查看/cluster目录下所有的节点
	 * 通过查看节点是否存在可以判定集群的状态和主机的状态
	 * /cluster目录下没有节点：集群状态为down
	 * /cluster目录下存在的节点：该节点代表的主机状态为running
	 * @return 节点的集合
	 * @throws AppstatusRestException
	 */
	public List<Node> listClusterNodes() throws AppstatusRestException;
	
	/**
	 * 调用该接口可以查看/master目录下的节点
	 * 通过查看节点节点上的信息确定当前的master节点
	 * /master目录下没有节点：先调用checkClusterState接口查看集群是否down。如果是down，返回
	 * @return
	 * @throws AppstatusRestException
	 */
	public Node getMasterNode() throws AppstatusRestException;
	
	/**
	 * 调用该接口可以判断本主机是否是单机模式
	 * @return
	 * @throws AppstatusRestException
	 */
	public boolean isStandalone() throws AppstatusRestException;
	
}