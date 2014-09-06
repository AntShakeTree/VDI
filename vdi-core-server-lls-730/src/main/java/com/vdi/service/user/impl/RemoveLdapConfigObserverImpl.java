package com.vdi.service.user.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.vdi.dao.suport.LdapSupport;
import com.vdi.dao.user.OrganizationDao;
import com.vdi.dao.user.UserDao;
import com.vdi.dao.user.domain.UserMapBridge;
import com.vdi.dao.user.domain.Organization;
import com.vdi.service.desktop.DestroyConnectionObserver;
import com.vdi.service.user.LdapStateSubject;
import com.vdi.service.user.RemoveLdapConfigObserver;

@Service
public class RemoveLdapConfigObserverImpl implements RemoveLdapConfigObserver {
	private UserMapBridge config;
	@Autowired private OrganizationDao organizationDao;
	@Autowired private UserDao userDao;
	@Autowired private DestroyConnectionObserver destroyConnectionObserver;
	@Override
	public void whenLdapStateChangeUpdateByLdapconfig(
			LdapStateSubject stateSubject) {
	//	organizationDao.excuteHql("update Organization set status=? where domainguid=?", LdapConfig.DELETING,config.getGuid());
		//organizationDao.excuteHql("update User set status=? where domainguid=?", LdapConfig.DELETING,config.getGuid());
		
		
//		stateSubject.registerStateChangeObserver(destroyConnectionObserver, config);
	}

	@Override
	public void setLdapConfig(UserMapBridge config) {
		this.config = config;
	}
}
