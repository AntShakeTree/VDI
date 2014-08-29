package com.vdi.dao.suport;

import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
//import javax.transaction.Transactional;



import org.springframework.stereotype.Repository;
//import org.springframework.transaction.annotation.Transactional;



import org.springframework.transaction.annotation.Transactional;

import com.vdi.common.PolicyValue;
import com.vdi.common.Session;
import com.vdi.common.State;
import com.vdi.common.cache.CacheDomain;
import com.vdi.dao.Dao;
import com.vdi.dao.GenericsUtils;
import com.vdi.dao.PageRequest;
import com.vdi.dao.Request;

@Repository("daoSuport")
public class JPADaoSuport<T> implements Dao<T> {

	@PersistenceContext
	private EntityManager entityManager;

	public EntityManager getEntityManager() {
		return entityManager;
	}

	@SuppressWarnings("unchecked")
	@Transactional(readOnly=true)
	public <V extends CacheDomain> List<V> listRequest(Request<V> req) {
		List<V> ls = null;
		String daoname = GenericsUtils.getMethodParameterGenericsInterfaceType(
				req).getSimpleName();
		QueryUtil queryUtil = null;
		try {
			queryUtil = QueryUtil.getHqlByDomain(req);
			State state = Session.getStateBySeed(daoname);
			if (state != null) {
				PolicyValue p = state.getPolicyValue(queryUtil);
				if (p != null) {
					ls = state.queryListByPolicyValue(p.getKeys());
					return ls;
				}
			}
			String hql = queryUtil.getHql();
			List<Object> args = queryUtil.getValues();
			Query query = entityManager.createQuery(hql);
			if (req instanceof PageRequest) {
				int pagesize= ((PageRequest<V>)req).getPagesize();
				if(pagesize!=-1){
				PageView pageView = new PageView(pagesize,
						((PageRequest<V>) req).getPage());

				ls = (List<V>) prepareParamlizedQuery(query, args.toArray())
						.setFirstResult(pageView.getFirstResult())
						.setMaxResults(pageView.getMaxresult()).getResultList();
				((PageRequest<V>) req).setAmount(getCount(hql, args));
				}else{
					ls=(List<V>)prepareParamlizedQuery(query, args.toArray()).getResultList();
				}
			} else {
				ls = JPADaoSuport.prepareParamlizedQuery(query, args.toArray())
						.getResultList();
			}
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			e.printStackTrace();
		} catch (NoSuchFieldException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SecurityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		State state = Session.getStateBySeed(daoname);
		if(ls!=null){
		synchronized (state) {
			try {
				state.openDoor();
				List<Object> keys = new ArrayList<Object>(ls.size());
				for (V t : ls) {
					keys.add(t.getId());
					state.putCache(t.getId(), t);
				}
				state.putPolicy(queryUtil, keys);
				state.closeDoor();
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			}
		}}
		return ls;
	}

	public int getCount(String hql, List<Object> values) {
		if (hql.contains("select")) {
			hql = hql.substring(hql.indexOf("from"));
		}
		Query q = this.entityManager.createQuery("select count(*) " + hql);
		return prepareParamlizedQuery(q, values.toArray()).getFirstResult();
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

	@Override
	@Transactional
	public void save(Object domain) {
		// 清除缓存
		Session.getStateBySeed(domain.getClass().getSimpleName()).gc();
		this.entityManager.persist(domain);
	}

	@Override
	public T get(Class<T> clazz, Serializable identity) {
		return this.entityManager.find(clazz, identity);
	}
	@Transactional
	@Override
	public T update(T domain) {
		return this.entityManager.merge(domain);
	}
//	@Transactional(readOnl)
	@javax.transaction.Transactional
	@Override
	public void delete(T domain) {
		this.entityManager.remove(entityManager.merge( domain));
	}

	public void excuteNative(String hql, Object... args) {
		prepareParamlizedQuery(this.entityManager.createNativeQuery(hql), args)
				.executeUpdate();
	}
	@Transactional
	public void excuteHql(String hql, Object... args) {
		prepareParamlizedQuery(this.entityManager.createQuery(hql), args)
				.executeUpdate();
	}

	@Override
	@Transactional
	public void deleteByIds(Class<T> clazz, Serializable... identities) {
		if (identities == null) {
			return;
		}
		for (Serializable identity : identities) {
			this.delete(get(clazz, identity));
		}
	}

	@SuppressWarnings("unchecked")
	Class<T> clazz = GenericsUtils.getSuperClassGenricType(getClass());

	public Class<T> getClazz() {
		return clazz;
	}

	@SuppressWarnings("unchecked")
	@Override
	public T findOneByKey(String filedName, Object key) {

		try {
			return (T) this.entityManager
					.createQuery(
							"from " + this.clazz.getSimpleName() + " where "
									+ filedName + " =?").setParameter(1, key)
					.getResultList().get(0);
		} catch (Exception e) {
			return null;
		}

	}

}
