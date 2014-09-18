package com.vdi.service.user.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.vdi.dao.user.OrganizationDao;
import com.vdi.dao.user.domain.Organization;
import com.vdi.dao.user.domain.UserMapBridge;
import com.vdi.service.desktop.DestroyConnectionObserver;
import com.vdi.service.user.RemoveOrganizationObserver;
import com.vdi.service.user.RemoveUserObserver;
import com.vdi.service.user.UserStateSubject;
@Service
public class RemoveOrganizationObserverImpl implements
		RemoveOrganizationObserver {
	private @Autowired OrganizationDao organizationDao;
	private  @Autowired DestroyConnectionObserver destroyConnectionObserver;
	private @Autowired RemoveUserObserver removeUserObserver;
	private UserMapBridge config;
	@Override
	public void whenUserStateChangeUpdateByLdapconfig(
			UserStateSubject stateSubject) {
		organizationDao.excuteHql("update Organization set status=? where status =? and domainguid=?",UserMapBridge.DELETING, UserMapBridge.SYNC_OU,config.getGuid());
		Organization organization =new Organization();
		organization.setDomainguid(config.getGuid());
		config.setOrganizations(organizationDao.listRequest(organization));
		config.setStatus(UserMapBridge.REMOVE_USER);
		stateSubject.registerUserStateChangeObserver(removeUserObserver, config);
		config.setStatus(UserMapBridge.DIS_CONN);
		stateSubject.registerUserStateChangeObserver(destroyConnectionObserver, config);
		organizationDao.excuteHql("delete from Organization where status =? and domainguid=? ",UserMapBridge.DELETING,config.getGuid());
		config.setOrganizations(null);
	}
	@Override
	public void setUserMapBridge(UserMapBridge config) {
		this.config=config;
	}

}
