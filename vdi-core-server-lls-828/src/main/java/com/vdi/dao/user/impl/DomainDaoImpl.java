/**
 * Project Name:vdi-core-server-lls
 * File Name:DomainDaoImpl.java
 * Package Name:com.vdi.dao.user.impl
 * Date:2014年8月15日下午1:19:00
 * Copyright (c) 2014 All Rights Reserved.
 *
*/

package com.vdi.dao.user.impl;

import org.springframework.stereotype.Repository;

import com.vdi.dao.suport.JPADaoSuport;
import com.vdi.dao.user.DomainDao;
import com.vdi.dao.user.domain.Domain;
@Repository
public class DomainDaoImpl extends JPADaoSuport<Domain> implements DomainDao {
}