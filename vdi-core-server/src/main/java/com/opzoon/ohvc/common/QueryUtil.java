/**   
* @Title: QueryUtil.java 
* @Package com.opzoon.ohvc.common 
* @Description: TODO 
* @author David   
* @date 2013-1-30 下午5:02:28 
* @version V1.0   
*/
package com.opzoon.ohvc.common;

import java.util.List;

/** 
 * ClassName: QueryUtil 
 * @Description: TODO
 * @author David 
 * @date 2013-1-30 下午5:02:28 
 *  
 */
public class QueryUtil {
	private String hql;
	private List<Object> values;
	/** 
	 * @return hql 
	 */
	public String getHql() {
		return hql;
	}
	/**
	 * @param hql the hql to set
	 */
	public void setHql(String hql) {
		this.hql = hql;
	}
	/** 
	 * @return values 
	 */
	public List<Object> getValues() {
		return values;
	}
	/**
	 * @param values the values to set
	 */
	public void setValues(List<Object> values) {
		this.values = values;
	}
	/** 
	* <p>Title: </p> 
	* <p>Description: </p> 
	* @param hql
	* @param values 
	*/
	
	QueryUtil(String hql, List<Object> values) {
		super();
		this.hql = hql;
		this.values = values;
	}
	
	
	
}
