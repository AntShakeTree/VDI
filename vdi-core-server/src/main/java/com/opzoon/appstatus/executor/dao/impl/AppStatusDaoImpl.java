/**
 * 
 * @Title AppStatusDaoImpl.java
 * @Description AppStatus模块内部的数据库访问对象接口实现
 * Copyright: Copyright (c) 2013, Opzoon and/or its affiliates. All rights reserved.
 * OPZOON PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 * 
 * @author NY
 * @date 2013-10-22 上午11:02:07
 * 
 */
package com.opzoon.appstatus.executor.dao.impl;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.opzoon.appstatus.common.exception.AppstatusExceptionHandle;
import com.opzoon.appstatus.common.exception.AppstatusRestException;
import com.opzoon.appstatus.common.exception.custom.AppstatusDatabaseException;
import com.opzoon.appstatus.domain.ClusterState;
import com.opzoon.appstatus.domain.Node;
import com.opzoon.appstatus.domain.NodeState;
import com.opzoon.appstatus.domain.NodeUpdate;
import com.opzoon.appstatus.domain.req.NodeReq;
import com.opzoon.appstatus.executor.dao.AppStatusDao;
import com.opzoon.vdi.core.facade.DatabaseFacade;
import com.opzoon.vdi.core.util.BeanUtils;

/**
 * 
 * AppStatus模块内部的数据库访问对象接口实现。
 * 
 * @author <a href="mailto:mynameisny@qq.com">Tony Joseph</a>
 * @version 2.0
 * 
 */
@Repository("appStatusDao")
public class AppStatusDaoImpl implements AppStatusDao {
	private static Logger logger = Logger.getLogger(AppStatusDaoImpl.class);
	@Autowired
	private DatabaseFacade databaseFacade;

	@Override
	@Transactional
	public void saveNode(Node node) {
		logger.debug("<=AppStatus=> saveNode: node = " + node + ".");
		databaseFacade.persist(node);
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<Node> findNodes(String jpaSql, Object[] params) {
		logger.debug("<=AppStatus=> findNodes: condString = " + jpaSql
				+ ", sql = " + jpaSql + ".");
		List<?> list = null;
		if (jpaSql != null && !"".equals(jpaSql)) {
			list = databaseFacade.find(jpaSql, params);
		}
		return (List<Node>) list;
	}

	@Override
	@Transactional
	public Node findNode(NodeReq nodeReq) throws AppstatusRestException {
		List<Node> nodeList = new ArrayList<Node>();
		try {
			logger.debug("<=AppStatus=> findNode: nodeReq = " + nodeReq
					+ ", nodeList = " + nodeList + ".");
			nodeList = this.findNodes(nodeReq);
		} catch (Exception e) {
			throw AppstatusExceptionHandle
					.throwAppstatusException(new AppstatusDatabaseException());
		}
		if (nodeList.size() > 0) {
			return nodeList.get(0);
		} else {
			return null;
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	@Transactional
	public List<Node> findNodes(NodeReq nodeReq) throws AppstatusRestException {
		String sql = "SELECT n FROM Node n WHERE 1=1 ";
		List<Object> params = new ArrayList<Object>();
		int i = 1;
		if (nodeReq.getId() != null) {
			sql = sql + " AND n.id = ?" + i;
			params.add(nodeReq.getId());
			i++;
		}
		if (nodeReq.getNodeAddress() != null
				&& !"".equals(nodeReq.getNodeAddress())) {
			sql = sql + " AND n.nodeAddress = ?" + i;
			params.add(nodeReq.getNodeAddress());
			i++;
		}
		if (nodeReq.getClusterState() != null
				&& !"".equals(nodeReq.getClusterState())) {
			sql = sql + " AND n.clusterState = ?" + i;
			params.add(nodeReq.getClusterState());
			i++;
		}
		if (nodeReq.getNodeState() != -1 && !"".equals(nodeReq.getNodeState())) {
			sql = sql + " AND n.nodeState = ?" + i;
			params.add(nodeReq.getNodeState());
			i++;
		}
		if (nodeReq.getServiceType() != null) {
			sql = sql + " AND n.serviceType = ?" + i;
			Integer serviceType = nodeReq.getServiceType();
			params.add(serviceType);
			i++;
		}
		if (nodeReq.getSortkey() != null && !"".equals(nodeReq)) {
			sql = sql + " ORDER BY n." + nodeReq.getSortkey();

			if (nodeReq.getAscend() == 0) {
				sql = sql + " DESC";
			} else {
				sql = sql + " ASC";
			}
		}
		List<?> list = new ArrayList<Node>();
		try {
			logger.debug("<=AppStatus=> findNodes: NodeReq = " + nodeReq
					+ ", sql = " + sql + ".");
			list = databaseFacade.find(sql, params.toArray());
		} catch (Exception e) {
			logger.error("findNodes ::: ",e);
			throw AppstatusExceptionHandle
					.throwAppstatusException(new AppstatusDatabaseException());
		}
		return (List<Node>) list;
	}

	/*
	 * @Override
	 * 
	 * @Transactional public int updateNode(Long id, String[] columnNames,
	 * Object[] columnValues) throws AppstatusDatabaseException { StringBuilder
	 * sql = new StringBuilder("UPDATE Node n SET "); List<Object> params = new
	 * ArrayList<Object>();
	 * 
	 * int i = 0; while(i < columnNames.length) { sql.append("n." +
	 * columnNames[i] + " = ?" + (i+1)); if (i < columnNames.length -1) {
	 * sql.append(","); } params.add(columnValues[i]); i++; }
	 * 
	 * if (id != null && id > 0) { i++; sql.append(" WHERE n.id = ?"+i);
	 * params.add(id); } else { return 0; } int result = 0; try {
	 * logger.debug("<=AppStatus=> updateNode: id = " + id +
	 * ", columnNames's length = " + columnNames.length +
	 * ", columnValue's length = " + columnValues.length + ", sql = " + sql +
	 * "."); result = databaseFacade.update(sql.toString(), params.toArray()); }
	 * catch (Exception e) {
	 * AppstatusExceptionHandle.throwAppstatusException(new
	 * AppstatusDatabaseException()); } return result; }
	 */
	@Override
	@Transactional
	public int updateNode(NodeUpdate nodeUpdate)
			throws AppstatusDatabaseException {
		Long id = nodeUpdate.getId();
		String[] columnNames = nodeUpdate.getColumnNames();
		Object[] columnValues = nodeUpdate.getColumnValues();

		StringBuilder sql = new StringBuilder("UPDATE Node n SET ");
		List<Object> params = new ArrayList<Object>();

		int i = 0;
		while (i < columnNames.length) {
			sql.append("n." + columnNames[i] + " = ?" + (i + 1));
			if (i < columnNames.length - 1) {
				sql.append(",");
			}
			params.add(columnValues[i]);
			i++;
		}
		sql.append(" WHERE 1=1 ");
		if (id != null && id > 0) {
			i++;
			sql.append(" AND n.id = ?" + i);
			params.add(id);
		} else if (nodeUpdate.getWhere().keySet().size() != 0) {
			for (String key : nodeUpdate.getWhere().keySet()) {
				i++;
				sql.append(" AND n." + key + "=?" + i);
				params.add(nodeUpdate.getWhere().get(key));
			}
		} else if (nodeUpdate.getNotWhere().keySet().size() != 0) {
			for (String key : nodeUpdate.getNotWhere().keySet()) {
				i++;
				sql.append(" AND n." + key + "<>?" + i);
				params.add(nodeUpdate.getNotWhere().get(key));
			}
		} else {
			return 0;
		}
		int result = 0;
		try {
		logger.info("<=AppStatus=> updateNode: id = " + id
					+ ", columnNames's length = " + columnNames.length
					+ ", columnValue's length = " + columnValues.length
					+ ", sql = " + sql + "  params :: "+ params.toArray().length);
		StringBuilder logappend = new StringBuilder("params  :::" );
		 
		for (Object v: params.toArray()) {
			logappend.append(v+",");
		}
			result = databaseFacade.update(sql.toString(), params.toArray());
		} catch (Exception e) {
			logger.error("updateNode :::",e);
			AppstatusExceptionHandle
					.throwAppstatusException(new AppstatusDatabaseException());
		}
		return result;
	}

	@Override
	@Transactional
	public void deleteEntityById(Class<?> classEntity, Serializable id) {
		Set<Serializable> idList = new TreeSet<Serializable>();
		idList.add(id);
		logger.debug("<=AppStatus=> deleteEntityById: classEntity = "
				+ classEntity + ", id = " + id + ".");
		databaseFacade.removeAll(classEntity, idList);
	}

	@Override
	@Transactional
	public void deleteEntitiesByIds(Class<?> classEntity,
			Set<Serializable> idList) {
		logger.debug("<=AppStatus=> deleteEntitiesByIds: classEntity = "
				+ classEntity + ", idList = " + idList + ".");
		System.out.println("<=AppStatus=> deleteEntitiesByIds: classEntity = "
				+ classEntity + ", idList = " + idList + ".");
		databaseFacade.removeAll(classEntity, idList);
	}

	public DatabaseFacade getDatabaseFacade() {
		return databaseFacade;
	}

	public void setDatabaseFacade(DatabaseFacade databaseFacade) {
		this.databaseFacade = databaseFacade;
	}

	@Override
	public void updateNode(Node node) throws AppstatusDatabaseException {
		logger.debug("updateNode(Node node) :: " + node);
		if (node.getId() != null) {
			Node dbNode = this.getDatabaseFacade().load(Node.class,
					node.getId());
			BeanUtils.copyProperties(node, dbNode, false);
			dbNode.setNodeState(node.getNodeState());
			dbNode.setClusterState(node.getClusterState());
			logger.debug("updateNode(Node node) ::" + dbNode);
			this.databaseFacade.merge(dbNode);
		}
	}
	public static void main(String[] args) throws AppstatusDatabaseException {
		AppStatusDaoImpl ip = new AppStatusDaoImpl();
		NodeUpdate nodeUpdate = new NodeUpdate();
		nodeUpdate.setColumnNames(new String[] { "nodeState","clusterState" });
		nodeUpdate.setColumnValues(new Object[] { NodeState.ERROR.getValue(),ClusterState.DOWN.ordinal() });

		
		Map<String, Object> where=new HashMap<String, Object>();
		where.put("nodeState", NodeState.ERROR.getValue());
		where.put("nodeState", NodeState.LOST.getValue());
		nodeUpdate.setWhere(where);
//		this.appStatusDao.updateNode(nodeUpdate);
		ip.updateNode(nodeUpdate);
		NodeUpdate nodeUpdateReady = new NodeUpdate();
		nodeUpdateReady.setColumnNames(new String[] { "nodeState","clusterState" });
		nodeUpdateReady.setColumnValues(new Object[] { NodeState.READY
				.getValue() ,ClusterState.DOWN.ordinal()});

		Map<String, Object> notwhere=new HashMap<String, Object>();
		notwhere.put("nodeState", NodeState.ERROR.getValue());
		nodeUpdateReady.setNotWhere(notwhere);
		ip.updateNode(nodeUpdateReady);
		
	}

}