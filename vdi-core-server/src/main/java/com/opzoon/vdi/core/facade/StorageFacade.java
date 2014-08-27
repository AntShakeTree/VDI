package com.opzoon.vdi.core.facade;

import java.io.Serializable;
import java.util.List;

/**
 * 存储接口.
 */
public interface StorageFacade {

	/**
	 * 插入实体.
	 * 
	 * @param entity
	 *            实体.
	 */
	void persist(final Object entity);

	/**
	 * 更新实体.
	 * 
	 * @param <E>
	 *            参数化实体.
	 * @param entity
	 *            实体.
	 * @return 更新后的实体.
	 */
	<E> E merge(final E entity);

	/**
	 * 查询并返回结果列表中的第一项.
	 * 
	 * @param queryS
	 *            查询语句.
	 * @param params
	 *            参数列表.
	 * @return 结果列表中的第一项.
	 */
	Object findFirst(final String queryS, final Object... params);

	/**
	 * 查询并返回结果列表.
	 * 
	 * @param queryS
	 *            查询语句.
	 * @param params
	 *            参数列表.
	 * @return 结果列表.
	 */
	List<?> find(String queryS, final Object... params);

	/**
	 * 指定范围查询并返回结果列表.
	 * 
	 * @param start
	 *            起始索引.
	 * @param delta
	 *            查询增量.
	 * @param queryS
	 *            查询语句.
	 * @param params
	 *            参数列表.
	 * @return 结果列表.
	 */
	List<?> find(final int start, final int delta, String queryS,
			final Object... params);

	/**
	 * 更新.
	 * 
	 * @param queryS
	 *            更新语句.
	 * @param params
	 *            参数列表.
	 * @return 受影响的记录数.
	 */
	int update(String queryS, final Object... params);

	/**
	 * 读取实体.
	 * 
	 * @param <E>
	 *            参数化实体类型.
	 * @param entityType
	 *            实体类型.
	 * @param id
	 *            实体的id.
	 * @return 拥有指定id的实体.
	 */
	<E> E load(final Class<E> entityType, final Serializable id);

	/**
	 * 删除实体.
	 * 
	 * @param entity
	 *            实体.
	 */
	void remove(final Object entity);

}
