/**   
 * @Title: ValidatorManager.java 
 * @Package com.opzoon.vdi.core.facade 
 * @Description: TODO 
 * @author David   
 * @date 2013-2-19 下午3:49:47 
 * @version V1.0   
 */
package com.opzoon.vdi.core.facade;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.opzoon.ohvc.common.OpzoonUtils;
import com.opzoon.vdi.core.util.StringUtils;

/**
 * ClassName: ValidatorManager
 * 
 * @Description: TODO
 * @author David
 * @date 2013-2-19 下午3:49:47
 */
public class ValidatorManager {
	private static final Logger log = LoggerFactory.getLogger(ValidatorManager.class);

	private DatabaseFacade databaseFacade;

	/** 
	 * @return databaseFacade 
	 */
	public DatabaseFacade getDatabaseFacade() {
		return databaseFacade;
	}

	/**
	 * @param databaseFacade the databaseFacade to set
	 */
	public void setDatabaseFacade(DatabaseFacade databaseFacade) {
		this.databaseFacade = databaseFacade;
	}
	/**
	 * Title: isRepeatable
	 * : validator utile
	 * @param entryName
	 * @param propertyName
	 * @param propertyValue
	 * @param
	 * @throws
	 */
	public boolean isRepeatable(String entryName, String propertyName, Object propertyValue) {
		String hql = StringUtils.strcat("select count(*) from ", entryName, " where ", propertyName, "=?");
		
		// Evan
		Object count = databaseFacade.findFirst(hql, propertyValue);
		if(count==null){
			count="0";
		}
		return Long.parseLong("0"+count ) > 0;
	}
	/**
	 * Title: isRepeatable
	 * : 判断IP是否重复
	 * @param entryName
	 * @param propertyName
	 * @param propertyValue
	 * @param
	 * @throws
	 */
	@SuppressWarnings("unchecked")
	public boolean isRepeatableIP(String entryName, String propertyName, Object propertyValue) {
		String hql = StringUtils.strcat("select ", propertyName, " from ", entryName, " where ", propertyName, "=?");
		// Evan
		List<String> dos = (List<String>) databaseFacade.find(hql, propertyValue);
		Set<String> has = new HashSet<String>();
		for (String ip : dos) {
			has.add(OpzoonUtils.covertDomainToIp(ip));
		}
		if (has.contains(OpzoonUtils.covertDomainToIp(propertyValue + ""))) {
			has = null;
			dos = null;
			return true;
		}
		return false;
	}


}
