/**
 * Project Name:vdi-core-server-lls
 * File Name:HostEntityDaoImpl.java
 * Package Name:com.vdi.dao.desktop.impl
 * Date:2014年8月11日下午1:50:12
 * Copyright (c) 2014 All Rights Reserved.
 *
*/

package com.vdi.dao.desktop.impl;

import org.springframework.stereotype.Repository;

import com.vdi.dao.desktop.HostDao;
import com.vdi.dao.desktop.domain.HostEntity;
import com.vdi.dao.suport.JPADaoSuport;
@Repository
public class HostEntityDaoImpl extends JPADaoSuport<HostEntity> implements HostDao {


}
