/**
 * Project Name:vdi-core-server-lls
 * File Name:LdapServiceImpl.java
 * Package Name:com.vdi.service.user.impl
 * Date:2014年8月15日下午1:36:45
 * Copyright (c) 2014 All Rights Reserved.
 *
*/

package com.vdi.service.user.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.vdi.dao.user.LdapConfigDao;
import com.vdi.dao.user.domain.LdapConfig;
import com.vdi.service.user.LdapService;
@Service
public class LdapServiceImpl implements LdapService {
	private @Autowired LdapConfigDao ldapconfigDao;
	
	@Override
	public void addLdapConfig(LdapConfig ldapConfig) {
		ldapconfigDao.save(ldapConfig);
	}

	
	@Override
	public boolean isExist(String url) {
		if(ldapconfigDao.findOneByKey("url", url)!=null){
			return true;
		}
		return false;
	}

}
