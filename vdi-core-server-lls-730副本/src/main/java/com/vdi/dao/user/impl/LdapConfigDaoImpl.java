/**
 * Project Name:vdi-core-server-lls
 * File Name:LdapConfigDaoImpl.java
 * Package Name:com.vdi.dao.user.impl
 * Date:2014年8月15日下午1:17:16
 * Copyright (c) 2014 All Rights Reserved.
 *
*/

package com.vdi.dao.user.impl;

import org.springframework.stereotype.Repository;

import com.vdi.dao.suport.JPADaoSuport;
import com.vdi.dao.user.LdapConfigDao;
import com.vdi.dao.user.domain.LdapConfigEntity;
@Repository
public class LdapConfigDaoImpl extends JPADaoSuport<LdapConfigEntity> implements LdapConfigDao {
}
