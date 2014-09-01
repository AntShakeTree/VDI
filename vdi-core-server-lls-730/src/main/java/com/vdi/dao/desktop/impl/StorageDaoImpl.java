package com.vdi.dao.desktop.impl;


import org.springframework.stereotype.Repository;

import com.vdi.dao.desktop.StorageDao;
import com.vdi.dao.desktop.domain.StorageEntity;
import com.vdi.dao.suport.JPADaoSuport;
@Repository
public class StorageDaoImpl extends JPADaoSuport<StorageEntity> implements StorageDao {
}
