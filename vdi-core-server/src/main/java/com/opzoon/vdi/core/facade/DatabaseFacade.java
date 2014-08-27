package com.opzoon.vdi.core.facade;

import static com.opzoon.vdi.core.util.StringUtils.strcat;

import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.opzoon.ohvc.common.OpzoonUtils;
import com.opzoon.ohvc.common.QueryUtil;
import com.opzoon.ohvc.request.PageRequest;
import com.opzoon.vdi.core.app.common.PageView;
import com.opzoon.vdi.core.util.QueryHelper;

/**
 * 数据库接口.
 */
public class DatabaseFacade implements StorageFacade {
	
	private static final Logger log = LoggerFactory.getLogger(DatabaseFacade.class);

	private EntityManager entityManager;

	/**
	 * 插入实体.
	 * 
	 * @param entity
	 *            实体.
	 */
	public void persist(final Object entity) {
		log.trace("persist: {}", entity);
		entityManager.persist(entity);
	}

	/**
	 * 查询并返回结果列表中的第一项.
	 * 
	 * @param queryS
	 *            查询语句.
	 * @param params
	 *            参数列表.
	 * @return 结果列表中的第一项.
	 */
	public Object findFirst(final String queryS, final Object... params) {
		List<?> list = find(0, 1, queryS, params);
		if (list.isEmpty()) {
			return null;
		}
		return list.get(0);
	}

	/**
	 * 查询并返回结果列表.
	 * 
	 * @param queryS
	 *            查询语句.
	 * @param params
	 *            参数列表.
	 * @return 结果列表.
	 */
	public List<?> find(String queryS, final Object... params) {
		queryS = replaceNull(queryS, params);
		if (log.isTraceEnabled()) {
			StringBuilder sb = new StringBuilder();
			sb.append("find: ");
			sb.append(queryS);
			sb.append(" params: ");
			for (Object param : params) {
				sb.append(param);
				sb.append(" ");
			}
			log.trace(sb.toString());
		}
		Query query = prepareParamlizedQuery(entityManager.createQuery(queryS),
				params);
		List<?> list = query.getResultList();
		log.trace("find result: {}", list);
		return list;
	}

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
	public List<?> find(final int start, final int delta, String queryS,
			final Object... params) {
		queryS = replaceNull(queryS, params);
		if (log.isTraceEnabled()) {
			StringBuilder sb = new StringBuilder();
			sb.append("find: ");
			sb.append(queryS);
			sb.append(" start: ");
			sb.append(start);
			sb.append(" delta: ");
			sb.append(delta);
			sb.append(" params: ");
			for (Object param : params) {
				sb.append(param);
				sb.append(" ");
			}
			log.trace(sb.toString());
		}
		Query query = prepareParamlizedQuery(entityManager.createQuery(queryS),
				params);
		query.setFirstResult(start);
		query.setMaxResults(delta);
		List<?> list = query.getResultList();
		log.trace("find result: {}", list);
		return list;
	}

	/**
	 * 使用本地SQL语句查询并返回结果列表.
	 * 
	 * @param queryS
	 *            本地SQL查询语句.
	 * @param params
	 *            参数列表.
	 * @return 结果列表.
	 */
	public List<?> findByNativeSQL(String queryS, final Object... params) {
		queryS = replaceNull(queryS, params);
		if (log.isTraceEnabled()) {
			StringBuilder sb = new StringBuilder();
			sb.append("findByNativeSQL: ");
			sb.append(queryS);
			sb.append(" params: ");
			for (Object param : params) {
				sb.append(param);
				sb.append(" ");
			}
			log.trace(sb.toString());
		}
		Query query = prepareParamlizedQuery(
				entityManager.createNativeQuery(queryS), params);
		List<?> list = query.getResultList();
		log.trace("find result: {}", list);
		return list;
	}

	// TODO Test it.
	public List<?> findByNativeSQL(final int start, final int delta, String queryS, final Object... params) {
		queryS = replaceNull(queryS, params);
		if (log.isTraceEnabled()) {
			StringBuilder sb = new StringBuilder();
			sb.append("findByNativeSQL: ");
			sb.append(queryS);
			sb.append(" params: ");
			for (Object param : params) {
				sb.append(param);
				sb.append(" ");
			}
			log.trace(sb.toString());
		}
		Query query = prepareParamlizedQuery(
				entityManager.createNativeQuery(queryS), params);
		query.setFirstResult(start);
		query.setMaxResults(delta);
		List<?> list = query.getResultList();
		log.trace("find result: {}", list);
		return list;
	}

	/**
	 * 更新.
	 * 
	 * @param queryS
	 *            更新语句.
	 * @param params
	 *            参数列表.
	 * @return 受影响的记录数.
	 */
	public int update(String queryS, final Object... params) {
		queryS = replaceNull(queryS, params);
		if (log.isTraceEnabled()) {
			StringBuilder sb = new StringBuilder();
			sb.append("update: ");
			sb.append(queryS);
			sb.append(" params: ");
			for (Object param : params) {
				sb.append(param);
				sb.append(" ");
			}
			log.trace(sb.toString());
		}
		Query query = prepareParamlizedQuery(entityManager.createQuery(queryS),
				params);
		int result = query.executeUpdate();
		log.trace("update result: {}", result);
		return result;
	}

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
	public <E> E load(final Class<E> entityType, final Serializable id) {
		log.trace("load: {} #{}", entityType, id);
		E entity = entityManager.find(entityType, id);
		log.trace("load result: {}", entity);
		if (entity == null)
    {
	    log.warn("load null: {} #{}", entityType, id);
    }
		return entity;
	}

	/**
	 * 更新实体.
	 * 
	 * @param <E>
	 *            参数化实体.
	 * @param entity
	 *            实体.
	 * @return 更新后的实体.
	 */
	public <E> E merge(final E entity) {
		log.trace("merge: {}", entity);
		return entityManager.merge(entity);
	}

	/**
	 * 删除实体.
	 * 
	 * @param entity
	 *            实体.
	 */
	public void remove(final Object entity) {
		log.trace("remove: {}", entity);
		entityManager.remove(entity);
	}

	/**
	 * 删除实体.
	 * 
	 * @param <E>
	 *            参数化实体类型.
	 * @param entityType
	 *            实体类型.
	 * @param id
	 *            实体的id集合.
	 */
	public <E> void removeAll(final Class<E> entityType,
			final Set<Serializable> ids) {
		if (ids.isEmpty()) {
			return;
		}
		final String whereClause = QueryHelper.buildOrIdWhereClause(ids.size(),
				"e.id");
		this.update(
				strcat("delete from ", entityType.getSimpleName(), " e where",
						whereClause), ids.toArray());
	}

	/**
	 * @param _entityManager
	 *            _entityManager
	 */
	@PersistenceContext
	public void setEntityManager(final EntityManager _entityManager) {
		this.entityManager = _entityManager;
	}

	private static String replaceNull(final String queryS,
			final Object... params) {
		if (params == null || params.length < 1) {
			return queryS;
		}
		List<int[]> indexOfReplacingEquals = null;
		int indexOfQuestion = -1;
		for (int i = 0; i < params.length; i++) {
			indexOfQuestion = queryS.indexOf('?', indexOfQuestion + 1);
			if (params[i] != null) {
				continue;
			}
			for (int j = indexOfQuestion; j >= indexOfQuestion - 2; j--) {
				if (queryS.charAt(j) == '=') {
					if (indexOfReplacingEquals == null) {
						indexOfReplacingEquals = new ArrayList<int[]>(
								params.length);
					}
					if (queryS.charAt(j - 1) == '!') {
						indexOfReplacingEquals.add(new int[] { j - 1,
								indexOfQuestion, 1 });
					} else {
						indexOfReplacingEquals.add(new int[] { j,
								indexOfQuestion, 0 });
					}
				}
			}
		}
		if (indexOfReplacingEquals == null) {
			return queryS;
		}
		StringBuilder querySB = new StringBuilder(queryS);
		int shift = 0;
		for (int[] indexOfReplacingEqual : indexOfReplacingEquals) {
			if (indexOfReplacingEqual[2] == 1) {
				querySB.replace(indexOfReplacingEqual[0] + shift,
						indexOfReplacingEqual[1] + 1 + shift, " is not null");
				shift += 12 - indexOfReplacingEqual[1]
						+ indexOfReplacingEqual[0] - 1;
			} else {
				querySB.replace(indexOfReplacingEqual[0] + shift,
						indexOfReplacingEqual[1] + 1 + shift, " is null");
				shift += 8 - indexOfReplacingEqual[1]
						+ indexOfReplacingEqual[0] - 1;
			}
		}
		return querySB.toString();
	}

	private static Query prepareParamlizedQuery(final Query query,
			final Object... params) {
		if (params == null) {
			return query;
		}
		int i = 0;
		for (Object param : params) {
			if (param == null) {
				continue;
			}
			query.setParameter(++i, param);
		}
		return query;
	}

	public void execNativeSql(String string) {
		entityManager.createNativeQuery(string).executeUpdate();
	}
	public <T> T get(Class<T> entityClass,Serializable primaryKey){
		return entityManager.find(entityClass, primaryKey);
	}
	@SuppressWarnings("unchecked")
	public <T> List<T> listPageRequest(PageRequest<T> req) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException {

		QueryUtil queryUtil = OpzoonUtils.getHqlByDomain(req);
		StringBuffer sb = new StringBuffer();
		sb.append(queryUtil.getHql());
		if (org.apache.commons.lang.StringUtils.isNotEmpty(req.getSortkey())) {
			sb.append(" order by ").append(req.getSortkey());
			if (req.getAscend() == 1) {
				sb.append(" asc");
			} else {
				sb.append(" desc");
			}
		}
		log.debug("listPageRequest ==>"+sb.toString());
		// Evan
		List<T> ls = null;
		if (req.getPage() != 0||req.getPagesize()!=-1) {
			PageView pageView = new PageView(req.getPagesize(), req.getPage());
			ls = (List<T>) this.find(pageView.getFirstResult(), pageView.getMaxresult(), sb.toString(), queryUtil.getValues().toArray());
		} else {
			ls = (List<T>) this.find(sb.toString(), queryUtil.getValues().toArray());
		}
		req.setAmount(getCount(sb.toString(), queryUtil.getValues()));
		return ls;
	}
	public int getCount(String string, List<Object> values) {
		if (string.contains("select")) {
			string = string.substring(string.indexOf("from"));
		}
		// Evan
		return Integer.parseInt(this.findFirst("select count(*) " + string, values.toArray()) + "");
	}
}
