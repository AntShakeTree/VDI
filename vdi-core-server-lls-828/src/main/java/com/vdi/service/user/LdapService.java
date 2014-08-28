/**
 * Project Name:vdi-core-server-lls
 * File Name:LdapService.java
 * Package Name:com.vdi.service.user
 * Date:2014年8月15日上午9:43:56
 * Copyright (c) 2014 All Rights Reserved.
 *
*/

package com.vdi.service.user;

import com.vdi.dao.user.domain.LdapConfig;


public interface LdapService {
	public void addLdapConfig(LdapConfig ldapConfig);
	public boolean isExist(String url);
}
