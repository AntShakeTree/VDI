/**
 * Project Name:vdi-core-server-lls
 * File Name:ComputePoolDaoImpl.java
 * Package Name:com.vdi.dao.desktop.impl
 * Date:2014年8月11日下午1:32:28
 * Copyright (c) 2014 All Rights Reserved.
 *
*/

package com.vdi.dao.desktop.impl;

import org.springframework.stereotype.Repository;

import com.vdi.dao.desktop.ComputePoolDao;
import com.vdi.dao.desktop.domain.ComputePoolEntity;
import com.vdi.dao.suport.JPADaoSuport;

@Repository
public class ComputePoolDaoImpl extends JPADaoSuport<ComputePoolEntity> implements ComputePoolDao{
}
