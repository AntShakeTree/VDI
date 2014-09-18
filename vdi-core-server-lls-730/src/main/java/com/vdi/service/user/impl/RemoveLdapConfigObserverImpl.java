package com.vdi.service.user.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.vdi.dao.user.OrganizationDao;
import com.vdi.dao.user.UserDao;
import com.vdi.dao.user.domain.UserMapBridge;
import com.vdi.service.desktop.DestroyConnectionObserver;
import com.vdi.service.user.RemoveLdapConfigObserver;
import com.vdi.service.user.UserStateSubject;

@Service
public class RemoveLdapConfigObserverImpl implements RemoveLdapConfigObserver {
	private UserMapBridge config;
	@Autowired private OrganizationDao organizationDao;
	@Autowired private UserDao userDao;
	@Autowired private DestroyConnectionObserver destroyConnectionObserver;
	@Override
	public void whenUserStateChangeUpdateByLdapconfig(
			UserStateSubject stateSubject) {
	//	organizationDao.excuteHql("update Organization set status=? where domainguid=?", LdapConfig.DELETING,config.getGuid());
		//organizationDao.excuteHql("update User set status=? where domainguid=?", LdapConfig.DELETING,config.getGuid());
		
		
//		stateSubject.registerStateChangeObserver(destroyConnectionObserver, config);
	}

	@Override
	public void setUserMapBridge(UserMapBridge config) {
		this.config = config;
	}
}
