/**
 * Project Name:vdi-core-server-lls
 * File Name:Dao.java
 * Package Name:com.vdi.dao
 * Date:2014年7月30日下午4:18:18
 * Copyright (c) 2014 All Rights Reserved.
 *
*/
/**
 * Project Name:vdi-core-server-lls
 * File Name:Dao.java
 * Package Name:com.vdi.dao
 * Date:2014年7月30日下午4:18:18
 * Copyright (c) 2014,  All Rights Reserved.
 *
 */

package com.vdi.dao;

import java.io.Serializable;
import java.util.List;

import com.vdi.common.cache.CacheDomain;

/**
 * ClassName: Dao <br/>
 * Function: TODO ADD FUNCTION. <br/>
 * date: 2014年7月30日 下午4:18:18 <br/>
 *
 * @author tree
 * @version 
 * @since JDK 1.7
 */
public interface Dao<T > {
	public  void save(Object domain);
	public  T get(Class<T> clazz,Serializable identity);
	public <V extends CacheDomain> List<V> listRequest(Request<V> request);
	public  T update(T domain);
	public  void deleteByIds(Class<T> clazz,Serializable ... identities);
	public  void delete(T domain);
	public T findOneByKey(String filedName,Object key);
	public void excuteNative(String hql, Object... args);
	public void excuteHql(String hql, Object... args);
}
