/**
 * 
 * @Title NodeReq.java
 * @Description 节点查询条件实体类
 * Copyright: Copyright (c) 2013, Opzoon and/or its affiliates. All rights reserved.
 * OPZOON PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 * 
 * @author NY
 * @date 2013-10-22 上午11:02:07
 * 
 */
package com.opzoon.appstatus.domain.req;

import com.opzoon.appstatus.domain.ClusterState;
import com.opzoon.appstatus.domain.Node;
import com.opzoon.ohvc.common.anotation.Required;
import com.opzoon.ohvc.request.PageRequest;

/**
 * NodeReq 节点查询条件实体类。
 * 
 * @author david
 * @version V0.2.1023（迭代3） Date：2013-11-08
 */
public class NodeReq implements PageRequest<Node> {
	/**
	 * 节点id
	 */
	private Long id;

	/**
	 * 节点IP地址
	 */
	@Required
	private String nodeAddress;

	/**
	 * 集群状态（EMPTY, SINGLE, DOWN, CLUSTER）
	 */
	private ClusterState clusterState;

	/**
	 * 节点状态（RUNNING, LOST, READY, ERROR）
	 */
	private int nodeState = -1;

	/**
	 * 操作指示（0：加入集群，-1：退出集群）
	 */
	@Required
	private Integer serviceType;

	/**
	 * 排序关键字
	 */
	private String sortkey;

	/**
	 * 排序条件（0：降序排列，1：升序排列）
	 */
	private int ascend;

	/**
	 * 页号，从1开始
	 */
	private int page = 1;

	/**
	 * 每页数量
	 */
	private int pagesize = -1;

	/**
	 * 总记录数
	 */
	private int amount;

	public Long getId() {
		return id;
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

	public int getNodeState() {
		return nodeState;
	}

	public void setNodeState(int nodeState) {
		this.nodeState = nodeState;
	}

	public Integer getServiceType() {
		return serviceType;
	}

	public void setServiceType(Integer serviceType) {
		this.serviceType = serviceType;
	}

	public String getSortkey() {
		return sortkey;
	}

	public void setSortkey(String sortkey) {
		this.sortkey = sortkey;
	}

	public int getAscend() {
		return ascend;
	}

	public void setAscend(int ascend) {
		this.ascend = ascend;
	}

	public int getPage() {
		return page;
	}

	public void setPage(int page) {
		this.page = page;
	}

	public int getPagesize() {
		return pagesize;
	}

	public void setPagesize(int pagesize) {
		this.pagesize = pagesize;
	}

	public int getAmount() {
		return amount;
	}

	public void setAmount(int amount) {
		this.amount = amount;
	}

	public NodeReq() {
	}

	public NodeReq(String nodeAddress, ClusterState clusterState,
			int nodeState, Integer serviceType, String sortkey, int ascend,
			int page, int pagesize, int amount) {
		super();
		this.nodeAddress = nodeAddress;
		this.clusterState = clusterState;
		this.nodeState = nodeState;
		this.serviceType = serviceType;
		this.sortkey = sortkey;
		this.ascend = ascend;
		this.page = page;
		this.pagesize = pagesize;
		this.amount = amount;
	}

	public NodeReq(Long id, String nodeAddress, ClusterState clusterState,
			int nodeState, Integer serviceType, String sortkey, int ascend,
			int page, int pagesize, int amount) {
		super();
		this.id = id;
		this.nodeAddress = nodeAddress;
		this.clusterState = clusterState;
		this.nodeState = nodeState;
		this.serviceType = serviceType;
		this.sortkey = sortkey;
		this.ascend = ascend;
		this.page = page;
		this.pagesize = pagesize;
		this.amount = amount;
	}

	@Override
	public String toString() {
		return "NodeReq [id=" + id + ", nodeAddress=" + nodeAddress
				+ ", clusterState=" + clusterState + ", nodeState=" + nodeState
				+ ", serviceType=" + serviceType + ", sortkey=" + sortkey
				+ ", ascend=" + ascend + ", page=" + page + ", pagesize="
				+ pagesize + ", amount=" + amount + "]";
	}
}
