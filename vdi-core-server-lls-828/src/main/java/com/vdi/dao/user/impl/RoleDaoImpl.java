/**
 * Project Name:vdi-core-server-lls
 * File Name:RoleDaoImpl.java
 * Package Name:com.vdi.dao.user.impl
 * Date:2014年8月8日上午10:53:36
 * Copyright (c) 2014 All Rights Reserved.
 *
*/

package com.vdi.dao.user.impl;

import org.springframework.stereotype.Repository;

import com.vdi.dao.suport.JPADaoSuport;
import com.vdi.dao.user.domain.Role;
import com.vdi.dao.user.RoleDao;

@Repository("roleDao")
public class RoleDaoImpl extends JPADaoSuport<Role> implements RoleDao {

}
