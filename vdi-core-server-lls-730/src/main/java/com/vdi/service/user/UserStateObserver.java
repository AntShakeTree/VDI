package com.vdi.service.user;

import com.vdi.dao.user.domain.UserMapBridge;

public interface UserStateObserver {
	void whenUserStateChangeUpdateByLdapconfig(UserStateSubject stateSubject) throws Exception;	
	void setUserMapBridge(UserMapBridge config);
}
