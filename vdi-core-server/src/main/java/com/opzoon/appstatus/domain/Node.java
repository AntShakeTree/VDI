/**
 * 
 * @Title Node.java
 * @Description 节点实体类
 * Copyright: Copyright (c) 2013, Opzoon and/or its affiliates. All rights reserved.
 * OPZOON PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 * 
 * @author NY
 * @date 2013-10-22 上午11:02:07
 * 
 */
package com.opzoon.appstatus.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;

/**
 * 
 * 节点实体类。
 * 
 * @author <a href="mailto:mynameisny@qq.com">Tony Joseph</a>
 * @version 2.0
 * 
 */
@Entity
@Table(name = "appstatusnode")
public class Node implements Comparable<Node> {
	/**
	 * 节点id
	 */
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;

	/**
	 * 节点IP地址
	 */
	private String nodeAddress;

	/**
	 * 集群状态（EMPTY, SINGLE, DOWN, CLUSTER）
	 */
	@Enumerated(EnumType.STRING)
	private ClusterState clusterState = ClusterState.EMPTY;

	/**
	 * 节点状态（RUNNING, LOST, READY, ERROR）
	 */
	@Enumerated(EnumType.ORDINAL)
	@Column(name = "nodeState")
	private int nodeState = NodeState.READY.ordinal();

	/**
	 * 节点
	 */
	private String authentication;

	/**
	 * 队列发送者的IP地址
	 */
	private String senderAddress;

	/**
	 * 节点描述（留用）
	 */
	private String clusterConfigure;

	/**
	 * 操作指示（0：加入集群，-1：退出集群）
	 */
	private int serviceType = -1;

	/**
	 * Master节点指示（true：是，false：否）
	 */
	private boolean master = false;

	/**
	 * 队列消息是否已经发送给当前节点（true：是， false：否）
	 * <p>
	 * <i>该属性用来判断JMS消息是否发送到了某台主机，只作持久化前的判断使用，不需要进行保存在DB中。</i>
	 * </p>
	 */
	@Transient
	private boolean accept = false;

	private String descInfo;

	public Long getId() {
		return id;
	}

	public String getDescInfo() {
		return descInfo;
	}

	public void setDescInfo(String descInfo) {
		this.descInfo = descInfo;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getNodeAddress() {
		return nodeAddress;
	}

	public void setNodeAddress(String nodeAddress) {
		this.nodeAddress = nodeAddress;
	}

	public ClusterState getClusterState() {
		return clusterState;
	}

	public void setClusterState(ClusterState clusterState) {
		this.clusterState = clusterState;
	}

	public NodeState getNodeState() {
		return NodeState.parseNodestate(this.nodeState);
	}

	public void setNodeState(NodeState nodeState) {
		this.nodeState = nodeState.getValue();
	}



	public String getAuthentication() {
		return authentication;
	}

	public void setAuthentication(String authentication) {
		this.authentication = authentication;
	}

	public String getSenderAddress() {
		return senderAddress;
	}

	public void setSenderAddress(String senderAddress) {
		this.senderAddress = senderAddress;
	}

	public String getClusterConfigure() {
		return clusterConfigure;
	}

	public void setClusterConfigure(String clusterConfigure) {
		this.clusterConfigure = clusterConfigure;
	}

	public int getServiceType() {
		return serviceType;
	}

	public void setServiceType(int serviceType) {
		this.serviceType = serviceType;
	}

	public boolean isMaster() {
		return master;
	}

	public void setMaster(boolean master) {
		this.master = master;
	}

	public boolean isAccept() {
		return accept;
	}

	public void setAccept(boolean accept) {
		this.accept = accept;
	}

	public Node() {
	}

	public Node(String nodeAddress, ClusterState clusterState,
			NodeState nodeState, String authentication, String senderAddress,
			String clusterConfigure, int serviceType, boolean master,
			boolean accept) {
		super();
		this.nodeAddress = nodeAddress;
		this.clusterState = clusterState;
		this.nodeState = nodeState.getValue();
//		this.descInfo = descInfo;
		this.authentication=authentication;

		this.senderAddress = senderAddress;
		this.clusterConfigure = clusterConfigure;
		this.serviceType = serviceType;
		this.master = master;
		this.accept = accept;
	}

	public Node(Long id, String nodeAddress, ClusterState clusterState,
			NodeState nodeState, String descInfo, String senderAddress,
			String clusterConfigure, int serviceType, boolean master,
			boolean accept) {
		super();
		this.id = id;
		this.nodeAddress = nodeAddress;
		this.clusterState = clusterState;
		this.nodeState = nodeState.getValue();
		this.senderAddress = senderAddress;
		this.clusterConfigure = clusterConfigure;
		this.serviceType = serviceType;
		this.master = master;
		this.accept = accept;
		this.descInfo=descInfo;
	}

	public Node(long id, String nodeAddress, String clusterState,
			String nodeState, String descInfo, String config,
			String senderAddress, int serviceType) {
		this.id = id;
		this.nodeAddress = nodeAddress;
		this.nodeState = NodeState.valueOf(nodeState).getValue();
		this.clusterConfigure = config;
		this.senderAddress = senderAddress;
		this.serviceType = serviceType;
		for (ClusterState state : ClusterState.values()) {
			if (state.name().equals(clusterState)) {
				this.clusterState = state;
				break;
			}
		}

		this.descInfo = descInfo;
	}
	
	public Node(long id, String nodeAddress, String clusterState,
			int nodeState, String descInfo, String config,
			String senderAddress, int serviceType) {
		this.id = id;
		this.nodeAddress = nodeAddress;
		this.nodeState = nodeState;
		this.clusterConfigure = config;
		this.senderAddress = senderAddress;
		this.serviceType = serviceType;
		for (ClusterState state : ClusterState.values()) {
			if (state.name().equals(clusterState)) {
				this.clusterState = state;
				break;
			}
		}

		this.descInfo = descInfo;
	}
	
	/*
	 * public Node(String nodeAddress, String clusterState, String descInfo,
	 * String config, String senderAddress, int serviceType) { this.nodeAddress
	 * = nodeAddress; this.clusterConfigure = config; this.senderAddress =
	 * senderAddress; this.serviceType = serviceType; for (ClusterState state :
	 * ClusterState.values()) { if (state.name().equals(clusterState)) {
	 * this.clusterState = state; break; } }
	 * 
	 * this.descInfo = descInfo; }
	 */

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((nodeAddress == null) ? 0 : nodeAddress.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		Node other = (Node) obj;
		if (nodeAddress == null) {
			if (other.nodeAddress != null) {
				return false;
			}
		} else if (!nodeAddress.equals(other.nodeAddress)) {
			return false;
		} else if (nodeState != other.nodeState) {
			return false;
		} else {
			return true;
		}
		return true;
	}

	@Override
	public String toString() {
		return "Node [id=" + id + ", nodeAddress=" + nodeAddress
				+ ", clusterState=" + clusterState + ", nodeState=" + nodeState
				+ ", descInfo=" + descInfo + ", senderAddress=" + senderAddress
				+ ", clusterConfigure=" + clusterConfigure + ", serviceType="
				+ serviceType + ", master=" + master + ", accept=" + accept
				+ "]";
	}

	@Override
	public int compareTo(Node o) {
		if (o == null) {
			return -1;
		}
		if (this.equals(o)) {
			return 0;
		}
		// if (this.getId() > o.getId())
		if (this.getServiceType() > o.getServiceType()) {
			return 1;
		} else {
			return -1;
		}
	}

	/**
	 * 获得该节点的ID
	 * 
	 * @return 如果该节点的IP地址不为空，则ID为IP地址去圆点；否则，为当前时间的毫秒表示
	 */
	public long getMyid() {
		if (this.nodeAddress != null) {
			String myidStr = this.nodeAddress.replace(".", "");
			return Long.parseLong(myidStr);
		}
		return System.currentTimeMillis();
	}

}