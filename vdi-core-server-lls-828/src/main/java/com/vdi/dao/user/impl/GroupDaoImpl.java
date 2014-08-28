/**
 * Project Name:vdi-core-server-lls
 * File Name:GroupDaoImpl.java
 * Package Name:com.vdi.dao.user.impl
 * Date:2014年8月15日下午1:27:01
 * Copyright (c) 2014 All Rights Reserved.
 *
*/

package com.vdi.dao.user.impl;

import org.springframework.stereotype.Repository;

import com.vdi.dao.suport.JPADaoSuport;
import com.vdi.dao.user.GroupDao;
import com.vdi.dao.user.domain.DeliveryGroup;
@Repository
public class GroupDaoImpl extends JPADaoSuport<DeliveryGroup> implements GroupDao {

}