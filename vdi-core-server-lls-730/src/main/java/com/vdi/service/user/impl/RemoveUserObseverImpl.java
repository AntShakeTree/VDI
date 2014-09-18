package com.vdi.service.user.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.vdi.dao.user.UserDao;
import com.vdi.dao.user.domain.Organization;
import com.vdi.dao.user.domain.User;
import com.vdi.dao.user.domain.UserMapBridge;
import com.vdi.service.desktop.DestroyConnectionObserver;
import com.vdi.service.user.DisAllocatGrouObserver;
import com.vdi.service.user.RemoveUserObserver;
import com.vdi.service.user.UserStateSubject;
@Service
public class RemoveUserObseverImpl implements RemoveUserObserver{
	private UserMapBridge config;
	private @Autowired UserDao userDao;
	private @Autowired DestroyConnectionObserver destroyConnectionObserver;
	private @Autowired DisAllocatGrouObserver disAllocatGroupObserver;
	@Override
	public void whenUserStateChangeUpdateByLdapconfig(
			UserStateSubject stateSubject) throws Exception {
		if(config.getStatus()!=UserMapBridge.REMOVE_USER){
			return ;
		}
		for (Organization organization : config.getOrganizations()) {
			userDao.excuteHql("update User set status=? where organizationid=?", UserMapBridge.DELETING,organization.getIdorganization());
			User user =new User();
			user.setOrganizationid(organization.getIdorganization());
			List<User> users = userDao.listRequest(user);
			config.setUsers(users);
			config.setStatus(UserMapBridge.DIS_CONN);
			stateSubject.registerUserStateChangeObserver(destroyConnectionObserver,config);
			config.setStatus(UserMapBridge.DIS_ALLOCAT_USER);
			stateSubject.registerUserStateChangeObserver(disAllocatGroupObserver, config);
			userDao.excuteHql("delete from User  where organizationid=?",organization.getIdorganization());
			config.setUsers(null);
		}		
	}

	@Override
	public void setUserMapBridge(UserMapBridge config) {
		this.config=config;
	}

}
