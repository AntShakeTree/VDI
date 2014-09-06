package com.vdi.service.user;

import com.vdi.dao.user.domain.LdapConfig;

public interface LdapStateObserver {
	void whenLdapStateChangeUpdateByLdapconfig(LdapStateSubject stateSubject) throws Exception;	
	void setLdapConfig(LdapConfig config);
}
