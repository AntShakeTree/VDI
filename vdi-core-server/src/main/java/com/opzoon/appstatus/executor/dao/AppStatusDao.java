/**
 * 
 * @Title AppStatusDao.java
 * @Description AppStatus模块内部的数据库访问对象接口定义
 * Copyright: Copyright (c) 2013, Opzoon and/or its affiliates. All rights reserved.
 * OPZOON PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 * 
 * @author NY
 * @date 2013-10-22 上午11:02:07
 * 
 */
package com.opzoon.appstatus.executor.dao;

import java.io.Serializable;
import java.util.List;
import java.util.Set;

import com.opzoon.appstatus.common.exception.AppstatusRestException;
import com.opzoon.appstatus.common.exception.custom.AppstatusDatabaseException;
import com.opzoon.appstatus.domain.Node;
import com.opzoon.appstatus.domain.NodeUpdate;
import com.opzoon.appstatus.domain.req.NodeReq;

/**
 * 
 * AppStatus模块内部的数据库访问对象接口定义。
 * 
 * @author <a href="mailto:mynameisny@qq.com">Tony Joseph</a>
 * @version 2.0
 * 
 */
public interface AppStatusDao {
	/**
	 * 将指定的节点保存到数据库中
	 * 
	 * @param node
	 *            需要被持久化的节点
	 */
	public abstract void saveNode(Node node);

	/**
	 * 根据查询条件返回符合条件的节点列表。
	 * 
	 * @param nodeReq
	 *            字符串表示的查询条件，使用:名称占位符或者?索引占位符
	 */
	List<Node> findNodes(String condString, Object[] params);

	/**
	 * 根据节点查询条件返回符合条件的某个节点。
	 * 
	 * @param nodeReq
	 *            nodeReq 查询条件
	 * @throws AppstatusRestException
	 */
	public abstract Node findNode(NodeReq nodeReq)
			throws AppstatusRestException;

	/**
	 * 根据查询条件返回符合条件的节点列表。
	 * 
	 * @param nodeReq
	 *            查询条件
	 * @throws AppstatusRestException
	 */
	public abstract List<Node> findNodes(NodeReq nodeReq)
			throws AppstatusRestException;

	/**
	 * 更新节点在数据库中的状态。
	 * 
	 * @param id
	 *            需要修改列的主键标识
	 * @param columnNames
	 *            需要修改的列名
	 * @param columnValues
	 *            需要修改的列值
	 * @return 受影响的记录数
	 * @throws AppstatusDatabaseException
	 */
	// public abstract int updateNode(Long id, String[] columnNames, Object[]
	// columnValues) throws AppstatusRestException;
	public abstract int updateNode(NodeUpdate nodeUpdate)
			throws AppstatusRestException;

	public abstract void updateNode(Node node) throws AppstatusDatabaseException;

	/**
	 * 删除数据库中指定的某个实体
	 * 
	 * @param classEntity
	 *            需要删除的实体Class类型
	 * @param id
	 *            需要删除的实体的主键id
	 */
	public abstract void deleteEntityById(Class<?> classEntity, Serializable id);

	/**
	 * 删除数据库中指定的全部节点
	 * 
	 * @param classEntity
	 *            需要删除的实体Class类型
	 * @param idList
	 *            需要删除的全部节点的主键id
	 */
	public abstract void deleteEntitiesByIds(Class<?> classEntity,
			Set<Serializable> idList);
}
