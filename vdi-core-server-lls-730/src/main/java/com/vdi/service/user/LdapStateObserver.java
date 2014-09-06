package com.vdi.service.user;

import com.vdi.dao.user.domain.UserMapBridge;

public interface LdapStateObserver {
	void whenLdapStateChangeUpdateByLdapconfig(LdapStateSubject stateSubject) throws Exception;	
	void setLdapConfig(UserMapBridge config);
}
