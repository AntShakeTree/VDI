package com.vdi.dao.desktop.impl;

import org.springframework.stereotype.Repository;

import com.vdi.dao.desktop.DesktopDao;
import com.vdi.dao.desktop.domain.Desktop;
import com.vdi.dao.suport.JPADaoSuport;
@Repository("desktopDao")
public class DesktopDaoImpl extends JPADaoSuport<Desktop> implements DesktopDao {
	
}
