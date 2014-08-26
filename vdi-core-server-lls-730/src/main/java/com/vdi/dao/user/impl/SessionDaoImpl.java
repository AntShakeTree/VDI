/**
 * Project Name:vdi-core-server-lls
 * File Name:SessionDaoImpl.java
 * Package Name:com.vdi.dao.user.impl
 * Date:2014年8月7日下午6:03:07
 * Copyright (c) 2014 All Rights Reserved.
 *
*/

package com.vdi.dao.user.impl;

import org.springframework.stereotype.Repository;

import com.vdi.dao.suport.JPADaoSuport;
import com.vdi.dao.user.SessionDao;
import com.vdi.dao.user.domain.Session;
@Repository("sessionDao")
public class SessionDaoImpl extends JPADaoSuport<Session> implements SessionDao{

}
